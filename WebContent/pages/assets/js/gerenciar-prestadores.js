/* ==========================================================================
   TELA: GERENCIAR PRESTADORES (Módulo Administrador)
   JavaScript específico desta tela: lista, filtra e permite ativar/
   desativar registros da tabela PRESTADOR (altera apenas o campo situacao).
   ========================================================================== */

let PRESTADORES = [
  { id_prestador: 1, nome_prestador: 'Carla Menezes', nome_fantasia: 'Studio Lente Viva', categoria: 'Fotografia', localizacao: 'Salvador - BA', disponibilidade: 'Disponível', situacao: 'ativo' },
  { id_prestador: 2, nome_prestador: 'Beatriz Andrade', nome_fantasia: 'Casa Encantada Decorações', categoria: 'Decoração', localizacao: 'Salvador - BA', disponibilidade: 'Disponível', situacao: 'ativo' },
  { id_prestador: 3, nome_prestador: 'Marcelo Souza', nome_fantasia: 'Sabor & Arte Buffet', categoria: 'Buffet', localizacao: 'Lauro de Freitas - BA', disponibilidade: 'Indisponível', situacao: 'ativo' },
  { id_prestador: 4, nome_prestador: 'Marcos Ferreira', nome_fantasia: 'DJ Marcos Ferreira', categoria: 'DJ', localizacao: 'Salvador - BA', disponibilidade: 'Disponível', situacao: 'ativo' },
  { id_prestador: 5, nome_prestador: 'Renata Dias', nome_fantasia: 'Cerimonial Elo Perfeito', categoria: 'Cerimonial', localizacao: 'Camaçari - BA', disponibilidade: 'Disponível', situacao: 'inativo' },
];

document.addEventListener('DOMContentLoaded', () => {
  document.querySelector('#filtro-categoria').addEventListener('change', aplicarFiltros);
  document.querySelector('#filtro-situacao').addEventListener('change', aplicarFiltros);
  document.querySelector('#filtro-localizacao').addEventListener('input', aplicarFiltros);
  aplicarFiltros();
});

function aplicarFiltros() {
  const categoria = document.querySelector('#filtro-categoria').value;
  const situacao = document.querySelector('#filtro-situacao').value;
  const localizacao = document.querySelector('#filtro-localizacao').value.trim().toLowerCase();

  const filtrados = PRESTADORES.filter((p) =>
    (!categoria || p.categoria === categoria) &&
    (!situacao || p.situacao === situacao) &&
    (!localizacao || p.localizacao.toLowerCase().includes(localizacao))
  );

  renderizarTabela(filtrados);
}

function renderizarTabela(lista) {
  const corpo = document.querySelector('#corpo-tabela');
  const vazio = document.querySelector('#estado-vazio');

  if (lista.length === 0) {
    corpo.innerHTML = '';
    vazio.classList.add('estado-vazio--visivel');
    return;
  }

  vazio.classList.remove('estado-vazio--visivel');

  corpo.innerHTML = lista.map((p) => `
    <tr data-id="${p.id_prestador}">
      <td>
        <div class="celula-principal">${p.nome_fantasia}</div>
        <div class="celula-secundaria">${p.nome_prestador}</div>
      </td>
      <td>${p.categoria}</td>
      <td>${p.localizacao}</td>
      <td><span class="badge ${p.disponibilidade === 'Disponível' ? 'badge--azul' : 'badge--roxo'}">${p.disponibilidade}</span></td>
      <td><span class="badge ${p.situacao === 'ativo' ? 'badge--azul' : 'badge--roxo'}">${p.situacao === 'ativo' ? 'Ativo' : 'Inativo'}</span></td>
      <td>
        <div class="celula-acoes">
          <button type="button" class="botao botao--secundario botao--pequeno" data-acao="alternar">${p.situacao === 'ativo' ? 'Desativar' : 'Ativar'}</button>
        </div>
      </td>
    </tr>
  `).join('');

  corpo.querySelectorAll('[data-acao="alternar"]').forEach((botao) => {
    botao.addEventListener('click', (evento) => alternarSituacao(evento.target.closest('tr').dataset.id));
  });
}

function alternarSituacao(idPrestador) {
  const prestador = PRESTADORES.find((p) => String(p.id_prestador) === String(idPrestador));
  if (!prestador) return;

  // Em produção: atualizar o campo situacao do registro correspondente
  // na tabela PRESTADOR.
  prestador.situacao = prestador.situacao === 'ativo' ? 'inativo' : 'ativo';
  aplicarFiltros();
}
