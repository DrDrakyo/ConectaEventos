/* ==========================================================================
   TELA: PESQUISA POR CATEGORIA (Módulo Visitante)
   JavaScript específico desta tela: renderiza as 17 categorias de serviço
   (tabela SERVICO) e leva o visitante para a Lista de Prestadores já
   filtrada pela categoria escolhida. Menu mobile tratado em common.js.
   ========================================================================== */

const CATEGORIAS = [
  'Fotografia', 'Filmagem', 'Decoração', 'Buffet', 'Cerimonial', 'Sonorização',
  'Iluminação', 'Segurança', 'Recepção', 'Produção de eventos', 'Bartender',
  'DJ', 'Banda', 'Mestre de cerimônias', 'Locução', 'Assessoria', 'Atrações artísticas',
];

/* Um ícone simples por categoria (SVG em linha, sem biblioteca externa) */
const ICONES_CATEGORIA = {
  'Fotografia': '<circle cx="12" cy="13" r="3.2"/><path d="M4 8h3l1.5-2h7L17 8h3a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V9a1 1 0 0 1 1-1z"/>',
  'Filmagem': '<rect x="3" y="6" width="12" height="12" rx="2"/><path d="M15 10l6-3v10l-6-3z"/>',
  'Decoração': '<path d="M12 3v6"/><circle cx="12" cy="13" r="4"/><path d="M9 21h6l-1-4H10z"/>',
  'Buffet': '<path d="M6 3v8a3 3 0 0 0 6 0V3"/><path d="M9 11v10"/><path d="M17 3c-1.5 0-3 1.5-3 4s1.5 4 3 4 1-2 1-4-1-4-1-4z"/><path d="M17 11v10"/>',
  'Cerimonial': '<path d="M12 3l2.5 5 5.5.8-4 3.9.9 5.5L12 15.9 7.1 18.2l.9-5.5-4-3.9 5.5-.8z"/>',
  'Sonorização': '<rect x="7" y="3" width="10" height="18" rx="5"/><circle cx="12" cy="8" r="1.5"/><circle cx="12" cy="15" r="1.5"/>',
  'Iluminação': '<circle cx="12" cy="10" r="5"/><path d="M9.5 21h5M10 18h4"/><path d="M12 2v1"/>',
  'Segurança': '<path d="M12 3l7 3v6c0 4.5-3 7.5-7 9-4-1.5-7-4.5-7-9V6z"/>',
  'Recepção': '<circle cx="9" cy="8" r="3"/><path d="M3 20c0-3 3-5 6-5s6 2 6 5"/><circle cx="18" cy="9" r="2.3"/><path d="M15.5 20c.3-2 2-3.5 4-3.7"/>',
  'Produção de eventos': '<rect x="5" y="4" width="14" height="17" rx="2"/><path d="M9 2v4M15 2v4M5 10h14"/>',
  'Bartender': '<path d="M5 4h14l-7 9z"/><path d="M12 13v7M9 20h6"/>',
  'DJ': '<circle cx="12" cy="12" r="8"/><circle cx="12" cy="12" r="2"/><path d="M2 12h4M18 12h4"/>',
  'Banda': '<circle cx="7" cy="17" r="3"/><path d="M10 17V4l8-2v13"/><circle cx="18" cy="15" r="3"/>',
  'Mestre de cerimônias': '<rect x="9" y="3" width="6" height="11" rx="3"/><path d="M6 11a6 6 0 0 0 12 0M12 17v4M9 21h6"/>',
  'Locução': '<path d="M4 10v4h4l5 4V6l-5 4H4z"/><path d="M17 9a4 4 0 0 1 0 6"/>',
  'Assessoria': '<rect x="4" y="7" width="16" height="12" rx="2"/><path d="M9 7V5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2"/>',
  'Atrações artísticas': '<path d="M12 2.5l2.9 6 6.6.9-4.8 4.6 1.1 6.5L12 17.6l-5.8 3-.9-6.5-4.8-4.6 6.6-.9z"/>',
};

document.addEventListener('DOMContentLoaded', () => {
  renderizarCategorias();
});

function renderizarCategorias() {
  const grade = document.querySelector('#grade-categorias-completa');
  if (!grade) return;

  grade.innerHTML = CATEGORIAS.map((categoria) => `
    <a class="categoria-card-grande card" href="lista-prestadores.html?categoria=${encodeURIComponent(categoria)}">
      <span class="categoria-card-grande__icone">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          ${ICONES_CATEGORIA[categoria]}
        </svg>
      </span>
      <span class="categoria-card-grande__nome">${categoria}</span>
    </a>
  `).join('');
}
