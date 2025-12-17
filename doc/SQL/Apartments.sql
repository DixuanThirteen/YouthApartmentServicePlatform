-- 公寓表（项目级）
CREATE TABLE apartments (
                            id               BIGINT PRIMARY KEY AUTO_INCREMENT,

                            provider_id      BIGINT NOT NULL,                 -- providers.id
                            name             VARCHAR(200) NOT NULL,
                            type             TINYINT NOT NULL DEFAULT 1,       -- 1=集中式 2=分散式 3=人才/保障等

                            province_code    VARCHAR(20),
                            city_code        VARCHAR(20),
                            district_code    VARCHAR(20),
                            address          VARCHAR(255) NOT NULL,

                            latitude         DECIMAL(10,7),
                            longitude        DECIMAL(10,7),

                            cover_url        VARCHAR(500),
                            description      TEXT,

                            rent_min_cent    INT UNSIGNED,
                            rent_max_cent    INT UNSIGNED,
                            deposit_cent     INT UNSIGNED,

                            pay_cycle        TINYINT,                          -- 1=月付 3=季付 6=半年 12=年付
                            min_lease_months INT UNSIGNED,
                            max_lease_months INT UNSIGNED,

                            status           TINYINT NOT NULL DEFAULT 1,        -- 1=active 2=disabled
                            publish_status   TINYINT NOT NULL DEFAULT 0,        -- 0=未上架 1=已上架
                            created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                            KEY idx_provider_id (provider_id),
                            KEY idx_region (province_code, city_code, district_code),
                            KEY idx_status_publish (status, publish_status),

                            CONSTRAINT fk_apartments_provider
                                FOREIGN KEY (provider_id) REFERENCES providers(id),

                            CONSTRAINT ck_apartments_rent_range
                                CHECK (rent_min_cent IS NULL OR rent_max_cent IS NULL OR rent_min_cent <= rent_max_cent),

                            CONSTRAINT ck_apartments_lease_range
                                CHECK (min_lease_months IS NULL OR max_lease_months IS NULL OR min_lease_months <= max_lease_months)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公寓表(项目/门店级)';