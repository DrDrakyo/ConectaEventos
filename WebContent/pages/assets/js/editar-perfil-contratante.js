/* ==========================================================================
   TELA: EDITAR PERFIL (Módulo Contratante)
   JavaScript puro, específico desta tela: pré-preenche o formulário com os
   dados atuais e valida antes do envio (campos da tabela CONTRATANTE).
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  preencherFormulario();
  inicializarValidacao();
});

async function preencherFormulario() {
  try {
    const response = await fetch('/editarPerfilContratante');
    if (response.ok) {
      const data = await response.json();
      if (data && data.sucesso && data.perfil) {
        const p = data.perfil;
        if (p.nome_contratante) document.querySelector('#campo-nome').value = p.nome_contratante;
        if (p.email_contratante) document.querySelector('#campo-email').value = p.email_contratante;
        if (p.telefone) document.querySelector('#campo-telefone').value = p.telefone;
        if (p.endereco) document.querySelector('#campo-endereco').value = p.endereco;
        if (p.cidade) document.querySelector('#campo-cidade').value = p.cidade;
        if (p.cpf_cnpj) document.querySelector('#campo-cpf-cnpj').value = p.cpf_cnpj;
      }
    }
  } catch (e) {
    console.warn('Erro ao carregar dados do perfil:', e);
  }
}

function inicializarValidacao() {
  const formulario = document.querySelector('#formulario-editar-perfil');
  const mensagem = document.querySelector('#mensagem-feedback');
  if (!formulario) return;

  formulario.addEventListener('submit', async (evento) => {
    evento.preventDefault();

    const campos = ['nome', 'email', 'telefone', 'endereco', 'cidade'];
    let valido = true;

    campos.forEach((nome) => {
      const input = document.querySelector(`#campo-${nome}`);
      const container = input.closest('.campo');
      if (!input.value.trim()) {
        container.classList.add('campo--invalido');
        valido = false;
      } else {
        container.classList.remove('campo--invalido');
      }
    });

    const email = document.querySelector('#campo-email');
    if (email.value && !email.value.includes('@')) {
      email.closest('.campo').classList.add('campo--invalido');
      valido = false;
    }

    mensagem.classList.remove('mensagem-feedback--sucesso', 'mensagem-feedback--erro');
    if (!valido) {
      mensagem.textContent = 'Preencha corretamente todos os campos obrigatórios.';
      mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
      return;
    }

    try {
      const formData = new URLSearchParams();
      formData.append('nome', document.querySelector('#campo-nome').value.trim());
      formData.append('email', document.querySelector('#campo-email').value.trim());
      formData.append('telefone', document.querySelector('#campo-telefone').value.trim());
      formData.append('endereco', document.querySelector('#campo-endereco').value.trim());
      formData.append('cidade', document.querySelector('#campo-cidade').value.trim());

      const response = await fetch('/editarPerfilContratante', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
        body: formData.toString()
      });

      const data = await response.json();
      if (response.ok && data && data.sucesso) {
        mensagem.textContent = 'Dados atualizados com sucesso no banco de dados! Redirecionando...';
        mensagem.classList.add('mensagem-feedback--sucesso', 'mensagem-feedback--visivel');

        setTimeout(() => {
          window.location.href = 'meu-perfil-contratante.html';
        }, 1500);
      } else {
        mensagem.textContent = data.mensagem || 'Erro ao atualizar dados.';
        mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
      }
    } catch (e) {
      console.error('Erro na atualização:', e);
      mensagem.textContent = 'Erro de comunicação ao atualizar perfil.';
      mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
    }
  });
}
