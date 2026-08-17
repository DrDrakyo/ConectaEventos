/* ==========================================================================
   TELA: RELATÓRIOS E ESTATÍSTICAS (Módulo Administrador)
   JavaScript específico desta tela: renderiza os indicadores estatísticos
   previstos no escopo — categorias mais procuradas (agregação de
   PORTFOLIO/ITEM_CONTRA por SERVICO.tipo), prestadores mais bem avaliados
   (média de CONTRATACAO.ava_nota), volume de contratações
   (CONTRATACAO.data_contratacao) e tendências (valor_total ao longo do
   tempo). Gráficos em SVG/HTML puro, sem bibliotecas externas.
   ========================================================================== */

const CATEGORIAS_MAIS_PROCURADAS = [
  { categoria: 'Fotografia', quantidade: 186 },
  { categoria: 'Buffet', quantidade: 154 },
  { categoria: 'Decoração', quantidade: 141 },
  { categoria: 'DJ', quantidade: 98 },
  { categoria: 'Cerimonial', quantidade: 76 },
  { categoria: 'Sonorização', quantidade: 52 },
];

const PRESTADORES_MELHOR_AVALIADOS = [
  { nome_fantasia: 'DJ Marcos Ferreira', nota_media: 5.0, avaliacoes: 34 },
  { nome_fantasia: 'Studio Lente Viva', nota_media: 4.9, avaliacoes: 51 },
  { nome_fantasia: 'Casa Encantada Decorações', nota_media: 4.8, avaliacoes: 28 },
  { nome_fantasia: 'Bartenders Blend', nota_media: 4.8, avaliacoes: 19 },
  { nome_fantasia: 'Prisma Filmes', nota_media: 4.7, avaliacoes: 22 },
];

/* Volume de contratações por mês (simula agregação de CONTRATACAO.data_contratacao) */
const VOLUME_MENSAL = [
  { mes: 'Fev', quantidade: 38 }, { mes: 'Mar', quantidade: 45 }, { mes: 'Abr', quantidade: 41 },
  { mes: 'Mai', quantidade: 57 }, { mes: 'Jun', quantidade: 63 }, { mes: 'Jul', quantidade: 71 },
];

document.addEventListener('DOMContentLoaded', () => {
  renderizarDestaques();
  renderizarGraficoBarras();
  renderizarGraficoLinha();
  renderizarTabelaPrestadores();
});

function renderizarDestaques() {
  const categoriaTop = CATEGORIAS_MAIS_PROCURADAS[0];
  const prestadorTop = PRESTADORES_MELHOR_AVALIADOS[0];
  const totalContratacoes = VOLUME_MENSAL.reduce((soma, mes) => soma + mes.quantidade, 0);
  const crescimento = Math.round(((VOLUME_MENSAL.at(-1).quantidade - VOLUME_MENSAL[0].quantidade) / VOLUME_MENSAL[0].quantidade) * 100);

  document.querySelector('#destaque-categoria').textContent = categoriaTop.categoria;
  document.querySelector('#destaque-categoria-extra').textContent = `${categoriaTop.quantidade} contratações`;

  document.querySelector('#destaque-prestador').textContent = prestadorTop.nome_fantasia;
  document.querySelector('#destaque-prestador-extra').textContent = `★ ${prestadorTop.nota_media.toFixed(1)} (${prestadorTop.avaliacoes} avaliações)`;

  document.querySelector('#destaque-volume').textContent = totalContratacoes;
  document.querySelector('#destaque-volume-extra').textContent = 'no período selecionado';

  document.querySelector('#destaque-tendencia').textContent = `${crescimento >= 0 ? '+' : ''}${crescimento}%`;
  document.querySelector('#destaque-tendencia-extra').textContent = 'comparado ao início do período';
}

function renderizarGraficoBarras() {
  const container = document.querySelector('#grafico-categorias');
  const maiorValor = Math.max(...CATEGORIAS_MAIS_PROCURADAS.map((c) => c.quantidade));

  container.innerHTML = CATEGORIAS_MAIS_PROCURADAS.map((c) => `
    <div>
      <div class="linha-barra__rotulo"><span>${c.categoria}</span><span>${c.quantidade}</span></div>
      <div class="linha-barra__fundo"><div class="linha-barra__preenchimento" style="width:${(c.quantidade / maiorValor) * 100}%"></div></div>
    </div>
  `).join('');
}

function renderizarGraficoLinha() {
  const largura = 560;
  const altura = 200;
  const margem = 30;
  const maiorValor = Math.max(...VOLUME_MENSAL.map((m) => m.quantidade));
  const passoX = (largura - margem * 2) / (VOLUME_MENSAL.length - 1);

  const pontos = VOLUME_MENSAL.map((mes, indice) => {
    const x = margem + indice * passoX;
    const y = altura - margem - (mes.quantidade / maiorValor) * (altura - margem * 2);
    return { x, y, mes: mes.mes, quantidade: mes.quantidade };
  });

  const linhaPath = pontos.map((p, i) => `${i === 0 ? 'M' : 'L'}${p.x},${p.y}`).join(' ');

  const circulos = pontos.map((p) => `<circle cx="${p.x}" cy="${p.y}" r="4" fill="var(--azul-600)"></circle>`).join('');
  const rotulos = pontos.map((p) => `<text x="${p.x}" y="${altura - 8}" font-size="11" fill="var(--cinza-500)" text-anchor="middle">${p.mes}</text>`).join('');

  document.querySelector('#grafico-volume').innerHTML = `
    <svg viewBox="0 0 ${largura} ${altura}" xmlns="http://www.w3.org/2000/svg">
      <path d="${linhaPath}" fill="none" stroke="var(--azul-600)" stroke-width="2.5" />
      ${circulos}
      ${rotulos}
    </svg>
  `;
}

function renderizarTabelaPrestadores() {
  const corpo = document.querySelector('#corpo-tabela-prestadores');
  corpo.innerHTML = PRESTADORES_MELHOR_AVALIADOS.map((p, indice) => `
    <tr>
      <td>${indice + 1}</td>
      <td>${p.nome_fantasia}</td>
      <td>★ ${p.nota_media.toFixed(1)}</td>
      <td>${p.avaliacoes}</td>
    </tr>
  `).join('');
}
