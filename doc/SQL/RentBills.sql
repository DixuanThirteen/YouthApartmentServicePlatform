CREATE TABLE rent_bills (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  bill_no       VARCHAR(64) NOT NULL,

  contract_id   BIGINT NOT NULL,
  period_start  DATE NOT NULL,
  period_end    DATE NOT NULL,

  amount_cent   INT UNSIGNED NOT NULL,         -- 本期应收(分)
  due_date      DATE NOT NULL,                 -- 到期日

  status        TINYINT NOT NULL DEFAULT 0,     -- 0=unpaid 1=paid 2=overdue 3=void
  paid_cent     INT UNSIGNED NOT NULL DEFAULT 0,
  paid_at       DATETIME NULL,

  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_bill_no (bill_no),
  KEY idx_contract_id (contract_id),
  KEY idx_status_due (status, due_date),

  CONSTRAINT fk_bills_contract FOREIGN KEY (contract_id) REFERENCES lease_contracts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租金账单';