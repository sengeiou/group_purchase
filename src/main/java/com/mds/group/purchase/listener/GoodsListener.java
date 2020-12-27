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

package com.mds.group.purchase.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.goods.model.GoodsAutoAddArea;
import com.mds.group.purchase.goods.result.ClassAndGoodsResult;
import com.mds.group.purchase.goods.result.GoodsFuzzyResult;
import com.mds.group.purchase.goods.service.GoodsAutoAddAreaService;
import com.mds.group.purchase.goods.service.GoodsService;
import com.mds.group.purchase.logistics.service.GoodsAreaMappingService;
import com.mds.group.purchase.utils.GoodsUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 普通商品的activityMQ队列消费者
 *
 * @author shuke
 */
@Component
public class GoodsListener {

    @Resource
    private GoodsUtil goodsUtil;
    @Resource
    private GoodsService goodsService;
    @Resource
    private RedisTemplate<String, List<ClassAndGoodsResult>> redisTemplate4CAG;
    @Resource
    private RedisTemplate<String, Map<String, GoodsFuzzyResult>> redisTemplate4GFR;
    @Resource
    private GoodsAutoAddAreaService autoAddAreaService;
    @Resource
    private GoodsAreaMappingService goodsAreaMappingService;

    /**
     * Update goods cache 4 poster.
     *
     * @param appmodelId the appmodel id
     */
    @JmsListener(destination = ActiviMqQueueName.GOODS_POSTER_CACHE)
    public void updateGoodsCache4Poster(String appmodelId) {
        List<ClassAndGoodsResult> classAndGoods = goodsService.findClassAndGoods(appmodelId);
        redisTemplate4CAG.opsForValue()
                .set(GroupMallProperties.getRedisPrefix() + appmodelId + ":goodsCache4Poster", classAndGoods,
                        RedisPrefix.EXPIRATION_TIME, TimeUnit.HOURS);
    }

    /**
     * Goods info cache.
     *
     * @param appmodelId the appmodel id
     */
    @JmsListener(destination = ActiviMqQueueName.GOODS_INFO_CACHE)
    public void goodsInfoCache(String appmodelId) {
        List<GoodsFuzzyResult> goods = goodsUtil.getGoodsFlushResult(appmodelId);
        Map<String, GoodsFuzzyResult> goodsFuzzyResultMap = goods.stream()
                .collect(Collectors.toMap(o -> o.getGoodsId().toString(), v -> v));
        redisTemplate4GFR.opsForValue()
                .set(GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.ALL_GOODS),
                        goodsFuzzyResultMap, RedisPrefix.EXPIRATION_TIME,
                        TimeUnit.HOURS);
    }

    /**
     * Community add to goods area.
     *
     * @param jsonData the json data
     */
    @JmsListener(destination = ActiviMqQueueName.AUTO_ADD_COMMUNITY)
    public void communityAddToGoodsArea(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        String ids = jsonObject.getString("id");
        String appmodelId = jsonObject.getString("appmodelId");
        List<String> stringList = Arrays.asList(ids.split(","));
        List<Long> communityids = stringList.stream().map(Long::valueOf).collect(Collectors.toList());

        //查询所有设置自动添加的商品
        List<GoodsAutoAddArea> byAppmodelId = autoAddAreaService.findByAppmodelId(appmodelId);
        List<Long> goods = byAppmodelId.stream().map(GoodsAutoAddArea::getGoodsId).collect(Collectors.toList());
        goodsAreaMappingService.autoAdd(goods, communityids, appmodelId);
    }

}
