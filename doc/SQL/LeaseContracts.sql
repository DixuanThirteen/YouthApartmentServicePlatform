CREATE TABLE lease_contracts (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  contract_no     VARCHAR(64) NOT NULL,

  booking_id      BIGINT NULL,                 -- 来源预订单(可空：线下直接签约)
  room_id         BIGINT NOT NULL,
  user_id         BIGINT NOT NULL,

  start_date      DATE NOT NULL,               -- 起租日
  end_date        DATE NOT NULL,               -- 到期日

  rent_cent       INT UNSIGNED NOT NULL,       -- 月租(分)快照
  deposit_cent    INT UNSIGNED NOT NULL,       -- 押金(分)快照
  pay_cycle       TINYINT NOT NULL DEFAULT 1,   -- 1月付/3季付等

  status          TINYINT NOT NULL DEFAULT 0,   -- 0=draft 1=signed 2=active 3=terminated 4=expired
  signed_at       DATETIME NULL,
  terminated_at   DATETIME NULL,
  terminate_reason VARCHAR(255) NULL,

  created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_contract_no (contract_no),
  KEY idx_room_id (room_id),
  KEY idx_user_id (user_id),
  KEY idx_status (status),

  CONSTRAINT fk_contracts_room FOREIGN KEY (room_id) REFERENCES rooms(id),
  CONSTRAINT fk_contracts_booking FOREIGN KEY (booking_id) REFERENCES room_bookings(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租赁合同';