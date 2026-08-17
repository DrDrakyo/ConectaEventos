/* ==========================================================================
   TELA: VISUALIZAR PORTFÓLIO (Módulo Visitante)
   JavaScript específico desta tela: alterna entre as miniaturas
   (imagem1, imagem2, imagem3 da tabela PORTFOLIO). Menu mobile em common.js.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  inicializarTrocaDeMiniaturas();
});

function inicializarTrocaDeMiniaturas() {
  const miniaturas = document.querySelectorAll('.miniatura');
  if (!miniaturas.length) return;

  miniaturas.forEach((miniatura) => {
    miniatura.addEventListener('click', () => {
      miniaturas.forEach((m) => m.classList.remove('miniatura--ativa'));
      miniatura.classList.add('miniatura--ativa');
      // Em uma versão com imagens reais, aqui a imagem principal seria trocada
      // pela correspondente à miniatura clicada (imagem1, imagem2 ou imagem3).
    });
  });
}
