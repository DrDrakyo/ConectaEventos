/* ==========================================================================
   TELA: LOGIN (Módulo Visitante)
   JavaScript específico desta tela: alterna entre "Sou Contratante" e
   "Sou Prestador" e valida o formulário antes do envio. A autenticação
   real consultaria CONTRATANTE ou PRESTADOR conforme o tipo selecionado.
   Menu mobile tratado em common.js.
   ========================================================================== */

let tipoUsuarioSelecionado = 'contratante';

document.addEventListener('DOMContentLoaded', () => {
  inicializarAlternadorTipoUsuario();
  document.querySelector('#formulario-login').addEventListener('submit', tratarEnvioDoFormulario);
});

function inicializarAlternadorTipoUsuario() {
  const botoes = document.querySelectorAll('.seletor-cadastro__item');

  botoes.forEach((botao) => {
    botao.addEventListener('click', () => {
      botoes.forEach((b) => {
        const estaSelecionado = b === botao;
        b.setAttribute('aria-selected', String(estaSelecionado));
        b.classList.toggle('seletor-cadastro__item--ativo', estaSelecionado);
      });
      tipoUsuarioSelecionado = botao.dataset.tipo;
    });
  });
}

function tratarEnvioDoFormulario(evento) {
  evento.preventDefault();
  const mensagem = document.querySelector('#mensagem-feedback');

  const emailValido = validarCampo('campo-email', 'email');
  const senhaValida = validarCampo('campo-senha');

  if (!emailValido || !senhaValida) {
    mensagem.textContent = 'Informe um e-mail e senha válidos.';
    mensagem.className = 'mensagem-feedback mensagem-feedback--erro mensagem-feedback--visivel';
    return;
  }

  // Protótipo sem autenticação real — apenas confirma visualmente o envio.
  // Em produção, consultaria email_contratante/senha_contratante (CONTRATANTE)
  // ou email_prestador/senha_prestador (PRESTADOR), conforme tipoUsuarioSelecionado.
  mensagem.textContent = `Login efetuado como ${tipoUsuarioSelecionado}. Redirecionando...`;
  mensagem.className = 'mensagem-feedback mensagem-feedback--sucesso mensagem-feedback--visivel';

  setTimeout(() => {
    if (tipoUsuarioSelecionado === 'prestador') {
      window.location.href = '../prestador/dashboard-prestador.html';
    } else {
      window.location.href = '../contratante/dashboard-contratante.html';
    }
  }, 1200);
}

function validarCampo(id, tipo) {
  const elemento = document.querySelector(`#${id}`);
  const grupo = elemento.closest('.campo');
  const valor = elemento.value.trim();

  let valido = valor.length > 0;
  if (valido && tipo === 'email') valido = /\S+@\S+\.\S+/.test(valor);

  grupo.classList.toggle('campo--invalido', !valido);
  return valido;
}
