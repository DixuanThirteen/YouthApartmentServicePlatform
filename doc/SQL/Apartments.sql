/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 90400 (9.4.0)
 Source Host           : localhost:3306
 Source Schema         : yasp

 Target Server Type    : MySQL
 Target Server Version : 90400 (9.4.0)
 File Encoding         : 65001

 Date: 23/12/2025 15:45:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for apartments
-- ----------------------------
DROP TABLE IF EXISTS `apartments`;
CREATE TABLE `apartments`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `provider_id` bigint NOT NULL,
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `type` tinyint NOT NULL DEFAULT 1,
  `province_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `city_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `district_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `latitude` decimal(10, 7) NULL DEFAULT NULL,
  `longitude` decimal(10, 7) NULL DEFAULT NULL,
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `rent_min_cent` int UNSIGNED NULL DEFAULT NULL,
  `rent_max_cent` int UNSIGNED NULL DEFAULT NULL,
  `deposit_cent` int UNSIGNED NULL DEFAULT NULL,
  `pay_cycle` tinyint NULL DEFAULT NULL,
  `min_lease_months` int UNSIGNED NULL DEFAULT NULL,
  `max_lease_months` int UNSIGNED NULL DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `publish_status` tinyint NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_provider_id`(`provider_id` ASC) USING BTREE,
  INDEX `idx_region`(`province_code` ASC, `city_code` ASC, `district_code` ASC) USING BTREE,
  INDEX `idx_status_publish`(`status` ASC, `publish_status` ASC) USING BTREE,
  CONSTRAINT `fk_apartments_provider` FOREIGN KEY (`provider_id`) REFERENCES `providers` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ck_apartments_lease_range` CHECK ((`min_lease_months` is null) or (`max_lease_months` is null) or (`min_lease_months` <= `max_lease_months`)),
  CONSTRAINT `ck_apartments_rent_range` CHECK ((`rent_min_cent` is null) or (`rent_max_cent` is null) or (`rent_min_cent` <= `rent_max_cent`))
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公寓表(项目/门店级)' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
