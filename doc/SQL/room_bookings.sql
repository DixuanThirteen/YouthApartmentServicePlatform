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

 Date: 23/12/2025 15:46:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for room_bookings
-- ----------------------------
DROP TABLE IF EXISTS `room_bookings`;
CREATE TABLE `room_bookings`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `booking_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `room_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `start_date` date NOT NULL,
  `lease_months` int UNSIGNED NOT NULL,
  `rent_cent` int UNSIGNED NULL DEFAULT NULL,
  `deposit_cent` int UNSIGNED NULL DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT 0,
  `hold_until` datetime NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_booking_no`(`booking_no` ASC) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status_hold`(`status` ASC, `hold_until` ASC) USING BTREE,
  CONSTRAINT `fk_bookings_room` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 44 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '房源预订/锁房单' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
