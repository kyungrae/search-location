CREATE TABLE IF NOT EXISTS `keyword_search_log`
(
    `keyword`      VARCHAR(255) NOT NULL,
    `search_count` BIGINT       NOT NULL,
    CONSTRAINT PRIMARY KEY (`keyword`),
    INDEX `IDX_search_count` (`search_count` DESC)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;