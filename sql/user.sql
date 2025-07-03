/*
 Navicat Premium Data Transfer

 Source Server         : node73_mysql
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : node73:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 15/11/2024 19:42:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                         `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
                         `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                         `age` tinyint UNSIGNED NULL DEFAULT NULL,
                         `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                         `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                         `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                         `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `is_delete` tinyint UNSIGNED NULL DEFAULT 0,
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
