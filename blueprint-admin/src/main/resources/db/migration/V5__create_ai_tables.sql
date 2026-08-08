-- ============================================================
-- V5__create_ai_tables.sql
-- AI 模块：供应商 / 会话 / 消息 / 用量审计
-- ============================================================

CREATE TABLE ai_provider (
    id              BIGSERIAL    PRIMARY KEY,
    provider_code   VARCHAR(50)  NOT NULL UNIQUE,
    provider_name   VARCHAR(100) NOT NULL,
    base_url        VARCHAR(255) NOT NULL,
    api_key         VARCHAR(512) NOT NULL,
    auth_header     VARCHAR(50)  DEFAULT 'Authorization',
    timeout_ms      INT          DEFAULT 60000,
    max_retries     INT          DEFAULT 2,
    enabled         BOOLEAN      DEFAULT TRUE,
    priority        INT          DEFAULT 0,
    create_time     TIMESTAMP,
    update_time     TIMESTAMP,
    create_by       VARCHAR(50),
    update_by       VARCHAR(50),
    deleted         BOOLEAN      DEFAULT FALSE,
    version         INT          DEFAULT 0
);
CREATE INDEX idx_ai_provider_code ON ai_provider(provider_code);
CREATE INDEX idx_ai_provider_enabled ON ai_provider(enabled);

CREATE TABLE chat_session (
    id              BIGSERIAL    PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES sys_user(id),
    title           VARCHAR(255) DEFAULT '新会话',
    model_id        BIGINT,
    system_prompt   TEXT,
    temperature     NUMERIC(3,2) DEFAULT 0.7,
    context_size    INT          DEFAULT 10,
    status          VARCHAR(15)  DEFAULT 'active',
    create_time     TIMESTAMP,
    update_time     TIMESTAMP,
    create_by       VARCHAR(50),
    update_by       VARCHAR(50),
    deleted         BOOLEAN      DEFAULT FALSE,
    version         INT          DEFAULT 0
);
CREATE INDEX idx_session_user ON chat_session(user_id);
CREATE INDEX idx_session_status ON chat_session(status);

CREATE TABLE chat_message (
    id              BIGSERIAL    PRIMARY KEY,
    session_id      BIGINT       NOT NULL REFERENCES chat_session(id),
    role            VARCHAR(20)  NOT NULL,
    content         TEXT         NOT NULL,
    model           VARCHAR(100),
    tokens_input    INT          DEFAULT 0,
    tokens_output   INT          DEFAULT 0,
    finish_reason   VARCHAR(30),
    create_time     TIMESTAMP,
    update_time     TIMESTAMP,
    create_by       VARCHAR(50),
    update_by       VARCHAR(50),
    deleted         BOOLEAN      DEFAULT FALSE,
    version         INT          DEFAULT 0
);
CREATE INDEX idx_message_session ON chat_message(session_id);
CREATE INDEX idx_message_created ON chat_message(session_id, create_time);

CREATE TABLE ai_usage_record (
    id              BIGSERIAL    PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    provider_code   VARCHAR(20)  NOT NULL,
    model_id        VARCHAR(100),
    tokens_input    INT          DEFAULT 0,
    tokens_output   INT          DEFAULT 0,
    request_count   INT          DEFAULT 1,
    record_date     DATE         NOT NULL DEFAULT CURRENT_DATE,
    create_time     TIMESTAMP
);
CREATE INDEX idx_usage_user_date ON ai_usage_record(user_id, record_date);