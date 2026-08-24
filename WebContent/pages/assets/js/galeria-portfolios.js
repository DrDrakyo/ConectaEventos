/* ==========================================================================
   TELA: GALERIA DE PORTFÓLIOS (Módulo Prestador)
   JavaScript específico desta tela: lista os itens de PORTFOLIO
   cadastrados pelo prestador autenticado.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', async () => {
  const grade = document.querySelector('#grade-portfolio');
  const vazio = document.querySelector('#estado-vazio');

  try {
    const response = await fetch('/visualizarPortfolio');
    if (!response.ok) {
      if (vazio) vazio.classList.add('estado-vazio--visivel');
      return;
    }

    const data = await response.json();
    const itens = (data && data.sucesso && data.itens) ? data.itens : [];

    if (itens.length === 0) {
      if (vazio) vazio.classList.add('estado-vazio--visivel');
      if (grade) grade.innerHTML = '';
      return;
    }

    if (grade) {
      grade.innerHTML = itens.map((item) => `
        <article class="item-galeria card">
          <div class="item-galeria__imagem">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <circle cx="12" cy="13" r="3.2"/><path d="M4 8h3l1.5-2h7L17 8h3a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V9a1 1 0 0 1 1-1z"/>
            </svg>
          </div>
          <div class="item-galeria__corpo">
            <span class="item-galeria__categoria">Portfólio</span>
            <h3 class="item-galeria__titulo">${item.titulo}</h3>
            <p style="color: var(--cor-texto-suave, #6b7280); font-size: 0.875rem; margin-bottom: 0.5rem;">${item.descricao || ''}</p>
            <div class="item-galeria__acoes">
              <a href="editar-portfolio.html?id=${item.id_portfolio}" class="botao botao--secundario botao--pequeno botao--bloco">Editar</a>
            </div>
          </div>
        </article>
      `).join('');
    }
  } catch (e) {
    console.error('Erro ao carregar portfólio:', e);
    if (vazio) vazio.classList.add('estado-vazio--visivel');
  }
});
