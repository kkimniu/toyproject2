ALTER TABLE members
ADD COLUMN ban_count INT NOT NULL DEFAULT 0
AFTER report_count;
