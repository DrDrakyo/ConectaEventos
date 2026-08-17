/* ==========================================================================
   TELA: RECUPERAÇÃO DE SENHA (Módulo Visitante)
   JavaScript específico desta tela: valida o e-mail informado e exibe a
   mensagem de confirmação de envio. Menu mobile tratado em common.js.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  document.querySelector('#formulario-recuperar-senha').addEventListener('submit', tratarEnvioDoFormulario);
});

function tratarEnvioDoFormulario(evento) {
  evento.preventDefault();
  const mensagem = document.querySelector('#mensagem-feedback');
  const campoEmail = document.querySelector('#campo-email');
  const grupo = campoEmail.closest('.campo');
  const valido = /\S+@\S+\.\S+/.test(campoEmail.value.trim());

  grupo.classList.toggle('campo--invalido', !valido);

  if (!valido) {
    mensagem.textContent = 'Informe um e-mail válido para continuar.';
    mensagem.className = 'mensagem-feedback mensagem-feedback--erro mensagem-feedback--visivel';
    return;
  }

  // Protótipo sem envio real — apenas confirma visualmente a solicitação.
  // Em produção, o e-mail seria validado contra email_contratante ou
  // email_prestador antes do envio das instruções de redefinição.
  mensagem.textContent = 'Se o e-mail informado estiver cadastrado, você receberá as instruções para redefinir sua senha. Redirecionando para o login...';
  mensagem.className = 'mensagem-feedback mensagem-feedback--sucesso mensagem-feedback--visivel';
  document.querySelector('#formulario-recuperar-senha').reset();

  setTimeout(() => {
    window.location.href = 'login.html';
  }, 2000);
}
