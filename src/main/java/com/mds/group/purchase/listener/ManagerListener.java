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

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.model.Line;
import com.mds.group.purchase.logistics.model.Streets;
import com.mds.group.purchase.logistics.service.CommunityService;
import com.mds.group.purchase.logistics.service.LineService;
import com.mds.group.purchase.logistics.service.StreetsService;
import com.mds.group.purchase.logistics.vo.CommunityVo;
import com.mds.group.purchase.logistics.vo.LineVo;
import com.mds.group.purchase.shop.dao.BottomPosterMapper;
import com.mds.group.purchase.shop.model.BottomPoster;
import com.mds.group.purchase.shop.model.Footer;
import com.mds.group.purchase.shop.model.Manager;
import com.mds.group.purchase.shop.model.ShopFunction;
import com.mds.group.purchase.shop.service.FooterService;
import com.mds.group.purchase.shop.service.ManagerService;
import com.mds.group.purchase.shop.service.ShopFunctionService;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.GroupLeaderService;
import com.mds.group.purchase.user.service.WxuserService;
import com.mds.group.purchase.utils.IdGenerateUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * The type Manager listener.
 *
 * @author Administrator
 */
@Component
@Log4j2
public class ManagerListener {

    @Resource
    private LineService lineService;
    @Resource
    private WxuserService wxuserService;
    @Resource
    private FooterService footerService;
    @Resource
    private ManagerService managerService;
    @Resource
    private StreetsService streetsService;
    @Resource
    private CommunityService communityService;
    @Resource
    private GroupLeaderService groupLeaderService;
    @Resource
    private BottomPosterMapper bottomPosterMapper;
    @Resource
    private ShopFunctionService shopFunctionService;

    /**
     * Init merchant data.
     *
     * @param data the data
     */
    @JmsListener(destination = ActiviMqQueueName.MERCHANT_DATA_INIT)
    public void initMerchantData(String data) {
        JSONObject jsonObject = JSON.parseObject(data);
        if (jsonObject != null) {
            String appmodelId = jsonObject.getString("appmodelId");
            Manager manager = managerService.findByAppmodelId(appmodelId);
            if (manager == null) {
                manager = new Manager();
                manager.setMiniCode(jsonObject.getString("miniCode"));
                manager.setMiniName(jsonObject.getString("miniName"));
                manager.setLogo(jsonObject.getString("logo"));
                manager.setAppmodelId(appmodelId);
                manager.setAppId(StringUtils.replace(appmodelId, "S00050001", ""));
                manager.setDelState(0);
                manager.setEnterprisePayState(false);
                manager.setCreateTime(jsonObject.getString("createTime"));
                managerService.save(manager);
                //初始化店铺装修
                initFoot(manager.getAppmodelId());
                //初始化小区数据
                initCommunityGroupleader(manager.getAppmodelId());
                initBottomPoster(manager.getAppmodelId());
            } else {
                manager.setMiniCode(jsonObject.getString("miniCode"));
                manager.setMiniName(jsonObject.getString("miniName"));
                manager.setLogo(jsonObject.getString("logo"));
                managerService.update(manager);
            }
        }
    }

    private void initCommunityGroupleader(String appmodelId) {
        //创建街道
        Streets streets = new Streets();
        streets.setAppmodelId(appmodelId);
        streets.setAreaid("110101");
        streets.setStreet("街道");
        streetsService.save(streets);
        String provinceId = "110000";
        String cityId = "110100";
        String areaId = "110101";
        //创建小区
        CommunityVo communityVo = new CommunityVo();
        communityVo.setAppmodelId(appmodelId);
        communityVo.setProvinceId(provinceId);
        communityVo.setCityId(cityId);
        communityVo.setAreaId(areaId);
        communityVo.setPcaAdr("北京市市辖区东城区");
        communityVo.setStreetId(streets.getStreetid());
        communityVo.setStreetName(streets.getStreet());
        communityVo.setCommunityName("小区");
        communityVo.setLocation("29.858926,121.541552");
        communityService.saveCommunity(communityVo, appmodelId);
        //创建线路
        LineVo lineVo = new LineVo();
        lineVo.setLineName("线路");
        lineVo.setDriverName("司机");
        lineVo.setDriverPhone("15888888888");
        lineVo.setAppmodelId(appmodelId);
        lineVo.setProvinceId(provinceId);
        lineVo.setCityId(cityId);
        lineVo.setAreaId(areaId);
        List<Long> communities = new LinkedList<>();
        Community community = communityService.findBy("appmodelId", appmodelId);
        communities.add(community.getCommunityId());
        lineVo.setCommunities(communities);
        lineService.saveLine(lineVo);

        Wxuser wxuser = new Wxuser();
        wxuser.setWxuserId(IdGenerateUtils.getItemId());
        wxuser.setCreateTime(DateUtil.date());
        wxuser.setWxuserName("账号昵称");
        wxuser.setMiniOpenId("openId");
        wxuser.setAppmodelId(appmodelId);
        wxuser.setUserStatus(2);
        wxuser.setCommunityId(community.getCommunityId());
        wxuser.setWxuserId(IdGenerateUtils.getItemId());
        wxuser.setIcon("账号");
        wxuserService.save(wxuser);

        GroupLeader groupLeader = new GroupLeader();
        groupLeader.setAppmodelId(appmodelId);
        groupLeader.setAddress("团长地址");
        groupLeader.setCommunityId(community.getCommunityId());
        groupLeader.setGroupName("团长名");
        groupLeader.setGroupPhone("13888888888");
        groupLeader.setWxuserId(wxuser.getWxuserId());
        groupLeader.setStatus(1);
        groupLeader.setDeleteState(false);
        Condition condition = new Condition(Line.class);
        condition.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("delFlag", 0);
        Line line = lineService.findByCondition(condition).get(0);
        groupLeader.setLineId(line.getLineId());
        groupLeader.setBrokerage(new BigDecimal(0.0));
        groupLeader.setGroupLeaderId("TZ" + IdGenerateUtils.getItemId());
        groupLeaderService.save(groupLeader);
    }

    private void initFoot(String appmodelId) {
        ShopFunction shopFunction = shopFunctionService.findBy("appmodelId", appmodelId);
        if (shopFunction == null) {
            shopFunction = new ShopFunction();
            shopFunction.setAppmodelId(appmodelId);
            shopFunction.setActivityAlert(5);
            shopFunction.setOrderBarrageSwitchDetail(false);
            shopFunction.setOrderBarrageSwitchIndex(false);
            shopFunction.setShopStyleId(1);
            shopFunction.setWithdrawLimit(new BigDecimal(50));
            shopFunctionService.save(shopFunction);
        }
        List<Footer> footerList = footerService.findByList("appmodelId", appmodelId);
        if (footerList == null || footerList.size() == 0) {
            footerList = new LinkedList<>();
            Footer footer1 = new Footer();
            footer1.setAppmodelId(appmodelId);
            footer1.setAppPageId(1);
            footer1.setSort(1);
            footer1.setFooterFlag(true);
            footer1.setFooterImgNo("https://www.superprism.cn/resource/public/images/groupmall/homeno.png");
            footer1.setFooterImgYes("https://www.superprism.cn/resource/public/images/groupmall/homeyes.png");
            footer1.setFooterName("推荐");
            footerList.add(footer1);

            Footer footer2 = new Footer();
            footer2.setAppmodelId(appmodelId);
            footer2.setAppPageId(2);
            footer2.setSort(2);
            footer2.setFooterFlag(true);
            footer2.setFooterImgNo("https://www.superprism.cn/resource/public/images/groupmall/seckillno.png");
            footer2.setFooterImgYes("https://www.superprism.cn/resource/public/images/groupmall/seckillyes.png");
            footer2.setFooterName("秒杀");
            footerList.add(footer2);

            Footer footer3 = new Footer();
            footer3.setAppmodelId(appmodelId);
            footer3.setAppPageId(4);
            footer3.setSort(4);
            footer3.setFooterFlag(true);
            footer3.setFooterImgNo("https://www.superprism.cn/resource/public/images/groupmall/groupno.png");
            footer3.setFooterImgYes("https://www.superprism.cn/resource/public/images/groupmall/groupyes.png");
            footer3.setFooterName("拼团");
            footerList.add(footer3);

            Footer footer4 = new Footer();
            footer4.setAppmodelId(appmodelId);
            footer4.setAppPageId(3);
            footer4.setSort(3);
            footer4.setFooterFlag(false);
            footer4.setFooterImgNo("https://www.superprism.cn/resource/public/images/groupmall/discoverno.png");
            footer4.setFooterImgYes("https://www.superprism.cn/resource/public/images/groupmall/discoveryes.png");
            footer4.setFooterName("发现");
            footerList.add(footer4);

            Footer footer5 = new Footer();
            footer5.setAppmodelId(appmodelId);
            footer5.setAppPageId(5);
            footer5.setSort(5);
            footer5.setFooterFlag(true);
            footer5.setFooterImgNo("https://www.superprism.cn/resource/public/images/groupmall/meno.png");
            footer5.setFooterImgYes("https://www.superprism.cn/resource/public/images/groupmall/meyes.png");
            footer5.setFooterName("我的");
            footerList.add(footer5);
            footerService.save(footerList);
        }
    }

    private void initBottomPoster(String appmodelId) {
        BottomPoster bottomPoster = new BottomPoster();
        bottomPoster.setStatus(1);
        bottomPoster.setPosterUrl("https://www.superprism.cn/resource/groupmall/zhaopintuanzhang2.jpg");
        bottomPoster.setPhone("18106661091");
        bottomPoster.setAppmodelId(appmodelId);
        bottomPosterMapper.insert(bottomPoster);
    }
}
