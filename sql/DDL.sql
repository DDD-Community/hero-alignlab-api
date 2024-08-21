-- scheme
CREATE
DATABASE hero CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- system log
CREATE TABLE `system_action_log`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `host`        varchar(256)                    DEFAULT NULL,
    `http_method` varchar(256)                    DEFAULT NULL,
    `ip_address`  varchar(256)                    DEFAULT NULL,
    `path`        varchar(256)                    DEFAULT NULL,
    `referer`     varchar(512)                    DEFAULT NULL,
    `user_agent`  varchar(256)                    DEFAULT NULL,
    `extra`       text COLLATE utf8mb4_general_ci DEFAULT NULL,
    `created_at`  datetime                        DEFAULT CURRENT_TIMESTAMP,
    `modified_at` datetime                        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT 'system log';

-- 이미지
CREATE TABLE `image_metadata`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT,
    `uid`         bigint       NOT NULL COMMENT 'uid, 미인증 요청인 경우 -1',
    `filename`    varchar(128) NOT NULL COMMENT '이미지 파일명',
    `image_url`   varchar(512) NOT NULL COMMENT '이미지 url',
    `type`        varchar(32)  NOT NULL COMMENT '이미지 사용처',
    `created_at`  datetime DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=200000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
CREATE INDEX idx__uid__type ON image_metadata (uid, type);
