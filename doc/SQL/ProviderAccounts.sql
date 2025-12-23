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

 Date: 23/12/2025 15:46:26
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for provideraccounts
-- ----------------------------
DROP TABLE IF EXISTS `provideraccounts`;
CREATE TABLE `provideraccounts`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `provider_id` bigint NOT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` tinyint NOT NULL DEFAULT 1,
  `status` tinyint NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `fk_ProviderAccount_Provider`(`provider_id` ASC) USING BTREE,
  CONSTRAINT `fk_ProviderAccount_Provider` FOREIGN KEY (`provider_id`) REFERENCES `providers` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
