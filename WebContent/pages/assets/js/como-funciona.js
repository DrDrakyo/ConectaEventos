/* ==========================================================================
   TELA: COMO FUNCIONA (Módulo Visitante)
   JavaScript específico desta tela: alterna entre o painel "Contratante"
   e o painel "Prestador". O menu mobile já é tratado em js/common.js.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  inicializarAlternadorPublico();
});

function inicializarAlternadorPublico() {
  const botoes = document.querySelectorAll('.alternador-publico__botao');
  const paineis = document.querySelectorAll('.painel-publico');

  if (!botoes.length || !paineis.length) return;

  botoes.forEach((botao) => {
    botao.addEventListener('click', () => {
      const alvo = botao.dataset.painel;

      botoes.forEach((b) => b.setAttribute('aria-selected', String(b === botao)));

      paineis.forEach((painel) => {
        painel.classList.toggle('painel-publico--ativo', painel.id === alvo);
      });
    });
  });
}
