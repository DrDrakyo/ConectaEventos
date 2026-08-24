/* ==========================================================================
   TELA: EDITAR PORTFÓLIO (Módulo Prestador)
   JavaScript específico desta tela: pré-preenche o formulário com o item
   selecionado (registro de PORTFOLIO) e valida antes de salvar. O select
   de categoria apenas lê o catálogo de SERVICO, sem permitir criar/editar.
   ========================================================================== */

const CATEGORIAS_SERVICO = [
  'Fotografia e Filmagem', 'Música e DJ', 'Buffet e Gastronomia', 'Decoração e Cenografia',
  'Espaço e Locação', 'Cerimonial e Assessoria', 'Animação e Recreação', 'Segurança e Apoio'
];

document.addEventListener('DOMContentLoaded', () => {
  preencherCategorias();
  carregarItemEPreencherFormulario();
  inicializarUploads();
  inicializarEnvio();
});

function preencherCategorias() {
  const select = document.querySelector('#campo-servico');
  if (!select) return;
  select.innerHTML = CATEGORIAS_SERVICO.map((categoria) => `<option value="${categoria}">${categoria}</option>`).join('');
}

async function carregarItemEPreencherFormulario() {
  const urlParams = new URLSearchParams(window.location.search);
  const idPortfolio = urlParams.get('id');
  if (!idPortfolio) return;

  try {
    const response = await fetch(`/visualizarPortfolio?id=${idPortfolio}`);
    if (response.ok) {
      const data = await response.json();
      if (data && data.sucesso && data.item) {
        const item = data.item;
        if (item.titulo) document.querySelector('#campo-titulo').value = item.titulo;
        if (item.descricao) document.querySelector('#campo-descricao').value = item.descricao;
      }
    }
  } catch (e) {
    console.warn('Erro ao carregar item de portfólio:', e);
  }
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

    const urlParams = new URLSearchParams(window.location.search);
    const idPortfolio = urlParams.get('id');

    const camposObrigatorios = ['titulo', 'descricao'];
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

    try {
      const titulo = document.querySelector('#campo-titulo').value.trim();
      const descricao = document.querySelector('#campo-descricao').value.trim();

      const formData = new URLSearchParams();
      formData.append('acao', 'atualizar');
      if (idPortfolio) formData.append('id', idPortfolio);
      formData.append('titulo', titulo);
      formData.append('descricao', descricao);
      formData.append('imagem_url', 'https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=800&q=80');

      const response = await fetch('/visualizarPortfolio', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
        body: formData.toString()
      });

      const data = await response.json();

      if (response.ok && data && data.sucesso) {
        mensagem.textContent = 'Item de portfólio atualizado com sucesso no MySQL! Redirecionando...';
        mensagem.classList.add('mensagem-feedback--sucesso', 'mensagem-feedback--visivel');

        setTimeout(() => {
          window.location.href = 'galeria-portfolios.html';
        }, 1500);
      } else {
        mensagem.textContent = data.mensagem || 'Erro ao atualizar item de portfólio no banco de dados.';
        mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
      }
    } catch (e) {
      console.error('Erro ao editar portfólio:', e);
      mensagem.textContent = 'Erro de comunicação ao atualizar portfólio.';
      mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
    }
  });
}
