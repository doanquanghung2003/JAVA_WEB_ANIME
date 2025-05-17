CREATE TABLE IF NOT EXISTS view_tracking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    anime_id INTEGER NOT NULL,
    account_id INTEGER NOT NULL,
    start_time DATETIME NOT NULL,
    watch_duration INTEGER NOT NULL DEFAULT 0,
    counted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (anime_id) REFERENCES anime(id),
    FOREIGN KEY (account_id) REFERENCES account(id)
); 