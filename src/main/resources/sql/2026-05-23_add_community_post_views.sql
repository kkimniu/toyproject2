CREATE TABLE IF NOT EXISTS community_post_views (
    community_post_view_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    community_post_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_community_post_views_post_member (community_post_id, member_id),

    FOREIGN KEY (community_post_id) REFERENCES community_posts(community_post_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

CREATE INDEX idx_community_post_views_post
ON community_post_views (community_post_id);
