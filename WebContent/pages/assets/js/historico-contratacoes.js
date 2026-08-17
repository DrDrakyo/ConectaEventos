/* ==========================================================================
   TELA: HISTÓRICO DE CONTRATAÇÕES (Módulo Contratante)
   JavaScript específico desta tela: lista todas as contratações do
   contratante autenticado (CONTRATACAO + ITEM_CONTRA + PORTFOLIO +
   PRESTADOR) e permite filtrar por situação.
   ========================================================================== */

const HISTORICO_CONTRATACOES = [
  { id_contratacao: 1041, prestador: 'Studio Lente Viva', data_contratacao: '2026-06-02', valor_total: 1850, situacao: 'iniciado' },
  { id_contratacao: 1038, prestador: 'Sabor & Arte Buffet', data_contratacao: '2026-05-20', valor_total: 7200, situacao: 'negociacao' },
  { id_contratacao: 1022, prestador: 'DJ Marcos Ferreira', data_contratacao: '2026-04-11', valor_total: 1200, situacao: 'concluido' },
  { id_contratacao: 1015, prestador: 'Cerimonial Elo Perfeito', data_contratacao: '2026-03-02', valor_total: 1800, situacao: 'concluido' },
  { id_contratacao: 1002, prestador: 'Luz & Cena Iluminação', data_contratacao: '2026-01-18', valor_total: 980, situacao: 'inativo' },
];

const ROTULO_STATUS = {
  disponivel: 'Disponível',
  negociacao: 'Em negociação',
  contratado: 'Contratado',
  iniciado: 'Serviço iniciado',
  concluido: 'Serviço concluído',
  inativo: 'Inativo',
};

document.addEventListener('DOMContentLoaded', () => {
  document.querySelector('#filtro-situacao').addEventListener('change', aplicarFiltro);
  aplicarFiltro();
});

function aplicarFiltro() {
  const situacao = document.querySelector('#filtro-situacao').value;
  const filtrados = situacao
    ? HISTORICO_CONTRATACOES.filter((c) => c.situacao === situacao)
    : HISTORICO_CONTRATACOES;

  renderizarLista(filtrados);
}

function renderizarLista(lista) {
  const container = document.querySelector('#lista-historico');
  const vazio = document.querySelector('#estado-vazio');

  if (lista.length === 0) {
    container.innerHTML = '';
    vazio.classList.add('estado-vazio--visivel');
    return;
  }

  vazio.classList.remove('estado-vazio--visivel');

  container.innerHTML = lista.map((c) => `
    <div class="linha-historico card">
      <span class="linha-historico__info">
        <span class="linha-historico__titulo">${c.prestador}</span>
        <span class="linha-historico__detalhe">Contratação #${c.id_contratacao} · ${formatarData(c.data_contratacao)}</span>
      </span>
      <span class="status status--${c.situacao}">${ROTULO_STATUS[c.situacao]}</span>
      <span class="linha-historico__valor">R$ ${c.valor_total.toLocaleString('pt-BR')}</span>
      <a class="linha-historico__link-detalhe" href="acompanhar-contratacao.html?id=${c.id_contratacao}">Ver detalhes →</a>
    </div>
  `).join('');
}

function formatarData(dataIso) {
  const [ano, mes, dia] = dataIso.split('-');
  return `${dia}/${mes}/${ano}`;
}
