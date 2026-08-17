/* ==========================================================================
   TELA: CONTRATAÇÕES RECEBIDAS (Módulo Prestador)
   JavaScript específico desta tela: lista as contratações vinculadas a
   itens de PORTFOLIO deste prestador (CONTRATACAO + ITEM_CONTRA +
   CONTRATANTE), com filtro por situação.
   ========================================================================== */

const CONTRATACOES_RECEBIDAS = [
  { id_contratacao: 1041, contratante: 'Maria Costa', servico: 'Cobertura fotográfica completa', data_contratacao: '2026-06-02', valor_total: 1850, situacao: 'iniciado' },
  { id_contratacao: 1030, contratante: 'João Almeida', servico: 'Ensaio pré-wedding', data_contratacao: '2026-05-28', valor_total: 650, situacao: 'negociacao' },
  { id_contratacao: 1019, contratante: 'Fernanda Lopes', servico: 'Cobertura de aniversário', data_contratacao: '2026-04-15', valor_total: 950, situacao: 'concluido' },
  { id_contratacao: 1005, contratante: 'Rodrigo Nascimento', servico: 'Formatura Direito UFBA', data_contratacao: '2026-02-20', valor_total: 1500, situacao: 'concluido' },
];

const ROTULO_STATUS = {
  disponivel: 'Disponível', negociacao: 'Em negociação', contratado: 'Contratado',
  iniciado: 'Serviço iniciado', concluido: 'Serviço concluído', inativo: 'Inativo',
};

document.addEventListener('DOMContentLoaded', () => {
  document.querySelector('#filtro-situacao').addEventListener('change', aplicarFiltro);
  aplicarFiltro();
});

function aplicarFiltro() {
  const situacao = document.querySelector('#filtro-situacao').value;
  const filtradas = situacao ? CONTRATACOES_RECEBIDAS.filter((c) => c.situacao === situacao) : CONTRATACOES_RECEBIDAS;
  renderizarLista(filtradas);
}

function renderizarLista(lista) {
  const container = document.querySelector('#lista-recebidas');
  const vazio = document.querySelector('#estado-vazio');

  if (lista.length === 0) {
    container.innerHTML = '';
    vazio.classList.add('estado-vazio--visivel');
    return;
  }

  vazio.classList.remove('estado-vazio--visivel');

  container.innerHTML = lista.map((c) => `
    <div class="linha-recebida card">
      <span class="linha-recebida__info">
        <span class="linha-recebida__titulo">${c.servico}</span>
        <span class="linha-recebida__detalhe">${c.contratante} · Contratação #${c.id_contratacao} · ${formatarData(c.data_contratacao)}</span>
      </span>
      <span class="status status--${c.situacao}">${ROTULO_STATUS[c.situacao]}</span>
      <span class="linha-recebida__valor">R$ ${c.valor_total.toLocaleString('pt-BR')}</span>
      ${c.situacao === 'negociacao' ? `
        <span class="linha-recebida__acoes">
          <button type="button" class="botao botao--primario botao--pequeno" data-acao="aceitar" data-id="${c.id_contratacao}">Aceitar</button>
          <button type="button" class="botao botao--perigo botao--pequeno" data-acao="recusar" data-id="${c.id_contratacao}">Recusar</button>
        </span>
      ` : ''}
      <a class="linha-recebida__link" href="detalhes-contratacao.html?id=${c.id_contratacao}">Ver detalhes →</a>
    </div>
  `).join('');

  container.querySelectorAll('[data-acao="aceitar"]').forEach((botao) => {
    botao.addEventListener('click', () => alterarSituacao(botao.dataset.id, 'contratado'));
  });
  container.querySelectorAll('[data-acao="recusar"]').forEach((botao) => {
    botao.addEventListener('click', () => alterarSituacao(botao.dataset.id, 'inativo'));
  });
}

function alterarSituacao(idContratacao, novaSituacao) {
  const contratacao = CONTRATACOES_RECEBIDAS.find((c) => c.id_contratacao === Number(idContratacao));
  if (!contratacao) return;
  contratacao.situacao = novaSituacao;
  // Em produção: enviar requisição ao servlet para persistir CONTRATACAO.situacao no banco.
  aplicarFiltro();
}

function formatarData(dataIso) {
  const [ano, mes, dia] = dataIso.split('-');
  return `${dia}/${mes}/${ano}`;
}
