/* ==========================================================================
   TELA: AVALIAR PRESTADOR (Módulo Contratante)
   JavaScript específico desta tela: seletor de nota por estrelas, upload
   de até 2 imagens e envio da avaliação (ava_nota, ava_comentario,
   ava_imagem1, ava_imagem2 na tabela CONTRATACAO). Se a contratação já
   tiver sido avaliada, o formulário é bloqueado e mostra a avaliação atual.
   ========================================================================== */

/* Dados de exemplo — simulam a CONTRATACAO selecionada. Quando ava_nota já
   existe, a tela entra em modo somente leitura. */
const CONTRATACAO_PARA_AVALIAR = {
  id_contratacao: 1022,
  prestador: 'DJ Marcos Ferreira',
  servico: 'Som e DJ para festa de 6h',
  ava_nota: null,
  ava_comentario: null,
};

let notaSelecionada = CONTRATACAO_PARA_AVALIAR.ava_nota || 0;

document.addEventListener('DOMContentLoaded', () => {
  document.querySelector('#resumo-titulo').textContent = CONTRATACAO_PARA_AVALIAR.servico;
  document.querySelector('#resumo-detalhe').textContent = CONTRATACAO_PARA_AVALIAR.prestador;

  if (CONTRATACAO_PARA_AVALIAR.ava_nota) {
    exibirModoJaAvaliado();
    return;
  }

  inicializarSeletorNota();
  inicializarUploads();
  inicializarEnvio();
});

function inicializarSeletorNota() {
  const botoes = document.querySelectorAll('#seletor-nota button');

  botoes.forEach((botao) => {
    botao.addEventListener('click', () => {
      notaSelecionada = Number(botao.dataset.nota);
      atualizarEstrelasSelecionadas();
    });
  });
}

function atualizarEstrelasSelecionadas() {
  document.querySelectorAll('#seletor-nota button').forEach((botao) => {
    const nota = Number(botao.dataset.nota);
    botao.classList.toggle('selecionada', nota <= notaSelecionada);
  });
  document.querySelector('#campo-nota-erro').style.display = notaSelecionada ? 'none' : 'block';
}

function inicializarUploads() {
  document.querySelectorAll('.campo-upload').forEach((campo) => {
    const input = campo.querySelector('input[type="file"]');
    campo.addEventListener('click', () => input.click());
    input.addEventListener('change', () => {
      const legenda = campo.querySelector('span');
      if (input.files.length > 0) {
        legenda.textContent = input.files[0].name;
        campo.classList.add('tem-arquivo');
      } else {
        legenda.textContent = campo.dataset.textoPadrao;
        campo.classList.remove('tem-arquivo');
      }
    });
  });
}

function inicializarEnvio() {
  const formulario = document.querySelector('#formulario-avaliacao');
  const mensagem = document.querySelector('#mensagem-feedback');

  formulario.addEventListener('submit', (evento) => {
    evento.preventDefault();

    mensagem.classList.remove('mensagem-feedback--sucesso', 'mensagem-feedback--erro');

    if (!notaSelecionada) {
      document.querySelector('#campo-nota-erro').style.display = 'block';
      mensagem.textContent = 'Selecione uma nota antes de enviar.';
      mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
      return;
    }

    // Em produção: gravar ava_nota, ava_comentario, ava_imagem1 e
    // ava_imagem2 no registro correspondente da tabela CONTRATACAO.
    mensagem.textContent = 'Avaliação enviada com sucesso! Redirecionando para o histórico de contratações...';
    mensagem.classList.add('mensagem-feedback--sucesso', 'mensagem-feedback--visivel');
    formulario.querySelector('button[type="submit"]').setAttribute('disabled', 'true');

    setTimeout(() => {
      window.location.href = 'historico-contratacoes.html';
    }, 1800);
  });
}

function exibirModoJaAvaliado() {
  document.querySelector('#formulario-avaliacao').style.display = 'none';
  const aviso = document.querySelector('#aviso-ja-avaliado');
  aviso.style.display = 'block';
  aviso.innerHTML = `
    Esta contratação já foi avaliada com nota <strong>${CONTRATACAO_PARA_AVALIAR.ava_nota}/5</strong>:<br>
    "${CONTRATACAO_PARA_AVALIAR.ava_comentario}"
  `;
}
