/* ==========================================================================
   TELA: MEU PERFIL (Módulo Contratante)
   JavaScript puro, específico desta tela.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  renderizarDadosContratante();
});

/* Dados de exemplo — simulam o registro autenticado da tabela CONTRATANTE. */
const CONTRATANTE_LOGADO = {
  nome_contratante: 'Maria Costa',
  email_contratante: 'maria.costa@email.com',
  telefone: '(71) 99876-5432',
  endereco: 'Rua das Flores, 120',
  cidade: 'Salvador - BA',
  cpf_cnpj: '123.456.789-00',
  data_cadastro: '2026-02-14',
  situacao: 'ativo',
};

function renderizarDadosContratante() {
  document.querySelector('#valor-nome').textContent = CONTRATANTE_LOGADO.nome_contratante;
  document.querySelector('#valor-email').textContent = CONTRATANTE_LOGADO.email_contratante;
  document.querySelector('#valor-telefone').textContent = CONTRATANTE_LOGADO.telefone;
  document.querySelector('#valor-endereco').textContent = CONTRATANTE_LOGADO.endereco;
  document.querySelector('#valor-cidade').textContent = CONTRATANTE_LOGADO.cidade;
  document.querySelector('#valor-cpf-cnpj').textContent = CONTRATANTE_LOGADO.cpf_cnpj;

  const [ano, mes, dia] = CONTRATANTE_LOGADO.data_cadastro.split('-');
  document.querySelector('#valor-data-cadastro').textContent = `${dia}/${mes}/${ano}`;

  const situacaoEl = document.querySelector('#valor-situacao');
  const ativo = CONTRATANTE_LOGADO.situacao === 'ativo';
  situacaoEl.textContent = ativo ? 'Ativo' : 'Inativo';
  situacaoEl.className = `badge ${ativo ? 'badge--azul' : 'badge--roxo'}`;

  document.querySelector('#nome-cartao').textContent = CONTRATANTE_LOGADO.nome_contratante;
  document.querySelector('#avatar-iniciais').textContent = obterIniciais(CONTRATANTE_LOGADO.nome_contratante);
}

function obterIniciais(nomeCompleto) {
  return nomeCompleto
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((parte) => parte[0].toUpperCase())
    .join('');
}
