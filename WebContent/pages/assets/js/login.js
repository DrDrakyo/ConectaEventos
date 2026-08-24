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

async function tratarEnvioDoFormulario(evento) {
  evento.preventDefault();
  const mensagem = document.querySelector('#mensagem-feedback');

  const emailValido = validarCampo('campo-email', 'email');
  const senhaValida = validarCampo('campo-senha');

  if (!emailValido || !senhaValida) {
    mensagem.textContent = 'Informe um e-mail e senha válidos.';
    mensagem.className = 'mensagem-feedback mensagem-feedback--erro mensagem-feedback--visivel';
    return;
  }

  const email = document.querySelector('#campo-email').value.trim();
  const senha = document.querySelector('#campo-senha').value.trim();

  mensagem.textContent = 'Autenticando no servidor...';
  mensagem.className = 'mensagem-feedback mensagem-feedback--visivel';

  try {
    const formData = new URLSearchParams();
    formData.append('email', email);
    formData.append('senha', senha);

    const response = await fetch('/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
      body: formData.toString()
    });

    const data = await response.json();

    if (response.ok && data && data.sucesso) {
      mensagem.textContent = `Login efetuado com sucesso! Redirecionando...`;
      mensagem.className = 'mensagem-feedback mensagem-feedback--sucesso mensagem-feedback--visivel';

      setTimeout(() => {
        if (data.tipo === 'prestador') {
          window.location.href = '../prestador/dashboard-prestador.html';
        } else if (data.tipo === 'administrador') {
          window.location.href = '../administrador/dashboard-admin.html';
        } else {
          window.location.href = '../contratante/dashboard-contratante.html';
        }
      }, 1000);
    } else {
      mensagem.textContent = data.mensagem || 'E-mail ou senha inválidos no banco de dados.';
      mensagem.className = 'mensagem-feedback mensagem-feedback--erro mensagem-feedback--visivel';
    }
  } catch (erro) {
    console.error('Erro na autenticação:', erro);
    mensagem.textContent = 'Erro de conexão com o servidor de autenticação.';
    mensagem.className = 'mensagem-feedback mensagem-feedback--erro mensagem-feedback--visivel';
  }
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
