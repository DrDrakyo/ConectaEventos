/* ==========================================================================
   TELA: EDITAR PORTFÓLIO (Módulo Prestador)
   JavaScript específico desta tela: pré-preenche o formulário com o item
   selecionado (registro de PORTFOLIO) e valida antes de salvar. O select
   de categoria apenas lê o catálogo de SERVICO, sem permitir criar/editar.
   ========================================================================== */

const CATEGORIAS_SERVICO = [
  'Fotografia', 'Filmagem', 'Decoração', 'Buffet', 'Cerimonial', 'Sonorização',
  'Iluminação', 'Segurança', 'Recepção', 'Produção de eventos', 'Bartender',
  'DJ', 'Banda', 'Mestre de cerimônias', 'Locução', 'Assessoria', 'Atrações artísticas',
];

/* Dados de exemplo — simulam o item de PORTFOLIO selecionado na Galeria. */
const ITEM_PORTFOLIO = {
  id_portfolio: 101,
  titulo: 'Casamento Ana & Rafael',
  descricao: 'Cobertura completa da cerimônia e da festa, incluindo making of da noiva, making of do noivo, cerimônia e primeiras horas da recepção.',
  id_servico: 'Fotografia',
  valor: 1200,
  certificacao: 'Certificado em Fotografia de Eventos - SENAC',
  experiencia: '8 anos de experiência com fotografia de casamentos',
};

document.addEventListener('DOMContentLoaded', () => {
  preencherCategorias();
  preencherFormulario();
  inicializarUploads();
  inicializarEnvio();
});

function preencherCategorias() {
  const select = document.querySelector('#campo-servico');
  select.innerHTML = CATEGORIAS_SERVICO.map((categoria) => `<option value="${categoria}">${categoria}</option>`).join('');
}

function preencherFormulario() {
  document.querySelector('#campo-titulo').value = ITEM_PORTFOLIO.titulo;
  document.querySelector('#campo-descricao').value = ITEM_PORTFOLIO.descricao;
  document.querySelector('#campo-servico').value = ITEM_PORTFOLIO.id_servico;
  document.querySelector('#campo-valor').value = ITEM_PORTFOLIO.valor;
  document.querySelector('#campo-certificacao').value = ITEM_PORTFOLIO.certificacao;
  document.querySelector('#campo-experiencia').value = ITEM_PORTFOLIO.experiencia;
}

function inicializarUploads() {
  document.querySelectorAll('.campo-upload').forEach((campo) => {
    const input = campo.querySelector('input[type="file"]');
    campo.addEventListener('click', () => input.click());
    input.addEventListener('change', () => {
      const legenda = campo.querySelector('span');
      if (input.files.length > 0) {
        legenda.textContent = input.files[0].name;
        campo.classList.add('tem-arquivo');
      } else {
        legenda.textContent = campo.dataset.textoPadrao;
        campo.classList.remove('tem-arquivo');
      }
    });
  });
}

function inicializarEnvio() {
  const formulario = document.querySelector('#formulario-portfolio');
  const mensagem = document.querySelector('#mensagem-feedback');

  formulario.addEventListener('submit', (evento) => {
    evento.preventDefault();

    const camposObrigatorios = ['titulo', 'descricao', 'servico', 'valor'];
    let valido = true;

    camposObrigatorios.forEach((nome) => {
      const campo = document.querySelector(`#campo-${nome}`);
      const container = campo.closest('.campo');
      if (!campo.value.trim()) {
        container.classList.add('campo--invalido');
        valido = false;
      } else {
        container.classList.remove('campo--invalido');
      }
    });

    mensagem.classList.remove('mensagem-feedback--sucesso', 'mensagem-feedback--erro');

    if (!valido) {
      mensagem.textContent = 'Preencha todos os campos obrigatórios.';
      mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
      return;
    }

    // Em produção: atualizar o registro de PORTFOLIO (id_portfolio) com
    // os novos valores de titulo, imagem1/2/3, descricao, certificacao,
    // experiencia, valor e id_servico.
    mensagem.textContent = 'Item de portfólio atualizado com sucesso! Redirecionando para a galeria...';
    mensagem.classList.add('mensagem-feedback--sucesso', 'mensagem-feedback--visivel');

    setTimeout(() => {
      window.location.href = 'galeria-portfolios.html';
    }, 1800);
  });
}
