USE book_order_microservice;

CREATE TABLE IF NOT EXISTS book (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    stock INT NOT NULL,
    version INT DEFAULT 1
);

INSERT INTO book (id, name, stock, version) VALUES (1, '微服务架构实战', 10, 1);

CREATE TABLE IF NOT EXISTS undo_log (
    branch_id     BIGINT       NOT NULL,
    xid           VARCHAR(128) NOT NULL,
    context       VARCHAR(128) NOT NULL,
    rollback_info LONGBLOB     NOT NULL,
    log_status    INT          NOT NULL,
    log_created   DATETIME(6)  NOT NULL,
    log_modified  DATETIME(6)  NOT NULL,
    UNIQUE KEY ux_undo_log (xid, branch_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;