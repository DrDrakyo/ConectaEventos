/* ==========================================================================
   TELA: DASHBOARD ADMINISTRATIVO (Módulo Administrador)
   JavaScript específico desta tela: exibe indicadores gerais da
   plataforma (PRESTADOR, CONTRATANTE, CONTRATACAO, ITEM_CONTRA, PORTFOLIO,
   SERVICO) — categorias mais procuradas e prestadores mais bem avaliados.
   ========================================================================== */

const INDICADORES = {
  prestadores_ativos: 128,
  contratantes_ativos: 342,
  contratacoes_andamento: 47,
  contratacoes_concluidas_mes: 63,
};

/* Ranking simulado a partir de PORTFOLIO/ITEM_CONTRA agrupado por SERVICO.tipo */
const CATEGORIAS_MAIS_PROCURADAS = [
  { categoria: 'Fotografia', quantidade: 186 },
  { categoria: 'Buffet', quantidade: 154 },
  { categoria: 'Decoração', quantidade: 141 },
  { categoria: 'DJ', quantidade: 98 },
  { categoria: 'Cerimonial', quantidade: 76 },
];

/* Ranking simulado a partir da média de CONTRATACAO.ava_nota por prestador */
const PRESTADORES_MELHOR_AVALIADOS = [
  { nome_fantasia: 'DJ Marcos Ferreira', nota_media: 5.0 },
  { nome_fantasia: 'Studio Lente Viva', nota_media: 4.9 },
  { nome_fantasia: 'Casa Encantada Decorações', nota_media: 4.8 },
  { nome_fantasia: 'Bartenders Blend', nota_media: 4.8 },
  { nome_fantasia: 'Prisma Filmes', nota_media: 4.7 },
];

document.addEventListener('DOMContentLoaded', () => {
  renderizarIndicadores();
  renderizarRanking('#ranking-categorias', CATEGORIAS_MAIS_PROCURADAS, (item) => item.categoria, (item) => `${item.quantidade} contratações`);
  renderizarRanking('#ranking-prestadores', PRESTADORES_MELHOR_AVALIADOS, (item) => item.nome_fantasia, (item) => `★ ${item.nota_media.toFixed(1)}`);
});

function renderizarIndicadores() {
  document.querySelector('#numero-prestadores').textContent = INDICADORES.prestadores_ativos;
  document.querySelector('#numero-contratantes').textContent = INDICADORES.contratantes_ativos;
  document.querySelector('#numero-andamento').textContent = INDICADORES.contratacoes_andamento;
  document.querySelector('#numero-concluidas-mes').textContent = INDICADORES.contratacoes_concluidas_mes;
}

function renderizarRanking(seletor, lista, obterNome, obterValor) {
  const container = document.querySelector(seletor);
  container.innerHTML = lista.map((item, indice) => `
    <div class="linha-ranking">
      <span class="linha-ranking__posicao">${indice + 1}</span>
      <span class="linha-ranking__nome">${obterNome(item)}</span>
      <span class="linha-ranking__valor">${obterValor(item)}</span>
    </div>
  `).join('');
}
