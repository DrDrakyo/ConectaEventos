/* ==========================================================================
   TELA: PESQUISA POR CIDADE (Módulo Visitante)
   JavaScript específico desta tela: trata o envio da busca por cidade e
   renderiza sugestões de cidades (baseadas em PRESTADOR.localizacao),
   levando à Lista de Prestadores já filtrada. Menu mobile em common.js.
   ========================================================================== */

/* Cidades de exemplo com a quantidade de prestadores (simula um agrupamento
   de PRESTADOR.localizacao — apenas para popular a tela). */
const CIDADES_SUGERIDAS = [
  { nome: 'Salvador - BA', quantidade: 24 },
  { nome: 'Lauro de Freitas - BA', quantidade: 8 },
  { nome: 'Camaçari - BA', quantidade: 6 },
  { nome: 'Feira de Santana - BA', quantidade: 5 },
  { nome: 'Vitória da Conquista - BA', quantidade: 3 },
  { nome: 'Ilhéus - BA', quantidade: 2 },
];

document.addEventListener('DOMContentLoaded', () => {
  inicializarBuscaCidade();
  renderizarCidadesSugeridas();
});

function inicializarBuscaCidade() {
  const formulario = document.querySelector('#formulario-busca-cidade');
  formulario.addEventListener('submit', (evento) => {
    evento.preventDefault();
    const cidade = document.querySelector('#campo-cidade').value.trim();
    irParaListaPorCidade(cidade);
  });
}

function renderizarCidadesSugeridas() {
  const grade = document.querySelector('#grade-cidades');

  grade.innerHTML = CIDADES_SUGERIDAS.map((cidade) => `
    <a class="cidade-card card" href="lista-prestadores.html?cidade=${encodeURIComponent(cidade.nome)}">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
        <path d="M12 21s7-6.5 7-11.5A7 7 0 0 0 5 9.5C5 14.5 12 21 12 21z"/><circle cx="12" cy="9.5" r="2.3"/>
      </svg>
      <span>
        <span class="cidade-card__nome">${cidade.nome}</span>
        <span class="cidade-card__quantidade">${cidade.quantidade} prestadores</span>
      </span>
    </a>
  `).join('');
}

function irParaListaPorCidade(cidade) {
  const parametros = new URLSearchParams();
  if (cidade) parametros.set('cidade', cidade);
  window.location.href = `lista-prestadores.html?${parametros.toString()}`;
}
