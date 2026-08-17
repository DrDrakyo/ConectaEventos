# ConectaEventos

Sistema de gestão e contratação de serviços para eventos, desenvolvido em Java Web (Servlets/JDBC) e MySQL.

---

## 🚀 Como Executar com Docker

A forma mais simples e recomendada de rodar a aplicação em qualquer máquina é utilizando o **Docker** e o **Docker Compose**.

### Pré-requisitos
- [Docker](https://docs.docker.com/get-docker/) instalado
- [Docker Compose](https://docs.docker.com/compose/install/) instalado

### 1. Iniciar a aplicação
No diretório raiz do projeto, execute:

```bash
docker compose up --build
```
*(ou `docker-compose up --build` em versões anteriores)*

Esse comando irá:
1. Compilar todo o código Java automaticamente em um container temporário.
2. Iniciar o container do **MySQL 8.0** e executar o script de criação das tabelas (`docker/init.sql`).
3. Aguardar o banco ficar pronto e iniciar o container da aplicação no **Apache Tomcat 9**.

### 2. Acessar a aplicação
Após a inicialização, a aplicação estará disponível em:

- `http://localhost:8080/`
- `http://localhost:8080/ConectaEventos/`

### 3. Parar a aplicação
Para parar os containers:

```bash
docker compose down
```

Para parar e resetar o volume do banco de dados:

```bash
docker compose down -v
```

---

## 🛠️ Endpoints da API

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/cadastroContratante` | Cadastro de novos contratantes |
| `POST` | `/cadastroPrestador` | Cadastro de novos prestadores |
| `POST` / `GET` | `/login` | Autenticação (Contratante e Prestador) e consulta de sessão |
| `GET` / `POST` | `/logout` | Encerramento de sessão |
| `GET` / `POST` | `/editarPerfilContratante` | Visualização e edição de perfil do contratante |
| `GET` | `/dashboardContratante` | Métricas e resumo do painel do contratante |
| `GET` / `POST` | `/configuracoesContaContratante` | Gestão de dados, senha e status da conta |

---

## 🗄️ Estrutura do Banco de Dados

O banco de dados `conectaeventos` é configurado automaticamente com as seguintes tabelas:

- `contratante`: Informações de clientes/contratantes de eventos.
- `prestador`: Informações de profissionais e prestadores de serviços.
- `contratacao`: Registros de contratações de serviços/eventos.
- `item_contratacao`: Itens e serviços vinculados a uma contratação.
