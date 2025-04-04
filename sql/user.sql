-- 유저 정보
CREATE TABLE `user_info`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT 'user id',
    `nickname`    varchar(256) NOT NULL COMMENT '닉네임',
    `level`       int      DEFAULT 1 COMMENT '유저 레벨',
    `created_at`  datetime DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=200000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='유저 정보';
CREATE UNIQUE INDEX uidx__nickname ON user_info (nickname);

-- 일반 회원가입 유저 정보
CREATE TABLE `credential_user_info`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT 'credential_user_info id',
    `uid`         bigint       NOT NULL COMMENT 'user id',
    `username`    varchar(256) NOT NULL COMMENT '로그인 id',
    `password`    varchar(512) NOT NULL COMMENT '로그인 pw',
    `created_at`  datetime DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=200000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='유저 정보';
CREATE UNIQUE INDEX uidx__uid ON credential_user_info (uid);
CREATE UNIQUE INDEX uidx__username ON credential_user_info (username);

-- OAuth 회원가입 유저 정보
CREATE TABLE `oauth_user_info`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT 'oauth user info id',
    `uid`         bigint       NOT NULL COMMENT 'user id',
    `provider`    varchar(32)  NOT NULL COMMENT 'oauth provider',
    `oauth_id`    varchar(512) NOT NULL COMMENT 'oauth id',
    `created_at`  datetime DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=200000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='oauth 유저 정보';
CREATE UNIQUE INDEX uidx__uid__provider ON oauth_user_info (uid, provider);
CREATE INDEX idx__oauth_id__provider ON oauth_user_info (oauth_id, provider);
