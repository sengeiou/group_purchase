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

package com.mds.group.purchase.user.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mds.group.purchase.GroupPurchaseApplicationTests;
import com.mds.group.purchase.common.CommunityUtil;
import com.mds.group.purchase.logistics.controller.VO.ProvinceCityAreaStreetsVO;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.model.Provinces;
import com.mds.group.purchase.logistics.model.Streets;
import com.mds.group.purchase.logistics.service.*;
import com.mds.group.purchase.logistics.vo.LineV12Vo;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.GroupBpavawiceOrderService;
import com.mds.group.purchase.user.service.GroupLeaderService;
import com.mds.group.purchase.user.service.WxuserService;
import com.mds.group.purchase.user.vo.*;
import com.mds.group.purchase.utils.GeoCodeUtil;
import com.mds.group.purchase.utils.IdGenerateUtils;
import com.mds.group.purchase.utils.ResultPage;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Transactional
public class GroupLeaderControllerTest extends GroupPurchaseApplicationTests {
    @Autowired
    private GroupLeaderService groupLeaderService;
    @Autowired
    private WxuserService wxuserService;
    @Autowired
    private CommunityUtil communityUtil;
    @Autowired
    private AreasService areasService;
    @Autowired
    private CitiesService citiesService;
    @Autowired
    private ProvincesService provincesService;
    @Autowired
    private LineService lineService;
    @Autowired
    private StreetsService streetsService;
    @Autowired
    private CommunityService communityService;

    @Autowired
    private GroupBpavawiceOrderService groupBpavawiceOrderService;

    private static String appmodelId = "S00050001wx219007e82b660f17";

    private Long wxuserId;

    @Before
    public void setUp() throws Exception {
        wxuserId=addWxuser();
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * 添加用户
     * @return
     */
    private Long addWxuser() {
        ProvinceCityAreaStreetsVO comminuty = communityUtil.createComminuty();
        Wxuser wxuser = new Wxuser();
        wxuser.setWxuserId(IdGenerateUtils.getItemId());
        wxuser.setCreateTime(DateUtil.date());
        wxuser.setWxuserName("简介昵称" + RandomUtil.randomString(10));
        wxuser.setWxuserDesc("简介测试" + RandomUtil.randomString(10));
        wxuser.setRemark("备注测试" + RandomUtil.randomString(5));
        wxuser.setMiniOpenId(IdGenerateUtils.getItemId() + "");
        wxuser.setSessionKey(IdGenerateUtils.getItemId() + "");
        wxuser.setAppmodelId(CommunityUtil.appmodelId);
        wxuser.setUserStatus(1);
        wxuser.setCommunityId(comminuty.getCommunity().getCommunityId());
        wxuser.setWxuserId(IdGenerateUtils.getItemId());
        wxuser.setIcon(
                "https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTL7ianzUvia6hYx9MKmicMuEKn1Ta3y3AHKeiaibLawiaHsico4hCOibicJZjPN1QTFL8klUcYa52jW6QcVEug/132");
        wxuserService.save(wxuser);
        return wxuser.getWxuserId();
    }

    /**
     * 申请成为团长
     */
    @Test
    @Ignore
    public void groupApplyRegister() {
        Long wxuserId = addWxuser();
        GroupApplyForVO groupApplyForVO = new GroupApplyForVO();
        groupApplyForVO.setAppmodelId(CommunityUtil.appmodelId);
        groupApplyForVO.setAddress("地址地址" + RandomUtil.randomString(10));
        ProvinceCityAreaStreetsVO comminuty = communityUtil.createLine();
        Community community = comminuty.getCommunity();
        groupApplyForVO.setCommunityId(community.getCommunityId());
        groupApplyForVO.setGroupName("团长名" + RandomUtil.randomString(10));
        groupApplyForVO.setGroupPhone("138" + RandomUtil.randomNumbers(8));
        groupApplyForVO.setWxuserId(wxuserId);
        groupApplyForVO.setOptionType(1);
        groupLeaderService.groupApplyRegister(groupApplyForVO);
        GroupLeader groupLeader = groupLeaderService.findBy("wxuserId", wxuserId);
        Assert.assertNotNull("团长未注册成功", groupLeader);
        Assert.assertEquals("申请状态应为待审核", Integer.valueOf(0), groupLeader.getStatus());
    }

    @Test
    @Ignore
    public void test1() {
        groupApplyRegister();
        groupApplyRegister();
        groupApplyRegister();
        groupApplyRegister();
        groupApplyRegister();
        groupApplyRegister();
        groupApplyRegister();
        groupApplyRegister();
        groupApplyRegister();
        groupApplyRegister();
        groupApplyRegister2();
        groupApplyRegister2();
        groupApplyRegister2();
        groupApplyRegister2();
        groupApplyRegister2();
        groupApplyRegister2();
        groupApplyRegister2();
        groupApplyRegister2();
    }

    /**
     * 新增团长
     */
    @Test
    @Ignore
    public void groupApplyRegister2() {
        Long wxuserId = addWxuser();
        GroupApplyForVO groupApplyForVO = new GroupApplyForVO();
        groupApplyForVO.setAppmodelId(appmodelId);
        groupApplyForVO.setAddress("地址地址" + RandomUtil.randomString(10));
        ProvinceCityAreaStreetsVO comminuty = communityUtil.createLine();
        Community community = comminuty.getCommunity();
        groupApplyForVO.setCommunityId(community.getCommunityId());
        groupApplyForVO.setGroupName("团长名" + RandomUtil.randomString(10));
        groupApplyForVO.setGroupPhone("138" + RandomUtil.randomNumbers(8));
        groupApplyForVO.setWxuserId(wxuserId);
        groupApplyForVO.setOptionType(2);
        groupLeaderService.groupApplyRegister(groupApplyForVO);
        GroupLeader groupLeader = groupLeaderService.findBy("wxuserId", wxuserId);
        Assert.assertNotNull("团长未添加成功", groupLeader);
        Assert.assertEquals("申请状态应为正常状态", Integer.valueOf(1), groupLeader.getStatus());
    }

    /**
     *同意/拒绝团长申请/禁用/同意
     */
    @Test
    @Ignore
    public void groupApply1() {
        groupApplyRegister();
        groupApplyRegister();
        //搜索类型  0-待审核 1-正常 2-拒绝 3-禁用中
        List<GroupManagerVO> groupManagerVOS = groupLeaderService
                .searchGroupManager(1, 2, 0, "", "", "", "", appmodelId);
        GroupManagerVO groupManagerVO1 = groupManagerVOS.get(0);
        GroupApplyVO groupApplyVO = new GroupApplyVO();
        groupApplyVO.setId(groupManagerVO1.getGroupLeaderId());
        groupApplyVO.setOptionType(1);
        groupLeaderService.groupApply(groupApplyVO);
        GroupLeader byId = groupLeaderService.findById(groupManagerVO1.getGroupLeaderId());
        Assert.assertEquals("同意申请未正确修改状态", Integer.valueOf(1), byId.getStatus());

        groupApplyVO.setOptionType(2);
        groupLeaderService.groupApply(groupApplyVO);
        byId = groupLeaderService.findById(groupManagerVO1.getGroupLeaderId());
        Assert.assertEquals("禁用状态未正确修改", Integer.valueOf(3), byId.getStatus());

        groupApplyVO.setOptionType(3);
        groupLeaderService.groupApply(groupApplyVO);
        byId = groupLeaderService.findById(groupManagerVO1.getGroupLeaderId());
        Assert.assertEquals("启用状态未正确修改", Integer.valueOf(1), byId.getStatus());

        GroupManagerVO groupManagerVO2 = groupManagerVOS.get(1);
        groupApplyVO.setId(groupManagerVO2.getGroupLeaderId());
        groupApplyVO.setOptionType(0);
        groupLeaderService.groupApply(groupApplyVO);
        byId = groupLeaderService.findById(groupManagerVO2.getGroupLeaderId());
        Assert.assertEquals("同意拒绝申请未正确修改状态", Integer.valueOf(2), byId.getStatus());
    }

    /**
     *批量删除单个/多个团长|申请记录
     */
    @Test
    public void groupDelete() {
        groupApplyRegister();
        groupApplyRegister();
        //searchType 0-待审核 1-正常 2-拒绝 3-禁用中
        List<GroupManagerVO> groupManagerVOS = groupLeaderService
                .searchGroupManager(1, 2, 0, "", "", "", "", appmodelId);
        String groupLeaderId1 = groupManagerVOS.get(0).getGroupLeaderId();
        String groupLeaderId2 = groupManagerVOS.get(1).getGroupLeaderId();
        DeleteVO deleteVO = new DeleteVO();
        deleteVO.setIds(groupLeaderId1 + "," + groupLeaderId2);
        groupLeaderService.groupDelete(deleteVO);
        GroupLeader byId = groupLeaderService.findById(groupLeaderId1);
        Assert.assertNull("申请记录未删除成功", byId);
        GroupLeader byId1 = groupLeaderService.findById(groupLeaderId1);
        Assert.assertNull("申请记录未删除成功", byId1);

        groupApply1();
        List<GroupLeader> statusLeaders = groupLeaderService.findByList("status", 2);
        Collections.shuffle(statusLeaders);
        groupLeaderId1 = statusLeaders.get(0).getGroupLeaderId();
        deleteVO.setIds(groupLeaderId1 + ",");
        groupLeaderService.groupDelete(deleteVO);
        byId = groupLeaderService.findById(groupLeaderId1);
        Assert.assertNull("多个团长未删除成功", byId);
    }

    @Test
    @Ignore
    public void withdrawMoneyApply() {
        WithdrawMoneyApplyVO withdrawMoneyVO = new WithdrawMoneyApplyVO();
        withdrawMoneyVO.setAppmodelId(CommunityUtil.appmodelId);
        withdrawMoneyVO.setGroupLeaderId("TZ1544235617666623");
        withdrawMoneyVO.setWithdrawMoney(RandomUtil.randomBigDecimal(new BigDecimal(10), new BigDecimal(30)));
        withdrawMoneyVO.setOptionType(1);
        withdrawMoneyVO.setWithdrawMoney(BigDecimal.valueOf(50.1));
        withdrawMoneyVO.setFormId("formid............");
        groupLeaderService.withdrawMoneyApply(withdrawMoneyVO);
        GroupLeader groupLeaderId = groupLeaderService.findBy("groupLeaderId", withdrawMoneyVO.getGroupLeaderId());
        Assert.assertNotNull("数据添加失败", groupLeaderId);
    }

    /**
     * 提现记录查询
     */
    @Test
    @Ignore
    public void withdrawMoneyDetails() {
        withdrawMoneyApply();
        withdrawMoneyApply();
        withdrawMoneyApply();
        ResultPage<List<WithdrawMoneyDetailsVO>> list = groupBpavawiceOrderService
                .withdrawMoneyDetails(0, 10, 0, "TZ1544235617666623", appmodelId);
        Assert.assertNotNull("数据为空", list);
    }


    /**
     * 团长提现记录备注
     */
    @Test
    @Ignore
    public void withdrawMoneyRemark() {
        withdrawMoneyApply();
        withdrawMoneyApply();
        ResultPage<List<WithdrawMoneyDetailsVO>> resultPage = groupBpavawiceOrderService
                .withdrawMoneyDetails(0, 10, 0, "TZ1544235617666623", appmodelId);
        List<WithdrawMoneyDetailsVO> list = resultPage.getList();
        StringBuilder sb = new StringBuilder();
        for (WithdrawMoneyDetailsVO withdrawMoneyDetailsVO : list) {
            sb.append(withdrawMoneyDetailsVO.getGroupBpavawiceOrderId()).append(",");
        }
        String ids = sb.toString().substring(0, sb.length() - 1);
        RemarkVO remarkVO = new RemarkVO();
        remarkVO.setIds(ids);
        String remark = "备注内容11111";
        remarkVO.setRemark(remark);
        remarkVO.setCoverType(1);
        groupBpavawiceOrderService.withdrawMoneyRemark(remarkVO);
        List<FinanceManagerVO> financeManagerVOS = groupBpavawiceOrderService
                .findanceManager(1, 10, 0, "", "", "", "", ",", null);
        for (FinanceManagerVO financeManagerVO : financeManagerVOS) {
            if (ids.contains(financeManagerVO.getGroupBpavawiceOrderId().toString())) {
                Assert.assertEquals("未备注成功", remark, financeManagerVO.getRemark());
            }
        }

    }

    /**
     * 团长申请单元测试
     * @since v1.19
     */
    @Test
    public void groupApplyRegisterV119() {
        GroupApplyRegisterV119VO groupApply = new GroupApplyRegisterV119VO();
        groupApply.setAppmodelId(appmodelId);
        groupApply.setCommunityName("单元测试小区");
        groupApply.setFormId("");
        groupApply.setGroupLocation("29.88615,121.55468");
        groupApply.setGroupName("shuke");
        groupApply.setPickUpAddr("洪塘易淘");
        groupApply.setWxuserId(wxuserId);
        groupLeaderService.groupApplyRegisterV119(groupApply);
        //查询是否成功插入数据库
        GroupLeader byStatusAppmodelId = groupLeaderService.findByWxuserId(wxuserId);
        Assert.assertNotNull(byStatusAppmodelId);
    }

    /**
     * 编辑团长信息单元测试
     * @since v1.1.9
     */
    @Test
    public void groupUpdateV119() {
        groupApplyRegisterV119();
    }

    /**
     * 审核团长单元测试
     * @since v1.1.9
     */
    @Test
    public void groupApplyV119() {
        groupApplyRegisterV119();
        GroupLeader byWxuserId = groupLeaderService.findByWxuserId(wxuserId);
        //根据前端传递的坐标得到省市区的信息
        String groupLocation = byWxuserId.getGroupLocation();
        int i = groupLocation.indexOf(",");
        String area = GeoCodeUtil.getArea(groupLocation.substring(i + 1),groupLocation.substring(0, i),  false);
        JSONObject jsonObject = JSON.parseObject(area);
        String cityName = jsonObject.getJSONObject("regeocode").getJSONObject("addressComponent").getString("city");
        String provinceName = jsonObject.getJSONObject("regeocode").getJSONObject("addressComponent").getString("province");
        String districtName = jsonObject.getJSONObject("regeocode").getJSONObject("addressComponent").getString("district");
        Provinces by = provincesService.findBy("province", provinceName);
        String cityId = citiesService.findIdLikeName(by.getProvinceid(), cityName);
        String districtId = areasService.findIdLikeName(cityId, districtName);

        //新建一个测试街道
        Streets streets = new Streets();
        streets.setStreet("测试街道");
        streets.setAppmodelId(appmodelId);
        streets.setAreaid(districtId);
        streetsService.save(streets);

        //新建一条测试线路
        LineV12Vo lineVo = new LineV12Vo();
        lineVo.setLineName("测试线路");
        lineVo.setDriverPhone("13333333333");
        lineVo.setDriverName("老爷");
        lineVo.setAppmodelId(appmodelId);
        lineVo.setAreaId(districtId);
        lineVo.setCityId(cityId);
        lineVo.setProvinceId(by.getProvinceid());
        Long lineId = lineService.saveLineV12(lineVo);

        //处理申请
        GroupApplyV119VO groupApplyV119VO = new GroupApplyV119VO();
        groupApplyV119VO.setAppmodelId(appmodelId);
        groupApplyV119VO.setLocation(byWxuserId.getGroupLocation());
        groupApplyV119VO.setCommunityName(byWxuserId.getApplyCommunityName());
        groupApplyV119VO.setGroupName(byWxuserId.getGroupName());
        groupApplyV119VO.setGroupPhone(byWxuserId.getGroupPhone());
        groupApplyV119VO.setOptionType(1);
        groupApplyV119VO.setAreaId(districtId);
        groupApplyV119VO.setCityId(cityId);
        groupApplyV119VO.setPcaAdr(provinceName+cityName+districtName);
        groupApplyV119VO.setProvinceId(by.getProvinceid());
        groupApplyV119VO.setStreetId(streets.getStreetid());
        groupApplyV119VO.setStreetName(streets.getStreet());
        groupApplyV119VO.setPickUpAddr(byWxuserId.getAddress());
        groupApplyV119VO.setLineId(lineId);
        groupApplyV119VO.setId(byWxuserId.getGroupLeaderId());
        groupLeaderService.groupApplyV119(groupApplyV119VO);

        GroupLeader byWxuserId1 = groupLeaderService.findByWxuserId(wxuserId);
        assert byWxuserId1.getStatus() == 1;
    }
}