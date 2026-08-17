-- Criação do banco de dados se não existir
CREATE DATABASE IF NOT EXISTS conectaeventos
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE conectaeventos;

-- Tabela: contratante
CREATE TABLE IF NOT EXISTS contratante (
    id_contratante INT AUTO_INCREMENT PRIMARY KEY,
    cpf_cnpj VARCHAR(20) NOT NULL UNIQUE,
    nome_contratante VARCHAR(255) NOT NULL,
    email_contratante VARCHAR(255) NOT NULL UNIQUE,
    senha_contratante VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    endereco VARCHAR(255),
    cidade VARCHAR(100),
    data_cadastro DATE,
    situacao VARCHAR(20) DEFAULT 'ATIVO'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela: prestador
CREATE TABLE IF NOT EXISTS prestador (
    id_prestador INT AUTO_INCREMENT PRIMARY KEY,
    cpf_cnpj VARCHAR(20) NOT NULL UNIQUE,
    nome_prestador VARCHAR(255) NOT NULL,
    email_prestador VARCHAR(255) NOT NULL UNIQUE,
    senha_prestador VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    endereco VARCHAR(255),
    cidade VARCHAR(100),
    categoria VARCHAR(100),
    descricao TEXT,
    data_cadastro DATE,
    situacao VARCHAR(20) DEFAULT 'ATIVO'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela: contratacao
CREATE TABLE IF NOT EXISTS contratacao (
    id_contratacao INT AUTO_INCREMENT PRIMARY KEY,
    cpf_cnpj_contratante VARCHAR(20) NOT NULL,
    cpf_cnpj_prestador VARCHAR(20) NOT NULL,
    titulo_evento VARCHAR(255) NOT NULL,
    descricao_evento TEXT,
    data_evento DATE,
    data_contratacao DATE,
    valor_total DECIMAL(10,2) DEFAULT 0.00,
    status VARCHAR(50) DEFAULT 'PENDENTE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela: item_contratacao
CREATE TABLE IF NOT EXISTS item_contratacao (
    id_item INT AUTO_INCREMENT PRIMARY KEY,
    id_contratacao INT NOT NULL,
    descricao_item VARCHAR(255) NOT NULL,
    quantidade INT NOT NULL DEFAULT 1,
    valor_unitario DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    valor_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_item_contratacao
        FOREIGN KEY (id_contratacao)
        REFERENCES contratacao (id_contratacao)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
