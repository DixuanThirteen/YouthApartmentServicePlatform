CREATE TABLE rooms (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,

  apartment_id     BIGINT NOT NULL,                 -- apartments.id
  room_type_id     BIGINT NOT NULL,                 -- room_types.id（别墅也可以定义成“整栋/整套”户型）

  -- 位置标识：允许为空以适配不同形态（别墅/分散式/公寓楼）
  building_no      VARCHAR(50) NULL,                -- 楼栋号/栋（如“8栋”）
  unit_no          VARCHAR(50) NULL,                -- 单元（可选）
  floor_no         INT NULL,                        -- 楼层（可选）
  room_no          VARCHAR(100) NULL,               -- 房号/门牌（可选：别墅可能没有）
  display_name     VARCHAR(200) NULL,               -- 展示名（如“翠湖别墅8栋”/“A座1203”）

  -- 为唯一性做归一化（MySQL UNIQUE 对 NULL 不严格）
  building_no_norm VARCHAR(50)
    GENERATED ALWAYS AS (IFNULL(building_no, '')) STORED,
  unit_no_norm     VARCHAR(50)
    GENERATED ALWAYS AS (IFNULL(unit_no, '')) STORED,
  room_no_norm     VARCHAR(100)
    GENERATED ALWAYS AS (IFNULL(room_no, '')) STORED,

  -- 可选覆盖字段
  area_sqm         DECIMAL(8,2),
  orientation      TINYINT,
  rent_cent        INT UNSIGNED,                    -- 分；为空则走 room_types.rent_cent
  deposit_cent     INT UNSIGNED,                    -- 分；为空则走 room_types.deposit_cent

  status           TINYINT NOT NULL DEFAULT 1,
  rent_status      TINYINT NOT NULL DEFAULT 0,       -- 0空置 1预订 2出租 3下架(自定义)

  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  -- 同一公寓下，“位置组合”必须唯一：支持仅楼栋的情况（room_no_norm='' 也会参与唯一）
  UNIQUE KEY uk_room_location (apartment_id, building_no_norm, unit_no_norm, room_no_norm),

  KEY idx_apartment_id (apartment_id),
  KEY idx_room_type_id (room_type_id),
  KEY idx_rent_status (rent_status),

  CONSTRAINT fk_rooms_apartment
    FOREIGN KEY (apartment_id) REFERENCES apartments(id),

  CONSTRAINT fk_rooms_room_type
    FOREIGN KEY (room_type_id) REFERENCES room_types(id),

  -- 至少要有一个位置标识（楼栋/房号/展示名三者至少填一个）
  CONSTRAINT ck_rooms_has_identifier
    CHECK (
      (building_no IS NOT NULL AND building_no <> '')
      OR (room_no IS NOT NULL AND room_no <> '')
      OR (display_name IS NOT NULL AND display_name <> '')
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间/房源表(兼容分散式/别墅/公寓楼)';

ALTER TABLE rooms
  ADD COLUMN room_code VARCHAR(64) NULL,
  ADD UNIQUE KEY uk_room_code (room_code);