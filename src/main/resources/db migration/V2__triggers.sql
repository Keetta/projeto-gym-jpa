-- LOG (AUDITORIA DE PAGAMENTOS)
CREATE TABLE log_pagamentos (
                                id SERIAL PRIMARY KEY,
                                pagamento_id INT,
                                valor DECIMAL(10,2),
                                status VARCHAR(20),
                                data_evento TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- LOG DE PAGAMENTOS
CREATE OR REPLACE FUNCTION fn_log_pagamento()
RETURNS TRIGGER AS $$
BEGIN
INSERT INTO log_pagamentos (pagamento_id, valor, status)
VALUES (NEW.id, NEW.valor, NEW.status);

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_log_pagamento
    AFTER INSERT OR UPDATE ON pagamentos
                        FOR EACH ROW
                        EXECUTE FUNCTION fn_log_pagamento();

-- BLOQUEAR MÚLTIPLAS MATRÍCULAS ATIVAS
CREATE OR REPLACE FUNCTION fn_bloquear_dupla_matricula()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'ATIVA' THEN
        IF EXISTS (
            SELECT 1
            FROM matriculas
            WHERE fk_aluno_id = NEW.fk_aluno_id
              AND status = 'ATIVA'
              AND id <> NEW.id
        ) THEN
            RAISE EXCEPTION 'Aluno já possui matrícula ativa!';
END IF;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_bloquear_dupla_matricula
    BEFORE INSERT OR UPDATE ON matriculas
                         FOR EACH ROW
                         EXECUTE FUNCTION fn_bloquear_dupla_matricula();

-- AUTOMATIZANDO STATUS DE MATRÍCULA EXPIRADA
CREATE OR REPLACE FUNCTION fn_finalizar_matricula()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.data_fim IS NOT NULL AND NEW.data_fim < CURRENT_TIMESTAMP THEN
        NEW.status := 'INATIVA';
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_finalizar_matricula
    BEFORE UPDATE ON matriculas
    FOR EACH ROW
    EXECUTE FUNCTION fn_finalizar_matricula();