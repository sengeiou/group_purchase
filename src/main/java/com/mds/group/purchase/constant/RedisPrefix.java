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
 * redis 前缀
 *
 * @author whh
 */
public interface RedisPrefix {


    /**
     * 用户缓存前缀，根据小程序openid
     */
    String USER = ":user:";


    /**
     * 用户缓存前缀，根据unionid
     */
    String USERUNION = ":userunion:";


    /**
     * 用户缓存前缀，根据mdsUnionid
     */
    String MPOPENID = ":mp_open_id:";

    /**
     * 首页订单通知
     */
    String PAY_OK_NOTIFY = ":payok_notify:";
    /**
     * The Constant PAY_OK_NOTIFY_NEW.
     */
    String PAY_OK_NOTIFY_NEW = ":payok_notify:new:";

    /**
     * The Constant PROVIDER_APPLY_REGISTER.
     */
    String PROVIDER_APPLY_REGISTER = ":provider_apply_register:";

    /**
     * 微信网站引用扫码回调
     */
    String WXCHAT_SCAN_QRCODE_NOTIFY = ":scan_qrcode_notify:";

    /**
     * 团长管理缓存
     */
    String SEARCH_GROUP_MANAGER = ":searchgroup_manager:";

    /**
     * 浏览量统计
     */
    String MANAGER_STATISTICS_PAGEVIEW = ":statistics_pageview:";

    /**
     * 访客量统计
     */
    String MANAGER_STATISTICS_VISITORSUM = ":statistics_visitorsum:";

    /**
     * 活动商品库存
     */
    String ACT_GOODS_STOCK = ":activityGoods:stock:";

    /**
     * 活动商品销量
     */
    String ACT_GOODS_SALES_VOLUME = ":activityGoods:saleVolume:";

    /**
     * 活动商品限购数量
     */
    String MAX_QUANTITY = ":activityGoods:maxQuantity:";

    /**
     * http请求访问限制
     */
    String HTTP_REQUEST_LIMIT = ":request_limit:";

    /**
     * 项目名前缀
     * 该常量已经被弃用
     * v1.2版本中将该常量提到了application.properties配置文件中
     * 避免了在切换测试和生产环境时需要手动修改的操作
     */
    @Deprecated
    String PROJECT_PREFIX = "groupmallTest:";

    /**
     * 所有省市区的数据
     */
    String ALL_PCA_PREFIX = "groupmall:pca:";

    /**
     * 首页拼团活动商品
     */
    String INDEX_GROUP_ACTGOODS = ":activity:groupActGoods:";

    /**
     * 所有活动商品(ActGoodsInfoResult)
     */
    String ALL_ACTGOODS = ":activity:allActGoods";

    /**
     * 所有活动商品(ActGoods)
     */
    String ALL_ACTGOODS_1 = ":activity:allActGoods1";

    /**
     * 所有活动商品
     */
    String ALL_GOODS_SCORE = ":AllGoodsScore";

    /**
     * 所有普通商品
     */
    String ALL_GOODS = ":Goods:allGoods";

    /**
     * 首页秒杀活动商品
     */
    String INDEX_SECKILL_ACTGOODS = ":activity:skillActGoods:";

    /**
     * 首页秒杀活动
     */
    String INDEX_SECKILL_ACT = ":activity:skillAct";

    /**
     * 首页拼团活动
     */
    String INDEX_GROUP_ACT = ":activity:groupAct";

    /**
     * 活动更新状态
     */
    String ACT_UPDATE_STATUS = ":activity:update:status:";

    /**
     * 商品投放区域缓存
     */
    String GOODS_AREA_MAPPING = ":goodsAreaMapping:all";

    /**
     * 商品投放区域缓存
     */
    String GOODS_AREA_MAPPING_HASH = ":goodsAreaMapping";

    /**
     * 要取消订单的id，如果重复则不能继续操作，该订单成功取消后删除该缓存
     */
    String CANCLE_ORDER_ID = ":cancleorder:";

    /**
     * 活动令牌
     */
    String ACTIVITY_TOKEN = ":activity:token";

    /**
     * 用户购买限额
     */
    String ACTIVITY_BUY_AMOUNT = ":activity:buy_amount:";

    /**
     * fromid
     */
    String FROMID = ":fromid:";

    /**
     * 通用过期时间
     */
    Integer EXPIRATION_TIME = 1;

    /**
     * 线路分拣单缓存
     */
    String LINE_SORTING_ORDER_CACHE = ":LineSortingOrderCache:";

    /**
     * 商品分拣单缓存
     */
    String GOODS_SORTING_ORDER_CACHE = ":GoodsSortingOrderCache:";

    /**
     * 团长签收单缓存
     */
    String RECEIPT_BILL_CACHE = ":ReceiptBillCache:";

    /**
     * h5页面访问量
     */
    String H5_PAGE_VIEW = ":H5PageViewCount:";

    /**
     * h5购买用户量
     */
    String H5_PAYED_USER_COUNT = ":h5_payed_user_count:";

    /**
     * 小程序名称
     */
    String MINI_NAME = ":mini_name:";
    /**
     * 商家小程序图标
     */
    String MINI_LOGO = ":MiniLogo";

    /**
     * 已绑定的群聊
     */
    String BOT_GROUP = ":wechatbot_group:";

}
