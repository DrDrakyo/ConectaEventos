/* ==========================================================================
   TELA: EDITAR PERFIL (Módulo Prestador)
   JavaScript específico desta tela: pré-preenche o formulário e valida
   antes do envio. O campo "categoria" apenas SELECIONA um tipo já
   cadastrado em SERVICO — o Prestador não cria nem edita categorias.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  preencherCategorias();
  preencherFormulario();
  inicializarValidacao();
});

const CATEGORIAS_SERVICO = [
  'Fotografia e Filmagem', 'Música e DJ', 'Buffet e Gastronomia', 'Decoração e Cenografia',
  'Espaço e Locação', 'Cerimonial e Assessoria', 'Animação e Recreação', 'Segurança e Apoio'
];

function preencherCategorias() {
  const select = document.querySelector('#campo-categoria');
  if (!select) return;
  select.innerHTML = CATEGORIAS_SERVICO.map((categoria) => `<option value="${categoria}">${categoria}</option>`).join('');
}

async function preencherFormulario() {
  try {
    const response = await fetch('/perfilPrestador');
    if (response.ok) {
      const data = await response.json();
      if (data && data.sucesso && data.prestador) {
        const p = data.prestador;
        if (p.nome_prestador) document.querySelector('#campo-nome').value = p.nome_prestador;
        if (p.telefone) document.querySelector('#campo-telefone').value = p.telefone;
        if (p.nome_prestador) document.querySelector('#campo-nome-fantasia').value = p.nome_prestador;
        if (p.categoria) document.querySelector('#campo-categoria').value = p.categoria;
        if (p.descricao) document.querySelector('#campo-descricao').value = p.descricao;
        if (p.cidade) document.querySelector('#campo-localizacao').value = p.cidade;
        if (p.telefone) document.querySelector('#campo-contatos').value = p.telefone;
      }
    }
  } catch (e) {
    console.warn('Erro ao carregar dados do prestador:', e);
  }
}

function inicializarValidacao() {
  const formulario = document.querySelector('#formulario-editar-perfil');
  const mensagem = document.querySelector('#mensagem-feedback');
  if (!formulario) return;

  formulario.addEventListener('submit', async (evento) => {
    evento.preventDefault();

    const camposObrigatorios = ['nome', 'telefone', 'nome-fantasia', 'categoria', 'descricao', 'localizacao'];
    let valido = true;

    camposObrigatorios.forEach((nome) => {
      const input = document.querySelector(`#campo-${nome}`);
      const container = input.closest('.campo');
      if (!input.value.trim()) {
        container.classList.add('campo--invalido');
        valido = false;
      } else {
        container.classList.remove('campo--invalido');
      }
    });

    mensagem.classList.remove('mensagem-feedback--sucesso', 'mensagem-feedback--erro');

    if (!valido) {
      mensagem.textContent = 'Preencha corretamente todos os campos obrigatórios.';
      mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
      return;
    }

    try {
      const formData = new URLSearchParams();
      formData.append('nome_prestador', document.querySelector('#campo-nome-fantasia').value.trim() || document.querySelector('#campo-nome').value.trim());
      formData.append('telefone', document.querySelector('#campo-telefone').value.trim());
      formData.append('categoria', document.querySelector('#campo-categoria').value);
      formData.append('descricao', document.querySelector('#campo-descricao').value.trim());
      formData.append('cidade', document.querySelector('#campo-localizacao').value.trim());

      const response = await fetch('/perfilPrestador', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
        body: formData.toString()
      });

      const data = await response.json();
      if (response.ok && data && data.sucesso) {
        mensagem.textContent = 'Dados do prestador atualizados com sucesso no MySQL! Redirecionando...';
        mensagem.classList.add('mensagem-feedback--sucesso', 'mensagem-feedback--visivel');

        setTimeout(() => {
          window.location.href = 'meu-perfil-prestador.html';
        }, 1500);
      } else {
        mensagem.textContent = data.mensagem || 'Erro ao atualizar dados.';
        mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
      }
    } catch (e) {
      console.error('Erro ao atualizar prestador:', e);
      mensagem.textContent = 'Erro de comunicação ao atualizar perfil.';
      mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
    }
  });
}
