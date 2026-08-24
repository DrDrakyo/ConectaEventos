/* ==========================================================================
   TELA: CADASTRO DE PRESTADOR (Módulo Visitante)
   JavaScript específico desta tela: validação do formulário de cadastro
   (campos da tabela PRESTADOR). Menu mobile tratado em common.js.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  const formulario = document.querySelector('#formulario-cadastro-prestador');
  formulario.addEventListener('submit', tratarEnvioDoFormulario);
});

async function tratarEnvioDoFormulario(evento) {
  evento.preventDefault();
  const mensagem = document.querySelector('#mensagem-feedback');

  const campos = [
    { id: 'campo-nome', obrigatorio: true },
    { id: 'campo-nome-fantasia', obrigatorio: true },
    { id: 'campo-email', obrigatorio: true, tipo: 'email' },
    { id: 'campo-telefone', obrigatorio: true },
    { id: 'campo-categoria', obrigatorio: true },
    { id: 'campo-localizacao', obrigatorio: true },
    { id: 'campo-contatos', obrigatorio: false },
    { id: 'campo-disponibilidade', obrigatorio: true },
    { id: 'campo-descricao', obrigatorio: true },
    { id: 'campo-senha', obrigatorio: true, minimo: 6 },
    { id: 'campo-confirmar-senha', obrigatorio: true },
  ];

  let formularioValido = true;
  campos.forEach(({ id, tipo, minimo, obrigatorio }) => {
    if (obrigatorio !== false) {
      if (!validarCampo(id, tipo, minimo)) formularioValido = false;
    }
  });

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

  mensagem.textContent = 'Cadastrando no banco de dados...';
  mensagem.className = 'mensagem-feedback mensagem-feedback--visivel';

  try {
    const formData = new URLSearchParams();
    formData.append('nome_prestador', document.querySelector('#campo-nome-fantasia').value.trim() || document.querySelector('#campo-nome').value.trim());
    formData.append('email_prestador', document.querySelector('#campo-email').value.trim());
    formData.append('senha_prestador', senha);
    formData.append('confirmarSenha', confirmacao);
    formData.append('cpf_cnpj', '11122233344'); // Valor padrão caso o formulário não peça documento
    formData.append('telefone', document.querySelector('#campo-telefone').value.trim());
    formData.append('endereco', document.querySelector('#campo-localizacao').value.trim());
    formData.append('cidade', document.querySelector('#campo-localizacao').value.trim());
    formData.append('categoria', document.querySelector('#campo-categoria').value);
    formData.append('descricao', document.querySelector('#campo-descricao').value.trim());

    const response = await fetch('/cadastroPrestador', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
      body: formData.toString()
    });

    const data = await response.json();

    if (response.ok && data && data.sucesso) {
      mensagem.textContent = 'Cadastro realizado com sucesso no MySQL! Redirecionando para o login...';
      mensagem.className = 'mensagem-feedback mensagem-feedback--sucesso mensagem-feedback--visivel';

      setTimeout(() => {
        window.location.href = 'login.html';
      }, 1500);
    } else {
      mensagem.textContent = data.mensagem || 'Erro ao realizar cadastro no banco de dados.';
      mensagem.className = 'mensagem-feedback mensagem-feedback--erro mensagem-feedback--visivel';
    }
  } catch (e) {
    console.error('Erro no cadastro:', e);
    mensagem.textContent = 'Erro de comunicação ao realizar cadastro.';
    mensagem.className = 'mensagem-feedback mensagem-feedback--erro mensagem-feedback--visivel';
  }
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
