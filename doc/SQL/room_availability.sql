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

 Date: 23/12/2025 15:46:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for room_availability
-- ----------------------------
DROP TABLE IF EXISTS `room_availability`;
CREATE TABLE `room_availability`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL,
  `occupied_from` date NOT NULL,
  `occupied_to` date NOT NULL,
  `source` enum('booking','contract') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `source_id` bigint NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  CONSTRAINT `fk_availability_room` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '房间占用时间段表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
