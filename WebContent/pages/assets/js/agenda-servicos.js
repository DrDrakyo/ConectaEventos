/* ==========================================================================
   TELA: AGENDA DE SERVIÇOS (Módulo Prestador)
   JavaScript específico desta tela: organiza os compromissos do prestador
   com base nas datas dos itens contratados (ITEM_CONTRA.data_inicio_prevista),
   agrupados por data e ordenados cronologicamente.
   ========================================================================== */

const MESES = ['jan', 'fev', 'mar', 'abr', 'mai', 'jun', 'jul', 'ago', 'set', 'out', 'nov', 'dez'];

/* Dados de exemplo — simulam ITEM_CONTRA + CONTRATACAO + CONTRATANTE
   vinculados a este prestador. */
const COMPROMISSOS = [
  { id_contratacao: 1041, item_contratacao: 'Cobertura fotográfica completa', contratante: 'Maria Costa', data_inicio_prevista: '2026-08-15', situacao_item: 'iniciado' },
  { id_contratacao: 1030, item_contratacao: 'Ensaio pré-wedding', contratante: 'João Almeida', data_inicio_prevista: '2026-08-15', situacao_item: 'contratado' },
  { id_contratacao: 1050, item_contratacao: 'Formatura Medicina UFBA', contratante: 'Patrícia Reis', data_inicio_prevista: '2026-09-02', situacao_item: 'contratado' },
  { id_contratacao: 1019, item_contratacao: 'Cobertura de aniversário', contratante: 'Fernanda Lopes', data_inicio_prevista: '2026-07-10', situacao_item: 'concluido' },
];

const ROTULO_STATUS = {
  disponivel: 'Disponível', negociacao: 'Em negociação', contratado: 'Contratado',
  iniciado: 'Serviço iniciado', concluido: 'Serviço concluído', inativo: 'Inativo',
};

document.addEventListener('DOMContentLoaded', () => {
  renderizarAgenda();
});

function renderizarAgenda() {
  const container = document.querySelector('#lista-agenda');
  const vazio = document.querySelector('#estado-vazio');

  if (COMPROMISSOS.length === 0) {
    vazio.classList.add('estado-vazio--visivel');
    return;
  }

  const ordenados = [...COMPROMISSOS].sort((a, b) => a.data_inicio_prevista.localeCompare(b.data_inicio_prevista));

  const agrupados = ordenados.reduce((grupos, compromisso) => {
    const chave = compromisso.data_inicio_prevista;
    if (!grupos[chave]) grupos[chave] = [];
    grupos[chave].push(compromisso);
    return grupos;
  }, {});

  container.innerHTML = Object.entries(agrupados).map(([data, compromissos]) => `
    <div class="grupo-data">
      <h3 class="grupo-data__titulo">${formatarDataExtensa(data)}</h3>
      ${compromissos.map((c) => `
        <div class="compromisso card">
          <div class="compromisso__data">
            <span class="compromisso__data-dia">${data.split('-')[2]}</span>
            <span class="compromisso__data-mes">${MESES[Number(data.split('-')[1]) - 1]}</span>
          </div>
          <div class="compromisso__info">
            <div class="compromisso__titulo">${c.item_contratacao}</div>
            <div class="compromisso__detalhe">${c.contratante} · <span class="status status--${c.situacao_item}" style="margin-left:4px;">${ROTULO_STATUS[c.situacao_item]}</span></div>
          </div>
          <a class="compromisso__link" href="detalhes-contratacao.html?id=${c.id_contratacao}">Ver detalhes →</a>
        </div>
      `).join('')}
    </div>
  `).join('');
}

function formatarDataExtensa(dataIso) {
  const [ano, mes, dia] = dataIso.split('-');
  return `${dia}/${mes}/${ano}`;
}
