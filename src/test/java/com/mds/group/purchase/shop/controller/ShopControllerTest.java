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

package com.mds.group.purchase.shop.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.mds.group.purchase.GroupPurchaseApplicationTests;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.model.Line;
import com.mds.group.purchase.logistics.model.Streets;
import com.mds.group.purchase.logistics.result.StreetsResult;
import com.mds.group.purchase.logistics.service.CommunityService;
import com.mds.group.purchase.logistics.service.LineService;
import com.mds.group.purchase.logistics.service.StreetsService;
import com.mds.group.purchase.logistics.vo.CommunityVo;
import com.mds.group.purchase.logistics.vo.LineVo;
import com.mds.group.purchase.shop.model.Manager;
import com.mds.group.purchase.shop.model.Shop;
import com.mds.group.purchase.shop.service.ManagerService;
import com.mds.group.purchase.shop.service.ShopFunctionService;
import com.mds.group.purchase.shop.service.ShopService;
import com.mds.group.purchase.shop.vo.ShopCreateUpdateVO;
import com.mds.group.purchase.user.dao.GroupLeaderMapper;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.WxuserService;
import com.mds.group.purchase.utils.IdGenerateUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
@Transactional
public class ShopControllerTest extends GroupPurchaseApplicationTests {

    @Autowired
    private ShopService shopService;

    @Autowired
    private ShopFunctionService shopFunctionService;
    @Autowired
    private CommunityService communityService;
    @Autowired
    private StreetsService streetsService;
    @Autowired
    private LineService lineService;
    @Autowired
    private WxuserService wxuserService;
    @Autowired
    private ManagerService managerService;
    @Resource
    private GroupLeaderMapper tGroupLeaderMapper;

    /**
     * 保存或更新店铺信息
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void saveOrUpdateShop() throws Exception {
        Shop shop = new Shop();
        String appmodelId = "S00050001wx219007e82b660f17";
        shop.setAppmodelId(appmodelId);
        shop.setShopName("店铺名称");
        shop.setShopPhone("15888030961");
        shop.setShopAddress("店铺地址");
        shop.setBusinessHours("2018-12-08 10:20:18");
        //添加店铺信息
        try {
            Shop shop1 = shopService.saveOrUpdate(shop);
            Assert.assertNotNull("店铺信息添加失败", shop1.getShopId());
        } catch (ServiceException e) {

        }
        Shop shop2 = shopService.getByAppmodelId(appmodelId);
        Assert.assertNotNull("创建时间未添加", shop2.getCreateTime());
        Assert.assertNotNull("店铺名不能空", shop2.getShopName());
        Assert.assertNotNull("手机号为空", shop2.getShopPhone());
        Assert.assertNotNull("营业时间不能为空", shop2.getBusinessHours());
        Assert.assertNotNull("店铺地址不能为空", shop2.getShopAddress());
        shop2.setShopAddress("店铺地址修改");
        shopService.saveOrUpdate(shop2);
        Shop shop3 = shopService.getByAppmodelId(appmodelId);
        Assert.assertEquals(shop2.getShopAddress(), shop3.getShopAddress());
    }

    /**
     * 保存或更新店铺设置
     */
    @Test
    @Ignore
    public void shopCreateUpdateSetting() {
        String appmodelId = "S00050001wx219007e82b660f17";
        ShopCreateUpdateVO shopCreateUpdateVO = new ShopCreateUpdateVO();
        shopCreateUpdateVO.setAppmodelId(appmodelId);
        shopCreateUpdateVO.setOrderBarrageSwitchIndex(true);
        shopCreateUpdateVO.setOrderBarrageSwitchDetail(true);
        //shopCreateUpdateVO.setFootMark(true);
        shopCreateUpdateVO.setEnterprisePayState(true);
        shopCreateUpdateVO.setJoinPhone("158880002");
        shopCreateUpdateVO.setWithdrawLimit(new BigDecimal(321));
        shopCreateUpdateVO.setAgreement("协议协议");
        try {
            //增加店铺信息
            shopFunctionService.shopCreateUpdate(shopCreateUpdateVO);
        } catch (ServiceException e) {

        }
        ShopCreateUpdateVO shop = shopFunctionService.findByAppmodelId(appmodelId);
        Assert.assertNotNull("店铺信息未空", shop);
        shopCreateUpdateVO.setShopFunctionId(shop.getShopFunctionId());
        Assert.assertEquals(shopCreateUpdateVO, shop);
        //修改店铺
        shop.setAgreement("协议修改修改");
        shopCreateUpdateVO.setOrderBarrageSwitchIndex(false);
        shopCreateUpdateVO.setOrderBarrageSwitchDetail(false);
        //shopCreateUpdateVO.setFootMark(false);
        shopCreateUpdateVO.setEnterprisePayState(false);
        shopFunctionService.shopCreateUpdate(shop);
        ShopCreateUpdateVO updateVO = shopFunctionService.findByAppmodelId(appmodelId);
        Assert.assertEquals(shop, updateVO);
    }

    @Test
    @Ignore
    public void test() {
        List<Manager> all = managerService.findAll();

        for (Manager manager : all) {
            String appmodelId = manager.getAppmodelId();
            //保证团长必须要有一个
            Condition condition1 = new Condition(GroupLeader.class);
            condition1.createCriteria().andEqualTo("deleteState", 0).andEqualTo("appmodelId", appmodelId);
            int sum = tGroupLeaderMapper.selectCountByCondition(condition1);
            if (sum == 0) {
                List<StreetsResult> streetsResultsList = streetsService.findByAppmodelId(appmodelId);
                Streets streets = null;
                if (CollectionUtil.isEmpty(streetsResultsList)) {
                    //创建街道
                    streets = new Streets();
                    streets.setAppmodelId(appmodelId);
                    streets.setAreaid("110101");
                    streets.setStreet("测试街道");
                    streetsService.save(streets);
                }
                String provinceId = "110000";
                String cityId = "110100";
                String areaId = "110101";
                List<Community> communityList = communityService.findByList("appmodelId", appmodelId);
                if (CollectionUtil.isNotEmpty(communityList)) {
                    communityList = communityList.stream().filter(obj -> obj.getDelFlag().equals(0))
                            .collect(Collectors.toList());
                }
                if (CollectionUtil.isEmpty(communityList)) {
                    //创建小区
                    CommunityVo communityVo = new CommunityVo();
                    communityVo.setAppmodelId(appmodelId);
                    communityVo.setProvinceId(provinceId);
                    communityVo.setCityId(cityId);
                    communityVo.setAreaId(areaId);
                    communityVo.setPcaAdr("北京市市辖区东城区");
                    communityVo.setStreetId(streets.getStreetid());
                    communityVo.setStreetName(streets.getStreet());
                    communityVo.setCommunityName("测试小区");
                    communityVo.setLocation("29.858926,121.541552");
                    communityService.saveCommunity(communityVo, appmodelId);
                }

                List<Line> lineList = lineService.findByList("appmodelId", appmodelId);
                if (CollectionUtil.isNotEmpty(lineList)) {
                    lineList = lineList.stream().filter(obj -> obj.getDelFlag().equals(0)).collect(Collectors.toList());
                }
                if (CollectionUtil.isEmpty(lineList)) {
                    //创建线路
                    LineVo lineVo = new LineVo();
                    lineVo.setLineName("测试线路");
                    lineVo.setDriverName("测试司机");
                    lineVo.setDriverPhone("15888888888");
                    lineVo.setAppmodelId(appmodelId);
                    lineVo.setProvinceId(provinceId);
                    lineVo.setCityId(cityId);
                    lineVo.setAreaId(areaId);
                    List<Long> communities = new LinkedList<>();
                    Condition condition = new Condition(Community.class);
                    condition.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("delFlag", 0);
                    List<Community> byCondition = communityService.findByCondition(condition);
                    Community community = byCondition.get(0);
                    communities.add(community.getCommunityId());
                    lineVo.setCommunities(communities);
                    lineService.saveLine(lineVo);
                }
                List<GroupLeader> groupLeaders = tGroupLeaderMapper.selectByAppmodelId(appmodelId);
                if (CollectionUtil.isEmpty(groupLeaders)) {
                    Condition condition = new Condition(Community.class);
                    condition.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("delFlag", 0);
                    List<Community> byCondition = communityService.findByCondition(condition);
                    Community community = byCondition.get(0);

                    Wxuser wxuser = new Wxuser();
                    wxuser.setWxuserId(IdGenerateUtils.getItemId());
                    wxuser.setCreateTime(DateUtil.date());
                    wxuser.setWxuserName("测试账号昵称");
                    wxuser.setMiniOpenId("测试openId");
                    wxuser.setAppmodelId(appmodelId);
                    wxuser.setUserStatus(2);
                    wxuser.setCommunityId(community.getCommunityId());
                    wxuser.setWxuserId(IdGenerateUtils.getItemId());
                    wxuser.setIcon("测试账号");
                    wxuserService.save(wxuser);

                    GroupLeader groupLeader = new GroupLeader();
                    groupLeader.setAppmodelId(appmodelId);
                    groupLeader.setAddress("测试团长地址");
                    groupLeader.setCommunityId(community.getCommunityId());
                    groupLeader.setGroupName("测试团长名");
                    groupLeader.setGroupPhone("13888888888");
                    groupLeader.setWxuserId(wxuser.getWxuserId());
                    groupLeader.setStatus(1);
                    groupLeader.setDeleteState(false);
                    Condition condition2 = new Condition(Line.class);
                    condition2.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("delFlag", 0);
                    Line line = lineService.findByCondition(condition2).get(0);
                    groupLeader.setLineId(line.getLineId());
                    groupLeader.setBrokerage(new BigDecimal(0.0));
                    groupLeader.setGroupLeaderId("TZ" + IdGenerateUtils.getItemId());
                    tGroupLeaderMapper.insertSelective(groupLeader);
                }
            }

        }


    }
}