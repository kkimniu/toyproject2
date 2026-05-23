CREATE TABLE IF NOT EXISTS community_comments (
    community_comment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    community_post_id BIGINT NOT NULL,
    parent_comment_id BIGINT,
    member_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    deleted TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (community_post_id) REFERENCES community_posts(community_post_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES community_comments(community_comment_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES members(member_id)
);

ALTER TABLE report
    ADD COLUMN community_post_id BIGINT NULL AFTER chat_room_id,
    ADD COLUMN community_comment_id BIGINT NULL AFTER community_post_id,
    MODIFY report_type ENUM('ROOM','MEMBER','CHAT','COMMUNITY_POST','COMMUNITY_COMMENT') NOT NULL DEFAULT 'ROOM';

CREATE INDEX idx_community_comments_post
ON community_comments (community_post_id, deleted, created_at, community_comment_id);

CREATE INDEX idx_community_comments_parent
ON community_comments (parent_comment_id, deleted, created_at, community_comment_id);
