CREATE TABLE providers (
id              BIGINT PRIMARY KEY AUTO_INCREMENT,
name            VARCHAR(200) NOT NULL,     -- 机构或企业名称
type            TINYINT NOT NULL,          -- 1=government, 2=enterprise, 3=other
contact_person  VARCHAR(100),              -- 对接人姓名
contact_phone   VARCHAR(30),
contact_email   VARCHAR(100),
address         VARCHAR(255),              -- 办公地址 / 注册地址（简单留一个字段）
license_number  VARCHAR(100),              -- 营业执照/备案号（可选）
description     TEXT,                      -- 简短介绍
status          TINYINT NOT NULL DEFAULT 1,  -- 1=active,0=disabled,pending等可后续扩展
register_data   DATE NOT NULL, 
created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)