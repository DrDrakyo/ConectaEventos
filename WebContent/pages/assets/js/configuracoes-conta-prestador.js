/* ==========================================================================
   TELA: CONFIGURAÇÕES DA CONTA (Módulo Prestador)
   JavaScript específico desta tela: altera a senha (senha_prestador) e
   permite desativar a conta (situacao) da tabela PRESTADOR.
   ========================================================================== */

const SENHA_ATUAL_SIMULADA = 'senha123';

document.addEventListener('DOMContentLoaded', () => {
  inicializarAlterarSenha();
  inicializarDesativarConta();
});

function inicializarAlterarSenha() {
  const formulario = document.querySelector('#formulario-senha');
  const mensagem = document.querySelector('#mensagem-feedback-senha');

  formulario.addEventListener('submit', (evento) => {
    evento.preventDefault();

    const senhaAtual = document.querySelector('#campo-senha-atual').value;
    const novaSenha = document.querySelector('#campo-senha-nova').value;
    const confirmacao = document.querySelector('#campo-senha-confirmacao').value;

    mensagem.classList.remove('mensagem-feedback--sucesso', 'mensagem-feedback--erro');

    if (senhaAtual !== SENHA_ATUAL_SIMULADA) {
      mensagem.textContent = 'Senha atual incorreta.';
      mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
      return;
    }

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

    // Em produção: atualizar o campo senha_prestador do registro
    // autenticado na tabela PRESTADOR (com o devido hash de senha).
    mensagem.textContent = 'Senha alterada com sucesso!';
    mensagem.classList.add('mensagem-feedback--sucesso', 'mensagem-feedback--visivel');
    formulario.reset();
  });
}

function inicializarDesativarConta() {
  const botao = document.querySelector('#botao-desativar-conta');
  const mensagem = document.querySelector('#mensagem-feedback-conta');

  botao.addEventListener('click', () => {
    const confirmar = window.confirm('Tem certeza que deseja desativar sua conta? Seu perfil deixará de ficar visível para novos contratantes.');
    if (!confirmar) return;

    // Em produção: atualizar o campo situacao do registro autenticado
    // na tabela PRESTADOR para "inativo".
    mensagem.textContent = 'Sua conta foi desativada com sucesso.';
    mensagem.classList.remove('mensagem-feedback--erro');
    mensagem.classList.add('mensagem-feedback--sucesso', 'mensagem-feedback--visivel');
    botao.setAttribute('disabled', 'true');
  });
}
