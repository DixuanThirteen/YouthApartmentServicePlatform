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

 Date: 23/12/2025 15:46:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for room_types
-- ----------------------------
DROP TABLE IF EXISTS `room_types`;
CREATE TABLE `room_types`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `apartment_id` bigint NOT NULL,
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `bedroom_count` tinyint UNSIGNED NULL DEFAULT 1,
  `living_count` tinyint UNSIGNED NULL DEFAULT 0,
  `bathroom_count` tinyint UNSIGNED NULL DEFAULT 1,
  `kitchen_count` tinyint UNSIGNED NULL DEFAULT 0,
  `area_sqm` decimal(8, 2) NULL DEFAULT NULL,
  `bed_count` tinyint UNSIGNED NULL DEFAULT NULL,
  `capacity` tinyint UNSIGNED NULL DEFAULT NULL,
  `rent_cent` int UNSIGNED NULL DEFAULT NULL,
  `deposit_cent` int UNSIGNED NULL DEFAULT NULL,
  `has_window` tinyint NOT NULL DEFAULT 1,
  `has_balcony` tinyint NOT NULL DEFAULT 0,
  `orientation` tinyint NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_apartment_id`(`apartment_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_room_types_apartment` FOREIGN KEY (`apartment_id`) REFERENCES `apartments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ck_room_types_area` CHECK ((`area_sqm` is null) or (`area_sqm` >= 0)),
  CONSTRAINT `ck_room_types_rent` CHECK ((`rent_cent` is null) or (`rent_cent` >= 0))
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '房型/户型表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
