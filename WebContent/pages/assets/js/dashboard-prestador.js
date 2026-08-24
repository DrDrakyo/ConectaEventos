/* ==========================================================================
   TELA: DASHBOARD (Módulo Prestador)
   JavaScript puro, específico desta tela. Menu mobile e menu do usuário
   já são tratados por js/common.js.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  renderizarResumo();
  renderizarContratacoesRecebidas();
});

/* Dados zerados — contratações serão lidas do banco conforme cadastradas */
const CONTRATACOES_RECEBIDAS = [];

const ROTULO_STATUS = {
  disponivel: 'Disponível', negociacao: 'Em negociação', contratado: 'Contratado',
  iniciado: 'Serviço iniciado', concluido: 'Serviço concluído', inativo: 'Inativo',
};

function renderizarResumo() {
  const emAndamento = CONTRATACOES_RECEBIDAS.filter((c) => ['negociacao', 'contratado', 'iniciado'].includes(c.situacao)).length;
  const concluidas = CONTRATACOES_RECEBIDAS.filter((c) => c.situacao === 'concluido').length;
  const notaMedia = 0.0;

  document.querySelector('#numero-em-andamento').textContent = emAndamento;
  document.querySelector('#numero-concluidas').textContent = concluidas;
  document.querySelector('#numero-nota-media').textContent = notaMedia.toFixed(1);
}

function renderizarContratacoesRecebidas() {
  const lista = document.querySelector('#lista-contratacoes-recentes');
  const vazio = document.querySelector('#estado-vazio-contratacoes');

  if (CONTRATACOES_RECEBIDAS.length === 0) {
    vazio.classList.add('estado-vazio--visivel');
    return;
  }

  lista.innerHTML = CONTRATACOES_RECEBIDAS.map((c) => `
    <a class="linha-contratacao card" href="detalhes-contratacao.html?id=${c.id_contratacao}">
      <span class="linha-contratacao__icone">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><rect x="3" y="4" width="18" height="14" rx="2"/><path d="M8 20h8M12 18v2"/></svg>
      </span>
      <span class="linha-contratacao__info">
        <span class="linha-contratacao__titulo">${c.servico}</span>
        <span class="linha-contratacao__detalhe">${c.contratante} · ${formatarData(c.data_contratacao)}</span>
      </span>
      <span class="status status--${c.situacao}">${ROTULO_STATUS[c.situacao]}</span>
      <span class="linha-contratacao__valor">R$ ${c.valor_total.toLocaleString('pt-BR')}</span>
    </a>
  `).join('');
}

function formatarData(dataIso) {
  const [ano, mes, dia] = dataIso.split('-');
  return `${dia}/${mes}/${ano}`;
}
