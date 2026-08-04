CREATE TABLE sys_file (
    id              BIGSERIAL    PRIMARY KEY,
    original_name   VARCHAR(255) NOT NULL,
    stored_name     VARCHAR(255) NOT NULL,
    extension       VARCHAR(20),
    size            BIGINT,
    md5             VARCHAR(32),
    mime_type       VARCHAR(100),
    url             VARCHAR(500),
    thumbnail_url   VARCHAR(500),
    storage_type    VARCHAR(20)  DEFAULT 'LOCAL',
    bucket          VARCHAR(50),
    object_name     VARCHAR(255),
    create_time     TIMESTAMP,
    update_time     TIMESTAMP,
    create_by       VARCHAR(50),
    update_by       VARCHAR(50),
    deleted         BOOLEAN      DEFAULT FALSE
);