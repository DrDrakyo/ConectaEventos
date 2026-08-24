/* ==========================================================================
   TELA: CONTRATAÇÕES RECEBIDAS (Módulo Prestador)
   JavaScript específico desta tela: lista as contratações vinculadas a
   itens de PORTFOLIO deste prestador (CONTRATACAO + ITEM_CONTRA +
   CONTRATANTE), com filtro por situação.
   ========================================================================== */

let contratacoesGlobal = [];

const ROTULO_STATUS = {
  PENDENTE: 'Pendente',
  CONFIRMADO: 'Contratado / Confirmado',
  CONCLUIDO: 'Serviço concluído',
  CANCELADO: 'Cancelado',
};

document.addEventListener('DOMContentLoaded', async () => {
  const selectFiltro = document.querySelector('#filtro-situacao');
  if (selectFiltro) {
    selectFiltro.addEventListener('change', aplicarFiltro);
  }
  await carregarContratacoes();
});

async function carregarContratacoes() {
  try {
    const response = await fetch('/acompanharContratacao');
    if (!response.ok) {
      renderizarLista([]);
      return;
    }

    const data = await response.json();
    if (data && data.sucesso && Array.isArray(data.contratacoes)) {
      contratacoesGlobal = data.contratacoes;
    } else {
      contratacoesGlobal = [];
    }
    aplicarFiltro();
  } catch (e) {
    console.error('Erro ao carregar contratações recebidas:', e);
    renderizarLista([]);
  }
}

function aplicarFiltro() {
  const filtroEl = document.querySelector('#filtro-situacao');
  const situacao = filtroEl ? filtroEl.value : '';
  const filtradas = situacao ? contratacoesGlobal.filter((c) => (c.status || '').toLowerCase() === situacao.toLowerCase()) : contratacoesGlobal;
  renderizarLista(filtradas);
}

function renderizarLista(lista) {
  const container = document.querySelector('#lista-recebidas');
  const vazio = document.querySelector('#estado-vazio');

  if (!lista || lista.length === 0) {
    if (container) container.innerHTML = '';
    if (vazio) vazio.classList.add('estado-vazio--visivel');
    return;
  }

  if (vazio) vazio.classList.remove('estado-vazio--visivel');

  if (container) {
    container.innerHTML = lista.map((c) => {
      const st = c.status || 'PENDENTE';
      const rotulo = ROTULO_STATUS[st] || st;
      const valorStr = (c.valor_total || 0).toLocaleString('pt-BR', { minimumFractionDigits: 2 });
      return `
        <div class="linha-recebida card">
          <span class="linha-recebida__info">
            <span class="linha-recebida__titulo">${c.titulo_evento || 'Serviço Contratado'}</span>
            <span class="linha-recebida__detalhe">Contratação #${c.id_contratacao} · Data: ${formatarData(c.data_contratacao || c.data_evento)}</span>
          </span>
          <span class="status status--${st.toLowerCase()}">${rotulo}</span>
          <span class="linha-recebida__valor">R$ ${valorStr}</span>
          ${st === 'PENDENTE' ? `
            <span class="linha-recebida__acoes">
              <button type="button" class="botao botao--primario botao--pequeno" data-acao="aceitar" data-id="${c.id_contratacao}">Aceitar</button>
              <button type="button" class="botao botao--perigo botao--pequeno" data-acao="recusar" data-id="${c.id_contratacao}">Recusar</button>
            </span>
          ` : ''}
          <a class="linha-recebida__link" href="detalhes-contratacao.html?id=${c.id_contratacao}">Ver detalhes →</a>
        </div>
      `;
    }).join('');

    container.querySelectorAll('[data-acao="aceitar"]').forEach((botao) => {
      botao.addEventListener('click', () => alterarSituacao(botao.dataset.id, 'confirmar'));
    });
    container.querySelectorAll('[data-acao="recusar"]').forEach((botao) => {
      botao.addEventListener('click', () => alterarSituacao(botao.dataset.id, 'cancelar'));
    });
  }
}

async function alterarSituacao(idContratacao, acaoBackend) {
  try {
    const formData = new URLSearchParams();
    formData.append('id', idContratacao);
    formData.append('acao', acaoBackend);

    const response = await fetch('/acompanharContratacao', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
      body: formData.toString()
    });

    const data = await response.json();
    if (response.ok && data && data.sucesso) {
      await carregarContratacoes();
    } else {
      alert(data.mensagem || 'Erro ao atualizar situação da contratação.');
    }
  } catch (e) {
    console.error('Erro ao atualizar contratação:', e);
  }
}

function formatarData(dataIso) {
  if (!dataIso) return '—';
  const partes = dataIso.split('-');
  if (partes.length < 3) return dataIso;
  return `${partes[2]}/${partes[1]}/${partes[0]}`;
}
