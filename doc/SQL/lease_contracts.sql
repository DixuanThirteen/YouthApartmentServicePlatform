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

 Date: 23/12/2025 15:46:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for lease_contracts
-- ----------------------------
DROP TABLE IF EXISTS `lease_contracts`;
CREATE TABLE `lease_contracts`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `contract_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `booking_id` bigint NULL DEFAULT NULL,
  `room_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `rent_cent` int UNSIGNED NOT NULL,
  `deposit_cent` int UNSIGNED NOT NULL,
  `pay_cycle` tinyint NOT NULL DEFAULT 1,
  `status` tinyint NOT NULL DEFAULT 0,
  `signed_at` datetime NULL DEFAULT NULL,
  `terminated_at` datetime NULL DEFAULT NULL,
  `terminate_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_contract_no`(`contract_no` ASC) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `fk_contracts_booking`(`booking_id` ASC) USING BTREE,
  CONSTRAINT `fk_contracts_booking` FOREIGN KEY (`booking_id`) REFERENCES `room_bookings` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_contracts_room` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '租赁合同' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
