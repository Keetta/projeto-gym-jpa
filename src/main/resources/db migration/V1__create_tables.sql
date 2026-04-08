-- TABELA ENDERECOS
CREATE TABLE enderecos (
    id SERIAL PRIMARY KEY,
    rua VARCHAR(255) NOT NULL,
    numero VARCHAR(10),
    cidade VARCHAR(255) NOT NULL
);

-- TABELA ALUNOS (1:1)
CREATE TABLE alunos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    idade INT NOT NULL,
    telefone VARCHAR(30) NOT NULL,
    altura DECIMAL(5,2),
    peso DECIMAL(5,2),

    endereco_id BIGINT UNIQUE NOT NULL,
    CONSTRAINT fk_aluno_endereco
        FOREIGN KEY (endereco_id)
        REFERENCES enderecos(id)
);

-- TABELA PLANOS
CREATE TABLE planos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    descricao VARCHAR(500)
);

-- TABELA TREINADORES
CREATE TABLE treinadores (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    especialidade VARCHAR(100) NOT NULL,
    telefone VARCHAR(30) NOT NULL
);

-- TABELA TREINOS (N:1)
CREATE TABLE treinos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(500),

    fk_treinador_id BIGINT,
    CONSTRAINT fk_treino_treinador
        FOREIGN KEY (fk_treinador_id)
        REFERENCES treinadores(id)
);

-- TABELA MATRICULAS (N:1 com aluno e plano)
CREATE TABLE matriculas (
    id SERIAL PRIMARY KEY,

    fk_aluno_id BIGINT NOT NULL,
    fk_plano_id BIGINT NOT NULL,

    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP,
    status VARCHAR(20) NOT NULL,

    CONSTRAINT fk_matricula_aluno
        FOREIGN KEY (fk_aluno_id)
        REFERENCES alunos(id),

    CONSTRAINT fk_matricula_plano
        FOREIGN KEY (fk_plano_id)
        REFERENCES planos(id)
);

-- TABELA PAGAMENTOS (N:1 com matricula)
CREATE TABLE pagamentos (
    id SERIAL PRIMARY KEY,

    fk_matricula_id BIGINT NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data_pagamento TIMESTAMP,
    status VARCHAR(20) NOT NULL,

    CONSTRAINT fk_pagamento_matricula
        FOREIGN KEY (fk_matricula_id)
        REFERENCES matriculas(id)
);

-- TABELA ALUNO_TREINO (N:N)
CREATE TABLE aluno_treino (
    aluno_id BIGINT NOT NULL,
    treino_id BIGINT NOT NULL,

    CONSTRAINT pk_aluno_treino
        PRIMARY KEY (aluno_id, treino_id),

    CONSTRAINT fk_aluno_treino_aluno
        FOREIGN KEY (aluno_id)
        REFERENCES alunos(id),

    CONSTRAINT fk_aluno_treino_treino
        FOREIGN KEY (treino_id)
        REFERENCES treinos(id)
);