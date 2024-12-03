-- 응원하기
CREATE TABLE `cheer_up`
(
    `id`          bigint NOT NULL AUTO_INCREMENT COMMENT 'cheer up id',
    `uid`         bigint NOT NULL COMMENT 'uid',
    `target_uid`  bigint NOT NULL COMMENT 'target_uid',
    `cheered_at`  date   NOT NULL COMMENT '응원한 날짜',
    `created_at`  datetime DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='응원하기';
CREATE UNIQUE INDEX uidx__uid__target_uid__cheered_at ON cheer_up (uid, target_uid, cheered_at);
CREATE INDEX idx__target ON cheer_up (target_uid);
CREATE INDEX idx__cheered_at__uid ON cheer_up (cheered_at, uid);
