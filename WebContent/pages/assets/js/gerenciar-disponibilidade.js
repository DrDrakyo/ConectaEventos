/* ==========================================================================
   TELA: GERENCIAR DISPONIBILIDADE (Módulo Prestador)
   JavaScript específico desta tela: atualiza o campo PRESTADOR.disponibilidade.
   ========================================================================== */

const DISPONIBILIDADE_ATUAL = 'Disponível';

document.addEventListener('DOMContentLoaded', () => {
  const statusEl = document.querySelector('#status-atual-valor');
  statusEl.textContent = DISPONIBILIDADE_ATUAL;
  statusEl.className = `badge ${DISPONIBILIDADE_ATUAL === 'Disponível' ? 'badge--azul' : 'badge--roxo'}`;

  const opcao = document.querySelector(`input[name="disponibilidade"][value="${DISPONIBILIDADE_ATUAL}"]`);
  if (opcao) opcao.checked = true;

  document.querySelector('#formulario-disponibilidade').addEventListener('submit', (evento) => {
    evento.preventDefault();

    const selecionada = document.querySelector('input[name="disponibilidade"]:checked');
    const mensagem = document.querySelector('#mensagem-feedback');

    mensagem.classList.remove('mensagem-feedback--sucesso', 'mensagem-feedback--erro');

    if (!selecionada) {
      mensagem.textContent = 'Selecione uma opção de disponibilidade.';
      mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
      return;
    }

    // Em produção: atualizar o campo disponibilidade do registro
    // autenticado na tabela PRESTADOR.
    mensagem.textContent = `Disponibilidade atualizada para "${selecionada.value}". Redirecionando...`;
    mensagem.classList.add('mensagem-feedback--sucesso', 'mensagem-feedback--visivel');

    setTimeout(() => {
      window.location.href = 'dashboard-prestador.html';
    }, 1800);
  });
});
