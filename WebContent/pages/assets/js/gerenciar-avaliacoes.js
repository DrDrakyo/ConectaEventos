/* ==========================================================================
   TELA: GERENCIAR AVALIAÇÕES (Módulo Administrador)
   JavaScript específico desta tela: lista as avaliações registradas em
   CONTRATACAO (ava_nota, ava_comentario), com filtro por nota e por
   prestador. A moderação disponível é remover a avaliação, o que limpa
   os campos ava_* do registro (não existe tabela própria de avaliação).
   ========================================================================== */

let AVALIACOES = [
  { id_contratacao: 1019, contratante: 'Fernanda Lopes', prestador: 'Studio Lente Viva', data: '2026-04-20', nota: 5, comentario: 'Trabalho impecável! As fotos ficaram lindas e a entrega foi rápida.' },
  { id_contratacao: 1005, contratante: 'Rodrigo Nascimento', prestador: 'Studio Lente Viva', data: '2026-03-05', nota: 5, comentario: 'Super pontual e profissional, recomendo muito.' },
  { id_contratacao: 1022, contratante: 'Maria Costa', prestador: 'DJ Marcos Ferreira', data: '2026-04-11', nota: 5, comentario: 'Animou a festa toda, ótima seleção musical.' },
  { id_contratacao: 1015, contratante: 'João Almeida', prestador: 'Cerimonial Elo Perfeito', data: '2026-03-02', nota: 4, comentario: 'Cerimônia muito bem conduzida, apenas um pequeno atraso no início.' },
];

document.addEventListener('DOMContentLoaded', () => {
  preencherFiltroPrestador();
  document.querySelector('#filtro-nota').addEventListener('change', aplicarFiltros);
  document.querySelector('#filtro-prestador').addEventListener('change', aplicarFiltros);
  aplicarFiltros();
});

function preencherFiltroPrestador() {
  const select = document.querySelector('#filtro-prestador');
  const prestadoresUnicos = [...new Set(AVALIACOES.map((a) => a.prestador))];
  select.innerHTML = '<option value="">Todos</option>' +
    prestadoresUnicos.map((nome) => `<option value="${nome}">${nome}</option>`).join('');
}

function aplicarFiltros() {
  const nota = document.querySelector('#filtro-nota').value;
  const prestador = document.querySelector('#filtro-prestador').value;

  const filtradas = AVALIACOES.filter((a) =>
    (!nota || String(a.nota) === nota) &&
    (!prestador || a.prestador === prestador)
  );

  renderizarLista(filtradas);
}

function renderizarLista(lista) {
  const container = document.querySelector('#lista-avaliacoes-admin');
  const vazio = document.querySelector('#estado-vazio');

  if (lista.length === 0) {
    container.innerHTML = '';
    vazio.classList.add('estado-vazio--visivel');
    return;
  }

  vazio.classList.remove('estado-vazio--visivel');

  container.innerHTML = lista.map((a) => `
    <article class="card-avaliacao-admin card" data-id="${a.id_contratacao}">
      <div class="card-avaliacao-admin__cabecalho">
        <div>
          <div class="card-avaliacao-admin__titulo">${a.prestador}</div>
          <div class="card-avaliacao-admin__legenda">Avaliado por ${a.contratante} · ${formatarData(a.data)} · Contratação #${a.id_contratacao}</div>
        </div>
        <div class="card-avaliacao-admin__estrelas">${gerarEstrelas(a.nota)}</div>
      </div>
      <p class="card-avaliacao-admin__comentario">"${a.comentario}"</p>
      <div class="card-avaliacao-admin__acoes">
        <button type="button" class="botao botao--perigo botao--pequeno" data-acao="remover">Remover Avaliação</button>
      </div>
    </article>
  `).join('');

  container.querySelectorAll('[data-acao="remover"]').forEach((botao) => {
    botao.addEventListener('click', (evento) => removerAvaliacao(evento.target.closest('.card-avaliacao-admin').dataset.id));
  });
}

function removerAvaliacao(idContratacao) {
  const confirmar = window.confirm('Tem certeza que deseja remover esta avaliação? Esta ação não pode ser desfeita.');
  if (!confirmar) return;

  // Em produção: limpar os campos ava_nota, ava_comentario, ava_imagem1
  // e ava_imagem2 do registro correspondente na tabela CONTRATACAO
  // (não existe uma tabela própria de avaliação).
  AVALIACOES = AVALIACOES.filter((a) => String(a.id_contratacao) !== String(idContratacao));
  preencherFiltroPrestador();
  aplicarFiltros();
}

function gerarEstrelas(nota) {
  return Array.from({ length: 5 }, (_, indice) => `
    <svg viewBox="0 0 24 24" style="opacity:${indice < nota ? 1 : 0.25}" aria-hidden="true"><path d="M12 2.5l2.9 6 6.6.9-4.8 4.6 1.1 6.5L12 17.6l-5.8 3-.9-6.5-4.8-4.6 6.6-.9z"/></svg>
  `).join('');
}

function formatarData(dataIso) {
  const [ano, mes, dia] = dataIso.split('-');
  return `${dia}/${mes}/${ano}`;
}
