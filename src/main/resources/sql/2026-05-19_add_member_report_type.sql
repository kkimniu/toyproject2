ALTER TABLE report
    MODIFY room_id BIGINT NULL,
    ADD COLUMN chat_room_id BIGINT NULL AFTER room_id,
    ADD COLUMN report_type ENUM('ROOM','MEMBER','CHAT') NOT NULL DEFAULT 'ROOM' AFTER target_member_id,
    ADD CONSTRAINT fk_report_chat_room
        FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(chat_room_id) ON DELETE CASCADE;

CREATE INDEX idx_report_type_status_created
ON report (report_type, status, report_created_at);

CREATE INDEX idx_report_chat_room_id
ON report (chat_room_id);

ALTER TABLE notifications
    MODIFY type ENUM('CHAT','SYSTEM','ROOM','REPORT') NOT NULL;
