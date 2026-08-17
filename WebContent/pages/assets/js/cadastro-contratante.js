/* ==========================================================================
   TELA: CADASTRO DE CONTRATANTE (Módulo Visitante)
   JavaScript específico desta tela: validação do formulário de cadastro
   (campos da tabela CONTRATANTE). Menu mobile tratado em common.js.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  const formulario = document.querySelector('#formulario-cadastro-contratante');
  formulario.addEventListener('submit', tratarEnvioDoFormulario);
});

function tratarEnvioDoFormulario(evento) {
  evento.preventDefault();
  const formulario = evento.target;
  const mensagem = document.querySelector('#mensagem-feedback');

  const campos = [
    { id: 'campo-cpf-cnpj', obrigatorio: true },
    { id: 'campo-nome', obrigatorio: true },
    { id: 'campo-email', obrigatorio: true, tipo: 'email' },
    { id: 'campo-telefone', obrigatorio: true },
    { id: 'campo-endereco', obrigatorio: true },
    { id: 'campo-cidade', obrigatorio: true },
    { id: 'campo-senha', obrigatorio: true, minimo: 6 },
    { id: 'campo-confirmar-senha', obrigatorio: true },
  ];

  let formularioValido = true;

  campos.forEach(({ id, tipo, minimo }) => {
    const valido = validarCampo(id, tipo, minimo);
    if (!valido) formularioValido = false;
  });

  // Validação específica: confirmação de senha precisa ser igual à senha
  const senha = document.querySelector('#campo-senha').value;
  const confirmacao = document.querySelector('#campo-confirmar-senha').value;
  if (senha && confirmacao && senha !== confirmacao) {
    marcarCampoComoInvalido('campo-confirmar-senha', 'As senhas não coincidem.');
    formularioValido = false;
  }

  if (!formularioValido) {
    mensagem.textContent = 'Preencha todos os campos obrigatórios corretamente.';
    mensagem.className = 'mensagem-feedback mensagem-feedback--erro mensagem-feedback--visivel';
    return;
  }

  // Neste protótipo não há persistência real em banco de dados —
  // apenas a confirmação visual do cadastro (campos data_cadastro e
  // situacao seriam preenchidos automaticamente pelo sistema).
  mensagem.textContent = 'Cadastro realizado com sucesso! Redirecionando para o login...';
  mensagem.className = 'mensagem-feedback mensagem-feedback--sucesso mensagem-feedback--visivel';

  setTimeout(() => {
    window.location.href = 'login.html';
  }, 1800);
}

function validarCampo(id, tipo, minimo) {
  const elemento = document.querySelector(`#${id}`);
  const grupo = elemento.closest('.campo');
  const valor = elemento.value.trim();

  let valido = valor.length > 0;
  if (valido && tipo === 'email') valido = /\S+@\S+\.\S+/.test(valor);
  if (valido && minimo) valido = valor.length >= minimo;

  grupo.classList.toggle('campo--invalido', !valido);
  return valido;
}

function marcarCampoComoInvalido(id, textoErro) {
  const elemento = document.querySelector(`#${id}`);
  const grupo = elemento.closest('.campo');
  grupo.classList.add('campo--invalido');
  grupo.querySelector('.campo__erro').textContent = textoErro;
}
