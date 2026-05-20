CREATE TABLE IF NOT EXISTS notices (
    notice_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    pinned TINYINT NOT NULL DEFAULT 0,
    published TINYINT NOT NULL DEFAULT 1,
    deleted TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_notices_admin
        FOREIGN KEY (admin_id) REFERENCES members(member_id)
);

CREATE INDEX idx_notices_public
ON notices (deleted, published, pinned, created_at);
