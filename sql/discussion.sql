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
