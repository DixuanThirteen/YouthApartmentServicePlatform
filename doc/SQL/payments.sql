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

 Date: 23/12/2025 15:46:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for payments
-- ----------------------------
DROP TABLE IF EXISTS `payments`;
CREATE TABLE `payments`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payment_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `booking_id` bigint NOT NULL,
  `amount_cent` int UNSIGNED NOT NULL,
  `status` enum('PENDING','SUCCESS','FAILED','CANCEL') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING',
  `gateway_response` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `transaction_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `payment_id`(`payment_id` ASC) USING BTREE,
  INDEX `fk_payments_booking`(`booking_id` ASC) USING BTREE,
  CONSTRAINT `fk_payments_booking` FOREIGN KEY (`booking_id`) REFERENCES `room_bookings` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
