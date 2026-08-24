/* ==========================================================================
   TELA: MEU PERFIL (Módulo Prestador)
   JavaScript puro, específico desta tela. Renderiza os dados do registro
   autenticado da tabela PRESTADOR.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', async () => {
  let prestador = null;
  try {
    const response = await fetch('/perfilPrestador');
    if (response.ok) {
      const data = await response.json();
      if (data && data.sucesso && data.prestador) {
        prestador = data.prestador;
      }
    }
  } catch (e) {
    console.warn('Erro ao carregar perfil do backend:', e);
  }

  renderizarDadosPrestador(prestador);
});

function renderizarDadosPrestador(p) {
  const nomeResponsavel = p ? p.nome_prestador || '—' : '—';
  const nomeFantasia = p ? p.nome_prestador || '—' : '—';
  const email = p ? p.email_prestador || '—' : '—';
  const telefone = p ? p.telefone || '—' : '—';
  const categoria = p ? p.categoria || '—' : '—';
  const localizacao = p ? (p.cidade ? `${p.cidade} - BA` : '—') : '—';
  const contatos = p ? p.telefone || '—' : '—';
  const descricao = p ? p.descricao || 'Nenhuma descrição informada.' : 'Nenhuma descrição informada.';
  const disponibilidade = p ? (p.situacao === 'ATIVO' ? 'Disponível' : 'Indisponível') : '—';
  const situacao = p ? (p.situacao === 'ATIVO' ? 'Ativo' : 'Inativo') : '—';

  document.querySelector('#valor-nome').textContent = nomeResponsavel;
  document.querySelector('#valor-nome-fantasia').textContent = nomeFantasia;
  document.querySelector('#valor-email').textContent = email;
  document.querySelector('#valor-telefone').textContent = telefone;
  document.querySelector('#valor-categoria').textContent = categoria;
  document.querySelector('#valor-localizacao').textContent = localizacao;
  document.querySelector('#valor-contatos').textContent = contatos;
  document.querySelector('#valor-descricao').textContent = descricao;

  const disponibilidadeEl = document.querySelector('#valor-disponibilidade');
  if (disponibilidadeEl) {
    disponibilidadeEl.textContent = disponibilidade;
    disponibilidadeEl.className = `badge ${disponibilidade === 'Disponível' ? 'badge--azul' : 'badge--roxo'}`;
  }

  const situacaoEl = document.querySelector('#valor-situacao');
  if (situacaoEl) {
    situacaoEl.textContent = situacao;
    situacaoEl.className = `badge ${situacao === 'Ativo' ? 'badge--azul' : 'badge--roxo'}`;
  }

  const cartaoNome = document.querySelector('#cartao-nome-fantasia');
  if (cartaoNome) cartaoNome.textContent = p ? (p.nome_prestador || 'Sem Perfil') : 'Sem Perfil';

  const cartaoCat = document.querySelector('#cartao-categoria');
  if (cartaoCat) cartaoCat.textContent = categoria;

  const avatar = document.querySelector('#avatar-iniciais');
  if (avatar) avatar.textContent = p ? obterIniciais(p.nome_prestador) : '--';
}

function obterIniciais(nome) {
  if (!nome) return '--';
  return nome.split(' ').filter(Boolean).slice(0, 2).map((p) => p[0].toUpperCase()).join('');
}
