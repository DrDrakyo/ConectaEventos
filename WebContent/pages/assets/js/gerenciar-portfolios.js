/* ==========================================================================
   TELA: GERENCIAR PORTFÓLIOS (Módulo Administrador)
   JavaScript específico desta tela: lista os itens de PORTFOLIO de todos
   os prestadores, com filtro por categoria (id_servico) e por prestador.
   A única ação de moderação disponível é EXCLUIR o registro — a tabela
   PORTFOLIO não possui campo de situação, então não há inativação aqui.
   ========================================================================== */

let ITENS_PORTFOLIO = [
  { id_portfolio: 101, titulo: 'Casamento Ana & Rafael', categoria: 'Fotografia', prestador: 'Studio Lente Viva', valor: 1200 },
  { id_portfolio: 102, titulo: 'Aniversário 15 anos - Beatriz', categoria: 'Fotografia', prestador: 'Studio Lente Viva', valor: 950 },
  { id_portfolio: 201, titulo: 'Decoração Boho Chic', categoria: 'Decoração', prestador: 'Casa Encantada Decorações', valor: 1800 },
  { id_portfolio: 301, titulo: 'Buffet Completo 150 Convidados', categoria: 'Buffet', prestador: 'Sabor & Arte Buffet', valor: 7200 },
  { id_portfolio: 401, titulo: 'Set Eletrônico 6 horas', categoria: 'DJ', prestador: 'DJ Marcos Ferreira', valor: 900 },
];

document.addEventListener('DOMContentLoaded', () => {
  preencherFiltroPrestador();
  document.querySelector('#filtro-categoria').addEventListener('change', aplicarFiltros);
  document.querySelector('#filtro-prestador').addEventListener('change', aplicarFiltros);
  aplicarFiltros();
});

function preencherFiltroPrestador() {
  const select = document.querySelector('#filtro-prestador');
  const prestadoresUnicos = [...new Set(ITENS_PORTFOLIO.map((item) => item.prestador))];
  select.innerHTML = '<option value="">Todos</option>' +
    prestadoresUnicos.map((nome) => `<option value="${nome}">${nome}</option>`).join('');
}

function aplicarFiltros() {
  const categoria = document.querySelector('#filtro-categoria').value;
  const prestador = document.querySelector('#filtro-prestador').value;

  const filtrados = ITENS_PORTFOLIO.filter((item) =>
    (!categoria || item.categoria === categoria) &&
    (!prestador || item.prestador === prestador)
  );

  renderizarGrade(filtrados);
}

function renderizarGrade(lista) {
  const grade = document.querySelector('#grade-portfolio-admin');
  const vazio = document.querySelector('#estado-vazio');

  if (lista.length === 0) {
    grade.innerHTML = '';
    vazio.classList.add('estado-vazio--visivel');
    return;
  }

  vazio.classList.remove('estado-vazio--visivel');

  grade.innerHTML = lista.map((item) => `
    <article class="item-galeria-admin card" data-id="${item.id_portfolio}">
      <div class="item-galeria-admin__imagem">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" aria-hidden="true"><circle cx="12" cy="13" r="3.2"/><path d="M4 8h3l1.5-2h7L17 8h3a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V9a1 1 0 0 1 1-1z"/></svg>
      </div>
      <div class="item-galeria-admin__corpo">
        <span class="item-galeria-admin__categoria">${item.categoria}</span>
        <h3 class="item-galeria-admin__titulo">${item.titulo}</h3>
        <span class="item-galeria-admin__prestador">${item.prestador}</span>
        <span class="item-galeria-admin__valor">Valor: <strong>R$ ${item.valor.toLocaleString('pt-BR')}</strong></span>
        <div class="item-galeria-admin__acoes">
          <button type="button" class="botao botao--perigo botao--pequeno botao--bloco" data-acao="excluir">Excluir</button>
        </div>
      </div>
    </article>
  `).join('');

  grade.querySelectorAll('[data-acao="excluir"]').forEach((botao) => {
    botao.addEventListener('click', (evento) => excluirItem(evento.target.closest('.item-galeria-admin').dataset.id));
  });
}

function excluirItem(idPortfolio) {
  const confirmar = window.confirm('Tem certeza que deseja excluir este item de portfólio? Esta ação não pode ser desfeita.');
  if (!confirmar) return;

  // Em produção: excluir o registro correspondente na tabela PORTFOLIO.
  ITENS_PORTFOLIO = ITENS_PORTFOLIO.filter((item) => String(item.id_portfolio) !== String(idPortfolio));
  preencherFiltroPrestador();
  aplicarFiltros();
}
