/* ==========================================================================
   TELA: ACOMPANHAR CONTRATAÇÃO (Módulo Contratante)
   JavaScript específico desta tela: renderiza os dados de CONTRATACAO e
   a lista de ITEM_CONTRA vinculados, e habilita "Avaliar Prestador"
   apenas quando a contratação está com situacao = concluído.
   ========================================================================== */

/* Dados de exemplo — simulam um registro de CONTRATACAO + seus ITEM_CONTRA. */
const CONTRATACAO = {
  id_contratacao: 1041,
  prestador: 'Studio Lente Viva',
  data_contratacao: '2026-06-02',
  situacao: 'iniciado', // disponivel | negociacao | contratado | iniciado | concluido | inativo
  valor_total: 1850,
  forma_pagamento: 'Pix',
  local_contratacao: 'Salvador - BA, Espaço Villa Jardim',
  itens: [
    {
      item_contratacao: 'Cobertura fotográfica completa',
      quantidade: 1,
      valor_unitario: 1200,
      situacao_item: 'iniciado',
      data_inicio_prevista: '2026-08-15',
      data_inicio: '2026-08-15',
      data_real: null,
      data_conclusao: null,
    },
    {
      item_contratacao: 'Ensaio pré-wedding',
      quantidade: 1,
      valor_unitario: 650,
      situacao_item: 'concluido',
      data_inicio_prevista: '2026-07-10',
      data_inicio: '2026-07-10',
      data_real: '2026-07-10',
      data_conclusao: '2026-07-10',
    },
  ],
};

const ROTULO_STATUS = {
  disponivel: 'Disponível',
  negociacao: 'Em negociação',
  contratado: 'Contratado',
  iniciado: 'Serviço iniciado',
  concluido: 'Serviço concluído',
  inativo: 'Inativo',
};

document.addEventListener('DOMContentLoaded', () => {
  renderizarDadosGerais();
  renderizarItens();
  configurarBotaoAvaliar();
});

function renderizarDadosGerais() {
  document.querySelector('#valor-prestador').textContent = CONTRATACAO.prestador;
  document.querySelector('#valor-local').textContent = CONTRATACAO.local_contratacao;
  document.querySelector('#valor-forma-pagamento').textContent = CONTRATACAO.forma_pagamento;
  document.querySelector('#valor-data-contratacao').textContent = formatarData(CONTRATACAO.data_contratacao);
  document.querySelector('#valor-total').textContent = `R$ ${CONTRATACAO.valor_total.toLocaleString('pt-BR')}`;

  const statusEl = document.querySelector('#valor-situacao');
  statusEl.textContent = ROTULO_STATUS[CONTRATACAO.situacao];
  statusEl.className = `status status--${CONTRATACAO.situacao}`;
}

function renderizarItens() {
  const lista = document.querySelector('#lista-itens-acompanhamento');

  lista.innerHTML = CONTRATACAO.itens.map((item) => `
    <div class="item-acompanhamento">
      <div class="item-acompanhamento__cabecalho">
        <span class="item-acompanhamento__titulo">${item.item_contratacao}</span>
        <span class="status status--${item.situacao_item}">${ROTULO_STATUS[item.situacao_item]}</span>
      </div>
      <div class="item-acompanhamento__quantidade-valor">
        Quantidade: ${item.quantidade} · Valor unitário: R$ ${item.valor_unitario.toLocaleString('pt-BR')}
      </div>
      <div class="item-acompanhamento__datas">
        <div><div class="item-acompanhamento__data-rotulo">Início previsto</div><div class="item-acompanhamento__data-valor">${formatarData(item.data_inicio_prevista)}</div></div>
        <div><div class="item-acompanhamento__data-rotulo">Início</div><div class="item-acompanhamento__data-valor">${formatarData(item.data_inicio)}</div></div>
        <div><div class="item-acompanhamento__data-rotulo">Conclusão real</div><div class="item-acompanhamento__data-valor">${formatarData(item.data_real)}</div></div>
        <div><div class="item-acompanhamento__data-rotulo">Concluído em</div><div class="item-acompanhamento__data-valor">${formatarData(item.data_conclusao)}</div></div>
      </div>
    </div>
  `).join('');
}

function configurarBotaoAvaliar() {
  const bloco = document.querySelector('#bloco-avaliacao');
  const botao = document.querySelector('#botao-avaliar');
  const mensagemStatus = document.querySelector('#mensagem-status-avaliacao');

  if (CONTRATACAO.situacao === 'concluido') {
    botao.removeAttribute('aria-disabled');
    botao.classList.remove('botao--desabilitado');
    mensagemStatus.textContent = 'O serviço foi concluído. Conte como foi sua experiência.';
  } else {
    botao.setAttribute('aria-disabled', 'true');
    botao.classList.add('botao--desabilitado');
    botao.addEventListener('click', (evento) => evento.preventDefault());
    mensagemStatus.textContent = 'A avaliação ficará disponível quando o serviço for concluído.';
  }
}

function formatarData(dataIso) {
  if (!dataIso) return '—';
  const [ano, mes, dia] = dataIso.split('-');
  return `${dia}/${mes}/${ano}`;
}
