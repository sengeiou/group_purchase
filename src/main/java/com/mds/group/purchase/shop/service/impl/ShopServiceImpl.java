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

package com.mds.group.purchase.shop.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.order.service.OrderService;
import com.mds.group.purchase.shop.dao.ShopRepository;
import com.mds.group.purchase.shop.model.Shop;
import com.mds.group.purchase.shop.model.Statistics;
import com.mds.group.purchase.shop.service.ShopService;
import com.mds.group.purchase.shop.service.StatisticsService;
import com.mds.group.purchase.shop.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


/**
 * The type Shop service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@Service
public class ShopServiceImpl implements ShopService {

    @Resource
    private OrderService orderService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private ShopRepository tShopRepository;
    @Resource
    private StatisticsService statisticsService;

    @Override
    public Shop saveOrUpdate(Shop shop) {
        String key = GroupMallProperties.getRedisPrefix().concat(shop.getAppmodelId()).concat(":find_shop");
        if (StringUtils.isBlank(shop.getShopId())) {
            shop.setShopId(null);
            Shop appmodelId = tShopRepository.getByAppmodelId(shop.getAppmodelId());
            if (appmodelId != null) {
                throw new ServiceException("商家已存在记录");
            }
            shop.setCreateTime(DateUtil.date());
        }
        Shop save = tShopRepository.save(shop);
        if (StringUtils.isNotBlank(save.getShopId())) {
            redisTemplate.opsForValue().set(key, shop);
        }
        return save;
    }

    @Override
    public Shop getByAppmodelId(String appmodelId) {
        String key = GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(":find_shop");
        Shop shopInfo = (Shop) redisTemplate.opsForValue().get(key);
        if (shopInfo == null) {
            shopInfo = tShopRepository.getByAppmodelId(appmodelId);
            redisTemplate.opsForValue().set(key, shopInfo);
        }
        return shopInfo;
    }

    @Override
    public GeneralSituationVO generalSituation(String appmodelId) {
        GeneralSituationVO generalSituationVO = new GeneralSituationVO();
        //获取当天的结束日期23:59:59 59:59:59
        DateTime currentDate = DateUtil.endOfDay(new DateTime());
        String currentDateStr = DateUtil.formatDateTime(currentDate);
        //获取一周前的开始日期00:00:00 00:00:00
        String lastWeek = DateUtil.formatDateTime(DateUtil.beginOfDay(DateUtil.offsetDay(currentDate, -6)));
        List<HistoricalTransactionDataVO> historicalTransactionDataVOS = orderService
                .findAweekVolumeOfBusinessData(appmodelId, currentDateStr, lastWeek);
        generalSituationVO.setHistoricalTransactionDataVO(historicalTransactionDataVOS);
        try {
            List<RealTimeGeneralSituationVO> realTimeGeneralSituationVOS1 = CompletableFuture.supplyAsync(() -> {
                List<RealTimeGeneralSituationVO> realTimeGeneralSituationVOS = new LinkedList<>();
                //实时概况,三个数组,[0]今日,[1]昨日,[2]最近七天
                //[0]今日
                RealTimeGeneralSituationVO realTimeGeneralSituationVO1 = new RealTimeGeneralSituationVO();
                realTimeGeneralSituationVO1
                        .setTodayVolumeOfBusiness(historicalTransactionDataVOS.get(0).getVolumeOfBusiness());
                realTimeGeneralSituationVO1.setTodayVolumeOfBusinessNumber(
                        historicalTransactionDataVOS.get(0).getVolumeOfBusinessNumber());
                //今日浏览量统计
                Integer pageview = (Integer) redisTemplate.opsForValue()
                        .get(GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.MANAGER_STATISTICS_PAGEVIEW));
                realTimeGeneralSituationVO1.setTodayVisitorVolume(pageview);
                //今日访客量统计
                Integer visitorsum = (Integer) redisTemplate.opsForValue()
                        .get(GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.MANAGER_STATISTICS_VISITORSUM));
                realTimeGeneralSituationVO1.setTodayAccessNumber(visitorsum);
                realTimeGeneralSituationVOS.add(realTimeGeneralSituationVO1);

                //[1]昨日
                //昨日浏览量统计|今日访客量统计
                RealTimeGeneralSituationVO realTimeGeneralSituationVO2 = new RealTimeGeneralSituationVO();
                realTimeGeneralSituationVO2
                        .setTodayVolumeOfBusiness(historicalTransactionDataVOS.get(1).getVolumeOfBusiness());
                realTimeGeneralSituationVO2.setTodayVolumeOfBusinessNumber(
                        historicalTransactionDataVOS.get(1).getVolumeOfBusinessNumber());
                Statistics todayStatisticData = statisticsService.findTodayStatisticData(appmodelId);
                if (todayStatisticData == null) {
                    realTimeGeneralSituationVO2.setTodayAccessNumber(0);
                    realTimeGeneralSituationVO2.setTodayVisitorVolume(0);
                } else {
                    realTimeGeneralSituationVO2.setTodayAccessNumber(todayStatisticData.getVisitorsum());
                    realTimeGeneralSituationVO2.setTodayVisitorVolume(todayStatisticData.getPageview());
                }
                realTimeGeneralSituationVOS.add(realTimeGeneralSituationVO2);

                //[2]最近七天
                RealTimeGeneralSituationVO realTimeGeneralSituationVO3 = new RealTimeGeneralSituationVO();
                double volumeOfBusinessTotle = historicalTransactionDataVOS.stream()
                        .mapToDouble(obj -> obj.getVolumeOfBusiness().doubleValue()).sum();
                realTimeGeneralSituationVO3.setTodayVolumeOfBusiness(NumberUtil.round(volumeOfBusinessTotle, 2));
                int volumeOfBusinessNumberTotle = historicalTransactionDataVOS.stream()
                        .mapToInt(HistoricalTransactionDataVO::getVolumeOfBusinessNumber).sum();
                realTimeGeneralSituationVO3.setTodayVolumeOfBusinessNumber(volumeOfBusinessNumberTotle);

                Statistics sevenDaysStatisticData = statisticsService
                        .findSevenDaysStatisticData(appmodelId, currentDateStr, lastWeek);
                //7天浏览量统计|今日访客量统计
                if (sevenDaysStatisticData != null) {
                    realTimeGeneralSituationVO3.setTodayVisitorVolume(sevenDaysStatisticData.getVisitorsum());
                    realTimeGeneralSituationVO3.setTodayAccessNumber(sevenDaysStatisticData.getPageview());
                } else {
                    realTimeGeneralSituationVO3.setTodayVisitorVolume(0);
                    realTimeGeneralSituationVO3.setTodayAccessNumber(0);
                }
                realTimeGeneralSituationVOS.add(realTimeGeneralSituationVO3);
                return realTimeGeneralSituationVOS;
            }).get();
            generalSituationVO.setRealTimeGeneralSituationVOList(realTimeGeneralSituationVOS1);

            OrderDataVO orderDataVO1 = CompletableFuture.supplyAsync(() -> {
                //订单统计
                return orderService.findByAppmodelIdStatistics(appmodelId);
            }).get();
            generalSituationVO.setOrderDataVO(orderDataVO1);


            List<SalesVolumeVO> salesVolumeVOS = CompletableFuture.supplyAsync(() -> {
                //团长销售额
                return orderService.findByGroupleaderSale(appmodelId);
            }).get();
            generalSituationVO.setSalesVolumeVO(salesVolumeVOS);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return generalSituationVO;
    }
}
