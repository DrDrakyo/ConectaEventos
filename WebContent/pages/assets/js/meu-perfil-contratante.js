/* ==========================================================================
   TELA: MEU PERFIL (Módulo Contratante)
   JavaScript puro, específico desta tela.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', async () => {
  let contratante = null;
  try {
    const response = await fetch('/meuPerfilContratante');
    if (response.ok) {
      const data = await response.json();
      if (data && data.sucesso && data.perfil) {
        contratante = data.perfil;
      }
    }
  } catch (e) {
    console.warn('Erro ao carregar perfil do contratante:', e);
  }

  renderizarDadosContratante(contratante);
});

function renderizarDadosContratante(c) {
  const nome = c ? c.nome_contratante || '—' : '—';
  const email = c ? c.email_contratante || '—' : '—';
  const telefone = c ? c.telefone || '—' : '—';
  const endereco = c ? c.endereco || '—' : '—';
  const cidade = c ? c.cidade || '—' : '—';
  const cpfCnpj = c ? c.cpf_cnpj || '—' : '—';
  const dataCad = c ? (c.data_cadastro || '—') : '—';
  const situacao = c ? (c.situacao === 'ATIVO' ? 'Ativo' : 'Inativo') : '—';

  const elNome = document.querySelector('#valor-nome');
  if (elNome) elNome.textContent = nome;

  const elEmail = document.querySelector('#valor-email');
  if (elEmail) elEmail.textContent = email;

  const elTel = document.querySelector('#valor-telefone');
  if (elTel) elTel.textContent = telefone;

  const elEnd = document.querySelector('#valor-endereco');
  if (elEnd) elEnd.textContent = endereco;

  const elCid = document.querySelector('#valor-cidade');
  if (elCid) elCid.textContent = cidade;

  const elCpf = document.querySelector('#valor-cpf-cnpj');
  if (elCpf) elCpf.textContent = cpfCnpj;

  const elData = document.querySelector('#valor-data-cadastro');
  if (elData) elData.textContent = dataCad;

  const situacaoEl = document.querySelector('#valor-situacao');
  if (situacaoEl) {
    situacaoEl.textContent = situacao;
    situacaoEl.className = `badge ${situacao === 'Ativo' ? 'badge--azul' : 'badge--roxo'}`;
  }

  const elCartao = document.querySelector('#nome-cartao');
  if (elCartao) elCartao.textContent = nome;

  const elAvatar = document.querySelector('#avatar-iniciais');
  if (elAvatar) elAvatar.textContent = c ? obterIniciais(nome) : '--';
}

function obterIniciais(nomeCompleto) {
  if (!nomeCompleto) return '--';
  return nomeCompleto
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((parte) => parte[0].toUpperCase())
    .join('');
}
