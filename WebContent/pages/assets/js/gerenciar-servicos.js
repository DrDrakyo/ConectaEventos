/* ==========================================================================
   TELA: GERENCIAR SERVIÇOS (Módulo Administrador)
   JavaScript específico desta tela: única tela do sistema que cria e
   edita registros da tabela SERVICO (id_servico, tipo, descricao,
   situacao). O Prestador apenas seleciona esses registros ao cadastrar
   um item de portfólio — nunca os cria ou edita.
   ========================================================================== */

let SERVICOS = [
  { id_servico: 1, tipo: 'Fotografia', descricao: 'Cobertura fotográfica de eventos em geral', situacao: 'ativo' },
  { id_servico: 2, tipo: 'Filmagem', descricao: 'Produção audiovisual de eventos', situacao: 'ativo' },
  { id_servico: 3, tipo: 'Decoração', descricao: 'Decoração temática para festas e cerimônias', situacao: 'ativo' },
  { id_servico: 4, tipo: 'Buffet', descricao: 'Serviço completo de alimentação para eventos', situacao: 'ativo' },
  { id_servico: 5, tipo: 'Cerimonial', descricao: 'Organização e condução de cerimônias', situacao: 'ativo' },
  { id_servico: 6, tipo: 'Sonorização', descricao: 'Equipamentos e serviços de som', situacao: 'ativo' },
  { id_servico: 7, tipo: 'Iluminação', descricao: 'Equipamentos e serviços de iluminação cênica', situacao: 'ativo' },
  { id_servico: 8, tipo: 'Segurança', descricao: 'Serviços de segurança para eventos', situacao: 'ativo' },
  { id_servico: 9, tipo: 'Recepção', descricao: 'Equipe de recepção e credenciamento', situacao: 'ativo' },
  { id_servico: 10, tipo: 'Produção de eventos', descricao: 'Planejamento e execução geral do evento', situacao: 'ativo' },
  { id_servico: 11, tipo: 'Bartender', descricao: 'Serviço de bar e coquetelaria', situacao: 'ativo' },
  { id_servico: 12, tipo: 'DJ', descricao: 'Discotecagem para festas e eventos', situacao: 'ativo' },
  { id_servico: 13, tipo: 'Banda', descricao: 'Apresentações musicais ao vivo', situacao: 'ativo' },
  { id_servico: 14, tipo: 'Mestre de cerimônias', descricao: 'Condução e animação de cerimônias', situacao: 'ativo' },
  { id_servico: 15, tipo: 'Locução', descricao: 'Locução profissional para eventos', situacao: 'ativo' },
  { id_servico: 16, tipo: 'Assessoria', descricao: 'Assessoria completa para organização de eventos', situacao: 'ativo' },
  { id_servico: 17, tipo: 'Atrações artísticas', descricao: 'Shows e atrações artísticas diversas', situacao: 'ativo' },
];

let idEmEdicao = null;
let proximoId = 18;

document.addEventListener('DOMContentLoaded', () => {
  renderizarTabela();
  document.querySelector('#formulario-servico').addEventListener('submit', salvarServico);
  document.querySelector('#botao-cancelar-edicao').addEventListener('click', prepararNovoServico);
});

function renderizarTabela() {
  const corpo = document.querySelector('#corpo-tabela');

  corpo.innerHTML = SERVICOS.map((s) => `
    <tr data-id="${s.id_servico}">
      <td>
        <div class="celula-principal">${s.tipo}</div>
        <div class="celula-secundaria">${s.descricao}</div>
      </td>
      <td><span class="badge ${s.situacao === 'ativo' ? 'badge--azul' : 'badge--roxo'}">${s.situacao === 'ativo' ? 'Ativo' : 'Inativo'}</span></td>
      <td>
        <div class="celula-acoes">
          <button type="button" class="botao botao--secundario botao--pequeno" data-acao="editar">Editar</button>
          <button type="button" class="botao botao--secundario botao--pequeno" data-acao="alternar">${s.situacao === 'ativo' ? 'Desativar' : 'Ativar'}</button>
        </div>
      </td>
    </tr>
  `).join('');

  corpo.querySelectorAll('[data-acao="editar"]').forEach((botao) => {
    botao.addEventListener('click', (evento) => prepararEdicao(evento.target.closest('tr').dataset.id));
  });
  corpo.querySelectorAll('[data-acao="alternar"]').forEach((botao) => {
    botao.addEventListener('click', (evento) => alternarSituacao(evento.target.closest('tr').dataset.id));
  });
}

function prepararNovoServico() {
  idEmEdicao = null;
  document.querySelector('#titulo-formulario-servico').textContent = 'Novo serviço';
  document.querySelector('#formulario-servico').reset();
  document.querySelector('#mensagem-feedback-servico').classList.remove('mensagem-feedback--visivel');
}

function prepararEdicao(idServico) {
  const servico = SERVICOS.find((s) => String(s.id_servico) === String(idServico));
  if (!servico) return;

  idEmEdicao = servico.id_servico;
  document.querySelector('#titulo-formulario-servico').textContent = `Editar: ${servico.tipo}`;
  document.querySelector('#campo-tipo').value = servico.tipo;
  document.querySelector('#campo-descricao').value = servico.descricao;
  document.querySelector('#mensagem-feedback-servico').classList.remove('mensagem-feedback--visivel');
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function alternarSituacao(idServico) {
  const servico = SERVICOS.find((s) => String(s.id_servico) === String(idServico));
  if (!servico) return;

  // Em produção: atualizar o campo situacao do registro correspondente
  // na tabela SERVICO.
  servico.situacao = servico.situacao === 'ativo' ? 'inativo' : 'ativo';
  renderizarTabela();
}

function salvarServico(evento) {
  evento.preventDefault();

  const tipo = document.querySelector('#campo-tipo').value.trim();
  const descricao = document.querySelector('#campo-descricao').value.trim();
  const mensagem = document.querySelector('#mensagem-feedback-servico');

  mensagem.classList.remove('mensagem-feedback--sucesso', 'mensagem-feedback--erro');

  if (!tipo || !descricao) {
    mensagem.textContent = 'Preencha o tipo e a descrição do serviço.';
    mensagem.classList.add('mensagem-feedback--erro', 'mensagem-feedback--visivel');
    return;
  }

  if (idEmEdicao) {
    // Em produção: atualizar o registro (id_servico) na tabela SERVICO.
    const servico = SERVICOS.find((s) => s.id_servico === idEmEdicao);
    servico.tipo = tipo;
    servico.descricao = descricao;
    mensagem.textContent = 'Serviço atualizado com sucesso!';
  } else {
    // Em produção: inserir um novo registro na tabela SERVICO
    // (situacao inicial "ativo").
    SERVICOS.push({ id_servico: proximoId++, tipo, descricao, situacao: 'ativo' });
    mensagem.textContent = 'Serviço cadastrado com sucesso!';
  }

  mensagem.classList.add('mensagem-feedback--sucesso', 'mensagem-feedback--visivel');
  renderizarTabela();
  prepararNovoServico();
}
