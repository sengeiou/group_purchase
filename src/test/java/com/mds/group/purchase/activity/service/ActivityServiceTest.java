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

package com.mds.group.purchase.activity.service;

import com.mds.group.purchase.activity.vo.ActivityGoodsVo;
import com.mds.group.purchase.activity.vo.ActivityVo;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.jobhandler.to.ActiveMqTaskTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@Rollback
@Transactional
@SpringBootTest
public class ActivityServiceTest {

    @Resource
    private ActivityService activityService;
    @Resource
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试添加活动
     * @throws ParseException
     */
    @Test
    public void createActivity() throws ParseException {
        ActivityVo activityVo = new ActivityVo();
        activityVo.setActivityName("单元测试活动");
        activityVo.setActivityPoster("http://OrderTest.poster.cn");
        activityVo.setActivityType(1);
        activityVo.setReadyTime("1:30");
        String string = "2019-1-1 00:00:00";
        activityVo.setStartTime(string);
        activityVo.setEndTime("2019-1-2 00:00:00");
        activityVo.setForecastReceiveTime("2019-1-3 00:00:00");
        List<ActivityGoodsVo> activityGoodsList = new ArrayList<>();
        ActivityGoodsVo activityGoodsVo = new ActivityGoodsVo();
        activityGoodsVo.setActivityDiscount(8.5);
        activityGoodsVo.setActivityStock(20);
        activityGoodsVo.setGoodsId(48L);
        activityGoodsVo.setIndexDisplay(true);
        activityGoodsVo.setMaxQuantity(1);
        activityGoodsList.add(activityGoodsVo);
        activityVo.setActGoodsList(activityGoodsList);
        activityService.createActivity(activityVo);
    }

    /**
     * 测试修改活动
     */
    @Test
    public void modifyActivity() throws ParseException {
        ActivityVo activityVo = new ActivityVo();
        activityVo.setActivityId(10086L);
        activityVo.setActivityName("单元测试活动");
        activityVo.setActivityPoster("http://OrderTest.poster.cn");
        activityVo.setActivityType(1);
        activityVo.setReadyTime("2:00");
        activityVo.setStartTime("2019-1-1 00:00:00");
        activityVo.setEndTime("2019-1-2 00:00:00");
        activityVo.setForecastReceiveTime("2019-1-3 00:00:00");
        List<ActivityGoodsVo> activityGoodsList = new ArrayList<>();
        ActivityGoodsVo activityGoodsVo = new ActivityGoodsVo();
        activityGoodsVo.setActivityDiscount(8.5);
        activityGoodsVo.setActivityStock(20);
        activityGoodsVo.setGoodsId(48L);
        activityGoodsVo.setIndexDisplay(true);
        activityGoodsVo.setMaxQuantity(1);
        activityGoodsList.add(activityGoodsVo);
        activityVo.setActGoodsList(activityGoodsList);
        activityService.modifyActivity(activityVo);
    }

    @Test
    public void deleteActivity() {
        Query query = new Query();
        List list = new ArrayList();
        list.add(ActiviMqQueueName.START_ACTIVITY_V1);
        list.add(ActiviMqQueueName.READY_ACTIVITY_V1);
        list.add(ActiviMqQueueName.END_ACTIVITY_V1);
        query.addCriteria(Criteria.where("queueName").in(list).and("jsonData").regex(".*?10458.*"));
        List<ActiveMqTaskTO> activeMqTaskTOS = mongoTemplate.find(query, ActiveMqTaskTO.class);
        System.out.println(activeMqTaskTOS);
        if (activeMqTaskTOS != null) {
            for (ActiveMqTaskTO activeMqTaskTO : activeMqTaskTOS) {
                mongoTemplate.remove(activeMqTaskTO);
            }
        }
        //	activityService.deleteActivity("10086", "sdaasd");
    }

    @Test
    public void findAllAct() {
        assertNotNull(activityService.findAllAct("S00050001wx219007e82b660f17", -1));
    }

    @Test
    public void findAct() {
        assertNotNull(activityService.findAct("S00050001wx219007e82b660f17", -1));
    }
}