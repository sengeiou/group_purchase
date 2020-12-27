/*
 * Copyright Ningbo Qishan Technology Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

CREATE DATABASE IF NOT EXISTS group_purchase DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

use group_purchase;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_activity_coupon
-- ----------------------------
DROP TABLE IF EXISTS `t_activity_coupon`;
CREATE TABLE `t_activity_coupon`
(
    `activity_coupon_id` int(11)                                                NOT NULL AUTO_INCREMENT COMMENT '编号',
  `activity_coupon_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动名称',
  `activity_remark` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动备注',
  `activity_model` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动模板',
  `activity_img` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动海报',
  `overlay_state` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否叠加（0不叠加，1叠加）',
  `now_state` int(1) NOT NULL DEFAULT 0 COMMENT '活动当前状态(0已结束，1未开始，2进行中)',
  `full_state` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否适用与所有商品的状态（0指定商品，1所有商品）',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `upate_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新时间',
  `start_date` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动开始时间',
  `end_date` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动结束时间',
  `delete_state` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户模板ID',
  PRIMARY KEY (`activity_coupon_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 328 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '优惠活动' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_activity_group
-- ----------------------------
DROP TABLE IF EXISTS `t_activity_group`;
CREATE TABLE `t_activity_group`  (
  `activity_group_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `activity_group_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动名称',
  `activity_remark` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动备注',
  `activity_img` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动海报',
  `limit_num` int(8) NULL DEFAULT NULL COMMENT '限制人数',
  `limit_time` int(8) NULL DEFAULT NULL COMMENT '限制时间',
  `overlay_state` tinyint(1) NULL DEFAULT NULL COMMENT '是否叠加（0不叠加，1叠加）',
  `now_state` int(1) NOT NULL DEFAULT 0 COMMENT '活动当前状态 同优惠券',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新时间',
  `start_date` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动开始时间',
  `end_date` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动结束时间',
  `delete_state` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户模板ID',
  PRIMARY KEY (`activity_group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '团购活动' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_activity_product
-- ----------------------------
DROP TABLE IF EXISTS `t_activity_product`;
CREATE TABLE `t_activity_product`  (
  `activity_product_id` bigint(20) NOT NULL COMMENT '编号',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '商品ID',
  `activity_id` int(11) NULL DEFAULT NULL COMMENT '活动ID',
  `activity_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '活动价',
  `activity_discount` decimal(3, 2) NULL DEFAULT NULL COMMENT '活动折扣',
  `activity_stock` int(8) NULL DEFAULT NULL COMMENT '活动库存',
  `sold_quantity` int(8) NOT NULL DEFAULT 0 COMMENT '已售数量',
  `max_quantity` int(8) NULL DEFAULT NULL COMMENT '限购',
  `activity_type` int(8) NULL DEFAULT NULL COMMENT '活动类型（优惠券活动1001，特价活动2001，拼团3001,秒杀4001，满减5001）',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户模板ID',
  `preheat_state` int(1) NULL DEFAULT NULL COMMENT '预热状态  0-无预热,1-带预热商品',
  `sort` int(3) NULL DEFAULT NULL,
  `home_view_stat` int(1) NULL DEFAULT NULL,
  `spec_type` int(1) NULL DEFAULT 1 COMMENT 'specType',
  `delete_state` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志',
  PRIMARY KEY (`activity_product_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '活动商品关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_activity_product_stock
-- ----------------------------
DROP TABLE IF EXISTS `t_activity_product_stock`;
CREATE TABLE `t_activity_product_stock`  (
  `activity_product_stock_id` int(11) NOT NULL AUTO_INCREMENT,
  `activity_product_id` bigint(20) NULL DEFAULT NULL,
  `product_spec_item_id` bigint(20) NULL DEFAULT NULL,
  `activity_stock` int(8) NULL DEFAULT NULL,
  `activity_price` double(10, 2) NULL DEFAULT NULL,
  `activity_type` int(4) NULL DEFAULT NULL,
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `delete_state` tinyint(1) NULL DEFAULT NULL,
  `activity_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`activity_product_stock_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 97 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_activity_seckill
-- ----------------------------
DROP TABLE IF EXISTS `t_activity_seckill`;
CREATE TABLE `t_activity_seckill`  (
  `activity_seckill_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `activity_seckill_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动名称',
  `activity_remark` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动备注',
  `activity_img` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动海报',
  `overlay_state` bit(1) NULL DEFAULT NULL COMMENT '是否叠加（0不叠加，1叠加）',
  `now_state` int(1) NULL DEFAULT NULL COMMENT '活动当前状态 同优惠券',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新时间',
  `start_date` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动开始时间',
  `end_date` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动结束时间',
  `delete_state` bit(1) NULL DEFAULT NULL COMMENT '删除标志',
  `preheat_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '预热时间  年-日-月 时-分 不填传空字符串',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户模板ID',
  PRIMARY KEY (`activity_seckill_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 172 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '秒杀活动' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_activity_special
-- ----------------------------
DROP TABLE IF EXISTS `t_activity_special`;
CREATE TABLE `t_activity_special`  (
  `activity_special_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `activity_special_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动名称',
  `activity_remark` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动备注',
  `activity_img` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动海报',
  `overlay_state` bit(1) NULL DEFAULT NULL COMMENT '是否叠加（0不叠加，1叠加）',
  `now_state` int(1) NULL DEFAULT NULL COMMENT '活动当前状态 同优惠券',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最后操作时间',
  `start_date` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动开始时间',
  `end_date` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动结束时间',
  `delete_state` bit(1) NULL DEFAULT NULL COMMENT '删除标志',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户模板ID',
  PRIMARY KEY (`activity_special_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '特价活动' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_agent
-- ----------------------------
DROP TABLE IF EXISTS `t_agent`;
CREATE TABLE `t_agent`  (
  `agent_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '代理人id',
  `wxuser_id` bigint(20) NOT NULL COMMENT '用户id',
  `agent_name` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '代理人姓名',
  `agent_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '代理人手机号',
  `agent_grade_id` int(11) NOT NULL COMMENT '代理等级',
  `agent_domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所在区域',
  `prohibit_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '禁用时间',
  `audit_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审核通过时间',
  `agent_status` int(1) NULL DEFAULT NULL COMMENT '代理人状态   1通过审核，2取消分销商资格(禁用)',
  `appmodel_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板id',
  `agent_bpavawice` double(10, 2) NULL DEFAULT NULL COMMENT '代理消费额度',
  `nike_name` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信昵称',
  PRIMARY KEY (`agent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_agent_grade
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_grade`;
CREATE TABLE `t_agent_grade`  (
  `agent_grade_id` int(11) NOT NULL AUTO_INCREMENT COMMENT ' 等级表id',
  `grade_name` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '等级名称',
  `grade_discount` decimal(10, 2) NULL DEFAULT NULL COMMENT '等级折扣',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `grade_info` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '代理等级说明',
  `upgrade_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '升级条件',
  `edit_state` int(1) NULL DEFAULT NULL COMMENT '可编辑状态  1-只可编辑折扣和说明,2-全部可编辑',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`agent_grade_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_agent_product
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_product`;
CREATE TABLE `t_agent_product`  (
  `agent_product_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `agent_stock` int(8) NULL DEFAULT NULL COMMENT '分销商品库存',
  `agent_sales_volume` int(8) NULL DEFAULT NULL COMMENT '分销商品销量',
  `agent_shelf_state` int(1) NOT NULL DEFAULT 0 COMMENT '分销商品上下架状态（默认上架，0------上架，1---------下架（仓库中），2---------已售完）',
  `price` double(10, 2) NULL DEFAULT NULL COMMENT '原价',
  `product_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `delete_state` int(1) NULL DEFAULT NULL COMMENT '删除标志 0-不删除  1-逻辑删除',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板id',
  `release_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布时间',
  `down_shelves_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '下架时间',
  `spec_type` tinyint(1) NULL DEFAULT NULL COMMENT '是否多规格  0false 1true',
  PRIMARY KEY (`agent_product_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 't_product_agent' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_agent_purchase
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_purchase`;
CREATE TABLE `t_agent_purchase`  (
  `purchase_id` bigint(20) NOT NULL COMMENT '采购表ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `quantity` int(8) NOT NULL COMMENT '商品数量',
  `product_spec_item_id` bigint(20) NULL DEFAULT NULL COMMENT '商品-规格详情编号',
  `wxuser_id` bigint(20) NOT NULL COMMENT '用户ID',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '添加时间',
  `delete_state` tinyint(1) NULL DEFAULT NULL COMMENT '删除标志',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模板ID',
  PRIMARY KEY (`purchase_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '采购表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_agent_purchase_order
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_purchase_order`;
CREATE TABLE `t_agent_purchase_order`  (
  `purchase_order_id` bigint(20) NOT NULL COMMENT '采购订单id',
  `agent_product_id` bigint(20) NULL DEFAULT NULL COMMENT '代理商品id',
  `out_trade_no_ext` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '再次支付订单号',
  `out_trade_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单号',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `pay_flag` int(1) NULL DEFAULT NULL COMMENT '支付状态  0.等待买家付款  1.买家已付款 待发货状态 2.卖家已发货  3.交易成功 4.订单关闭(买家自己关闭) 5订单超时关闭,6.商家关闭',
  `pay_fee` decimal(10, 2) NOT NULL COMMENT '支付价',
  `total_fee` decimal(10, 2) NULL DEFAULT NULL COMMENT '总价',
  `wxuser_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `pay_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付时间',
  `send_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发货时间',
  `record_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收货时间',
  `refound_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '退款时间',
  `remark` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `address` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '收货地址',
  `pay_type` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付方式',
  `wl_num` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物流单号',
  `wl_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物流公司代码',
  `wl_price` bigint(10) NULL DEFAULT NULL COMMENT '邮费',
  `wl_name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物流公司名称',
  `distri_mode` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配送方式',
  `back_remark` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商家备注',
  `close_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单关闭时间',
  `delete_state` tinyint(1) NULL DEFAULT NULL COMMENT '删除标志',
  `appmodel_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模板ID',
  `update_time` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新时间',
  `delivery_staff` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配送信息',
  PRIMARY KEY (`purchase_order_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '采购订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_agent_purchase_order_detailed
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_purchase_order_detailed`;
CREATE TABLE `t_agent_purchase_order_detailed`  (
  `purchase_order_detailed_id` bigint(20) NOT NULL COMMENT '采购订单表id',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '商品ID',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品价格',
  `product_img` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品主图',
  `quantity` int(8) NULL DEFAULT NULL COMMENT '商品数量',
  `product_spec_info` varchar(520) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品-规格详情编号',
  `activity_info` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动详情编号',
  `purchase_order_id` bigint(32) NULL DEFAULT NULL COMMENT '采购订单ID',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '添加时间',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模板ID',
  `delete_state` tinyint(1) NULL DEFAULT NULL COMMENT '删除标志',
  PRIMARY KEY (`purchase_order_detailed_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '采购订单详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_agent_register
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_register`;
CREATE TABLE `t_agent_register`  (
  `reg_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '申请表id',
  `wxuser_id` bigint(20) NULL DEFAULT NULL COMMENT '用户id',
  `reg_name` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请人名称',
  `reg_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请人手机',
  `reg_code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '绑定码',
  `binding` int(1) NULL DEFAULT NULL COMMENT '是否已绑定   0-未绑定  1-绑定',
  `type` int(1) NULL DEFAULT NULL COMMENT '申请类别   1-小程序端申请,2-后端注册时需要发送绑定码',
  `agent_grade_id` int(11) NULL DEFAULT NULL COMMENT '申请等级',
  `reg_auditor` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审核人',
  `reg_state` tinyint(1) NULL DEFAULT NULL COMMENT '审核状态  0-审核中   1-拒绝申请    2.同意申请',
  `reg_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审核时间',
  `reg_remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `refuse_remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `appmodel_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板id',
  `agent_domain` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '所在区域',
  PRIMARY KEY (`reg_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代理申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_category
-- ----------------------------
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category`  (
  `category_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '分类id',
  `category_name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类名',
  `category_type` int(1) NULL DEFAULT 1 COMMENT '(1-一级分类 2-二级分类)',
  `father_id` bigint(20) NULL DEFAULT -1 COMMENT 'father_id 分类父ID',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `category_icon` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类图标',
  `category_img` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类主图',
  `sort_index` int(8) NULL DEFAULT NULL COMMENT '排序值',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  PRIMARY KEY (`category_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品分类有二级分类，一个一级下有多个二级分类，一个商品有多个一级分类，多个二级分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_column_flag
-- ----------------------------
DROP TABLE IF EXISTS `t_column_flag`;
CREATE TABLE `t_column_flag`  (
  `column_flag_id` int(11) NOT NULL AUTO_INCREMENT,
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模板Id',
  `serarch_flag` bit(1) NOT NULL DEFAULT b'1' COMMENT '搜索栏开关',
  `notice_flag` bit(1) NOT NULL DEFAULT b'1' COMMENT '店铺公告开关',
  `shopInfo_flag` bit(1) NOT NULL DEFAULT b'1' COMMENT '店铺故事开关',
  `classify_flag` bit(1) NOT NULL DEFAULT b'1' COMMENT '分类页开关',
  `article_flag` bit(1) NOT NULL DEFAULT b'1' COMMENT '发现页开关',
  `member_flag` bit(1) NOT NULL DEFAULT b'1' COMMENT '会员功能开关',
  `member_recharge_flag` bit(1) NOT NULL DEFAULT b'1' COMMENT '会员储值功能',
  `shop_flag` bit(1) NOT NULL DEFAULT b'1' COMMENT '积分商城开关',
  `proxy_flag` bit(1) NOT NULL DEFAULT b'1' COMMENT '代理功能开关',
  `foot_flag` bit(1) NOT NULL DEFAULT b'1' COMMENT '底部打标开关',
  PRIMARY KEY (`column_flag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '栏目开关' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_coupon
-- ----------------------------
DROP TABLE IF EXISTS `t_coupon`;
CREATE TABLE `t_coupon`  (
  `coupon_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `activity_coupon_id` int(11) NULL DEFAULT NULL COMMENT '优惠活动ID',
  `stock_quantity` int(8) NULL DEFAULT NULL COMMENT '发行量',
  `limit_quantity` int(8) NULL DEFAULT NULL COMMENT '单人限领量',
  `min_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '优惠值',
  `max_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '满减值',
  `discount` decimal(6, 2) NULL DEFAULT NULL COMMENT '折扣值',
  `coupon_type` int(2) NULL DEFAULT NULL COMMENT '优惠券类型（1---满减，2---满折，3---无门槛现金券，4---无门槛折扣）',
  `source_type` int(2) NULL DEFAULT NULL COMMENT '优惠券来源类型（1---优惠活动创建，2---积分领取）',
  `obtain_quantity` int(8) NULL DEFAULT NULL COMMENT '领取量',
  `used_quantity` int(8) NULL DEFAULT NULL COMMENT '使用量',
  `appmodel_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户模板ID',
  `delete_state` bit(1) NULL DEFAULT NULL COMMENT '删除标志',
  `convert_price` int(11) NULL DEFAULT NULL COMMENT '兑换所需积分',
  `live_time` tinyint(1) NULL DEFAULT NULL COMMENT '使用期限(0-无时间限制,1-有效天数,2-有效日期)',
  `day` int(11) NULL DEFAULT NULL COMMENT '有效天数',
  `statr_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '开始日期',
  `end_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '结束日期',
  PRIMARY KEY (`coupon_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '优惠卷' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_coupon_wxuser
-- ----------------------------
DROP TABLE IF EXISTS `t_coupon_wxuser`;
CREATE TABLE `t_coupon_wxuser`  (
  `wxuser_coupon_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `wxuser_id` bigint(11) NULL DEFAULT NULL COMMENT '用户ID',
  `coupon_id` int(11) NULL DEFAULT NULL COMMENT '优惠券ID',
  `flag` tinyint(1) NULL DEFAULT NULL COMMENT '使用状态',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '领取优惠券时间',
  `use_time` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '使用优惠券时间',
  `appmodel_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  `activity_coupon_id` int(11) NULL DEFAULT NULL COMMENT '优惠活动ID',
  PRIMARY KEY (`wxuser_coupon_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户优惠券' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_firstpage_classify
-- ----------------------------
DROP TABLE IF EXISTS `t_firstpage_classify`;
CREATE TABLE `t_firstpage_classify`  (
  `classify_id` int(11) NOT NULL AUTO_INCREMENT,
  `classify_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类名称',
  `classify_img` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类图片',
  `link_type` int(10) NULL DEFAULT NULL COMMENT '链接类型',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '商品Id',
  `target_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '商品分类id',
  `notice_id` int(10) NULL DEFAULT NULL COMMENT '公告Id',
  `article_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文章Id',
  `appmodel_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板Id',
  `sort` int(10) NULL DEFAULT NULL COMMENT '排序值',
  `target_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '跳转地址',
  PRIMARY KEY (`classify_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 129 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '首页分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_footer
-- ----------------------------
DROP TABLE IF EXISTS `t_footer`;
CREATE TABLE `t_footer`  (
  `footer_id` int(11) NOT NULL AUTO_INCREMENT,
  `appmodel_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板Id',
  `footer_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '导航名称',
  `footer_img_no` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '未选中图片',
  `footer_img_yes` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '选中图片',
  `footer_link` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '链接',
  `footer_flag` bit(1) NOT NULL DEFAULT b'1' COMMENT '开启关闭状态',
  PRIMARY KEY (`footer_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 86 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '底部导航' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_group
-- ----------------------------
DROP TABLE IF EXISTS `t_group`;
CREATE TABLE `t_group`  (
  `group_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `wxuser_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `activity_group_id` int(11) NULL DEFAULT NULL COMMENT '开团用户ID',
  `create_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '开团时间',
  `end_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '拼团结束时间',
  `group_state` tinyint(1) NULL DEFAULT NULL COMMENT '成团状态 0--待成团，1--已成团 ，2--失败',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '产品ID',
  `limit_num` int(8) NULL DEFAULT NULL COMMENT '限购人数',
  `delete_state` bit(1) NULL DEFAULT NULL COMMENT '删除状态',
  `appmodel_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户模板ID',
  PRIMARY KEY (`group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '一个团' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_group_member
-- ----------------------------
DROP TABLE IF EXISTS `t_group_member`;
CREATE TABLE `t_group_member`  (
  `group_member_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `wxuser_id` bigint(11) NULL DEFAULT NULL COMMENT '用户ID',
  `group_id` int(11) NULL DEFAULT NULL COMMENT '团ID',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '订单ID',
  `create_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参团时间',
  `member_type` bit(1) NULL DEFAULT NULL COMMENT '参团人类型',
  `nick_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `appmodel_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  `delete_state` bit(1) NULL DEFAULT NULL COMMENT '删除标志',
  PRIMARY KEY (`group_member_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '团成员' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_logistic_cancel
-- ----------------------------
DROP TABLE IF EXISTS `t_logistic_cancel`;
CREATE TABLE `t_logistic_cancel`  (
  `logistic_cancel_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '退货地址ID',
  `user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系人',
  `phone` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `post_code` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮编',
  `location_json` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址',
  `default_state` tinyint(1) NULL DEFAULT NULL COMMENT '是否默认',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  PRIMARY KEY (`logistic_cancel_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '退货地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_logistic_charge
-- ----------------------------
DROP TABLE IF EXISTS `t_logistic_charge`;
CREATE TABLE `t_logistic_charge`  (
  `logistic_charge_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '物流价格ID',
  `logistic_model_id` int(11) NULL DEFAULT NULL COMMENT '物流模板Id',
  `charge_address` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物流地区',
  `first_weight` int(16) NULL DEFAULT NULL COMMENT '物流首重',
  `first_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '首重价格',
  `next_weight` int(16) NULL DEFAULT NULL COMMENT '物流续重',
  `next_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '续重价格',
  `default_state` tinyint(1) NULL DEFAULT NULL COMMENT '默认状态',
  `appmodel_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  PRIMARY KEY (`logistic_charge_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 216 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '物流计价' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_logistic_distrobution
-- ----------------------------
DROP TABLE IF EXISTS `t_logistic_distrobution`;
CREATE TABLE `t_logistic_distrobution`  (
  `distrobution_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商家配送ID',
  `min_pay_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '起送价（顾客支付最低xx元才配送）',
  `delivery_range` int(16) NULL DEFAULT NULL COMMENT '配送范围（单位KM）',
  `seller_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '配送费用',
  `turn_state` tinyint(1) NOT NULL DEFAULT 1 COMMENT '开启关闭',
  `operating_time` varchar(70) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '营业时间',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户模板Id',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `shop_address` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商家地址',
  `delivery_staffs` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配送员',
  PRIMARY KEY (`distrobution_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商家配送' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_logistic_free
-- ----------------------------
DROP TABLE IF EXISTS `t_logistic_free`;
CREATE TABLE `t_logistic_free`  (
  `free_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '包邮ID',
  `free_address` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '包邮地区',
  `unit_type` int(1) NULL DEFAULT NULL COMMENT '包邮类型(0件数,1金额,2件数+金额)',
  `max_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '包邮条件-价钱（满100包邮）',
  `logistic_model_id` int(11) NULL DEFAULT NULL COMMENT '物流模板ID',
  `condition_ship` int(8) NULL DEFAULT NULL COMMENT '包邮条件-数量（1件）',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户模板ID',
  PRIMARY KEY (`free_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 83 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '物流包邮' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_logistic_model
-- ----------------------------
DROP TABLE IF EXISTS `t_logistic_model`;
CREATE TABLE `t_logistic_model`  (
  `logistic_model_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '物流配模板送ID',
  `logistic_model_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配送模板名称',
  `valuation_type` int(1) NULL DEFAULT NULL COMMENT '计价方式（0-件 1-kg 2-m³）',
  `turn_state` tinyint(1) NOT NULL DEFAULT 1 COMMENT '模板开启关闭的状态',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户模板Id',
  `free_state` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否指定包邮',
  PRIMARY KEY (`logistic_model_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 93 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '物流模板' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_manager
-- ----------------------------
DROP TABLE IF EXISTS `t_manager`;
CREATE TABLE `t_manager`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `appmodel_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户模板ID',
  `version` int(1) NULL DEFAULT NULL COMMENT '版本 1-基础班,2-标准版,3-营销版',
  `create_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建用户时间',
  `expiry_date_notify` bigint(1) NULL DEFAULT 0,
  `expiry_date` datetime(0) NULL DEFAULT NULL COMMENT '截止日期',
  `app_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `mch_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `mch_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `certificate_path` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商户支付证书路径',
  `mini_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '小程序名称',
  `version_subscript` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本下标',
  `mini_code` varchar(168) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '小程序二维码',
  `logo` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '小程序log',
  `bpavawice` decimal(7, 2) NULL DEFAULT 0.00 COMMENT '余额',
  `flag` tinyint(1) NULL DEFAULT 1 COMMENT '是否缴纳保证金',
  `last_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上次登录时间',
  `del_state` int(1) NULL DEFAULT 0 COMMENT '删除标志',
  `secret` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '后台管理员' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_member
-- ----------------------------
DROP TABLE IF EXISTS `t_member`;
CREATE TABLE `t_member`  (
  `member_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '会员id',
  `membership_number` varchar(28) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会员卡编号',
  `code_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会员卡二维码',
  `wxuser_id` bigint(20) NOT NULL COMMENT '用户id',
  `supply_bonus` double(10, 2) NULL DEFAULT 0.00 COMMENT '会员卡余额',
  `growth_value` int(11) NULL DEFAULT 0 COMMENT '成长值',
  `integral` int(11) NULL DEFAULT 0 COMMENT '积分值',
  `integral_total` int(11) NULL DEFAULT 0 COMMENT '总积分值',
  `phone` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `create_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `upgrade_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后升级时间',
  `state` int(1) NOT NULL DEFAULT 0 COMMENT '注册状态0未注册,1已注册',
  `rank_id` int(11) NULL DEFAULT NULL COMMENT '等级表Id',
  `member_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `appmodel_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板id',
  PRIMARY KEY (`member_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1567280612404673 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_member_active_recharge
-- ----------------------------
DROP TABLE IF EXISTS `t_member_active_recharge`;
CREATE TABLE `t_member_active_recharge`  (
  `active_recharge_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '活动id',
  `active_name` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动名称',
  `start_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '截止时间',
  `state` int(1) NULL DEFAULT 1 COMMENT '活动状态  1-进行中 2.已结束',
  `send_price` double(11, 2) NULL DEFAULT NULL COMMENT '赠送金额',
  `max_price` double(11, 2) NULL DEFAULT NULL COMMENT '达标价格',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `appmodel_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板id',
  `delete_state` int(11) NULL DEFAULT NULL COMMENT '删除标志',
  PRIMARY KEY (`active_recharge_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 82 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '会员充值活动表\r\n1.活动规则存储json字符串规则必须包含一个\r\n2.多个活动之间时间不能产生交集' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_member_group
-- ----------------------------
DROP TABLE IF EXISTS `t_member_group`;
CREATE TABLE `t_member_group`  (
  `group_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '组别id',
  `group_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '组名',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `appmodel_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模板id',
  PRIMARY KEY (`group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 139 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '会员分组表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_member_group_category
-- ----------------------------
DROP TABLE IF EXISTS `t_member_group_category`;
CREATE TABLE `t_member_group_category`  (
  `group_category_id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL COMMENT '组id',
  `member_id` bigint(20) NOT NULL COMMENT '会员id',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模板id',
  PRIMARY KEY (`group_category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 407 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_member_rank
-- ----------------------------
DROP TABLE IF EXISTS `t_member_rank`;
CREATE TABLE `t_member_rank`  (
  `rank_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '等级表id',
  `rank_name` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '等级名称',
  `discount` double(10, 1) NULL DEFAULT NULL COMMENT '折扣',
  `back_ground_pic_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会员卡图片',
  `title` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会员卡标题',
  `deduct_growth` int(11) NULL DEFAULT NULL COMMENT '到期后减扣得成长值',
  `create_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `delete_state` int(1) NULL DEFAULT NULL COMMENT '删除状态 0-可用 1-不可删除',
  `appmodel_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板id',
  `growth_value` int(11) NULL DEFAULT NULL COMMENT '达标成长值',
  PRIMARY KEY (`rank_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 74 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会员等级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_member_rank_rule
-- ----------------------------
DROP TABLE IF EXISTS `t_member_rank_rule`;
CREATE TABLE `t_member_rank_rule`  (
  `member_rule_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '规则表id',
  `login_integral` int(11) NULL DEFAULT 0 COMMENT '登录可获得成长值',
  `share_integral` int(11) NULL DEFAULT 0 COMMENT '分享可获得成长值',
  `consume_integral` int(11) NULL DEFAULT 0 COMMENT '消费可获得成长值',
  `validity` int(2) NULL DEFAULT 12 COMMENT '等级有效期   12',
  `introduce` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '等级介绍',
  `explain_info` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '升降级说明',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `explain_state` int(1) NOT NULL COMMENT '是否开启等级有效期功能, 0关闭  1开启  默认关闭',
  PRIMARY KEY (`member_rule_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '会员卡规则' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_member_recharge_record
-- ----------------------------
DROP TABLE IF EXISTS `t_member_recharge_record`;
CREATE TABLE `t_member_recharge_record`  (
  `recharge_record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '表id',
  `order_number` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单号',
  `type` int(1) NULL DEFAULT NULL COMMENT '充值方式  1.微信支付,2银行卡充值',
  `member_id` bigint(20) NOT NULL COMMENT '会员id',
  `price` double(10, 2) NULL DEFAULT NULL COMMENT '充值金额',
  `active_recharge_id` int(11) NULL DEFAULT NULL COMMENT '充值活动id',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '充值时间',
  `appmodel_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板id',
  `pay_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付时间',
  `state` int(1) NULL DEFAULT NULL COMMENT '充值状态:0未付款,1已付款',
  PRIMARY KEY (`recharge_record_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 189 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '会员充值记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_notice
-- ----------------------------
DROP TABLE IF EXISTS `t_notice`;
CREATE TABLE `t_notice`  (
  `notice_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `notice_img` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '顶部图片',
  `create_time` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发布公告时间',
  `notice_body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '公告内容',
  `product_id` bigint(3) NULL DEFAULT NULL COMMENT '商品id',
  `target_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `appmodel_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  `target_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '跳转地址',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序值',
  `start_time` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '展示开始时间',
  `end_time` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '展示结束时间',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 64 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '公告信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
  `order_id` bigint(30) NOT NULL COMMENT '编号',
  `tel_phone` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `pay_flag` int(4) NOT NULL DEFAULT 0 COMMENT '支付状态  \r\n0.等待买家付款\r\n1.买家已付款 待发货状态\r\n2.卖家已发货  待收货状态\r\n3.交易成功\r\n4.订单关闭,\r\n5用户超时关闭订单\r\n6.商家关闭订单',
  `out_trade_no` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单号',
  `pay_fee` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付价',
  `total_fee` decimal(10, 2) NULL DEFAULT NULL COMMENT '总价',
  `wxuser_id` bigint(30) NULL DEFAULT NULL COMMENT '用户ID',
  `create_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `pay_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付时间',
  `send_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发货时间',
  `record_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货时间',
  `remark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `wl_name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物流公司名称',
  `wl_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物流单号',
  `wl_code` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物流公司代码',
  `user_address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '收货地址',
  `user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货人',
  `wl_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '邮费',
  `out_trade_no_ext` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '再次支付订单号',
  `activity_id` int(11) NULL DEFAULT NULL,
  `order_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单类型',
  `back_remark` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '商家备注',
  `delete_state` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识   0正常  1逻辑删除',
  `appmodel_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '模板ID',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `distribute_mode` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配送方式（商家配送，物流配送）',
  `delivery_staff` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '配送信息',
  `group_state` int(1) NULL DEFAULT -1 COMMENT '成团状态',
  `group_member_id` int(11) NULL DEFAULT NULL COMMENT '团成员ID',
  `factPay_wxuser_id` bigint(20) NULL DEFAULT NULL COMMENT '实际支付人id',
  `pay_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付方式,微信支付,余额支付,好友代付',
  `member_discount` double(5, 2) NULL DEFAULT NULL COMMENT '会员折扣',
  `wxuser_coupon_id` int(11) NULL DEFAULT NULL COMMENT '用户使用的优惠券id',
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_order_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_order_detail`;
CREATE TABLE `t_order_detail`  (
  `order_detail_id` bigint(30) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `order_id` bigint(30) NULL DEFAULT NULL COMMENT '订单编号',
  `product_id` bigint(30) NULL DEFAULT NULL COMMENT '产品ID',
  `quantity` int(8) NULL DEFAULT NULL COMMENT '商品数量',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品价格',
  `product_img` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品主图',
  `product_spec_info` varchar(528) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品规格(可能是活动规格或普通商品规格)',
  `update_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新时间',
  `appmodel_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  `delete_state` tinyint(1) NULL DEFAULT NULL COMMENT '删除标志',
  `activity_info` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '活动信息',
  `apply_state` int(1) NULL DEFAULT NULL COMMENT '申请状态 0-正常订单商品 1申请中  2退款成功  3退款失败',
  `number` int(1) NULL DEFAULT 0 COMMENT '商品退款次数',
  `refuse_state` int(1) NULL DEFAULT NULL COMMENT '售后状态 0正常订单  1.申请中 \r\n2.卖家同意退货退款\r\n3.卖家已拒绝  4.退款关闭 \r\n5.退款完成',
  `preferential` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠价格',
  `record` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '操作记录',
  `wl_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '物流价格',
  PRIMARY KEY (`order_detail_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1566632738180045 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单详情' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_order_refound
-- ----------------------------
DROP TABLE IF EXISTS `t_order_refound`;
CREATE TABLE `t_order_refound`  (
  `order_refound_id` bigint(20) NOT NULL COMMENT '退款ID',
  `order_detail_id` bigint(20) NULL DEFAULT NULL COMMENT '订单详情id',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `wxuser_id` bigint(20) NOT NULL COMMENT '用户ID',
  `flow_path` int(2) NULL DEFAULT NULL COMMENT '流程状态 \r\n0-用户申请退款\r\n1-退款订单类型为仅退款,卖家超时未处理,自动退款\r\n2-订单属于待发货状态,卖家拒绝,订单发货\r\n3-同意退款退货\r\n4-退款订单类型为退款退货,卖家超时未处理,自动填写物流 5-用户填写物流 6-买家物流填写超时,退款关闭 7-商家确认收货并退款 8-拒绝退款\r\n9-拒绝退货退款  10-用户撤消申请\r\n 11-卖家拒绝,用户超时未处理   12-同意退款\r\n13-商家超时未确认收货,自动退款',
  `business_state` int(2) NULL DEFAULT NULL COMMENT '交易状态 1.申请中,\r\n2卖家同意退货退款 \r\n3.卖家已拒绝\r\n4.退款关闭\r\n5.退款完成\r\n',
  `refound_type` tinyint(1) NOT NULL COMMENT '退款类型  0--只退款，1--退货退款',
  `reason` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '退款原因',
  `refound_fee` decimal(10, 2) NOT NULL COMMENT '退款金额',
  `remark` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '退款说明',
  `product_state` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '货物状态',
  `refuse_reason` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `u_wl_name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '退款物流公司',
  `u_wl_remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '快递备注',
  `u_wl_phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '快递联系人手机号',
  `u_wl_code` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '快递公司代码',
  `u_wl_num` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物流单号',
  `shop_address` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '卖家收货地址',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '申请时间',
  `update_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作更新时间',
  `refound_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '退款时间',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模板ID',
  `delete_state` tinyint(1) NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`order_refound_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '退款信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_parameter
-- ----------------------------
DROP TABLE IF EXISTS `t_parameter`;
CREATE TABLE `t_parameter`  (
  `param_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `param_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数名称',
  `param_value` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数值',
  `param_class_id` int(11) NULL DEFAULT NULL COMMENT '参数分类ID',
  `appmodel_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  PRIMARY KEY (`param_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 261 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '规格参数' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_parameter_class
-- ----------------------------
DROP TABLE IF EXISTS `t_parameter_class`;
CREATE TABLE `t_parameter_class`  (
  `param_class_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '总分类编号',
  `param_class_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类别名称',
  `create_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `appmodel_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  PRIMARY KEY (`param_class_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '规格参数分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_plate
-- ----------------------------
DROP TABLE IF EXISTS `t_plate`;
CREATE TABLE `t_plate`  (
  `plate_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `plate_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '板块名称',
  `plate_img` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '板块图片',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '板块简介',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  `product_num` int(8) NULL DEFAULT NULL COMMENT '产品数量',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序值',
  `plate_flag` bit(1) NULL DEFAULT NULL COMMENT '板块状态',
  PRIMARY KEY (`plate_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '首页板块产品' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_plate_product
-- ----------------------------
DROP TABLE IF EXISTS `t_plate_product`;
CREATE TABLE `t_plate_product`  (
  `plate_product_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `plate_id` int(11) NULL DEFAULT NULL COMMENT '板块ID',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '产品ID',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  PRIMARY KEY (`plate_product_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 129 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '首页板块产品关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_poster
-- ----------------------------
DROP TABLE IF EXISTS `t_poster`;
CREATE TABLE `t_poster`  (
  `poster_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `jump_type` int(2) NULL DEFAULT NULL COMMENT '跳转类型（0-跳转到商品，1-跳转到公告，2-跳转到发现）',
  `poster_img` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '轮播图图片',
  `create_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `target_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '跳转地址',
  `target_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '链接名称',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序值',
  `appmodel_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '商品id',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '商品分类id',
  `article_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文章id',
  `notice_id` int(11) NULL DEFAULT NULL COMMENT '公告id',
  PRIMARY KEY (`poster_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '轮播图' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_prize
-- ----------------------------
DROP TABLE IF EXISTS `t_prize`;
CREATE TABLE `t_prize`  (
  `prize_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '积分商品id',
  `prize_type` int(2) NULL DEFAULT NULL COMMENT '积分商品类型 奖品类型：1.商品 2.优惠券',
  `coupon_id` int(11) NULL DEFAULT NULL COMMENT '优惠券id',
  `prize_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '积分商品名',
  `prize_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '积分商品主图片,如果是奖励实物的话，该字段存储实物图片地址，比如店铺宝贝主图src',
  `round_sowing_img` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '积分商品轮播图,存储json格式',
  `remark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '描述',
  `send_place` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发货地址',
  `send_date` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发货日期',
  `delete_state` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志',
  `create_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `update_state` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新时间',
  `prize_stock` int(10) NULL DEFAULT 0 COMMENT '积分商品库存',
  `sales_volume` int(11) NULL DEFAULT NULL COMMENT '销量',
  `all_integral` int(11) NULL DEFAULT NULL COMMENT '兑换总额',
  `price` double(10, 2) NULL DEFAULT NULL COMMENT '实际价格',
  `convert_price` int(11) NULL DEFAULT NULL COMMENT '兑换积分',
  `state` int(1) NULL DEFAULT NULL COMMENT '商品状态    商品状态    1.上架  2.仓库中 3.已售完',
  `delivery_fees` double(10, 2) NULL DEFAULT NULL COMMENT '统一邮费',
  `logistic_model_id` int(11) NULL DEFAULT NULL COMMENT '邮费模板',
  `property_model_id` int(11) NULL DEFAULT NULL COMMENT '属性模板',
  `property_mine` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '自定义属性',
  `text_img` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '图文详情',
  `appmodel_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '模板id',
  `product_bulk` double(10, 2) NULL DEFAULT NULL COMMENT '商品的体积或重量 计算方式根据运费模板的类型计算',
  PRIMARY KEY (`prize_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '积分商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_prize_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_prize_detail`;
CREATE TABLE `t_prize_detail`  (
  `integral_detail_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '明细表id',
  `type` int(11) NULL DEFAULT NULL COMMENT '使用类别  1. 兑换商品 2.购买商品 3,登录增加积分,4分享增加积分',
  `quantity` int(11) NULL DEFAULT NULL COMMENT '购买商品数量',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '添加时间',
  `integral` int(11) NULL DEFAULT NULL COMMENT '积分',
  `prize_id` int(11) NULL DEFAULT NULL COMMENT '积分商品id',
  `wxuser_id` bigint(20) NULL DEFAULT NULL COMMENT '用户id',
  `appmodel_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模板id',
  `delete_state` bit(1) NULL DEFAULT NULL COMMENT '过期状态',
  PRIMARY KEY (`integral_detail_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 619 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '积分明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_prize_order
-- ----------------------------
DROP TABLE IF EXISTS `t_prize_order`;
CREATE TABLE `t_prize_order`  (
  `prize_order_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `change_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '兑换单号',
  `wxuser_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '用户id',
  `pay_type` int(1) NULL DEFAULT NULL,
  `wxuser_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `prize_id` int(11) NOT NULL COMMENT '积分商品id',
  `pay_fee` double(10, 2) NULL DEFAULT NULL COMMENT '支付费用',
  `order_type` int(2) NULL DEFAULT NULL COMMENT '订单类型（1商品，2优惠券）',
  `order_state` tinyint(1) NULL DEFAULT NULL COMMENT '订单状态 (0等待买家付款,1买家已付款    ,2卖家已发货,3交易成功,4退款中,5退款完成,6已关闭)',
  `price` double(10, 2) NULL DEFAULT NULL COMMENT '实际价格',
  `sum` int(11) NULL DEFAULT NULL COMMENT '数量',
  `expend_integral` int(10) NULL DEFAULT NULL COMMENT '付款积分',
  `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货人',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货地址',
  `tel_phone` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `wl_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `wl_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物流单号',
  `wl_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物流公司代码',
  `wl_price` double(10, 2) NULL DEFAULT NULL COMMENT '邮费',
  `distri_mode` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配送方式   (商家配送，物流配送，到店自取）',
  `delivery_staff` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配送信息',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `back_remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商家备注',
  `pay_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付时间',
  `send_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发货时间',
  `record_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货时间',
  `refound_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '退款时间',
  `delete_state` tinyint(1) NULL DEFAULT 1 COMMENT '删除状态  0-删除 ,1-正常',
  `create_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新时间',
  `appmodel_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板id',
  PRIMARY KEY (`prize_order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1544850025679674 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '积分订单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_prize_rule
-- ----------------------------
DROP TABLE IF EXISTS `t_prize_rule`;
CREATE TABLE `t_prize_rule`  (
  `prize_rule_id` int(11) NOT NULL AUTO_INCREMENT,
  `type_one` int(10) NULL DEFAULT NULL COMMENT '分享获取积分值',
  `type_two` int(10) NULL DEFAULT NULL COMMENT '登录获取积分值',
  `type_three_pay` int(10) NULL DEFAULT NULL COMMENT '购买满值',
  `type_three_get` int(10) NULL DEFAULT NULL COMMENT '购买满获取积分值',
  `indate` int(10) NULL DEFAULT NULL COMMENT '有效期',
  `info` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '说明',
  `appmodel_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板id',
  PRIMARY KEY (`prize_rule_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_product
-- ----------------------------
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product`  (
  `product_id` bigint(20) NOT NULL COMMENT '商品编号',
  `delete_state` tinyint(1) NOT NULL DEFAULT 0 COMMENT '1删除，	0不删除',
  `spec_type` tinyint(1) NULL DEFAULT NULL COMMENT '是否统一规格 0多规格,1-统一规格',
  `stock` int(8) NOT NULL DEFAULT 0 COMMENT '库存',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `remark` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `shelf_state` int(1) NOT NULL DEFAULT 0 COMMENT '上下状态(默认上架，0--上架，1--下架（仓库中），2--已售完)',
  `send_place` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发货地',
  `send_date` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发货日期',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '销售价（购买价）',
  `market_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '市场价（划掉的价格）',
  `sales_volume` int(8) NOT NULL DEFAULT 0 COMMENT '销量',
  `delivery_type` tinyint(1) NULL DEFAULT NULL COMMENT '是否统一邮费',
  `delivery_fees` decimal(10, 2) NULL DEFAULT NULL COMMENT '统一邮费',
  `logistic_model_id` int(11) NULL DEFAULT NULL COMMENT '邮费模板',
  `distribute_type` tinyint(1) NULL DEFAULT NULL COMMENT '是否配送',
  `service_assurance` varchar(600) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务保障',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  `product_img` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主图',
  `rimg_url` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '轮播图',
  `text_img` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '图文详情',
  `param_value` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '规格参数',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `upate_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新时间',
  `product_type_list` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '活动类型',
  `product_bulk` double(10, 2) NULL DEFAULT NULL COMMENT '商品的体积或重量 计算方式根据运费模板的类型计算',
  PRIMARY KEY (`product_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_product_category
-- ----------------------------
DROP TABLE IF EXISTS `t_product_category`;
CREATE TABLE `t_product_category`  (
  `product_category_id` bigint(20) NOT NULL COMMENT '商品分类编号',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '分类ID',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '商品编号',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  PRIMARY KEY (`product_category_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品-分类关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_product_spec
-- ----------------------------
DROP TABLE IF EXISTS `t_product_spec`;
CREATE TABLE `t_product_spec`  (
  `product_spec_id` bigint(20) NOT NULL COMMENT '编号',
  `specification_class_id` int(11) NULL DEFAULT NULL COMMENT '规格分类编号',
  `specification_id` int(11) NULL DEFAULT NULL COMMENT '规格ID',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '商品编号',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  PRIMARY KEY (`product_spec_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品-规格关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_product_spec_class
-- ----------------------------
DROP TABLE IF EXISTS `t_product_spec_class`;
CREATE TABLE `t_product_spec_class`  (
  `product_spec_class_id` bigint(20) NOT NULL COMMENT '编号',
  `specification_class_id` int(11) NULL DEFAULT NULL COMMENT '规格分类编号',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '商品编号',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  PRIMARY KEY (`product_spec_class_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品-规格分类关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_product_spec_item
-- ----------------------------
DROP TABLE IF EXISTS `t_product_spec_item`;
CREATE TABLE `t_product_spec_item`  (
  `product_spec_item_id` bigint(20) NOT NULL COMMENT '编号',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '商品ID',
  `sku_img` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'sku图',
  `specification_value` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格值',
  `specification_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `stock` int(8) NOT NULL DEFAULT 0 COMMENT '库存',
  `activity_price` decimal(10, 2) NULL DEFAULT NULL,
  `activity_stock` int(8) NULL DEFAULT 0 COMMENT '活动库存',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  `agent_stock` int(8) NULL DEFAULT 0 COMMENT '代理商品库存',
  PRIMARY KEY (`product_spec_item_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品-规格详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_search_word
-- ----------------------------
DROP TABLE IF EXISTS `t_search_word`;
CREATE TABLE `t_search_word`  (
  `search_word_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `keyword` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关键词',
  `wordtype` tinyint(1) NULL DEFAULT NULL COMMENT '关键词类型',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  PRIMARY KEY (`search_word_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '搜索关键词' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_sms
-- ----------------------------
DROP TABLE IF EXISTS `t_sms`;
CREATE TABLE `t_sms`  (
  `sms_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '短信id',
  `user_tel` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '手机号',
  `sms_code` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '验证码',
  `type` int(1) NOT NULL COMMENT '验证类型 1.会员注册获取验证码 2.代理商绑定获取验证码3.代理商申请获取验证码,4,商家同意退款验证,5.余额解冻验证,6余额提现验证',
  `create_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新时间',
  `appmodel_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板id',
  PRIMARY KEY (`sms_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 133 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '短信表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_specification
-- ----------------------------
DROP TABLE IF EXISTS `t_specification`;
CREATE TABLE `t_specification`  (
  `specification_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `specification_class_id` int(11) NULL DEFAULT NULL COMMENT '规格分类',
  `name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格名称',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  `delete_state` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志（1删除，	0不删除）',
  PRIMARY KEY (`specification_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 212 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '规格值表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_specification_class
-- ----------------------------
DROP TABLE IF EXISTS `t_specification_class`;
CREATE TABLE `t_specification_class`  (
  `specification_class_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格分类名称',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  `delete_state` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志（1删除，	0不删除）',
  PRIMARY KEY (`specification_class_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 93 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '规格表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_wlcompany
-- ----------------------------
DROP TABLE IF EXISTS `t_wlcompany`;
CREATE TABLE `t_wlcompany`  (
  `wl_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `company_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物流公司名称',
  `company_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公司编码',
  PRIMARY KEY (`wl_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 146 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '物流公司' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_wxuser
-- ----------------------------
DROP TABLE IF EXISTS `t_wxuser`;
CREATE TABLE `t_wxuser`  (
                             `wxuser_id`      bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '用户id',
                             `open_id`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '微信用户openId',
                             `nike_name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '用户名称',
                             `avatar_url`     varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像url',
                             `create_time`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '授权时间',
                             `last_time`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '最后登录时间',
                             `authorize_type` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT '0' COMMENT '是否授权',
                             `delete_state`   int(1)                                                        NULL DEFAULT 0 COMMENT '删除标示  0-可用,1-删除',
                             `appmodel_id`    varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '模板id',
                             `back_remark`    varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '后台备注',
                             `mark_level`     int(1)                                                        NULL DEFAULT NULL COMMENT '备注等级',
                             `member_id`      bigint(20)                                                    NULL DEFAULT NULL COMMENT '会员id',
                             `session_key`    varchar(28) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '用户的sessionkey',
                             PRIMARY KEY (`wxuser_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1567280612404037
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '微信用户表'
  ROW_FORMAT = Dynamic;

/*
 * Copyright Ningbo Qishan Technology Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

-- ----------------------------
-- Table structure for t_wxuser_formid
-- ----------------------------
DROP TABLE IF EXISTS `t_wxuser_formid`;
CREATE TABLE `t_wxuser_formid`
(
    `user_form_id` int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT '编号',
    `open_id`      varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '用户openid',
    `form_value`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL DEFAULT '' COMMENT 'formId',
    `create_time`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`user_form_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_wxuser_group
-- ----------------------------
DROP TABLE IF EXISTS `t_wxuser_group`;
CREATE TABLE `t_wxuser_group`  (
  `wxuser_group_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '组id',
  `wx_group_name` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组名称',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`wxuser_group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 119 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户组表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_wxuser_group_category
-- ----------------------------
DROP TABLE IF EXISTS `t_wxuser_group_category`;
CREATE TABLE `t_wxuser_group_category`  (
  `wx_group_category_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分组id',
  `wxuser_group_id` int(11) NULL DEFAULT NULL COMMENT '组id',
  `wxuser_id` bigint(11) NULL DEFAULT NULL COMMENT '会员id',
  `create_time` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
  `appmodel_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板id',
  PRIMARY KEY (`wx_group_category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 71 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户分组表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
