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

package com.mds.group.purchase.constant;

/**
 * The interface Activi mq queue name.
 *
 * @author pavawi
 */
public interface ActiviMqQueueName {

    /**
     * 服务号模板消息
     */
    String ORDER_FWH_MESSAGE = "Basemall_Fwh_Message";

    /**
     * 拼团项目商家数据初始化
     */
    String MERCHANT_DATA_INIT = "GroupMall_Merchant_data_init";
    /**
     * 关闭订单
     */
    String ORDER_CLOSE = "GroupMall_order_close";

    /**
     * 创建订单
     */
    String SAVE_ORDER = "GroupMall_order_save";

    /**
     * 预热活动
     */
    String READY_ACTIVITY_V1 = "GroupMall_ready_activity_v1";

    /**
     * 开启活动
     */
    String START_ACTIVITY_V1 = "GroupMall_start_activity_v1";

    /**
     * 结束活动
     */
    String END_ACTIVITY_V1 = "GroupMall_end_activity_v1";

    /**
     * 线路分拣单
     */
    String LINE_SORTING_ORDER = "GroupMall_line_sorting_order";

    /**
     * 团长签收单
     */
    String SAVE_RECEIPT_BILL = "GroupMall_save_receipt_bill";

    /**
     * 商品分拣单
     */
    String GOODS_SORTING_ORDER = "GroupMall_goods_sorting_order";

    /**
     * 发送模板消息
     */
    String ORDER_MINIPROGRAM_TEMPLATE_MESSAGE = "GroupMall_OrderMiniProgramTemplateMessage";

    /**
     * 更新商品缓存
     */
    String GOODS_POSTER_CACHE = "GroupMall_goods_poster_cache";

    /**
     * 更新拼团活动缓存
     */
    String ACTIVITY_GROUP_CACHE = "GroupMall_activity_group_cache";

    /**
     * 更新活动缓存
     */
    String ACTIVITY_CACHE = "GroupMall_activity_cache";

    /**
     * 删除单个活动时更新活动缓存
     */
    String DEL_ONE_ACTIVITY_CACHE = "GroupMall_del_one_activity_cache";

    /**
     * 修改普通商品时更新活动商品缓存
     */
    String UPDATE_GOODS_FLASH_ACTGOODS_CACHE = "GroupMall_update_goods_flash_actGoods_cache";

    /**
     * 普通商品缓存
     */
    String GOODS_INFO_CACHE = "GroupMall_goods_info_cache";

    /**
     * 生成发货单
     */
    String GENERATE_SENDBILL = "GroupMall_generate_send_bill";

    /**
     * 将小区自动加入到商品的投放区域中
     */
    String AUTO_ADD_COMMUNITY = "GroupMall_community_auto_add_goods_area";

    /**
     * 生成线路分拣单缓存
     */
    String LINE_SORTING_ORDER_CACHE = "Line_sorting_order_cache";

    /**
     * 生成商品分拣单缓存
     */
    String GOODS_SORTING_ORDER_CACHE = "Goods_sorting_order_cache";

    /**
     * 生成团长确认单缓存
     */
    String RECEIPT_BILL_CACHE = "Receipt_bill_cache";

    /**
     * 收货成功后，改变发货单状态
     */
    String SENDBILL_SUCCESS = "SendBill_success";


    /**
     * 商家自动确认
     */
    String MERCHANT_AUTO_CONFIRM = "Merchant_auto_confirm";

    /**
     * 用户自动取消
     */
    String USER_AUTO_CANCEL = "User_auto_cancel";

    /**
     * 用户自动确认
     */
    String USER_AUTO_CONFIRM = "User_auto_confirm";

    /**
     * 团长自动取消
     */
    String LEADER_AUTO_CONFIRM = "Leader_auto_confirm";

    /**
     * 付款成功后增加团长佣金明细
     */
    String ADD_BROKERAGE = "add_group_brokerage";

    /**
     * 订单广播
     */
    String ORDER_BROADCAST = "order_broadcast";

    /**
     * 接龙活动开始
     */
    String SOLITAIRE_ACT_START_MSG = "solitaire_act_start_msg";

    /**
     * 接龙参与记录
     */
    String SOLITAIRE_RECORD_MSG = "solitaire_record_msg";

    /**
     * 在微信群中@购买者
     */
    String SOLITAIRE_AT_MSG = "solitaire_at_msg";

    /**
     * 新用户进入群
     */
    String NEW_PERSON_JOIN_GROUP_SENDMSG = "newPersonJoinGroupSendMSg";

    /**
     * 生成接龙记录
     */
    String GENERATE_SOLITAIRE_RECORD = "generate_solitaire_record";

    /**
     * H5页面访问量
     */
    String H5_PAGE_VIEW = "h5_page_view";

    /**
     * 支付成功后会异步生成对应该订单的订单发货单关系映射数据
     */
    String GENERATE_ORDER_SENDBILL_MAPPING = "generate_order_sendbill_mapping";

    /**
     * 支付成功后增加购买过的用户数量
     */
    String UPDATE_PAYED_USER_NUM = "update_payed_user_num";
}
