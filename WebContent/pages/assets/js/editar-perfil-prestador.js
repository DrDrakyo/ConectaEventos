/* ==========================================================================
   TELA: EDITAR PERFIL (Módulo Prestador)
   JavaScript específico desta tela: pré-preenche o formulário e valida
   antes do envio. O campo "categoria" apenas SELECIONA um tipo já
   cadastrado em SERVICO — o Prestador não cria nem edita categorias.
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  preencherCategorias();
  preencherFormulario();
  inicializarValidacao();
});

const PRESTADOR_LOGADO = {
  nome_prestador: 'Carla Menezes',
  telefone: '(71) 99999-0000',
  nome_fantasia: 'Studio Lente Viva',
  categoria: 'Fotografia',
  descricao: 'Fotografia profissional para casamentos, aniversários e formaturas, com mais de 8 anos de experiência no mercado de eventos em Salvador e região.',
  localizacao: 'Salvador - BA',
  contatos: 'WhatsApp: (71) 99999-0000',
};

/* Simula a leitura do catálogo global da tabela SERVICO (mantido
   exclusivamente pelo Administrador) para popular o select de categoria. */
const CATEGORIAS_SERVICO = [
  'Fotografia', 'Filmagem', 'Decoração', 'Buffet', 'Cerimonial', 'Sonorização',
  'Iluminação', 'Segurança', 'Recepção', 'Produção de eventos', 'Bartender',
  'DJ', 'Banda', 'Mestre de cerimônias', 'Locução', 'Assessoria', 'Atrações artísticas',
];

function preencherCategorias() {
  const select = document.querySelector('#campo-categoria');
  select.innerHTML = CATEGORIAS_SERVICO.map((categoria) => `<option value="${categoria}">${categoria}</option>`).join('');
}

function preencherFormulario() {
  document.querySelector('#campo-nome').value = PRESTADOR_LOGADO.nome_prestador;
  document.querySelector('#campo-telefone').value = PRESTADOR_LOGADO.telefone;
  document.querySelector('#campo-nome-fantasia').value = PRESTADOR_LOGADO.nome_fantasia;
  document.querySelector('#campo-categoria').value = PRESTADOR_LOGADO.categoria;
  document.querySelector('#campo-descricao').value = PRESTADOR_LOGADO.descricao;
  document.querySelector('#campo-localizacao').value = PRESTADOR_LOGADO.localizacao;
  document.querySelector('#campo-contatos').value = PRESTADOR_LOGADO.contatos;
}

function inicializarValidacao() {
  const formulario = document.querySelector('#formulario-editar-perfil');
  const mensagem = document.querySelector('#mensagem-feedback');

  formulario.addEventListener('submit', (evento) => {
    evento.preventDefault();

    const camposObrigatorios = ['nome', 'telefone', 'nome-fantasia', 'categoria', 'descricao', 'localizacao'];
    let valido = true;

    camposObrigatorios.forEach((nome) => {
      const input = document.querySelector(`#campo-${nome}`);
      const container = input.closest('.campo');
      if (!input.value.trim()) {
        container.classList.add('campo--invalido');
        valido = false;
      } else {
        container.classList.remove('campo--invalido');
      }
    });

    mensagem.classList.remove('mensagem-feedback--sucesso', 'mensagem-feedback--erro');

    if (!valido) {
      mensagem.textContent = 'Preencha corretamente todos os campos obrigatórios.';
      mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
      return;
    }

    // Em produção: atualizar o registro correspondente na tabela PRESTADOR.
    mensagem.textContent = 'Dados atualizados com sucesso! Redirecionando para seu perfil...';
    mensagem.classList.add('mensagem-feedback--sucesso', 'mensagem-feedback--visivel');

    setTimeout(() => {
      window.location.href = 'meu-perfil-prestador.html';
    }, 1800);
  });
}
