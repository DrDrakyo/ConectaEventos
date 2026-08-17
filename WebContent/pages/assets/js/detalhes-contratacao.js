/* ==========================================================================
   TELA: DETALHES DA CONTRATAÇÃO (Módulo Prestador)
   JavaScript específico desta tela:
   - Exibe os dados de CONTRATACAO e, se situacao='negociacao', permite
     Aceitar (-> 'contratado') ou Recusar (-> 'inativo') a solicitação.
   - Por item (ITEM_CONTRA), permite Iniciar Serviço (-> 'iniciado',
     grava data_inicio) e Concluir Serviço (-> 'concluido', grava
     data_conclusao) quando a contratação já está 'contratado' ou além.
   - CONTRATACAO.situacao avança automaticamente para 'iniciado' quando
     o 1º item inicia, e para 'concluido' quando todos os itens concluem.
   ========================================================================== */

const CONTRATACAO = {
  id_contratacao: 1041,
  contratante: 'Maria Costa',
  situacao: 'iniciado', // CONTRATACAO.situacao — controla se Aceitar/Recusar aparecem
  data_contratacao: '2026-06-02',
  valor_total: 1850,
  forma_pagamento: 'Pix',
  local_contratacao: 'Salvador - BA, Espaço Villa Jardim',
  itens: [
    {
      id_item: 5001,
      item_contratacao: 'Cobertura fotográfica completa',
      quantidade: 1,
      valor_unitario: 1200,
      situacao_item: 'iniciado',
      data_inicio_prevista: '2026-08-15',
      data_inicio: '2026-08-15',
      data_conclusao: '',
    },
    {
      id_item: 5002,
      item_contratacao: 'Ensaio pré-wedding',
      quantidade: 1,
      valor_unitario: 650,
      situacao_item: 'concluido',
      data_inicio_prevista: '2026-07-10',
      data_inicio: '2026-07-10',
      data_conclusao: '2026-07-10',
    },
  ],
};

const ROTULO_STATUS_CONTRATACAO = {
  disponivel: 'Disponível', negociacao: 'Em negociação', contratado: 'Contratado',
  iniciado: 'Serviço iniciado', concluido: 'Serviço concluído', inativo: 'Inativo',
};

const ROTULO_STATUS_ITEM = {
  aguardando: 'Aguardando início', iniciado: 'Serviço iniciado', concluido: 'Serviço concluído',
};

document.addEventListener('DOMContentLoaded', () => {
  renderizarDadosGerais();
  renderizarItens();
});

function renderizarDadosGerais() {
  document.querySelector('#valor-contratante').textContent = CONTRATACAO.contratante;
  document.querySelector('#valor-data-contratacao').textContent = formatarData(CONTRATACAO.data_contratacao);
  document.querySelector('#valor-total').textContent = `R$ ${CONTRATACAO.valor_total.toLocaleString('pt-BR')}`;
  document.querySelector('#valor-forma-pagamento').textContent = CONTRATACAO.forma_pagamento;
  document.querySelector('#valor-local').textContent = CONTRATACAO.local_contratacao;

  const badgeSituacao = document.querySelector('#valor-situacao');
  badgeSituacao.textContent = ROTULO_STATUS_CONTRATACAO[CONTRATACAO.situacao];
  badgeSituacao.className = `status status--${CONTRATACAO.situacao}`;

  const acoes = document.querySelector('#acoes-contratacao');
  if (CONTRATACAO.situacao === 'negociacao') {
    acoes.innerHTML = `
      <button type="button" class="botao botao--primario botao--pequeno" id="botao-aceitar-contratacao">Aceitar</button>
      <button type="button" class="botao botao--perigo botao--pequeno" id="botao-recusar-contratacao">Recusar</button>
    `;
    document.querySelector('#botao-aceitar-contratacao').addEventListener('click', () => {
      CONTRATACAO.situacao = 'contratado';
      // Em produção: enviar requisição ao servlet para persistir CONTRATACAO.situacao no banco.
      renderizarDadosGerais();
    });
    document.querySelector('#botao-recusar-contratacao').addEventListener('click', () => {
      CONTRATACAO.situacao = 'inativo';
      // Em produção: enviar requisição ao servlet para persistir CONTRATACAO.situacao no banco.
      renderizarDadosGerais();
    });
  } else {
    acoes.innerHTML = '';
  }
}

function renderizarItens() {
  const lista = document.querySelector('#lista-itens-detalhe');

  // Antes de 'contratado', ainda não faz sentido iniciar/concluir nenhum item.
  const podeAgir = ['contratado', 'iniciado', 'concluido'].includes(CONTRATACAO.situacao);

  lista.innerHTML = CONTRATACAO.itens.map((item) => `
    <div class="item-detalhe" data-id-item="${item.id_item}">
      <div class="item-detalhe__cabecalho">
        <span class="item-detalhe__titulo">${item.item_contratacao}</span>
        <span class="status status--${item.situacao_item}">${ROTULO_STATUS_ITEM[item.situacao_item]}</span>
      </div>
      <div class="item-detalhe__quantidade-valor">Quantidade: ${item.quantidade} · Valor unitário: R$ ${item.valor_unitario.toLocaleString('pt-BR')}</div>

      <div class="item-detalhe__grade">
        <div class="item-detalhe__campo-leitura">
          <span class="rotulo">Início previsto</span>
          <span class="valor">${formatarData(item.data_inicio_prevista)}</span>
        </div>
        <div class="item-detalhe__campo-leitura">
          <span class="rotulo">Data de início</span>
          <span class="valor">${formatarData(item.data_inicio)}</span>
        </div>
        <div class="item-detalhe__campo-leitura">
          <span class="rotulo">Data de conclusão</span>
          <span class="valor">${formatarData(item.data_conclusao)}</span>
        </div>
      </div>

      ${podeAgir && item.situacao_item === 'aguardando' ? `
        <div class="item-detalhe__salvar">
          <button type="button" class="botao botao--secundario botao--pequeno" data-acao="iniciar">Iniciar Serviço</button>
        </div>
      ` : ''}
      ${podeAgir && item.situacao_item === 'iniciado' ? `
        <div class="item-detalhe__salvar">
          <button type="button" class="botao botao--primario botao--pequeno" data-acao="concluir">Concluir Serviço</button>
        </div>
      ` : ''}
    </div>
  `).join('');

  lista.querySelectorAll('[data-acao="iniciar"]').forEach((botao) => {
    botao.addEventListener('click', () => mudarSituacaoItem(botao, 'iniciado'));
  });
  lista.querySelectorAll('[data-acao="concluir"]').forEach((botao) => {
    botao.addEventListener('click', () => mudarSituacaoItem(botao, 'concluido'));
  });
}

function mudarSituacaoItem(botao, novaSituacao) {
  const itemEl = botao.closest('.item-detalhe');
  const idItem = Number(itemEl.dataset.idItem);
  const item = CONTRATACAO.itens.find((i) => i.id_item === idItem);
  if (!item) return;

  const hoje = new Date().toISOString().slice(0, 10);
  item.situacao_item = novaSituacao;
  if (novaSituacao === 'iniciado') item.data_inicio = hoje;
  if (novaSituacao === 'concluido') item.data_conclusao = hoje;

  // Em produção: enviar requisição ao servlet para persistir ITEM_CONTRA
  // (situacao_item + a data correspondente) no banco.
  atualizarSituacaoContratacaoAutomaticamente();
  renderizarDadosGerais();
  renderizarItens();
}

function atualizarSituacaoContratacaoAutomaticamente() {
  // Regra combinada: assim que o 1º item inicia, a contratação vira "iniciado";
  // quando todos os itens estão concluídos, a contratação vira "concluido".
  // Não mexe se a contratação estiver "inativo" (recusada).
  if (CONTRATACAO.situacao === 'inativo') return;

  const todosConcluidos = CONTRATACAO.itens.every((i) => i.situacao_item === 'concluido');
  const algumIniciadoOuConcluido = CONTRATACAO.itens.some((i) => i.situacao_item !== 'aguardando');

  if (todosConcluidos) {
    CONTRATACAO.situacao = 'concluido';
  } else if (algumIniciadoOuConcluido) {
    CONTRATACAO.situacao = 'iniciado';
  }
}

function formatarData(dataIso) {
  if (!dataIso) return '—';
  const [ano, mes, dia] = dataIso.split('-');
  return `${dia}/${mes}/${ano}`;
}
