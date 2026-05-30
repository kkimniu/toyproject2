CREATE TABLE IF NOT EXISTS admin_settings (
    setting_key VARCHAR(100) PRIMARY KEY,
    setting_value VARCHAR(100) NOT NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO admin_settings (setting_key, setting_value)
VALUES
    ('SANCTION_CANDIDATE_REPORT_THRESHOLD', '3'),
    ('REPORT_TREND_DAYS', '7')
ON DUPLICATE KEY UPDATE setting_value = setting_value;
