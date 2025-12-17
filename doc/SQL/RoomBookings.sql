CREATE TABLE room_bookings (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  booking_no    VARCHAR(64) NOT NULL,

  room_id       BIGINT NOT NULL,
  user_id       BIGINT NOT NULL,

  start_date    DATE NOT NULL,                 -- 计划入住日期
  lease_months  INT UNSIGNED NOT NULL,         -- 计划租期(月)
  rent_cent     INT UNSIGNED,                  -- 下单时确认的月租(分，可快照)
  deposit_cent  INT UNSIGNED,                  -- 下单时确认的押金(分，可快照)

  status        TINYINT NOT NULL DEFAULT 0,     -- 0=pending 1=confirmed 2=canceled 3=expired
  hold_until    DATETIME NULL,                 -- 锁房截止时间（到期可释放）

  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_booking_no (booking_no),
  KEY idx_room_id (room_id),
  KEY idx_user_id (user_id),
  KEY idx_status_hold (status, hold_until),

  CONSTRAINT fk_bookings_room FOREIGN KEY (room_id) REFERENCES rooms(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源预订/锁房单';