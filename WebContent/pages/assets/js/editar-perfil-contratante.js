/* ==========================================================================
   TELA: EDITAR PERFIL (Módulo Contratante)
   JavaScript puro, específico desta tela: pré-preenche o formulário com os
   dados atuais e valida antes do envio (campos da tabela CONTRATANTE).
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  preencherFormulario();
  inicializarValidacao();
});

/* Dados de exemplo — simulam o registro autenticado da tabela CONTRATANTE. */
const CONTRATANTE_LOGADO = {
  nome_contratante: 'Maria Costa',
  email_contratante: 'maria.costa@email.com',
  telefone: '(71) 99876-5432',
  endereco: 'Rua das Flores, 120',
  cidade: 'Salvador - BA',
  cpf_cnpj: '123.456.789-00',
};

function preencherFormulario() {
  document.querySelector('#campo-nome').value = CONTRATANTE_LOGADO.nome_contratante;
  document.querySelector('#campo-email').value = CONTRATANTE_LOGADO.email_contratante;
  document.querySelector('#campo-telefone').value = CONTRATANTE_LOGADO.telefone;
  document.querySelector('#campo-endereco').value = CONTRATANTE_LOGADO.endereco;
  document.querySelector('#campo-cidade').value = CONTRATANTE_LOGADO.cidade;
  // cpf_cnpj é somente leitura: identifica o cadastro e não é editável.
  document.querySelector('#campo-cpf-cnpj').value = CONTRATANTE_LOGADO.cpf_cnpj;
}

function inicializarValidacao() {
  const formulario = document.querySelector('#formulario-editar-perfil');
  const mensagem = document.querySelector('#mensagem-feedback');
  if (!formulario) return;

  formulario.addEventListener('submit', (evento) => {
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

    // Em produção: enviar os campos alterados para atualizar o registro
    // correspondente na tabela CONTRATANTE.
    mensagem.textContent = 'Dados atualizados com sucesso! Redirecionando para seu perfil...';
    mensagem.classList.add('mensagem-feedback--sucesso', 'mensagem-feedback--visivel');

    setTimeout(() => {
      window.location.href = 'meu-perfil-contratante.html';
    }, 1800);
  });
}
