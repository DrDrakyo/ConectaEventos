/* ==========================================================================
   TELA: ACOMPANHAR CONTRATAÇÃO (Módulo Contratante)
   JavaScript específico desta tela: renderiza os dados de CONTRATACAO e
   a lista de ITEM_CONTRA vinculados, e habilita "Avaliar Prestador"
   apenas quando a contratação está com situacao = concluído.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', async () => {
  const urlParams = new URLSearchParams(window.location.search);
  const idContratacao = urlParams.get('id');

  if (!idContratacao) {
    exibirErro('Nenhuma contratação selecionada.');
    return;
  }

  try {
    const response = await fetch(`/acompanharContratacao?id=${idContratacao}`);
    if (!response.ok) {
      exibirErro('Contratação não encontrada no sistema.');
      return;
    }

    const data = await response.json();
    if (data && data.sucesso && data.contratacao) {
      renderizarDadosGerais(data);
      renderizarItens(data.itens || []);
      configurarBotaoAvaliar(data.contratacao.status);
    } else {
      exibirErro(data.mensagem || 'Não foi possível carregar os detalhes da contratação.');
    }
  } catch (e) {
    console.error('Erro ao carregar contratação:', e);
    exibirErro('Erro de conexão ao buscar detalhes da contratação.');
  }
});

const ROTULO_STATUS = {
  PENDENTE: 'Pendente',
  CONFIRMADO: 'Contratado / Confirmado',
  CONCLUIDO: 'Serviço concluído',
  CANCELADO: 'Cancelado',
};

function renderizarDadosGerais(data) {
  const c = data.contratacao;
  const p = data.prestador || {};

  document.querySelector('#valor-prestador').textContent = p.nome || 'Prestador não informado';
  document.querySelector('#valor-local').textContent = c.titulo_evento || 'Local/Evento não informado';
  document.querySelector('#valor-forma-pagamento').textContent = 'Pix / Cartão';
  document.querySelector('#valor-data-contratacao').textContent = formatarData(c.data_contratacao);
  document.querySelector('#valor-total').textContent = `R$ ${(c.valor_total || 0).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`;

  const statusEl = document.querySelector('#valor-situacao');
  const statusStr = c.status || 'PENDENTE';
  statusEl.textContent = ROTULO_STATUS[statusStr] || statusStr;
  statusEl.className = `status status--${statusStr.toLowerCase()}`;
}

function renderizarItens(itens) {
  const lista = document.querySelector('#lista-itens-acompanhamento');
  if (!lista) return;

  if (itens.length === 0) {
    lista.innerHTML = '<p class="texto-suave">Nenhum item cadastrado para esta contratação.</p>';
    return;
  }

  lista.innerHTML = itens.map((item) => `
    <div class="item-acompanhamento">
      <div class="item-acompanhamento__cabecalho">
        <span class="item-acompanhamento__titulo">${item.descricao_item}</span>
        <span class="status status--concluido">Qtd: ${item.quantidade}</span>
      </div>
      <div class="item-acompanhamento__quantidade-valor">
        Quantidade: ${item.quantidade} · Valor unitário: R$ ${(item.valor_unitario || 0).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
      </div>
    </div>
  `).join('');
}

function configurarBotaoAvaliar(statusStr) {
  const botao = document.querySelector('#botao-avaliar');
  const mensagemStatus = document.querySelector('#mensagem-status-avaliacao');

  if (statusStr === 'CONCLUIDO') {
    if (botao) {
      botao.removeAttribute('aria-disabled');
      botao.classList.remove('botao--desabilitado');
    }
    if (mensagemStatus) mensagemStatus.textContent = 'O serviço foi concluído. Conte como foi sua experiência.';
  } else {
    if (botao) {
      botao.setAttribute('aria-disabled', 'true');
      botao.classList.add('botao--desabilitado');
      botao.addEventListener('click', (evento) => evento.preventDefault());
    }
    if (mensagemStatus) mensagemStatus.textContent = 'A avaliação ficará disponível quando o serviço for concluído.';
  }
}

function exibirErro(msg) {
  const container = document.querySelector('.miolo-pagina .container');
  if (container) {
    container.innerHTML = `<div class="estado-vazio estado-vazio--visivel"><p>${msg}</p><a href="dashboard-contratante.html" class="botao botao--primario" style="margin-top: 1rem;">Voltar ao Dashboard</a></div>`;
  }
}

function formatarData(dataIso) {
  if (!dataIso) return '—';
  const partes = dataIso.split('-');
  if (partes.length < 3) return dataIso;
  return `${partes[2]}/${partes[1]}/${partes[0]}`;
}
