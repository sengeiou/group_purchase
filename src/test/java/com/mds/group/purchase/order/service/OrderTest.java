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

package com.mds.group.purchase.order.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.mds.group.purchase.GroupPurchaseApplicationTests;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.service.CommunityService;
import com.mds.group.purchase.order.vo.PayOkNotify;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.WxuserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.LinkedHashSet;

public class OrderTest extends GroupPurchaseApplicationTests {

    @Autowired
    private WxuserService wxuserService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CommunityService communityService;

    @org.junit.Test
    public void test1() {

        Wxuser wxuser = wxuserService.findById(1545276527131537L);
        Community community = communityService.findByGroupleaderId("TZ1546067050230446");
        PayOkNotify payOkNotify = new PayOkNotify();
        payOkNotify.setContent(
                community.getCommunityName().concat("的").concat(wxuser.getWxuserName()).concat("成功抢购1件...")
                        .concat(RandomUtil.randomString(5)));
        payOkNotify.setCreateTime(DateUtil.date());
        redisTemplate.opsForZSet().add("groupmall:payOkNotify:S00050001wx219007e82b660f17", payOkNotify,
                DateUtil.currentSeconds() + RandomUtil.randomInt(1000));


        wxuser = wxuserService.findById(1545371555175145L);
        community = communityService.findByGroupleaderId("TZ1546067061285147");
        payOkNotify = new PayOkNotify();
        payOkNotify.setContent(
                community.getCommunityName().concat("的").concat(wxuser.getWxuserName()).concat("成功抢购1件...")
                        .concat(RandomUtil.randomString(5)));
        payOkNotify.setCreateTime(DateUtil.date());
        redisTemplate.opsForZSet().add("groupmall:payOkNotify:S00050001wx219007e82b660f17", payOkNotify,
                DateUtil.currentSeconds() + RandomUtil.randomInt(1000));


        wxuser = wxuserService.findById(1544169688415057L);
        community = communityService.findByGroupleaderId("TZ1546067038880056");
        payOkNotify = new PayOkNotify();
        payOkNotify.setContent(
                community.getCommunityName().concat("的").concat(wxuser.getWxuserName()).concat("成功抢购1件...")
                        .concat(RandomUtil.randomString(5)));
        payOkNotify.setCreateTime(DateUtil.date());
        redisTemplate.opsForZSet().add("groupmall:payOkNotify:S00050001wx219007e82b660f17", payOkNotify,
                DateUtil.currentSeconds() + RandomUtil.randomInt(1000));
    }

    @org.junit.Test
    public void test2() {
        String redisKey = "groupmall:payOkNotify:S00050001wx219007e82b660f17";
        LinkedHashSet<PayOkNotify> payOkNotifyList = (LinkedHashSet<PayOkNotify>) redisTemplate.opsForZSet()
                .range(redisKey, 0, -1);
        Long size = redisTemplate.opsForZSet().size(redisKey);
        if (size > 20) {
            size = size - 20;
            if (size > 20) {
                redisTemplate.opsForZSet().removeRange(redisKey, 0, 19);
            } else {
                redisTemplate.opsForZSet().removeRange(redisKey, 0, size - 1);
            }
        }
        System.out.println(payOkNotifyList.toString());
    }
}
