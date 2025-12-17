CREATE TABLE room_types (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,

  apartment_id     BIGINT NOT NULL,                 -- 所属公寓项目 apartments.id
  name             VARCHAR(200) NOT NULL,           -- 户型名称：如“一居室A/朝南大床房”
  bedroom_count    TINYINT UNSIGNED DEFAULT 1,       -- 室
  living_count     TINYINT UNSIGNED DEFAULT 0,       -- 厅（可选）
  bathroom_count   TINYINT UNSIGNED DEFAULT 1,       -- 卫
  kitchen_count    TINYINT UNSIGNED DEFAULT 0,       -- 厨（可选）

  area_sqm         DECIMAL(8,2),                    -- 面积(㎡)
  bed_count        TINYINT UNSIGNED,                -- 床位数（合租场景可用）
  capacity         TINYINT UNSIGNED,                -- 建议入住人数

  rent_cent        INT UNSIGNED,                    -- 该户型基础月租(分，可选)
  deposit_cent     INT UNSIGNED,                    -- 该户型押金(分，可选)

  has_window       TINYINT NOT NULL DEFAULT 1,       -- 0=无窗 1=有窗（可选）
  has_balcony      TINYINT NOT NULL DEFAULT 0,       -- 0/1（可选）
  orientation      TINYINT,                          -- 朝向(1东2南3西4北等，自定义字典)

  description      TEXT,

  status           TINYINT NOT NULL DEFAULT 1,
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  KEY idx_apartment_id (apartment_id),
  KEY idx_status (status),

  CONSTRAINT fk_room_types_apartment
    FOREIGN KEY (apartment_id) REFERENCES apartments(id),

  CONSTRAINT ck_room_types_rent
    CHECK (rent_cent IS NULL OR rent_cent >= 0),

  CONSTRAINT ck_room_types_area
    CHECK (area_sqm IS NULL OR area_sqm >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房型/户型表';