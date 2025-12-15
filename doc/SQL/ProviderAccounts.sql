CREATE TABLE ProviderAccounts (
id              BIGINT PRIMARY KEY AUTO_INCREMENT,
provider_id     BIGINT NOT NULL,
username        VARCHAR(50) NOT NULL UNIQUE,
email           VARCHAR(100),
phone           VARCHAR(20),
password_hash   VARCHAR(255) NOT NULL,     -- 存 BCrypt 密码哈希
role            TINYINT NOT NULL DEFAULT 1, -- 1=admin,2=staff 等
status          TINYINT NOT NULL DEFAULT 1, -- 1=active,0=disabled
created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
CONSTRAINT fk_ProviderAccount_Provider
FOREIGN KEY (provider_id) REFERENCES providers(id)
);