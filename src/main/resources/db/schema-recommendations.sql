-- Recommended database hardening for the current RoomMate schema.
-- Review against the actual database before running.
-- This file is not executed automatically by the application.

-- members
ALTER TABLE members
    ADD UNIQUE KEY uk_members_email (email);

ALTER TABLE members
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN banned_until DATETIME NULL,
    ADD COLUMN gender VARCHAR(20) NULL,
    ADD COLUMN birth_date DATE NULL;

CREATE INDEX idx_members_status_deleted ON members (status, deleted);

-- token_refresh
ALTER TABLE token_refresh
    ADD UNIQUE KEY uk_token_refresh_member_id (member_id);

-- rooms
CREATE INDEX idx_rooms_status_deleted_created ON rooms (status, deleted, room_created_at);
CREATE INDEX idx_rooms_member_deleted ON rooms (member_id, deleted);
CREATE INDEX idx_rooms_lat_lng ON rooms (lat, lng);
CREATE INDEX idx_rooms_legal_dong ON rooms (legal_dong);

-- room_image
ALTER TABLE room_image
    ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0;

CREATE INDEX idx_room_image_room_sort ON room_image (room_id, sort_order, image_id);

-- favorites
ALTER TABLE favorites
    ADD UNIQUE KEY uk_favorites_member_room (member_id, room_id);

CREATE INDEX idx_favorites_member_created ON favorites (member_id, created_at);
CREATE INDEX idx_favorites_room ON favorites (room_id);

-- member code join tables
ALTER TABLE member_hobbies
    ADD UNIQUE KEY uk_member_hobbies_member_hobby (member_id, hobby_id);

ALTER TABLE member_preferences
    ADD UNIQUE KEY uk_member_preferences_member_preference (member_id, preference_id);

ALTER TABLE member_pets
    ADD UNIQUE KEY uk_member_pets_member_pet (member_id, pet_id);

-- temp uploads
CREATE INDEX idx_temp_upload_files_used_created ON temp_upload_files (used, created_at);

-- chat rooms
CREATE INDEX idx_chat_rooms_room_members ON chat_rooms (room_id, owner_id, partner_id);
