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

-- 그룹
CREATE TABLE `group`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT 'group id',
    `name`        varchar(32)  NOT NULL COMMENT '그룹명',
    `description` varchar(512)          DEFAULT NULL COMMENT '그룹 설명',
    `owner_uid`   bigint       NOT NULL COMMENT 'owner uid',
    `is_hidden`   tinyint      NOT NULL DEFAULT 0 COMMENT '숨김 여부',
    `join_code`   varchar(128) not null comment '참여코드',
    `created_at`  datetime              DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at` datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=200000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='그룹';

CREATE UNIQUE INDEX uidx__name ON `group` (name);
CREATE INDEX idx__owner_uid ON `group` (owner_uid);
CREATE INDEX idx__join_code ON `group` (join_code);

-- 그룹 유저
CREATE TABLE `group_user`
(
    `id`          bigint NOT NULL AUTO_INCREMENT COMMENT 'group user id',
    `group_id`    bigint NOT NULL COMMENT 'group id',
    `uid`         bigint NOT NULL COMMENT 'uid',
    `created_at`  datetime DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=200000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='그룹 유저';
CREATE UNIQUE INDEX uidx__group_id__uid ON group_user (group_id, uid);
CREATE INDEX idx__uid ON group_user (uid);

-- 포즈 스냅샵
CREATE TABLE `pose_snapshot`
(
    `id`          bigint          NOT NULL AUTO_INCREMENT COMMENT 'pose snapshot id',
    `uid`         bigint          NOT NULL COMMENT 'uid',
    `score`       DECIMAL(20, 16) NOT NULL COMMENT '포즈 신뢰도 종합',
    `type`        VARCHAR(32)     NOT NULL COMMENT '포즈 타입',
    `image_url`   VARCHAR(512)    NOT NULL COMMENT '포즈 이미지 url',
    `created_at`  datetime DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='포즈 스냅샷';
CREATE INDEX idx__uid ON pose_snapshot (uid);
CREATE INDEX idx__created_at ON pose_snapshot (created_at);

-- 포즈 key point 스냅샷
CREATE TABLE `pose_key_point_snapshot`
(
    `id`               bigint          NOT NULL AUTO_INCREMENT COMMENT 'pose key point snapshot id',
    `pose_snapshot_id` bigint          NOT NULL COMMENT 'post snapshot id',
    `position`         VARCHAR(32)     NOT NULL COMMENT '스냅샷 위치',
    `x`                DECIMAL(20, 16) NOT NULL COMMENT 'x 좌표',
    `y`                DECIMAL(20, 16) NOT NULL COMMENT 'y 좌표',
    `confidence`       DECIMAL(20, 16) NOT NULL COMMENT '신뢰도'
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='포즈 key point';
CREATE INDEX uidx__pose_snapshot_id__position ON pose_key_point_snapshot (pose_snapshot_id, position);

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

-- 문의하기
CREATE TABLE `discussion`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT,
    `uid`         bigint       NOT NULL COMMENT 'uid',
    `type`        varchar(32)  NOT NULL COMMENT '문의하기 유형',
    `title`       varchar(512) NOT NULL COMMENT '문의하기 제목',
    `content`     text         NOT NULL COMMENT '문의하기 본문',
    `created_at`  datetime DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=200000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
CREATE INDEX idx__uid ON discussion (uid);

-- 문의하기 답변
CREATE TABLE `discussion_comment`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `uid`           bigint       NOT NULL COMMENT 'uid',
    `discussion_id` bigint       NOT NULL COMMENT 'discussion_id',
    `title`         varchar(512) NOT NULL COMMENT '답변 제목',
    `content`       text         NOT NULL COMMENT '답변 본문',
    `created_at`    datetime DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at`   datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=200000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
CREATE INDEX idx__discussion_id ON discussion_comment (discussion_id);
CREATE INDEX idx__uid ON discussion_comment (uid);

-- 포즈 카운트 집계
CREATE TABLE `pose_count`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `uid`         bigint NOT NULL COMMENT 'uid',
    `total_count` text   NOT NULL COMMENT '집계 데이터',
    `date`        datetime DEFAULT CURRENT_TIMESTAMP COMMENT '기준 날짜',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
CREATE INDEX idx__uid__date ON pose_count (uid, date);
CREATE INDEX idx__date ON pose_count (date);
