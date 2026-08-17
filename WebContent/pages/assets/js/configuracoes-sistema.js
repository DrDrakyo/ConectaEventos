/* ==========================================================================
   TELA: CONFIGURAÇÕES DO SISTEMA (Módulo Administrador)
   JavaScript específico desta tela: filtro rápido dos atalhos exibidos.
   Não há dado a persistir aqui — o banco não possui tabela de parâmetros
   do sistema, então esta tela apenas organiza a navegação para as telas
   de gestão já existentes (Gerenciar Serviços, Prestadores, Contratantes).
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  const campoBusca = document.querySelector('#busca-atalhos');
  const atalhos = document.querySelectorAll('.atalho-sistema');

  campoBusca.addEventListener('input', () => {
    const termo = campoBusca.value.trim().toLowerCase();

    atalhos.forEach((atalho) => {
      const titulo = atalho.querySelector('.atalho-sistema__titulo').textContent.toLowerCase();
      const legenda = atalho.querySelector('.atalho-sistema__legenda').textContent.toLowerCase();
      const combina = !termo || titulo.includes(termo) || legenda.includes(termo);
      atalho.style.display = combina ? '' : 'none';
    });
  });
});
