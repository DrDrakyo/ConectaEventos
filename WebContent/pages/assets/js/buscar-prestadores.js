/* ==========================================================================
   TELA: BUSCAR PRESTADORES (Módulo Contratante)
   JavaScript específico desta tela: lê parâmetros da URL, aplica os
   filtros (categoria, cidade, disponibilidade, faixa de preço, reputação),
   ordena e renderiza os resultados. O menu mobile é tratado em common.js.

   Os dados abaixo são um MOCK que simula os registros de PRESTADOR
   combinados com PORTFOLIO (valor) e a média de CONTRATACAO.ava_nota —
   apenas para popular a tela, sem alterar a modelagem do banco.
   ========================================================================== */

const ITENS_POR_PAGINA = 6;
let paginaAtual = 1;

document.addEventListener('DOMContentLoaded', () => {
  preencherFiltrosComParametrosDaUrl();
  inicializarEventosDeFiltro();
  aplicarFiltros();
});

/* Lê ?busca= e ?categoria= vindos da Home ou de outras telas de pesquisa
   e já deixa o formulário de filtros preenchido. */
function preencherFiltrosComParametrosDaUrl() {
  const parametros = new URLSearchParams(window.location.search);
  const busca = parametros.get('busca');
  const categoria = parametros.get('categoria');

  if (busca) document.querySelector('#filtro-busca').value = busca;
  if (categoria) document.querySelector('#filtro-categoria').value = categoria;
}

function inicializarEventosDeFiltro() {
  document.querySelector('#formulario-filtros').addEventListener('submit', (evento) => {
    evento.preventDefault();
    paginaAtual = 1;
    aplicarFiltros();
  });

  document.querySelector('#botao-limpar-filtros').addEventListener('click', () => {
    document.querySelector('#formulario-filtros').reset();
    paginaAtual = 1;
    aplicarFiltros();
  });

  document.querySelector('#ordenacao').addEventListener('change', () => aplicarFiltros());
}

async function aplicarFiltros() {
  const busca = document.querySelector('#filtro-busca').value.trim();
  const categoria = document.querySelector('#filtro-categoria').value;
  const cidade = document.querySelector('#filtro-cidade').value.trim();
  const disponibilidade = document.querySelector('#filtro-disponibilidade').value;
  const precoMax = Number(document.querySelector('#filtro-preco-max').value) || Infinity;
  const notaMinima = Number(document.querySelector('#filtro-reputacao').value) || 0;
  const ordenacao = document.querySelector('#ordenacao').value;

  let listaPrestadores = [];

  try {
    const params = new URLSearchParams();
    if (busca) params.set('termo', busca);
    if (categoria) params.set('categoria', categoria);
    if (cidade) params.set('cidade', cidade);

    const response = await fetch(`/buscarPrestadores?${params.toString()}`);
    if (response.ok) {
      const data = await response.json();
      if (data && data.sucesso && Array.isArray(data.prestadores)) {
        listaPrestadores = data.prestadores.map((p) => ({
          ...p,
          nome_fantasia: p.nome_prestador,
          localizacao: p.cidade ? `${p.cidade} - BA` : 'Brasil',
          disponibilidade: p.situacao === 'ATIVO' ? 'Disponível' : 'Indisponível',
          valor_a_partir: 0,
          nota_media: p.media_avaliacoes || 0,
        }));
      }
    }
  } catch (e) {
    console.warn('Erro ao carregar prestadores do BD:', e);
  }

  let filtrados = listaPrestadores.filter((prestador) => {
    const combinaBusca = !busca || (prestador.nome_fantasia || prestador.nome_prestador || '').toLowerCase().includes(busca.toLowerCase());
    const combinaCategoria = !categoria || prestador.categoria === categoria;
    const combinaCidade = !cidade || (prestador.localizacao || prestador.cidade || '').toLowerCase().includes(cidade.toLowerCase());
    const combinaDisponibilidade = !disponibilidade || prestador.disponibilidade === disponibilidade;
    const combinaPreco = (prestador.valor_a_partir || 0) <= precoMax;
    const combinaReputacao = (prestador.nota_media || 0) >= notaMinima;

    return combinaBusca && combinaCategoria && combinaCidade && combinaDisponibilidade && combinaPreco && combinaReputacao;
  });

  filtrados = ordenarResultados(filtrados, ordenacao);

  renderizarResultados(filtrados);
}

function ordenarResultados(lista, criterio) {
  const copia = [...lista];
  if (criterio === 'menor-preco') copia.sort((a, b) => a.valor_a_partir - b.valor_a_partir);
  if (criterio === 'maior-preco') copia.sort((a, b) => b.valor_a_partir - a.valor_a_partir);
  if (criterio === 'melhor-avaliacao') copia.sort((a, b) => b.nota_media - a.nota_media);
  return copia;
}

function renderizarResultados(lista) {
  const grade = document.querySelector('#grade-prestadores');
  const contagem = document.querySelector('#resultados-contagem');
  const estadoVazio = document.querySelector('#estado-vazio');

  contagem.textContent = `${lista.length} prestador${lista.length === 1 ? '' : 'es'} encontrado${lista.length === 1 ? '' : 's'}`;

  if (lista.length === 0) {
    grade.innerHTML = '';
    estadoVazio.classList.add('estado-vazio--visivel');
    renderizarPaginacao(0);
    return;
  }

  estadoVazio.classList.remove('estado-vazio--visivel');

  const totalPaginas = Math.ceil(lista.length / ITENS_POR_PAGINA);
  if (paginaAtual > totalPaginas) paginaAtual = 1;

  const inicio = (paginaAtual - 1) * ITENS_POR_PAGINA;
  const itensDaPagina = lista.slice(inicio, inicio + ITENS_POR_PAGINA);

  grade.innerHTML = itensDaPagina.map((prestador) => `
    <article class="prestador-card card">
      <div class="prestador-card__imagem">
        ${iconeCamera()}
        <span class="prestador-card__disponibilidade badge ${prestador.disponibilidade === 'Disponível' ? 'badge--azul' : 'badge--roxo'}">
          ${prestador.disponibilidade}
        </span>
      </div>
      <div class="prestador-card__corpo">
        <span class="prestador-card__categoria">${prestador.categoria}</span>
        <h3 class="prestador-card__nome">${prestador.nome_fantasia}</h3>
        <span class="prestador-card__local">${iconePin()} ${prestador.localizacao}</span>
        <div class="prestador-card__rodape">
          <span class="prestador-card__preco">a partir de <strong>R$ ${prestador.valor_a_partir.toLocaleString('pt-BR')}</strong></span>
          <span class="avaliacao">${iconeEstrela()} ${prestador.nota_media.toFixed(1)}</span>
        </div>
      </div>
      <a href="perfil-prestador-contratante.html" style="position:absolute;inset:0;" aria-label="Ver perfil de ${prestador.nome_fantasia}"></a>
    </article>
  `).join('');

  // Garante posicionamento relativo para o link-overlay do card
  grade.querySelectorAll('.prestador-card').forEach((card) => (card.style.position = 'relative'));

  renderizarPaginacao(totalPaginas);
}

function renderizarPaginacao(totalPaginas) {
  const container = document.querySelector('#paginacao');
  container.innerHTML = '';

  for (let pagina = 1; pagina <= totalPaginas; pagina++) {
    const botao = document.createElement('button');
    botao.textContent = String(pagina);
    botao.setAttribute('aria-current', String(pagina === paginaAtual));
    botao.addEventListener('click', () => {
      paginaAtual = pagina;
      aplicarFiltros();
      window.scrollTo({ top: document.querySelector('.pagina-busca').offsetTop - 90, behavior: 'smooth' });
    });
    container.appendChild(botao);
  }
}

/* ---------- Ícones SVG reutilizados nesta tela ---------- */
function iconeCamera() {
  return '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="13" r="3.2"/><path d="M4 8h3l1.5-2h7L17 8h3a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V9a1 1 0 0 1 1-1z"/></svg>';
}
function iconePin() {
  return '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" width="14" height="14" aria-hidden="true"><path d="M12 21s7-6.5 7-11.5A7 7 0 0 0 5 9.5C5 14.5 12 21 12 21z"/><circle cx="12" cy="9.5" r="2.3"/></svg>';
}
function iconeEstrela() {
  return '<svg viewBox="0 0 24 24" fill="#f59e0b" stroke="none" width="16" height="16" aria-hidden="true"><path d="M12 2.5l2.9 6 6.6.9-4.8 4.6 1.1 6.5L12 17.6l-5.8 3-.9-6.5-4.8-4.6 6.6-.9z"/></svg>';
}
