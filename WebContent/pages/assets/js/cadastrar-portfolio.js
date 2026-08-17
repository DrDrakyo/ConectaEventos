/* ==========================================================================
   TELA: CADASTRAR PORTFÓLIO (Módulo Prestador)
   JavaScript específico desta tela: preenche o select de categoria a
   partir do catálogo de SERVICO (somente leitura — o Prestador nunca cria
   ou edita SERVICO, apenas seleciona) e valida antes de enviar um novo
   registro em PORTFOLIO.
   ========================================================================== */

const CATEGORIAS_SERVICO = [
  'Fotografia', 'Filmagem', 'Decoração', 'Buffet', 'Cerimonial', 'Sonorização',
  'Iluminação', 'Segurança', 'Recepção', 'Produção de eventos', 'Bartender',
  'DJ', 'Banda', 'Mestre de cerimônias', 'Locução', 'Assessoria', 'Atrações artísticas',
];

document.addEventListener('DOMContentLoaded', () => {
  preencherCategorias();
  inicializarUploads();
  inicializarEnvio();
});

function preencherCategorias() {
  const select = document.querySelector('#campo-servico');
  select.innerHTML = '<option value="">Selecione</option>' +
    CATEGORIAS_SERVICO.map((categoria) => `<option value="${categoria}">${categoria}</option>`).join('');
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

    // Em produção: gravar um novo registro em PORTFOLIO com titulo,
    // imagem1/2/3, descricao, certificacao, experiencia, valor,
    // id_servico (selecionado) e id_prestador do usuário autenticado.
    // data_publicacao é preenchida automaticamente pelo sistema.
    mensagem.textContent = 'Item de portfólio cadastrado com sucesso! Redirecionando para a galeria...';
    mensagem.classList.add('mensagem-feedback--sucesso', 'mensagem-feedback--visivel');
    formulario.reset();
    document.querySelectorAll('.campo-upload').forEach((campo) => {
      campo.classList.remove('tem-arquivo');
      campo.querySelector('span').textContent = campo.dataset.textoPadrao;
    });

    setTimeout(() => {
      window.location.href = 'galeria-portfolios.html';
    }, 1800);
  });
}
