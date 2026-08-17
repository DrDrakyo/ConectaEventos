/* ==========================================================================
   TELA: AVALIAÇÕES RECEBIDAS (Módulo Prestador)
   JavaScript específico desta tela: exibe as avaliações registradas em
   CONTRATACAO (ava_nota, ava_comentario) vinculadas, via ITEM_CONTRA e
   PORTFOLIO, a este prestador — somente leitura.
   ========================================================================== */

const AVALIACOES = [
  { contratante: 'Fernanda Lopes', servico: 'Cobertura de aniversário', data: '2026-04-20', nota: 5, comentario: 'Trabalho impecável! As fotos ficaram lindas e a entrega foi rápida.' },
  { contratante: 'Rodrigo Nascimento', servico: 'Formatura Direito UFBA', data: '2026-03-05', nota: 5, comentario: 'Super pontual e profissional, recomendo muito.' },
  { contratante: 'Juliana Prado', servico: 'Ensaio de gestante', data: '2026-01-18', nota: 4, comentario: 'Ótimas fotos, só demorou um pouco mais que o combinado para entregar.' },
];

document.addEventListener('DOMContentLoaded', () => {
  renderizarResumo();
  renderizarLista();
});

function renderizarResumo() {
  const total = AVALIACOES.length;
  const media = total ? AVALIACOES.reduce((soma, a) => soma + a.nota, 0) / total : 0;

  document.querySelector('#resumo-nota-media').textContent = media.toFixed(1);
  document.querySelector('#resumo-total').textContent = `Baseado em ${total} avaliaç${total === 1 ? 'ão' : 'ões'}`;

  const contagemPorNota = [5, 4, 3, 2, 1].map((nota) => AVALIACOES.filter((a) => a.nota === nota).length);
  const container = document.querySelector('#distribuicao-notas');

  container.innerHTML = [5, 4, 3, 2, 1].map((nota, indice) => {
    const quantidade = contagemPorNota[indice];
    const percentual = total ? Math.round((quantidade / total) * 100) : 0;
    return `
      <div class="linha-distribuicao">
        <span>${nota} ★</span>
        <span class="linha-distribuicao__barra-fundo"><span class="linha-distribuicao__barra" style="width:${percentual}%"></span></span>
        <span>${quantidade}</span>
      </div>
    `;
  }).join('');
}

function renderizarLista() {
  const lista = document.querySelector('#lista-avaliacoes');
  const vazio = document.querySelector('#estado-vazio');

  if (AVALIACOES.length === 0) {
    vazio.classList.add('estado-vazio--visivel');
    return;
  }

  lista.innerHTML = AVALIACOES.map((a) => `
    <article class="card-avaliacao card">
      <div class="card-avaliacao__cabecalho">
        <span class="card-avaliacao__contratante">${a.contratante}</span>
        <span class="card-avaliacao__data">${formatarData(a.data)}</span>
      </div>
      <span class="card-avaliacao__servico">${a.servico}</span>
      <div class="card-avaliacao__estrelas">${gerarEstrelas(a.nota)}</div>
      <p class="card-avaliacao__comentario">"${a.comentario}"</p>
    </article>
  `).join('');
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
