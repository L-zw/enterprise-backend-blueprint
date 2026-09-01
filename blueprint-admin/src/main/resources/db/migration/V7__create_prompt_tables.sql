-- ============================================================
-- V7__create_prompt_tables.sql
-- AI 模块：Prompt 模板表 + 内置预设
-- ============================================================

CREATE TABLE prompt_template (
    id              BIGSERIAL    PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    category        VARCHAR(50)  NOT NULL,
    content         TEXT         NOT NULL,
    variables       VARCHAR(500),
    icon            VARCHAR(50),
    is_builtin      BOOLEAN      DEFAULT FALSE,
    sort_order      INT          DEFAULT 0,
    enabled         BOOLEAN      DEFAULT TRUE,
    create_time     TIMESTAMP,
    update_time     TIMESTAMP,
    create_by       VARCHAR(50),
    update_by       VARCHAR(50),
    deleted         BOOLEAN      DEFAULT FALSE,
    version         INT          DEFAULT 0
);
CREATE INDEX idx_prompt_category ON prompt_template(category);
CREATE INDEX idx_prompt_builtin ON prompt_template(is_builtin);

-- 内置预设模板
INSERT INTO prompt_template (name, category, content, variables, is_builtin, sort_order, enabled)
VALUES
    ('代码解释', 'dev', '请解释以下代码并指出潜在问题：\n${code}', 'code', TRUE, 1, TRUE),
    ('文案润色', 'writing', '请润色以下文本，使其更专业流畅：\n${text}', 'text', TRUE, 2, TRUE);
