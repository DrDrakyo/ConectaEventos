/* ==========================================================================
   TELA: CONFIGURAÇÕES DA CONTA (Módulo Prestador)
   JavaScript específico desta tela: altera a senha (senha_prestador) e
   permite desativar a conta (situacao) da tabela PRESTADOR.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  inicializarAlterarSenha();
  inicializarDesativarConta();
});

function inicializarAlterarSenha() {
  const formulario = document.querySelector('#formulario-senha');
  const mensagem = document.querySelector('#mensagem-feedback-senha');
  if (!formulario) return;

  formulario.addEventListener('submit', async (evento) => {
    evento.preventDefault();

    const senhaAtual = document.querySelector('#campo-senha-atual').value;
    const novaSenha = document.querySelector('#campo-senha-nova').value;
    const confirmacao = document.querySelector('#campo-senha-confirmacao').value;

    mensagem.classList.remove('mensagem-feedback--sucesso', 'mensagem-feedback--erro');

    if (novaSenha.length < 6) {
      mensagem.textContent = 'A nova senha deve ter pelo menos 6 caracteres.';
      mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
      return;
    }

    if (novaSenha !== confirmacao) {
      mensagem.textContent = 'A confirmação não corresponde à nova senha.';
      mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
      return;
    }

    try {
      const formData = new URLSearchParams();
      formData.append('acao', 'alterar_senha');
      formData.append('senhaAtual', senhaAtual);
      formData.append('novaSenha', novaSenha);
      formData.append('confirmarNovaSenha', confirmacao);

      const response = await fetch('/configuracoesContaContratante', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
        body: formData.toString()
      });

      const data = await response.json();

      if (response.ok && data && data.sucesso) {
        mensagem.textContent = 'Senha alterada com sucesso no banco de dados!';
        mensagem.classList.add('mensagem-feedback--sucesso', 'mensagem-feedback--visivel');
        formulario.reset();
      } else {
        mensagem.textContent = data.mensagem || 'Erro ao alterar senha.';
        mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
      }
    } catch (e) {
      console.error('Erro ao alterar senha:', e);
      mensagem.textContent = 'Erro de comunicação ao alterar senha.';
      mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
    }
  });
}

function inicializarDesativarConta() {
  const botao = document.querySelector('#botao-desativar-conta');
  const mensagem = document.querySelector('#mensagem-feedback-conta');
  if (!botao) return;

  botao.addEventListener('click', async () => {
    const confirmar = window.confirm('Tem certeza que deseja desativar sua conta? Seu perfil deixará de ficar visível.');
    if (!confirmar) return;

    try {
      const formData = new URLSearchParams();
      formData.append('acao', 'desativar');

      const response = await fetch('/configuracoesContaContratante', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
        body: formData.toString()
      });

      const data = await response.json();
      if (response.ok && data && data.sucesso) {
        mensagem.textContent = 'Sua conta foi desativada com sucesso no banco de dados.';
        mensagem.classList.remove('mensagem-feedback--erro');
        mensagem.classList.add('mensagem-feedback--sucesso', 'mensagem-feedback--visivel');
        botao.setAttribute('disabled', 'true');
      } else {
        mensagem.textContent = data.mensagem || 'Erro ao desativar conta.';
        mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
      }
    } catch (e) {
      console.error('Erro ao desativar conta:', e);
    }
  });
}
