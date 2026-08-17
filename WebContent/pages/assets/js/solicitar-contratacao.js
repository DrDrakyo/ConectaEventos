/* ==========================================================================
   TELA: SOLICITAR CONTRATAÇÃO (Módulo Contratante)
   JavaScript específico desta tela: monta os itens da contratação
   (ITEM_CONTRA), calcula subtotal/total e valida antes do envio.
   O registro criado corresponde a CONTRATACAO + um ou mais ITEM_CONTRA.
   ========================================================================== */

let proximoIdItem = 2;

/* Item pré-selecionado ao vir do Perfil/Visualizar Portfólio do prestador
   (simula PORTFOLIO.id_portfolio + valor já escolhidos). */
const ITEM_INICIAL = {
  id_portfolio: 101,
  item_contratacao: 'Cobertura fotográfica completa',
  valor_unitario: 1200,
};

document.addEventListener('DOMContentLoaded', () => {
  adicionarLinhaItem(ITEM_INICIAL);
  document.querySelector('#data-contratacao').textContent = formatarDataAtual();
  document.querySelector('#botao-adicionar-item').addEventListener('click', () => adicionarLinhaItem());
  document.querySelector('#formulario-solicitacao').addEventListener('submit', tratarEnvio);
  recalcularTotal();
});

function adicionarLinhaItem(itemPredefinido) {
  const lista = document.querySelector('#lista-itens');
  const idItem = proximoIdItem++;

  const item = document.createElement('div');
  item.className = 'item-solicitacao';
  item.dataset.idItem = idItem;
  item.innerHTML = `
    <div class="item-solicitacao__cabecalho">
      <span class="item-solicitacao__titulo">Item da contratação</span>
      <button type="button" class="item-solicitacao__remover" data-acao="remover">Remover</button>
    </div>
    <div class="campo">
      <label>Serviço (item_contratacao) *</label>
      <input type="text" class="campo-item-descricao" required value="${itemPredefinido ? itemPredefinido.item_contratacao : ''}" placeholder="Ex: Cobertura fotográfica completa" />
    </div>
    <div class="item-solicitacao__grade">
      <div class="campo">
        <label>Quantidade *</label>
        <input type="number" class="campo-item-quantidade" min="1" step="1" value="1" required />
      </div>
      <div class="campo">
        <label>Valor unitário (R$) *</label>
        <input type="number" class="campo-item-valor" min="0" step="0.01" required value="${itemPredefinido ? itemPredefinido.valor_unitario : ''}" />
      </div>
      <div class="campo">
        <label>Início previsto *</label>
        <input type="date" class="campo-item-data" required />
      </div>
    </div>
    <div class="item-solicitacao__subtotal">Subtotal: <strong class="valor-subtotal">R$ 0,00</strong></div>
  `;

  lista.appendChild(item);

  item.querySelectorAll('.campo-item-quantidade, .campo-item-valor').forEach((campo) => {
    campo.addEventListener('input', () => {
      atualizarSubtotal(item);
      recalcularTotal();
    });
  });

  item.querySelector('[data-acao="remover"]').addEventListener('click', () => {
    if (lista.children.length === 1) return; // mantém ao menos 1 item
    item.remove();
    recalcularTotal();
  });

  atualizarSubtotal(item);
  recalcularTotal();
}

function atualizarSubtotal(itemEl) {
  const quantidade = Number(itemEl.querySelector('.campo-item-quantidade').value) || 0;
  const valorUnitario = Number(itemEl.querySelector('.campo-item-valor').value) || 0;
  const subtotal = quantidade * valorUnitario;
  itemEl.querySelector('.valor-subtotal').textContent = formatarMoeda(subtotal);
}

function recalcularTotal() {
  let total = 0;
  document.querySelectorAll('#lista-itens .item-solicitacao').forEach((itemEl) => {
    const quantidade = Number(itemEl.querySelector('.campo-item-quantidade').value) || 0;
    const valorUnitario = Number(itemEl.querySelector('.campo-item-valor').value) || 0;
    total += quantidade * valorUnitario;
  });

  document.querySelector('#resumo-quantidade-itens').textContent = document.querySelectorAll('#lista-itens .item-solicitacao').length;
  document.querySelector('#resumo-valor-total').textContent = formatarMoeda(total);
}

function tratarEnvio(evento) {
  evento.preventDefault();
  const mensagem = document.querySelector('#mensagem-feedback');

  const local = document.querySelector('#campo-local').value.trim();
  const formaPagamento = document.querySelector('#campo-forma-pagamento').value;
  let valido = Boolean(local) && Boolean(formaPagamento);

  document.querySelectorAll('#lista-itens .item-solicitacao').forEach((itemEl) => {
    const descricao = itemEl.querySelector('.campo-item-descricao').value.trim();
    const quantidade = itemEl.querySelector('.campo-item-quantidade').value;
    const valorUnitario = itemEl.querySelector('.campo-item-valor').value;
    const dataInicio = itemEl.querySelector('.campo-item-data').value;
    if (!descricao || !quantidade || !valorUnitario || !dataInicio) valido = false;
  });

  mensagem.classList.remove('mensagem-feedback--sucesso', 'mensagem-feedback--erro');

  if (!valido) {
    mensagem.textContent = 'Preencha todos os campos obrigatórios antes de enviar.';
    mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
    return;
  }

  // Em produção: gravar um registro em CONTRATACAO (situacao inicial
  // "Em negociação") e um registro em ITEM_CONTRA para cada item da lista.
  mensagem.textContent = 'Solicitação enviada com sucesso! Redirecionando para o histórico de contratações...';
  mensagem.classList.add('mensagem-feedback--sucesso', 'mensagem-feedback--visivel');

  setTimeout(() => {
    window.location.href = 'historico-contratacoes.html';
  }, 1800);
}

function formatarMoeda(valor) {
  return `R$ ${valor.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`;
}

function formatarDataAtual() {
  const hoje = new Date();
  return hoje.toLocaleDateString('pt-BR');
}
