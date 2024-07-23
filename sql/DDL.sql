-- scheme
CREATE
DATABASE hero CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 유저 정보
CREATE TABLE `user_info`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT 'user id',
    `nickname`    varchar(64) NOT NULL COMMENT '닉네임',
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

-- 팀
CREATE TABLE `team`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT 'team id',
    `name`        varchar(32) NOT NULL COMMENT '팀명',
    `description` varchar(512) DEFAULT NULL COMMENT '팀 설명',
    `owner_uid`   bigint      NOT NULL COMMENT 'owner uid',
    `created_at`  datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=200000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='팀';

CREATE UNIQUE INDEX uidx__name ON `team` (name);
CREATE INDEX idx__owner_uid ON `team` (owner_uid);

-- 팀 유저
CREATE TABLE `team_user`
(
    `id`          bigint NOT NULL AUTO_INCREMENT COMMENT 'team user id',
    `team_id`    bigint NOT NULL COMMENT 'team id',
    `uid`         bigint NOT NULL COMMENT 'uid',
    `created_at`  datetime DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=200000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='팀 유저';

CREATE UNIQUE INDEX uidx__team_id__uid ON team_user (team_id, uid);
CREATE INDEX uidx__uid ON team_user (uid);
