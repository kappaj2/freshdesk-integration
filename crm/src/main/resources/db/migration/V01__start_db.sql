CREATE TABLE IF NOT EXISTS `customers`
(
    `customer_id`     bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `name`            VARCHAR(255)        NOT NULL,
    `surname`         VARCHAR(255)        NOT NULL,
    `email`           VARCHAR(255)        NOT NULL,
    `cell_number`     VARCHAR(25)         NULL,
    `id_number`       VARCHAR(25)         NULL,
    `customer_status` enum ('PENDING_VALIDATION','ACTIVE','SUSPENDED','DELETED') DEFAULT NULL,
    `country_code`    VARCHAR(255)        NULL                                   DEFAULT NULL,
    `date_created`    timestamp           NOT NULL                               DEFAULT current_timestamp(),
    `date_modified`   timestamp           NULL                                   DEFAULT NULL,
    PRIMARY KEY (`customer_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;






