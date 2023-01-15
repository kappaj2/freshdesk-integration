CREATE TABLE IF NOT EXISTS crm_agents
(
    `agent_id`      bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `name`          VARCHAR(255)        NOT NULL,
    `surname`       VARCHAR(255)        NOT NULL,
    `email_address` VARCHAR(255)        NOT NULL,
    `date_created`  TIMESTAMP           NOT NULL DEFAULT NOW(),
    `date_modified` TIMESTAMP           NULL,
    `modified_by`   VARCHAR(255)        NULL,
    PRIMARY KEY (`agent_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;