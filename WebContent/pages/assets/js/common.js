/* ==========================================================================
   CONECTA EVENTOS — COMMON.JS
   Script único e compartilhado por TODAS as telas do sistema.

   Arquitetura do projeto (padrão obrigatório para toda nova tela):
     /css
       variables.css   → tokens de design (cores, fontes, espaçamentos)
       global.css      → reset + componentes reutilizáveis (header, footer,
                          botões, cards, badges)
       <tela>.css      → estilos exclusivos de cada tela
     /js
       common.js       → ESTE arquivo. Tudo que é reutilizável entre telas
                          (menu responsivo, navbar, footer, utilidades,
                          funções compartilhadas) vive exclusivamente aqui.
       <tela>.js       → APENAS o comportamento específico daquela tela.
                          Nunca replicar aqui algo que já exista em common.js.
     <tela>.html       → um arquivo HTML por tela, incluindo sempre:
                          variables.css, global.css, <tela>.css e depois
                          common.js, <tela>.js (nessa ordem).

   Toda página inclui common.js ANTES do seu JS específico.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  inicializarMenuMobile();
  inicializarMenuUsuario();
});

/* Abre/fecha o menu principal em telas pequenas (mesmo comportamento em
   todas as telas: header sticky com botão hambúrguer). */
function inicializarMenuMobile() {
  const botao = document.querySelector('.botao-menu-mobile');
  const menu = document.querySelector('.menu-principal');

  if (!botao || !menu) return;

  botao.addEventListener('click', () => {
    const aberto = menu.classList.toggle('menu-principal--aberto');
    botao.setAttribute('aria-expanded', String(aberto));
  });
}

/* Abre/fecha o menu suspenso do usuário logado (Contratante, Prestador ou
   Administrador). Reutilizado por todas as telas da área logada — cada
   tela só precisa incluir o markup com a classe .cabecalho__usuario. */
function inicializarMenuUsuario() {
  const container = document.querySelector('.cabecalho__usuario');
  if (!container) return;

  const botao = container.querySelector('.botao-usuario');
  if (!botao) return;

  botao.addEventListener('click', (evento) => {
    evento.stopPropagation();
    const aberto = container.classList.toggle('cabecalho__usuario--aberto');
    botao.setAttribute('aria-expanded', String(aberto));
  });

  // Fecha o menu ao clicar fora dele
  document.addEventListener('click', (evento) => {
    if (!container.contains(evento.target)) {
      container.classList.remove('cabecalho__usuario--aberto');
      botao.setAttribute('aria-expanded', 'false');
    }
  });

  // Fecha o menu com a tecla Esc
  document.addEventListener('keydown', (evento) => {
    if (evento.key === 'Escape') {
      container.classList.remove('cabecalho__usuario--aberto');
      botao.setAttribute('aria-expanded', 'false');
    }
  });
}
