CREATE TABLE IF NOT EXISTS fresh_outgoing_messages
(
    `message_id`             bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `date_created`           TIMESTAMP           NOT NULL,
    `freshdesk_message_type` enum ('CUSTOMER_CREATION','CUSTOMER_UPDATE') DEFAULT NULL,
    `message_payload`        VARCHAR(8192)       NOT NULL,
    `aws_queue_message_id`   VARCHAR(255),
    `message_uuid`           VARCHAR(255),
    PRIMARY KEY (`message_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;