ALTER TABLE report
    ADD COLUMN resolution_type ENUM('ACCEPTED','REJECTED','NO_ACTION') NULL AFTER status,
    ADD COLUMN resolution_message VARCHAR(500) NULL AFTER resolution_type,
    ADD COLUMN processed_by BIGINT NULL AFTER resolution_message,
    ADD COLUMN processed_at DATETIME NULL AFTER processed_by,
    ADD CONSTRAINT fk_report_processed_by
        FOREIGN KEY (processed_by) REFERENCES members(member_id);
