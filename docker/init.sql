-- Criação do banco de dados se não existir
CREATE DATABASE IF NOT EXISTS conectaeventos
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE conectaeventos;

-- Tabela: categoria
CREATE TABLE IF NOT EXISTS categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nome_categoria VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Carga inicial de categorias comuns de eventos
INSERT IGNORE INTO categoria (nome_categoria, descricao) VALUES
('Fotografia e Filmagem', 'Profissionais de cobertura fotográfica e gravação de vídeo'),
('Música e DJ', 'Bandas, cantores, DJs e sonorização'),
('Buffet e Gastronomia', 'Serviços de alimentação, doces, bolos e bebidas'),
('Decoração e Cenografia', 'Decoração de ambientes, flores e cenografia temática'),
('Espaço e Locação', 'Salões de festas, sítios e espaços para eventos'),
('Cerimonial e Assessoria', 'Planejamento, coordenação e cerimonialistas'),
('Animação e Recreação', 'Animadores, mágicos e brinquedos para eventos infantis'),
('Segurança e Apoio', 'Equipes de segurança, recepcionistas e brigadistas');

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

-- Tabela: avaliacao
CREATE TABLE IF NOT EXISTS avaliacao (
    id_avaliacao INT AUTO_INCREMENT PRIMARY KEY,
    id_contratacao INT NOT NULL,
    cpf_cnpj_contratante VARCHAR(20) NOT NULL,
    cpf_cnpj_prestador VARCHAR(20) NOT NULL,
    nota INT NOT NULL,
    comentario TEXT,
    data_avaliacao DATE,
    CONSTRAINT fk_avaliacao_contratacao
        FOREIGN KEY (id_contratacao)
        REFERENCES contratacao (id_contratacao)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela: foto_avaliacao
CREATE TABLE IF NOT EXISTS foto_avaliacao (
    id_foto INT AUTO_INCREMENT PRIMARY KEY,
    id_avaliacao INT NOT NULL,
    url_foto VARCHAR(500) NOT NULL,
    descricao_foto VARCHAR(255),
    CONSTRAINT fk_foto_avaliacao
        FOREIGN KEY (id_avaliacao)
        REFERENCES avaliacao (id_avaliacao)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela: portfolio_item
CREATE TABLE IF NOT EXISTS portfolio_item (
    id_portfolio INT AUTO_INCREMENT PRIMARY KEY,
    cpf_cnpj_prestador VARCHAR(20) NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    imagem_url VARCHAR(500),
    data_publicacao DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela: administrador
CREATE TABLE IF NOT EXISTS administrador (
    id_administrador INT AUTO_INCREMENT PRIMARY KEY,
    nome_administrador VARCHAR(255) NOT NULL,
    email_administrador VARCHAR(255) NOT NULL UNIQUE,
    senha_administrador VARCHAR(255) NOT NULL,
    data_cadastro DATE,
    situacao VARCHAR(20) DEFAULT 'ATIVO'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Carga inicial do administrador padrão
INSERT IGNORE INTO administrador (id_administrador, nome_administrador, email_administrador, senha_administrador, data_cadastro, situacao) VALUES
(1, 'Administrador do Sistema', 'admin@conectaeventos.com', 'admin123', CURDATE(), 'ATIVO');

