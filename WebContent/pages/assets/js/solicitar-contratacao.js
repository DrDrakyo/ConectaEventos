/* ==========================================================================
   TELA: SOLICITAR CONTRATAÇÃO (Módulo Contratante)
   JavaScript específico desta tela: monta os itens da contratação
   (ITEM_CONTRA), calcula subtotal/total e valida antes do envio.
   O registro criado corresponde a CONTRATACAO + um ou mais ITEM_CONTRA.
   ========================================================================== */

let proximoIdItem = 2;

document.addEventListener('DOMContentLoaded', () => {
  adicionarLinhaItem();
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
        <input type="number" class="campo-item-valor" min="0" step="0.01" required value="${itemPredefinido ? itemPredefinido.valor_unitario : ''}" placeholder="0.00" />
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

async function tratarEnvio(evento) {
  evento.preventDefault();
  const mensagem = document.querySelector('#mensagem-feedback');

  const local = document.querySelector('#campo-local').value.trim();
  const formaPagamento = document.querySelector('#campo-forma-pagamento').value;
  const urlParams = new URLSearchParams(window.location.search);
  const cpfPrestador = urlParams.get('cpf_prestador') || urlParams.get('cpf') || '00000000000';

  let valido = Boolean(local) && Boolean(formaPagamento);

  const emData = document.querySelector('.campo-item-data') ? document.querySelector('.campo-item-data').value : '';
  const tituloEvento = local || 'Evento de Festa';

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

  mensagem.textContent = 'Gravando contratação no banco de dados...';
  mensagem.classList.add('mensagem-feedback--visivel');

  try {
    const formData = new URLSearchParams();
    formData.append('acao', 'cadastrar');
    formData.append('cpf_cnpj_prestador', cpfPrestador);
    formData.append('titulo_evento', tituloEvento);
    formData.append('descricao_evento', `Forma de pagamento: ${formaPagamento}. Local: ${local}`);
    formData.append('data_evento', emData || new Date().toISOString().split('T')[0]);

    const response = await fetch('/acompanharContratacao', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
      body: formData.toString()
    });

    const data = await response.json();

    if (response.ok && data && data.sucesso) {
      mensagem.textContent = 'Solicitação gravada no banco de dados com sucesso! Redirecionando...';
      mensagem.classList.add('mensagem-feedback--sucesso', 'mensagem-feedback--visivel');

      setTimeout(() => {
        window.location.href = `acompanhar-contratacao.html?id=${data.id_contratacao}`;
      }, 1500);
    } else {
      mensagem.textContent = data.mensagem || 'Erro ao gravar solicitação de contratação.';
      mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
    }
  } catch (e) {
    console.error('Erro na solicitação:', e);
    mensagem.textContent = 'Erro de comunicação ao enviar solicitação.';
    mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
  }
}

function formatarMoeda(valor) {
  return `R$ ${valor.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`;
}

function formatarDataAtual() {
  const hoje = new Date();
  return hoje.toLocaleDateString('pt-BR');
}
