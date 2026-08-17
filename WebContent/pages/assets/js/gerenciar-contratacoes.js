/* ==========================================================================
   TELA: GERENCIAR CONTRATAÇÕES (Módulo Administrador)
   JavaScript específico desta tela: lista as contratações da plataforma
   (CONTRATACAO + ITEM_CONTRA + PRESTADOR + CONTRATANTE), somente leitura,
   com filtro por situação e por local.
   ========================================================================== */

const CONTRATACOES = [
  { id_contratacao: 1041, contratante: 'Maria Costa', prestador: 'Studio Lente Viva', data_contratacao: '2026-06-02', local_contratacao: 'Salvador - BA', valor_total: 1850, situacao: 'iniciado' },
  { id_contratacao: 1038, contratante: 'Camila Ferreira', prestador: 'Sabor & Arte Buffet', data_contratacao: '2026-05-20', local_contratacao: 'Lauro de Freitas - BA', valor_total: 7200, situacao: 'negociacao' },
  { id_contratacao: 1022, contratante: 'Maria Costa', prestador: 'DJ Marcos Ferreira', data_contratacao: '2026-04-11', local_contratacao: 'Salvador - BA', valor_total: 1200, situacao: 'concluido' },
  { id_contratacao: 1015, contratante: 'João Almeida', prestador: 'Cerimonial Elo Perfeito', data_contratacao: '2026-03-02', local_contratacao: 'Camaçari - BA', valor_total: 1800, situacao: 'concluido' },
  { id_contratacao: 1002, contratante: 'Fernanda Lopes', prestador: 'Luz & Cena Iluminação', data_contratacao: '2026-01-18', local_contratacao: 'Salvador - BA', valor_total: 980, situacao: 'inativo' },
];

const ROTULO_STATUS = {
  disponivel: 'Disponível', negociacao: 'Em negociação', contratado: 'Contratado',
  iniciado: 'Serviço iniciado', concluido: 'Serviço concluído', inativo: 'Inativo',
};

document.addEventListener('DOMContentLoaded', () => {
  document.querySelector('#filtro-situacao').addEventListener('change', aplicarFiltros);
  document.querySelector('#filtro-local').addEventListener('input', aplicarFiltros);
  aplicarFiltros();
});

function aplicarFiltros() {
  const situacao = document.querySelector('#filtro-situacao').value;
  const local = document.querySelector('#filtro-local').value.trim().toLowerCase();

  const filtradas = CONTRATACOES.filter((c) =>
    (!situacao || c.situacao === situacao) &&
    (!local || c.local_contratacao.toLowerCase().includes(local))
  );

  renderizarTabela(filtradas);
}

function renderizarTabela(lista) {
  const corpo = document.querySelector('#corpo-tabela');
  const vazio = document.querySelector('#estado-vazio');

  if (lista.length === 0) {
    corpo.innerHTML = '';
    vazio.classList.add('estado-vazio--visivel');
    return;
  }

  vazio.classList.remove('estado-vazio--visivel');

  corpo.innerHTML = lista.map((c) => `
    <tr>
      <td>
        <div class="celula-principal">#${c.id_contratacao} · ${c.prestador}</div>
        <div class="celula-secundaria">Contratante: ${c.contratante}</div>
      </td>
      <td>${formatarData(c.data_contratacao)}</td>
      <td>${c.local_contratacao}</td>
      <td>R$ ${c.valor_total.toLocaleString('pt-BR')}</td>
      <td><span class="status status--${c.situacao}">${ROTULO_STATUS[c.situacao]}</span></td>
    </tr>
  `).join('');
}

function formatarData(dataIso) {
  const [ano, mes, dia] = dataIso.split('-');
  return `${dia}/${mes}/${ano}`;
}
