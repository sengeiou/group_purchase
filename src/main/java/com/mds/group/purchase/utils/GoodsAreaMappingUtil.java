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

package com.mds.group.purchase.utils;

import cn.hutool.core.util.HashUtil;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.logistics.model.GoodsAreaMapping;
import com.mds.group.purchase.logistics.service.GoodsAreaMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Goods area mapping util.
 *
 * @author shuke
 * @date 2019 -2-23
 */
@Component
public class GoodsAreaMappingUtil {

    @Resource
    private GoodsAreaMappingService goodsAreaMappingService;
    @Resource
    private RedisTemplate<String, Map<String, List<GoodsAreaMapping>>> redisTemplate;
    @Resource
    private RedisTemplate<String, List<GoodsAreaMapping>> redisTemplateHash;

    private Logger logger = LoggerFactory.getLogger(GoodsAreaMappingUtil.class);

    /**
     * Flush goods area mapping.
     *
     * @param appmodelId the appmodel id
     */
    public void flushGoodsAreaMapping(String appmodelId) {
        List<GoodsAreaMapping> list = goodsAreaMappingService.findByList("appmodelId", appmodelId);
        if (list == null || list.isEmpty()) {
            return;
        }
        Map<String, List<GoodsAreaMapping>> map = list.stream().distinct()
                .collect(Collectors.groupingBy(o -> o.getGoodsId().toString()));
        try {
            map.forEach((k, v) -> {
                String key = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.GOODS_AREA_MAPPING_HASH;
                String hashKey = String.valueOf(HashUtil.bkdrHash(key.concat(":").concat(k)));
                redisTemplateHash.opsForHash().put(key, hashKey, v);
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        String key = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.GOODS_AREA_MAPPING;
        redisTemplate.opsForValue().set(key, map);
    }

    /**
     * 得到投放区域缓存map
     *
     * @param appmodelId the appmodel id
     * @return the cache map
     */
    Map<String, List<GoodsAreaMapping>> getCacheMap(String appmodelId) {
        //获得投放区域缓存
        String key = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.GOODS_AREA_MAPPING;
        Map<String, List<GoodsAreaMapping>> map = redisTemplate.opsForValue()
                .get(key);
        if (map == null || map.isEmpty()) {
            flushGoodsAreaMapping(appmodelId);
            List<GoodsAreaMapping> list = goodsAreaMappingService.findByList("appmodelId", appmodelId);
            if (list != null && !list.isEmpty()) {
                map = list.stream().distinct().collect(Collectors.groupingBy((o -> o.getGoodsId().toString())));
            } else {
                map = new HashMap<>(8);
            }
        }
        return map;
    }
}
