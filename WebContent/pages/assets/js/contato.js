/* ==========================================================================
   TELA: CONTATO (Módulo Visitante)
   JavaScript específico desta tela: valida e trata o envio do formulário
   de contato. Não existe tabela de mensagens no banco de dados do
   projeto, então este envio é apenas simulado nesta tela (sem
   persistência). Menu mobile tratado em common.js.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  document.querySelector('#formulario-contato').addEventListener('submit', tratarEnvioDoFormulario);
});

function tratarEnvioDoFormulario(evento) {
  evento.preventDefault();
  const mensagem = document.querySelector('#mensagem-feedback');

  const camposObrigatorios = ['campo-nome', 'campo-email', 'campo-assunto', 'campo-mensagem'];
  let valido = true;

  camposObrigatorios.forEach((id) => {
    const elemento = document.querySelector(`#${id}`);
    const grupo = elemento.closest('.campo');
    const preenchido = elemento.value.trim().length > 0;
    grupo.classList.toggle('campo--invalido', !preenchido);
    if (!preenchido) valido = false;
  });

  const campoEmail = document.querySelector('#campo-email');
  const emailValido = /\S+@\S+\.\S+/.test(campoEmail.value.trim());
  campoEmail.closest('.campo').classList.toggle('campo--invalido', !emailValido);
  if (!emailValido) valido = false;

  if (!valido) {
    mensagem.textContent = 'Preencha todos os campos antes de enviar.';
    mensagem.className = 'mensagem-feedback mensagem-feedback--erro mensagem-feedback--visivel';
    return;
  }

  mensagem.textContent = 'Mensagem enviada com sucesso! Em breve entraremos em contato.';
  mensagem.className = 'mensagem-feedback mensagem-feedback--sucesso mensagem-feedback--visivel';
  document.querySelector('#formulario-contato').reset();
}
