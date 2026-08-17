/* ==========================================================================
   TELA: GALERIA DE PORTFÓLIOS (Módulo Prestador)
   JavaScript específico desta tela: lista os itens de PORTFOLIO
   cadastrados pelo prestador autenticado.
   ========================================================================== */

const ITENS_PORTFOLIO = [
  { id_portfolio: 101, titulo: 'Casamento Ana & Rafael', categoria: 'Fotografia', valor: 1200 },
  { id_portfolio: 102, titulo: 'Aniversário 15 anos - Beatriz', categoria: 'Fotografia', valor: 950 },
  { id_portfolio: 103, titulo: 'Formatura Direito UFBA', categoria: 'Fotografia', valor: 1500 },
  { id_portfolio: 104, titulo: 'Casamento na Praia', categoria: 'Fotografia', valor: 1800 },
];

document.addEventListener('DOMContentLoaded', () => {
  renderizarGaleria();
});

function renderizarGaleria() {
  const grade = document.querySelector('#grade-portfolio');
  const vazio = document.querySelector('#estado-vazio');

  if (ITENS_PORTFOLIO.length === 0) {
    vazio.classList.add('estado-vazio--visivel');
    return;
  }

  grade.innerHTML = ITENS_PORTFOLIO.map((item) => `
    <article class="item-galeria card">
      <div class="item-galeria__imagem">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <circle cx="12" cy="13" r="3.2"/><path d="M4 8h3l1.5-2h7L17 8h3a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V9a1 1 0 0 1 1-1z"/>
        </svg>
      </div>
      <div class="item-galeria__corpo">
        <span class="item-galeria__categoria">${item.categoria}</span>
        <h3 class="item-galeria__titulo">${item.titulo}</h3>
        <span class="item-galeria__valor">Valor: <strong>R$ ${item.valor.toLocaleString('pt-BR')}</strong></span>
        <div class="item-galeria__acoes">
          <a href="editar-portfolio.html?id=${item.id_portfolio}" class="botao botao--secundario botao--pequeno botao--bloco">Editar</a>
        </div>
      </div>
    </article>
  `).join('');
}
