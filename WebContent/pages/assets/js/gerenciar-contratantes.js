/* ==========================================================================
   TELA: GERENCIAR CONTRATANTES (Módulo Administrador)
   JavaScript específico desta tela: lista, filtra e permite ativar/
   desativar registros da tabela CONTRATANTE (altera apenas situacao).
   ========================================================================== */

let CONTRATANTES = [
  { cpf_cnpj: '123.456.789-00', nome_contratante: 'Maria Costa', email_contratante: 'maria.costa@email.com', cidade: 'Salvador - BA', data_cadastro: '2026-02-14', situacao: 'ativo' },
  { cpf_cnpj: '234.567.890-11', nome_contratante: 'João Almeida', email_contratante: 'joao.almeida@email.com', cidade: 'Lauro de Freitas - BA', data_cadastro: '2026-01-30', situacao: 'ativo' },
  { cpf_cnpj: '345.678.901-22', nome_contratante: 'Fernanda Lopes', email_contratante: 'fernanda.lopes@email.com', cidade: 'Salvador - BA', data_cadastro: '2025-12-05', situacao: 'ativo' },
  { cpf_cnpj: '456.789.012-33', nome_contratante: 'Rodrigo Nascimento', email_contratante: 'rodrigo.nascimento@email.com', cidade: 'Camaçari - BA', data_cadastro: '2025-11-18', situacao: 'inativo' },
];

document.addEventListener('DOMContentLoaded', () => {
  document.querySelector('#filtro-cidade').addEventListener('input', aplicarFiltros);
  document.querySelector('#filtro-situacao').addEventListener('change', aplicarFiltros);
  aplicarFiltros();
});

function aplicarFiltros() {
  const cidade = document.querySelector('#filtro-cidade').value.trim().toLowerCase();
  const situacao = document.querySelector('#filtro-situacao').value;

  const filtrados = CONTRATANTES.filter((c) =>
    (!cidade || c.cidade.toLowerCase().includes(cidade)) &&
    (!situacao || c.situacao === situacao)
  );

  renderizarTabela(filtrados);
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
    <tr data-cpf="${c.cpf_cnpj}">
      <td>
        <div class="celula-principal">${c.nome_contratante}</div>
        <div class="celula-secundaria">${c.email_contratante}</div>
      </td>
      <td>${c.cidade}</td>
      <td>${formatarData(c.data_cadastro)}</td>
      <td><span class="badge ${c.situacao === 'ativo' ? 'badge--azul' : 'badge--roxo'}">${c.situacao === 'ativo' ? 'Ativo' : 'Inativo'}</span></td>
      <td>
        <div class="celula-acoes">
          <button type="button" class="botao botao--secundario botao--pequeno" data-acao="alternar">${c.situacao === 'ativo' ? 'Desativar' : 'Ativar'}</button>
        </div>
      </td>
    </tr>
  `).join('');

  corpo.querySelectorAll('[data-acao="alternar"]').forEach((botao) => {
    botao.addEventListener('click', (evento) => alternarSituacao(evento.target.closest('tr').dataset.cpf));
  });
}

function alternarSituacao(cpfCnpj) {
  const contratante = CONTRATANTES.find((c) => c.cpf_cnpj === cpfCnpj);
  if (!contratante) return;

  // Em produção: atualizar o campo situacao do registro correspondente
  // na tabela CONTRATANTE.
  contratante.situacao = contratante.situacao === 'ativo' ? 'inativo' : 'ativo';
  aplicarFiltros();
}

function formatarData(dataIso) {
  const [ano, mes, dia] = dataIso.split('-');
  return `${dia}/${mes}/${ano}`;
}
