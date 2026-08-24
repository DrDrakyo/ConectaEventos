/* ==========================================================================
   TELA: CADASTRAR PORTFÓLIO (Módulo Prestador)
   JavaScript específico desta tela: preenche o select de categoria a
   partir do catálogo de SERVICO (somente leitura — o Prestador nunca cria
   ou edita SERVICO, apenas seleciona) e valida antes de enviar um novo
   registro em PORTFOLIO.
   ========================================================================== */

const CATEGORIAS_SERVICO = [
  'Fotografia e Filmagem', 'Música e DJ', 'Buffet e Gastronomia', 'Decoração e Cenografia',
  'Espaço e Locação', 'Cerimonial e Assessoria', 'Animação e Recreação', 'Segurança e Apoio'
];

document.addEventListener('DOMContentLoaded', () => {
  preencherCategorias();
  inicializarUploads();
  inicializarEnvio();
});

function preencherCategorias() {
  const select = document.querySelector('#campo-servico');
  if (!select) return;
  select.innerHTML = '<option value="">Selecione</option>' +
    CATEGORIAS_SERVICO.map((categoria) => `<option value="${categoria}">${categoria}</option>`).join('');
}

function inicializarUploads() {
  document.querySelectorAll('.campo-upload').forEach((campo) => {
    const input = campo.querySelector('input[type="file"]');
    if (!input) return;
    campo.addEventListener('click', () => input.click());
    input.addEventListener('change', () => {
      const legenda = campo.querySelector('span');
      if (input.files.length > 0) {
        legenda.textContent = input.files[0].name;
        campo.classList.add('tem-arquivo');
      } else {
        legenda.textContent = campo.dataset.textoPadrao || 'Escolher arquivo';
        campo.classList.remove('tem-arquivo');
      }
    });
  });
}

function inicializarEnvio() {
  const formulario = document.querySelector('#formulario-portfolio');
  const mensagem = document.querySelector('#mensagem-feedback');
  if (!formulario) return;

  formulario.addEventListener('submit', async (evento) => {
    evento.preventDefault();

    const camposObrigatorios = ['titulo', 'descricao', 'servico'];
    let valido = true;

    camposObrigatorios.forEach((nome) => {
      const campo = document.querySelector(`#campo-${nome}`);
      if (!campo) return;
      const container = campo.closest('.campo');
      if (!campo.value.trim()) {
        if (container) container.classList.add('campo--invalido');
        valido = false;
      } else {
        if (container) container.classList.remove('campo--invalido');
      }
    });

    mensagem.classList.remove('mensagem-feedback--sucesso', 'mensagem-feedback--erro');

    if (!valido) {
      mensagem.textContent = 'Preencha todos os campos obrigatórios.';
      mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
      return;
    }

    mensagem.textContent = 'Cadastrando item de portfólio no banco de dados...';
    mensagem.classList.add('mensagem-feedback--visivel');

    try {
      const titulo = document.querySelector('#campo-titulo').value.trim();
      const descricao = document.querySelector('#campo-descricao').value.trim();
      const servico = document.querySelector('#campo-servico').value;

      const formData = new URLSearchParams();
      formData.append('titulo', titulo);
      formData.append('descricao', `Categoria: ${servico}. ${descricao}`);
      formData.append('imagem_url', 'https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=800&q=80');

      const response = await fetch('/visualizarPortfolio', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
        body: formData.toString()
      });

      const data = await response.json();

      if (response.ok && data && data.sucesso) {
        mensagem.textContent = 'Item de portfólio cadastrado com sucesso no MySQL! Redirecionando...';
        mensagem.classList.add('mensagem-feedback--sucesso', 'mensagem-feedback--visivel');
        formulario.reset();

        setTimeout(() => {
          window.location.href = 'galeria-portfolios.html';
        }, 1500);
      } else {
        mensagem.textContent = data.mensagem || 'Erro ao cadastrar item de portfólio no banco de dados.';
        mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
      }
    } catch (e) {
      console.error('Erro ao cadastrar portfólio:', e);
      mensagem.textContent = 'Erro de comunicação ao cadastrar portfólio.';
      mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
    }
  });
}
