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

-- 포즈 카운트 집계
CREATE TABLE `pose_count`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `uid`         bigint NOT NULL COMMENT 'uid',
    `total_count` text   NOT NULL COMMENT '집계 데이터',
    `date`        date   NOT NULL COMMENT '기준 날짜',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
CREATE UNIQUE INDEX uidx__uid__date ON pose_count (uid, date);
CREATE INDEX idx__date ON pose_count (date);

-- 자세 알림
CREATE TABLE `pose_noti`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT,
    `uid`         bigint      NOT NULL COMMENT 'uid',
    `is_active`   tinyint     NOT NULL DEFAULT 0 COMMENT '활성화 여부',
    `duration`    varchar(32) NOT NULL COMMENT '알림 주기',
    `created_at`  datetime             DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at` datetime             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT '자세 알림';
CREATE INDEX uidx__uid ON pose_noti (uid);

-- 포즈 레이아웃
CREATE TABLE `pose_layout`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `uid`         bigint NOT NULL COMMENT 'uid',
    `created_at`  datetime DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `modified_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT '포즈 레이아웃';
CREATE INDEX idx__uid ON pose_layout (uid);

-- 포즈 레이아웃 포인트
CREATE TABLE `pose_layout_point`
(
    `id`             bigint          NOT NULL AUTO_INCREMENT COMMENT 'pose key point snapshot id',
    `pose_layout_id` bigint          NOT NULL COMMENT 'pose layout id',
    `position`       VARCHAR(32)     NOT NULL COMMENT '스냅샷 위치',
    `x`              DECIMAL(20, 16) NOT NULL COMMENT 'x 좌표',
    `y`              DECIMAL(20, 16) NOT NULL COMMENT 'y 좌표',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='포즈 레이아웃 point';
CREATE INDEX uidx__pose_snapshot_id__position ON pose_layout_point (pose_layout_id, position);
