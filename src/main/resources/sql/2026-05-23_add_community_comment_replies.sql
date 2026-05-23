ALTER TABLE community_comments
    ADD COLUMN parent_comment_id BIGINT NULL AFTER community_post_id,
    ADD CONSTRAINT fk_community_comments_parent
        FOREIGN KEY (parent_comment_id) REFERENCES community_comments(community_comment_id) ON DELETE CASCADE;

CREATE INDEX idx_community_comments_parent
ON community_comments (parent_comment_id, deleted, created_at, community_comment_id);
