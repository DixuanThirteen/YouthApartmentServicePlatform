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

 Date: 23/12/2025 15:46:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for rooms
-- ----------------------------
DROP TABLE IF EXISTS `rooms`;
CREATE TABLE `rooms`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `apartment_id` bigint NOT NULL,
  `room_type_id` bigint NOT NULL,
  `building_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `unit_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `floor_no` int NULL DEFAULT NULL,
  `room_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `display_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `building_no_norm` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci GENERATED ALWAYS AS (ifnull(`building_no`,_utf8mb4'')) STORED NULL,
  `unit_no_norm` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci GENERATED ALWAYS AS (ifnull(`unit_no`,_utf8mb4'')) STORED NULL,
  `room_no_norm` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci GENERATED ALWAYS AS (ifnull(`room_no`,_utf8mb4'')) STORED NULL,
  `area_sqm` decimal(8, 2) NULL DEFAULT NULL,
  `orientation` tinyint NULL DEFAULT NULL,
  `rent_cent` int UNSIGNED NOT NULL,
  `deposit_cent` int UNSIGNED NOT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `rent_status` tinyint NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `room_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_room_location`(`apartment_id` ASC, `building_no_norm` ASC, `unit_no_norm` ASC, `room_no_norm` ASC) USING BTREE,
  UNIQUE INDEX `uk_room_code`(`room_code` ASC) USING BTREE,
  INDEX `idx_apartment_id`(`apartment_id` ASC) USING BTREE,
  INDEX `idx_room_type_id`(`room_type_id` ASC) USING BTREE,
  INDEX `idx_rent_status`(`rent_status` ASC) USING BTREE,
  CONSTRAINT `fk_rooms_apartment` FOREIGN KEY (`apartment_id`) REFERENCES `apartments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_rooms_room_type` FOREIGN KEY (`room_type_id`) REFERENCES `room_types` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ck_rooms_has_identifier` CHECK (((`building_no` is not null) and (`building_no` <> _utf8mb4'')) or ((`room_no` is not null) and (`room_no` <> _utf8mb4'')) or ((`display_name` is not null) and (`display_name` <> _utf8mb4'')))
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '房间/房源表(兼容分散式/别墅/公寓楼)' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
