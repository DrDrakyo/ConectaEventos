/* ==========================================================================
   TELA: MEU PERFIL (Módulo Prestador)
   JavaScript puro, específico desta tela. Renderiza os dados do registro
   autenticado da tabela PRESTADOR.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  renderizarDadosPrestador();
});

/* Dados de exemplo — simulam o registro autenticado da tabela PRESTADOR. */
const PRESTADOR_LOGADO = {
  nome_prestador: 'Carla Menezes',
  email_prestador: 'contato@studiolenteviva.com.br',
  telefone: '(71) 99999-0000',
  nome_fantasia: 'Studio Lente Viva',
  categoria: 'Fotografia',
  descricao: 'Fotografia profissional para casamentos, aniversários e formaturas, com mais de 8 anos de experiência no mercado de eventos em Salvador e região.',
  localizacao: 'Salvador - BA',
  contatos: 'WhatsApp: (71) 99999-0000',
  disponibilidade: 'Disponível',
  situacao: 'ativo',
};

function renderizarDadosPrestador() {
  document.querySelector('#valor-nome').textContent = PRESTADOR_LOGADO.nome_prestador;
  document.querySelector('#valor-nome-fantasia').textContent = PRESTADOR_LOGADO.nome_fantasia;
  document.querySelector('#valor-email').textContent = PRESTADOR_LOGADO.email_prestador;
  document.querySelector('#valor-telefone').textContent = PRESTADOR_LOGADO.telefone;
  document.querySelector('#valor-categoria').textContent = PRESTADOR_LOGADO.categoria;
  document.querySelector('#valor-localizacao').textContent = PRESTADOR_LOGADO.localizacao;
  document.querySelector('#valor-contatos').textContent = PRESTADOR_LOGADO.contatos;
  document.querySelector('#valor-descricao').textContent = PRESTADOR_LOGADO.descricao;

  const disponibilidadeEl = document.querySelector('#valor-disponibilidade');
  const disponivel = PRESTADOR_LOGADO.disponibilidade === 'Disponível';
  disponibilidadeEl.textContent = PRESTADOR_LOGADO.disponibilidade;
  disponibilidadeEl.className = `badge ${disponivel ? 'badge--azul' : 'badge--roxo'}`;

  const situacaoEl = document.querySelector('#valor-situacao');
  const ativo = PRESTADOR_LOGADO.situacao === 'ativo';
  situacaoEl.textContent = ativo ? 'Ativo' : 'Inativo';
  situacaoEl.className = `badge ${ativo ? 'badge--azul' : 'badge--roxo'}`;

  document.querySelector('#cartao-nome-fantasia').textContent = PRESTADOR_LOGADO.nome_fantasia;
  document.querySelector('#cartao-categoria').textContent = PRESTADOR_LOGADO.categoria;
  document.querySelector('#avatar-iniciais').textContent = obterIniciais(PRESTADOR_LOGADO.nome_fantasia);
}

function obterIniciais(nome) {
  return nome.split(' ').filter(Boolean).slice(0, 2).map((p) => p[0].toUpperCase()).join('');
}
