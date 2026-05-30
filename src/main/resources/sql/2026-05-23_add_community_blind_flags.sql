ALTER TABLE community_posts
    ADD COLUMN blinded TINYINT NOT NULL DEFAULT 0 AFTER views;

ALTER TABLE community_comments
    ADD COLUMN blinded TINYINT NOT NULL DEFAULT 0 AFTER content;
