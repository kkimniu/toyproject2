CREATE TABLE admin_action_log (
    admin_action_log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    action_type ENUM('MEMBER_BANNED','MEMBER_UNBANNED','REPORT_RESOLVED') NOT NULL,
    target_type ENUM('MEMBER','REPORT') NOT NULL,
    target_id BIGINT NOT NULL,
    action_detail VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (admin_id) REFERENCES members(member_id)
);
