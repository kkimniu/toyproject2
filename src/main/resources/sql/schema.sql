CREATE TABLE work_types (
    work_type_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    work_type_name VARCHAR(50) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE members (
    member_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(30),
    photo_url VARCHAR(500),
    role ENUM('USER','ADMIN','SUPER_ADMIN','BANNED') NOT NULL DEFAULT 'USER',
    report_count INT NOT NULL DEFAULT 0,
    ban_count INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    sleep_time VARCHAR(50),
    work_type_id BIGINT,
    smoking ENUM('NON_SMOKER','SMOKER') NOT NULL DEFAULT 'NON_SMOKER',
    drinking ENUM('NONE','SOCIAL','OFTEN') NOT NULL DEFAULT 'NONE',
    mbti CHAR(4),
    member_created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    member_updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (work_type_id) REFERENCES work_types(work_type_id)
);

CREATE TABLE room_types (
    room_type_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_type_name VARCHAR(50) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rooms (
    room_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    room_title VARCHAR(255) NOT NULL,
    room_content TEXT,
    room_type_id BIGINT,
    monthly_rent DECIMAL(15,2) NOT NULL,
    deposit DECIMAL(15,2) NOT NULL DEFAULT 0,
    area_m2 FLOAT,
    floor INT,
    address VARCHAR(255),
    legal_dong VARCHAR(100),
    land_number VARCHAR(50),
    lat DOUBLE,
    lng DOUBLE,
    available_from DATE,
    max_roommates INT,
    views INT NOT NULL DEFAULT 0,
    interest_count INT NOT NULL DEFAULT 0,
    status ENUM('OPEN','RESERVED','CLOSED','HIDDEN') NOT NULL DEFAULT 'OPEN',
    deleted TINYINT NOT NULL DEFAULT 0,
    room_created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    room_updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (member_id) REFERENCES members(member_id),
    FOREIGN KEY (room_type_id) REFERENCES room_types(room_type_id)
);

CREATE TABLE room_image (
    image_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    sort_order INT DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE
);

CREATE TABLE favorites (
    favorite_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_favorites_member_room (member_id, room_id),

    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

CREATE TABLE report (
    report_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reporter_id BIGINT NOT NULL,
    room_id BIGINT,
    chat_room_id BIGINT,
    community_post_id BIGINT,
    community_comment_id BIGINT,
    target_member_id BIGINT NOT NULL,
    report_type ENUM('ROOM','MEMBER','CHAT','COMMUNITY_POST','COMMUNITY_COMMENT') NOT NULL DEFAULT 'ROOM',
    reason TEXT,
    status ENUM('PENDING','RESOLVED') NOT NULL DEFAULT 'PENDING',
    resolution_type ENUM('ACCEPTED','REJECTED','NO_ACTION'),
    resolution_message VARCHAR(500),
    processed_by BIGINT,
    processed_at DATETIME,
    report_created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE,
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(chat_room_id) ON DELETE CASCADE,
    FOREIGN KEY (reporter_id) REFERENCES members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (target_member_id) REFERENCES members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES members(member_id)
);

CREATE TABLE admin_action_log (
    admin_action_log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    action_type ENUM(
        'MEMBER_BANNED',
        'MEMBER_UNBANNED',
        'REPORT_RESOLVED',
        'MEMBER_PROMOTED_TO_ADMIN',
        'MEMBER_DEMOTED_TO_USER',
        'MEMBER_DELETED'
    ) NOT NULL,
    target_type ENUM('MEMBER','REPORT') NOT NULL,
    target_id BIGINT NOT NULL,
    action_detail VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (admin_id) REFERENCES members(member_id)
);

CREATE TABLE chat_rooms (
    chat_room_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    partner_id BIGINT NOT NULL,
    deleted_by_owner TINYINT NOT NULL DEFAULT 0,
    deleted_by_partner TINYINT NOT NULL DEFAULT 0,

    last_message_id BIGINT NULL,
    last_message_at DATETIME NULL,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_chat_room (room_id, owner_id, partner_id),

    CHECK (owner_id <> partner_id),

    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE,
    FOREIGN KEY (owner_id) REFERENCES members(member_id),
    FOREIGN KEY (partner_id) REFERENCES members(member_id)
);

CREATE TABLE chat_messages (
    message_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    chat_room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    deleted_by_sender TINYINT NOT NULL DEFAULT 0,
    deleted_by_receiver TINYINT NOT NULL DEFAULT 0,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    deleted_at DATETIME NULL,

    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(chat_room_id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES members(member_id)
);

CREATE TABLE chat_room_members (
    chat_room_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,

    joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    last_read_message_id BIGINT NULL,
    last_read_at DATETIME NULL,

    is_hidden TINYINT NOT NULL DEFAULT 0,
    hidden_at DATETIME NULL,

    left_at DATETIME NULL,

    notifications_enabled TINYINT NOT NULL DEFAULT 1,

    PRIMARY KEY (chat_room_id, member_id),

    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(chat_room_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (last_read_message_id) REFERENCES chat_messages(message_id) ON DELETE SET NULL
);

ALTER TABLE chat_rooms
ADD CONSTRAINT fk_chat_rooms_last_message
FOREIGN KEY (last_message_id)
REFERENCES chat_messages(message_id)
ON DELETE SET NULL;

CREATE TABLE notifications (
    notification_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    type ENUM('CHAT','SYSTEM','ROOM','REPORT') NOT NULL,
    reference_id BIGINT,
    message VARCHAR(255),
    is_read TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

CREATE TABLE notices (
    notice_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    pinned TINYINT NOT NULL DEFAULT 0,
    published TINYINT NOT NULL DEFAULT 1,
    deleted TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (admin_id) REFERENCES members(member_id)
);

CREATE TABLE community_posts (
    community_post_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    views INT NOT NULL DEFAULT 0,
    blinded TINYINT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (member_id) REFERENCES members(member_id)
);

CREATE TABLE community_comments (
    community_comment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    community_post_id BIGINT NOT NULL,
    parent_comment_id BIGINT,
    member_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    blinded TINYINT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (community_post_id) REFERENCES community_posts(community_post_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES community_comments(community_comment_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES members(member_id)
);

CREATE TABLE community_post_views (
    community_post_view_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    community_post_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_community_post_views_post_member (community_post_id, member_id),

    FOREIGN KEY (community_post_id) REFERENCES community_posts(community_post_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

CREATE TABLE token_refresh (
    token_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    refresh_token_hash VARCHAR(255) NOT NULL,
    expires_at DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_token_refresh_member (member_id),

    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

CREATE TABLE hobbies (
    hobby_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    hobby_name VARCHAR(50) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE member_hobbies (
    member_id BIGINT NOT NULL,
    hobby_id BIGINT NOT NULL,

    PRIMARY KEY (member_id, hobby_id),

    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (hobby_id) REFERENCES hobbies(hobby_id) ON DELETE CASCADE
);

CREATE TABLE preferences (
    preference_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    preference_name VARCHAR(50) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE member_preferences (
    member_id BIGINT NOT NULL,
    preference_id BIGINT NOT NULL,

    PRIMARY KEY (member_id, preference_id),

    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (preference_id) REFERENCES preferences(preference_id) ON DELETE CASCADE
);

CREATE TABLE pets (
    pet_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pet_name VARCHAR(50) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE member_pets (
    member_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,

    PRIMARY KEY (member_id, pet_id),

    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE
);

CREATE TABLE temp_upload_files (
    temp_file_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NULL,
    signup_key VARCHAR(64) NULL,
    original_name VARCHAR(255) NOT NULL,
    temp_path VARCHAR(500) NOT NULL,
    used TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

CREATE TABLE room_views (
    room_view_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_room_views_room_member (room_id, member_id),

    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

CREATE INDEX idx_members_work_type_id
ON members (work_type_id);

CREATE INDEX idx_rooms_member_id
ON rooms (member_id);

CREATE INDEX idx_rooms_room_type_id
ON rooms (room_type_id);

CREATE INDEX idx_rooms_status_deleted
ON rooms (status, deleted);

CREATE INDEX idx_room_image_room_id
ON room_image (room_id);

CREATE INDEX idx_room_image_room_deleted_sort
ON room_image (room_id, deleted, sort_order, image_id);

CREATE INDEX idx_report_reporter_id
ON report (reporter_id);

CREATE INDEX idx_report_target_member_id
ON report (target_member_id);

CREATE INDEX idx_chat_rooms_room_id
ON chat_rooms (room_id);

CREATE INDEX idx_chat_rooms_owner_id
ON chat_rooms (owner_id);

CREATE INDEX idx_chat_rooms_partner_id
ON chat_rooms (partner_id);

CREATE INDEX idx_chat_rooms_owner_deleted
ON chat_rooms (owner_id, deleted_by_owner);

CREATE INDEX idx_chat_rooms_partner_deleted
ON chat_rooms (partner_id, deleted_by_partner);

CREATE INDEX idx_chat_rooms_last_message_at
ON chat_rooms (last_message_at);

CREATE INDEX idx_chat_messages_room_sent
ON chat_messages (chat_room_id, sent_at);

CREATE INDEX idx_chat_messages_room_message
ON chat_messages (chat_room_id, message_id);

CREATE INDEX idx_chat_messages_sender_id
ON chat_messages (sender_id);

CREATE INDEX idx_chat_room_members_member_hidden
ON chat_room_members (member_id, is_hidden);

CREATE INDEX idx_chat_room_members_member_left
ON chat_room_members (member_id, left_at);

CREATE INDEX idx_notifications_member_read
ON notifications (member_id, is_read);

CREATE INDEX idx_notifications_type_reference
ON notifications (type, reference_id);

CREATE INDEX idx_notices_public
ON notices (deleted, published, pinned, created_at);

CREATE INDEX idx_community_posts_list
ON community_posts (deleted, created_at, community_post_id);

CREATE INDEX idx_community_comments_post
ON community_comments (community_post_id, deleted, created_at, community_comment_id);

CREATE INDEX idx_community_comments_parent
ON community_comments (parent_comment_id, deleted, created_at, community_comment_id);

CREATE INDEX idx_community_post_views_post
ON community_post_views (community_post_id);

CREATE INDEX idx_temp_member_used_created
ON temp_upload_files (member_id, used, created_at);

CREATE INDEX idx_temp_signup_used_created
ON temp_upload_files (signup_key, used, created_at);

CREATE INDEX idx_temp_used_created
ON temp_upload_files (used, created_at);

