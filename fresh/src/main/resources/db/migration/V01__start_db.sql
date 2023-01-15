--
--  Table to record all message received.
--
CREATE TABLE IF NOT EXISTS fresh_received_messages
(
    `fresh_message_id`     bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `aws_queue_message_id` VARCHAR(255)        NOT NULL,
    `message_payload`      VARCHAR(8192)       NOT NULL,
    `message_type`         VARCHAR(255)        NOT NULL,
    `originator`           VARCHAR(255)        NOT NULL,
    `message_status`       VARCHAR(50)         NOT NULL,
    `retry_count`          INT                 NOT NULL default 0,
    `last_delay_time`      INT                 NOT NULL default 15,
    `sent_timestamp`       TIMESTAMP           NOT NULL,
    `date_created`         TIMESTAMP           NOT NULL DEFAULT now(),
    `date_modified`        TIMESTAMP           NULL,
    PRIMARY KEY (`fresh_message_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
--  These are all Freshdesk related message handling information
--
CREATE TABLE IF NOT EXISTS fresh_message_processing
(
    `process_message_id`     bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `fresh_message_id`       bigint(20) unsigned NOT NULL,
    `message_type`           VARCHAR(255)        NOT NULL,
    `fresh_request_payload`  VARCHAR(8192)       NOT NULL,
    `fresh_response_payload` VARCHAR(8192)       NULL,
    `fresh_response_code`    INT                 NULL,
    `date_created`           TIMESTAMP           NOT NULL DEFAULT now(),
    `date_modified`          TIMESTAMP           NULL,
    PRIMARY KEY (`process_message_id`),
    CONSTRAINT `fk_fresh_message_id`
        FOREIGN KEY (`fresh_message_id`)
            REFERENCES `fresh_received_messages` (`fresh_message_id`)
            ON DELETE CASCADE
            ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
--  Deadletter table for reporting failures.
--
CREATE TABLE IF NOT EXISTS fresh_dead_letter_messages
(
    `fresh_failed_id`  bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `fresh_message_id` bigint(20) unsigned NOT NULL,
    `last_status`      VARCHAR(255)        NOT NULL,
    `date_created`     TIMESTAMP           NOT NULL DEFAULT now(),
    `email_reported`   BOOLEAN             NOT NULL DEFAULT FALSE,
    `date_modified`    TIMESTAMP           NULL,
    PRIMARY KEY (`fresh_failed_id`),
    INDEX `message_id_indx3` (`fresh_message_id`),
    INDEX `email_reported_indx` (`email_reported`),
    CONSTRAINT `fk_fresh_message_id2`
        FOREIGN KEY (`fresh_message_id`)
            REFERENCES `fresh_received_messages` (`fresh_message_id`)
            ON DELETE CASCADE
            ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;