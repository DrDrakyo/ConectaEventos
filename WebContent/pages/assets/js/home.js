/* ==========================================================================
   TELA: HOME (Módulo Visitante)
   JavaScript puro, sem dependências externas.
   Responsável por:
   1) Abrir/fechar o menu mobile
   2) Tratar o envio da busca rápida do hero
   3) Renderizar dinamicamente as categorias e os prestadores em destaque
      (dados de exemplo simulando os registros de SERVICO e PRESTADOR)
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  // Observação: o menu mobile é tratado em js/common.js, incluído
  // antes deste arquivo em home.html.
  inicializarBuscaRapida();
  renderizarCategoriasDestaque();
  inicializarCarrosselCategorias();
  renderizarPrestadoresDestaque();
});

/* ---------- 1) Busca rápida do hero ---------- */
function inicializarBuscaRapida() {
  const formulario = document.querySelector('.busca-rapida');
  if (!formulario) return;

  formulario.addEventListener('submit', (evento) => {
    evento.preventDefault();

    const termo = formulario.querySelector('#campo-busca').value.trim();
    const categoria = formulario.querySelector('#campo-categoria').value;

    // Monta os parâmetros de busca que serão usados pela tela
    // "Lista de Prestadores" (próxima tela do módulo Visitante a ser criada).
    const parametros = new URLSearchParams();
    if (termo) parametros.set('busca', termo);
    if (categoria) parametros.set('categoria', categoria);

    window.location.href = `lista-prestadores.html?${parametros.toString()}`;
  });
}

/* ---------- 2) Dados de exemplo (simulam registros do banco) ----------
   Em produção, estes dados viriam de SERVICO (tipo) e de PRESTADOR
   combinado com PORTFOLIO (imagem, valor) e CONTRATACAO (nota média).
   Aqui ficam como constantes apenas para demonstrar a tela already
   populada, sem alterar a modelagem do banco de dados. */

const CATEGORIAS_DESTAQUE = [
  { nome: 'Fotografia', icone: 'camera' },
  { nome: 'Filmagem', icone: 'video' },
  { nome: 'Decoração', icone: 'decoracao' },
  { nome: 'Buffet', icone: 'buffet' },
  { nome: 'Cerimonial', icone: 'cerimonial' },
  { nome: 'Sonorização', icone: 'som' },
  { nome: 'Iluminação', icone: 'iluminacao' },
  { nome: 'Segurança', icone: 'seguranca' },
  { nome: 'Recepção', icone: 'recepcao' },
  { nome: 'Produção de eventos', icone: 'producao' },
  { nome: 'Bartender', icone: 'bartender' },
  { nome: 'DJ', icone: 'dj' },
  { nome: 'Banda', icone: 'banda' },
  { nome: 'Mestre de cerimônias', icone: 'mestre' },
  { nome: 'Locução', icone: 'locucao' },
  { nome: 'Assessoria', icone: 'assessoria' },
  { nome: 'Atrações artísticas', icone: 'atracoes' },
];

const PRESTADORES_DESTAQUE = [
  {
    nome_fantasia: 'Studio Lente Viva',
    categoria: 'Fotografia',
    localizacao: 'Salvador - BA',
    disponibilidade: 'Disponível',
    valor_a_partir: 850,
    nota_media: 4.9,
  },
  {
    nome_fantasia: 'Casa Encantada Decorações',
    categoria: 'Decoração',
    localizacao: 'Salvador - BA',
    disponibilidade: 'Disponível',
    valor_a_partir: 1200,
    nota_media: 4.8,
  },
  {
    nome_fantasia: 'Sabor & Arte Buffet',
    categoria: 'Buffet',
    localizacao: 'Lauro de Freitas - BA',
    disponibilidade: 'Indisponível',
    valor_a_partir: 3500,
    nota_media: 4.7,
  },
  {
    nome_fantasia: 'DJ Marcos Ferreira',
    categoria: 'DJ',
    localizacao: 'Salvador - BA',
    disponibilidade: 'Disponível',
    valor_a_partir: 900,
    nota_media: 5.0,
  },
];

/* ---------- Ícones em SVG (linha simples, sem biblioteca externa) ---------- */
const ICONES = {
  camera: '<circle cx="12" cy="13" r="3.2"/><path d="M4 8h3l1.5-2h7L17 8h3a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V9a1 1 0 0 1 1-1z"/>',
  video: '<rect x="3" y="6" width="12" height="12" rx="2"/><path d="M15 10l6-3v10l-6-3z"/>',
  decoracao: '<path d="M12 3v6"/><circle cx="12" cy="13" r="4"/><path d="M9 21h6l-1-4H10z"/>',
  buffet: '<path d="M6 3v8a3 3 0 0 0 6 0V3"/><path d="M9 11v10"/><path d="M17 3c-1.5 0-3 1.5-3 4s1.5 4 3 4 1-2 1-4-1-4-1-4z"/><path d="M17 11v10"/>',
  cerimonial: '<path d="M12 3l2.5 5 5.5.8-4 3.9.9 5.5L12 15.9 7.1 18.2l.9-5.5-4-3.9 5.5-.8z"/>',
  som: '<rect x="7" y="3" width="10" height="18" rx="5"/><circle cx="12" cy="8" r="1.5"/><circle cx="12" cy="15" r="1.5"/>',
  iluminacao: '<circle cx="12" cy="10" r="5"/><path d="M9.5 21h5M10 18h4"/><path d="M12 2v1"/>',
  seguranca: '<path d="M12 3l7 3v6c0 4.5-3 7.5-7 9-4-1.5-7-4.5-7-9V6z"/>',
  recepcao: '<circle cx="9" cy="8" r="3"/><path d="M3 20c0-3 3-5 6-5s6 2 6 5"/><circle cx="18" cy="9" r="2.3"/><path d="M15.5 20c.3-2 2-3.5 4-3.7"/>',
  producao: '<rect x="5" y="4" width="14" height="17" rx="2"/><path d="M9 2v4M15 2v4M5 10h14"/>',
  bartender: '<path d="M5 4h14l-7 9z"/><path d="M12 13v7M9 20h6"/>',
  dj: '<circle cx="12" cy="12" r="8"/><circle cx="12" cy="12" r="2"/><path d="M2 12h4M18 12h4"/>',
  banda: '<circle cx="7" cy="17" r="3"/><path d="M10 17V4l8-2v13"/><circle cx="18" cy="15" r="3"/>',
  mestre: '<rect x="9" y="3" width="6" height="11" rx="3"/><path d="M6 11a6 6 0 0 0 12 0M12 17v4M9 21h6"/>',
  locucao: '<path d="M4 10v4h4l5 4V6l-5 4H4z"/><path d="M17 9a4 4 0 0 1 0 6"/>',
  assessoria: '<rect x="4" y="7" width="16" height="12" rx="2"/><path d="M9 7V5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2"/>',
  atracoes: '<path d="M12 2.5l2.9 6 6.6.9-4.8 4.6 1.1 6.5L12 17.6l-5.8 3-.9-6.5-4.8-4.6 6.6-.9z"/>',
  pin: '<path d="M12 21s7-6.5 7-11.5A7 7 0 0 0 5 9.5C5 14.5 12 21 12 21z"/><circle cx="12" cy="9.5" r="2.3"/>',
  estrela: '<path d="M12 2.5l2.9 6 6.6.9-4.8 4.6 1.1 6.5L12 17.6l-5.8 3-.9-6.5-4.8-4.6 6.6-.9z"/>',
  cadeado: '<rect x="5" y="10" width="14" height="10" rx="2"/><path d="M8 10V7a4 4 0 0 1 8 0v3"/>',
};

function criarIconeSvg(nomeIcone, extraClasse = '') {
  const conteudo = ICONES[nomeIcone] || ICONES.estrela;
  return `<svg class="${extraClasse}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${conteudo}</svg>`;
}

/* ---------- Renderização das categorias em destaque ---------- */
function renderizarCategoriasDestaque() {
  const grade = document.querySelector('#grade-categorias');
  if (!grade) return;

  grade.innerHTML = CATEGORIAS_DESTAQUE.map((categoria) => `
    <a class="categoria-card" href="lista-prestadores.html?categoria=${encodeURIComponent(categoria.nome)}">
      <span class="categoria-card__icone">${criarIconeSvg(categoria.icone)}</span>
      <span class="categoria-card__nome">${categoria.nome}</span>
    </a>
  `).join('');
}

/* ---------- Carrossel de categorias ---------- */
function inicializarCarrosselCategorias() {
  const grade = document.querySelector('#grade-categorias');
  const botaoAnterior = document.querySelector('.carrossel-categorias__botao--anterior');
  const botaoProximo = document.querySelector('.carrossel-categorias__botao--proximo');

  if (!grade || !botaoAnterior || !botaoProximo) return;

  const distanciaScroll = 320;

  botaoAnterior.addEventListener('click', () => {
    grade.scrollBy({ left: -distanciaScroll, behavior: 'smooth' });
  });

  botaoProximo.addEventListener('click', () => {
    grade.scrollBy({ left: distanciaScroll, behavior: 'smooth' });
  });

  // Suporte a arrastar com o mouse (drag to scroll)
  let isDown = false;
  let startX;
  let scrollLeft;

  grade.addEventListener('mousedown', (e) => {
    isDown = true;
    startX = e.pageX - grade.offsetLeft;
    scrollLeft = grade.scrollLeft;
  });

  grade.addEventListener('mouseleave', () => {
    isDown = false;
  });

  grade.addEventListener('mouseup', () => {
    isDown = false;
  });

  grade.addEventListener('mousemove', (e) => {
    if (!isDown) return;
    e.preventDefault();
    const x = e.pageX - grade.offsetLeft;
    const walk = (x - startX) * 2;
    grade.scrollLeft = scrollLeft - walk;
  });

  // Atualiza visibilidade dos botões conforme posição do scroll
  function atualizarBotoes() {
    const noInicio = grade.scrollLeft <= 10;
    const noFim = grade.scrollLeft + grade.clientWidth >= grade.scrollWidth - 10;

    botaoAnterior.classList.toggle('carrossel-categorias__botao--oculto', noInicio);
    botaoProximo.classList.toggle('carrossel-categorias__botao--oculto', noFim);
  }

  grade.addEventListener('scroll', atualizarBotoes);
  window.addEventListener('resize', atualizarBotoes);

  // Verificação inicial
  atualizarBotoes();
}

/* ---------- Renderização dos prestadores em destaque ---------- */
function renderizarPrestadoresDestaque() {
  const grade = document.querySelector('#grade-prestadores');
  if (!grade) return;

  grade.innerHTML = PRESTADORES_DESTAQUE.map((prestador) => `
    <article class="prestador-card card">
      <div class="prestador-card__imagem">
        ${criarIconeSvg('camera')}
        <span class="prestador-card__disponibilidade badge ${prestador.disponibilidade === 'Disponível' ? 'badge--azul' : 'badge--roxo'}">
          ${prestador.disponibilidade}
        </span>
      </div>
      <div class="prestador-card__corpo">
        <span class="prestador-card__categoria">${prestador.categoria}</span>
        <h3 class="prestador-card__nome">${prestador.nome_fantasia}</h3>
        <span class="prestador-card__local">
          ${criarIconeSvg('pin')}
          ${prestador.localizacao}
        </span>
        <div class="prestador-card__rodape">
          <span class="prestador-card__preco">a partir de <strong>R$ ${prestador.valor_a_partir.toLocaleString('pt-BR')}</strong></span>
          <span class="avaliacao">${criarIconeSvg('estrela')} ${prestador.nota_media.toFixed(1)}</span>
        </div>
      </div>
    </article>
  `).join('');
}
