-- ============================================================
-- V1__init.sql
-- 系统基础表：用户 / 角色 / 菜单 / 部门 / 关联关系
-- ============================================================

CREATE TABLE sys_dept (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    parent_id   BIGINT       DEFAULT 0,
    sort        INT          DEFAULT 0,
    status      INT          DEFAULT 1,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    create_by   VARCHAR(50),
    update_by   VARCHAR(50),
    deleted     BOOLEAN      DEFAULT FALSE,
    version     INT          DEFAULT 0
);

CREATE TABLE sys_user (
    id          BIGSERIAL    PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    real_name   VARCHAR(100),
    email       VARCHAR(100),
    phone       VARCHAR(20),
    avatar      VARCHAR(255),
    status      INT          DEFAULT 1,
    dept_id     BIGINT,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    create_by   VARCHAR(50),
    update_by   VARCHAR(50),
    deleted     BOOLEAN      DEFAULT FALSE,
    version     INT          DEFAULT 0
);

CREATE TABLE sys_role (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    code        VARCHAR(100) NOT NULL UNIQUE,
    status      INT          DEFAULT 1,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    create_by   VARCHAR(50),
    update_by   VARCHAR(50),
    deleted     BOOLEAN      DEFAULT FALSE,
    version     INT          DEFAULT 0
);

CREATE TABLE sys_menu (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    permission  VARCHAR(200),
    path        VARCHAR(200),
    parent_id   BIGINT       DEFAULT 0,
    type        INT          DEFAULT 1,
    sort        INT          DEFAULT 0,
    icon        VARCHAR(100),
    status      INT          DEFAULT 1,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    create_by   VARCHAR(50),
    update_by   VARCHAR(50),
    deleted     BOOLEAN      DEFAULT FALSE,
    version     INT          DEFAULT 0
);

CREATE TABLE sys_user_role (
    id          BIGSERIAL   PRIMARY KEY,
    user_id     BIGINT      NOT NULL,
    role_id     BIGINT      NOT NULL,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    create_by   VARCHAR(50),
    update_by   VARCHAR(50)
);

CREATE TABLE sys_role_menu (
    id          BIGSERIAL   PRIMARY KEY,
    role_id     BIGINT      NOT NULL,
    menu_id     BIGINT      NOT NULL,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    create_by   VARCHAR(50),
    update_by   VARCHAR(50)
);