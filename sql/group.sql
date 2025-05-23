-- 그룹
CREATE TABLE `group`
(
    `id`            bigint      NOT NULL AUTO_INCREMENT COMMENT 'group id',
    `name`          varchar(32) NOT NULL COMMENT '그룹명',
    `description`   varchar(512)         DEFAULT NULL COMMENT '그룹 설명',
    `owner_uid`     bigint      NOT NULL COMMENT 'owner uid',
    `is_hidden`     tinyint     NOT NULL DEFAULT 0 COMMENT '숨김 여부',
    `join_code`     varchar(128)         DEFAULT NULL COMMENT '참여코드',
    `user_count`    int         NOT NULL DEFAULT 1 COMMENT '그룹원 수',
    `user_capacity` int         NOT NULL DEFAULT 0 COMMENT '그룹원 정원',
    `created_at`    datetime             DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at`   datetime             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
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

-- 그룹 유저 스코어
CREATE TABLE `group_user_score`
(
    `id`            bigint NOT NULL AUTO_INCREMENT COMMENT 'group user score id',
    `group_id`      bigint NOT NULL COMMENT 'group id',
    `group_user_id` bigint NOT NULL COMMENT 'group user id',
    `uid`           bigint NOT NULL COMMENT 'uid',
    `score`         int      DEFAULT NULL COMMENT '스코어',
    `created_at`    datetime DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at`   datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=200000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='그룹 유저 스코어';
CREATE UNIQUE INDEX uidx__group_user_id ON group_user_score (group_user_id);
CREATE INDEX idx__group_id__group_user_id ON group_user_score (group_id, group_user_id);
CREATE INDEX idx__uid ON group_user_score (uid);

-- 그룹 태그
CREATE TABLE `group_tag`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT 'group tag id',
    `name`        varchar(30) NOT NULL COMMENT '태그명',
    `created_at`  datetime DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT=200000 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='그룹 태그';
CREATE UNIQUE INDEX uidx__name ON group_tag (name);

-- 그룹 태그 매핑
CREATE TABLE `group_tag_map`
(
    `id`          bigint NOT NULL AUTO_INCREMENT COMMENT 'group tag map id',
    `group_id`    bigint NOT NULL COMMENT 'group id',
    `tag_id`      bigint NOT NULL COMMENT 'group tag id',
    `created_at`  datetime DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT=200000 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='그룹 태그 매핑';
CREATE UNIQUE INDEX uidx__group_id__tag_id ON group_tag_map (group_id, tag_id);
CREATE INDEX idx__tag_id ON group_tag_map (tag_id);
