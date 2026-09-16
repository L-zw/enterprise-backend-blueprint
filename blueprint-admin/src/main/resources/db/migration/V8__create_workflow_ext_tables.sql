-- ============================================================
-- V8__create_workflow_ext_tables.sql
-- 工作流模块：业务增强表（流程分类 / 实例扩展 / 抄送）
-- Flowable 自身的 ACT_* 表由引擎启动时自动创建，此处不建
-- ============================================================

CREATE TABLE wf_category (
    id          BIGSERIAL    PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    sort_order  INT          DEFAULT 0,
    enabled     BOOLEAN      DEFAULT TRUE,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    create_by   VARCHAR(50),
    update_by   VARCHAR(50),
    deleted     BOOLEAN      DEFAULT FALSE,
    version     INT          DEFAULT 0
);
CREATE INDEX idx_wf_category_code ON wf_category(code);
CREATE INDEX idx_wf_category_enabled ON wf_category(enabled);

CREATE TABLE wf_instance_ext (
    id          BIGSERIAL    PRIMARY KEY,
    instance_id VARCHAR(64)  NOT NULL,
    bpmn_key    VARCHAR(100),
    initiator   BIGINT       NOT NULL REFERENCES sys_user(id),
    title       VARCHAR(500) NOT NULL,
    form_data   TEXT,
    status      VARCHAR(20)  DEFAULT 'approving',
    start_time  TIMESTAMP,
    end_time    TIMESTAMP,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    create_by   VARCHAR(50),
    update_by   VARCHAR(50),
    deleted     BOOLEAN      DEFAULT FALSE,
    version     INT          DEFAULT 0
);
CREATE INDEX idx_wf_ext_instance ON wf_instance_ext(instance_id);
CREATE INDEX idx_wf_ext_status ON wf_instance_ext(status);

CREATE TABLE wf_copy (
    id          BIGSERIAL    PRIMARY KEY,
    instance_id VARCHAR(64)  NOT NULL,
    user_id     BIGINT       NOT NULL REFERENCES sys_user(id),
    read_status VARCHAR(20)  DEFAULT 'UNREAD',
    read_time   TIMESTAMP,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    create_by   VARCHAR(50),
    update_by   VARCHAR(50),
    deleted     BOOLEAN      DEFAULT FALSE,
    version     INT          DEFAULT 0,
    CONSTRAINT uk_wf_copy_instance_user UNIQUE (instance_id, user_id)
);
CREATE INDEX idx_wf_copy_instance ON wf_copy(instance_id);
CREATE INDEX idx_wf_copy_user ON wf_copy(user_id);
