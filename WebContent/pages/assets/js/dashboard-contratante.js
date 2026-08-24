/* ==========================================================================
   TELA: DASHBOARD (Módulo Contratante)
   JavaScript puro, específico desta tela. O menu mobile e o menu do
   usuário já são tratados por js/common.js.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  renderizarResumo();
  renderizarContratacoesRecentes();
});

/* Dados zerados — contratações serão lidas do banco conforme cadastradas */
const CONTRATACOES_CONTRATANTE = [];

const ROTULO_STATUS = {
  disponivel: 'Disponível',
  negociacao: 'Em negociação',
  contratado: 'Contratado',
  iniciado: 'Serviço iniciado',
  concluido: 'Serviço concluído',
  inativo: 'Inativo',
};

/* ---------- Cards de resumo ---------- */
function renderizarResumo() {
  const emAndamento = CONTRATACOES_CONTRATANTE.filter((c) =>
    ['negociacao', 'contratado', 'iniciado'].includes(c.situacao)
  ).length;
  const concluidas = CONTRATACOES_CONTRATANTE.filter((c) => c.situacao === 'concluido').length;

  const elAndamento = document.querySelector('#numero-em-andamento');
  const elConcluidas = document.querySelector('#numero-concluidas');
  const elTotal = document.querySelector('#numero-total');

  if (elAndamento) elAndamento.textContent = emAndamento;
  if (elConcluidas) elConcluidas.textContent = concluidas;
  if (elTotal) elTotal.textContent = CONTRATACOES_CONTRATANTE.length;
}

/* ---------- Lista de contratações recentes ---------- */
function renderizarContratacoesRecentes() {
  const lista = document.querySelector('#lista-contratacoes-recentes');
  const vazio = document.querySelector('#estado-vazio-contratacoes');
  if (!lista) return;

  if (CONTRATACOES_CONTRATANTE.length === 0) {
    lista.style.display = 'none';
    if (vazio) vazio.classList.add('estado-vazio--visivel');
    return;
  }

  lista.innerHTML = CONTRATACOES_CONTRATANTE.map((c) => `
    <a class="linha-contratacao card" href="acompanhar-contratacao.html?id=${c.id_contratacao}">
      <span class="linha-contratacao__icone">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true">
          <rect x="3" y="4" width="18" height="14" rx="2"/><path d="M8 20h8M12 18v2"/>
        </svg>
      </span>
      <span class="linha-contratacao__info">
        <span class="linha-contratacao__titulo">${c.servico}</span>
        <span class="linha-contratacao__detalhe">${c.prestador} · ${formatarData(c.data_contratacao)}</span>
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
