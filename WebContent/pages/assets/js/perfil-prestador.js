/* ==========================================================================
   TELA: PERFIL PÚBLICO DO PRESTADOR (Módulo Visitante)
   JavaScript específico desta tela: renderiza os itens de portfólio
   (tabela PORTFOLIO) e as avaliações recebidas (campos ava_* da tabela
   CONTRATACAO). Menu mobile tratado em common.js.
   ========================================================================== */

const ITENS_PORTFOLIO = [
  { titulo: 'Casamento Ana & Rafael', valor: 1200 },
  { titulo: 'Aniversário 15 anos - Beatriz', valor: 950 },
  { titulo: 'Formatura Direito UFBA', valor: 1500 },
  { titulo: 'Casamento na Praia', valor: 1800 },
];

const AVALIACOES = [
  { autor: 'Contratante verificado', nota: 5, comentario: 'Profissional pontual e muito atencioso, entregou tudo antes do prazo combinado.' },
  { autor: 'Contratante verificado', nota: 5, comentario: 'Superou as expectativas, recomendo para qualquer tipo de evento.' },
  { autor: 'Contratante verificado', nota: 4, comentario: 'Ótimo trabalho, apenas a entrega demorou um pouco mais que o previsto.' },
];

document.addEventListener('DOMContentLoaded', () => {
  renderizarPortfolio();
  renderizarAvaliacoes();
});

function renderizarPortfolio() {
  const galeria = document.querySelector('#galeria-portfolio-grade');
  if (!galeria) return;

  galeria.innerHTML = ITENS_PORTFOLIO.map((item) => `
    <a class="item-portfolio" href="visualizar-portfolio.html">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
        <circle cx="12" cy="13" r="3.2"/><path d="M4 8h3l1.5-2h7L17 8h3a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V9a1 1 0 0 1 1-1z"/>
      </svg>
      <span class="item-portfolio__legenda">${item.titulo}</span>
    </a>
  `).join('');
}

function renderizarAvaliacoes() {
  const lista = document.querySelector('#lista-avaliacoes');
  if (!lista) return;

  lista.innerHTML = AVALIACOES.map((avaliacao) => `
    <div class="avaliacao-card">
      <div class="avaliacao-card__cabecalho">
        <span class="avaliacao-card__autor">${avaliacao.autor}</span>
        <span class="avaliacao">${'★'.repeat(avaliacao.nota)}${'☆'.repeat(5 - avaliacao.nota)}</span>
      </div>
      <p class="avaliacao-card__comentario">${avaliacao.comentario}</p>
    </div>
  `).join('');
}
