document.addEventListener('DOMContentLoaded', async () => {
  const parametros = new URLSearchParams(window.location.search);
  const id = parametros.get('id');
  const cpf = parametros.get('cpf');

  let portfolio = [];
  let avaliacoes = [];

  if (id || cpf) {
    try {
      const url = id ? `/perfilPrestador?id=${id}` : `/perfilPrestador?cpf_cnpj=${cpf}`;
      const response = await fetch(url);
      if (response.ok) {
        const data = await response.json();
        if (data && data.sucesso) {
          portfolio = data.portfolio || [];
          avaliacoes = data.avaliacoes || [];
          if (data.prestador) {
            preencherDadosPrestador(data.prestador);
          }
        }
      }
    } catch (e) {
      console.warn('Erro ao carregar dados do prestador do BD:', e);
    }
  }

  renderizarPortfolio(portfolio);
  renderizarAvaliacoes(avaliacoes);
});

function preencherDadosPrestador(prestador) {
  const nomeEl = document.querySelector('.prestador-perfil__nome');
  if (nomeEl && prestador.nome_prestador) nomeEl.textContent = prestador.nome_prestador;

  const catEl = document.querySelector('.prestador-perfil__categoria');
  if (catEl && prestador.categoria) catEl.textContent = prestador.categoria;

  const locEl = document.querySelector('.prestador-perfil__local');
  if (locEl && prestador.cidade) locEl.textContent = prestador.cidade;

  const descEl = document.querySelector('.prestador-perfil__descricao');
  if (descEl && prestador.descricao) descEl.textContent = prestador.descricao;
}

function renderizarPortfolio(itens) {
  const galeria = document.querySelector('#galeria-portfolio-grade');
  if (!galeria) return;

  if (!itens || itens.length === 0) {
    galeria.innerHTML = '<p class="secao__legenda" style="grid-column: 1/-1;">Nenhum item de portfólio cadastrado.</p>';
    return;
  }

  galeria.innerHTML = itens.map((item) => `
    <a class="item-portfolio" href="visualizar-portfolio.html?id=${item.id_portfolio}">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
        <circle cx="12" cy="13" r="3.2"/><path d="M4 8h3l1.5-2h7L17 8h3a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V9a1 1 0 0 1 1-1z"/>
      </svg>
      <span class="item-portfolio__legenda">${item.titulo}</span>
    </a>
  `).join('');
}

function renderizarAvaliacoes(listaAvaliacoes) {
  const lista = document.querySelector('#lista-avaliacoes');
  if (!lista) return;

  if (!listaAvaliacoes || listaAvaliacoes.length === 0) {
    lista.innerHTML = '<p class="secao__legenda">Nenhuma avaliação recebida até o momento.</p>';
    return;
  }

  lista.innerHTML = listaAvaliacoes.map((avaliacao) => `
    <div class="avaliacao-card">
      <div class="avaliacao-card__cabecalho">
        <span class="avaliacao-card__autor">${avaliacao.cpf_cnpj_contratante || 'Contratante'}</span>
        <span class="avaliacao">${'★'.repeat(avaliacao.nota || 5)}${'☆'.repeat(5 - (avaliacao.nota || 5))}</span>
      </div>
      <p class="avaliacao-card__comentario">${avaliacao.comentario || ''}</p>
    </div>
  `).join('');
}
