CREATE TABLE sys_operation_log (
    id              BIGSERIAL    PRIMARY KEY,
    user_id         BIGINT,
    username        VARCHAR(50),
    operation       VARCHAR(100),
    module          VARCHAR(100),
    target          VARCHAR(200),
    request_url     VARCHAR(255),
    request_method  VARCHAR(10),
    request_params  TEXT,
    result          VARCHAR(10),
    error_msg       TEXT,
    duration        BIGINT,
    ip              VARCHAR(50),
    create_time     TIMESTAMP
);