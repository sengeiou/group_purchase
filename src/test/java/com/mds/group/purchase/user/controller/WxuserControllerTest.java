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
import com.mds.group.purchase.GroupPurchaseApplicationTests;
import com.mds.group.purchase.user.model.Consignee;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.ConsigneeService;
import com.mds.group.purchase.user.service.WxuserService;
import com.mds.group.purchase.user.vo.AddersVO;
import com.mds.group.purchase.user.vo.WxuserInfoUpdateVO;
import com.mds.group.purchase.utils.BeanMapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class WxuserControllerTest extends GroupPurchaseApplicationTests {

    @Autowired
    private WxuserService wxuserService;
    @Autowired
    private ConsigneeService consigneeService;

    private static String appmodelId = "S00050001wx4c0a3c2e450c2c7d";

    /**
     * 更新微信用户信息
     */
    @Test
    @Ignore
    public void update() {
        Long wxuserId = 1544168325763903L;
        WxuserInfoUpdateVO wxuserInfoUpdateVO = new WxuserInfoUpdateVO();
        wxuserInfoUpdateVO.setIcon("地址");
        wxuserInfoUpdateVO.setUserStatus(0);
        wxuserInfoUpdateVO.setWxuserId(wxuserId);
        wxuserInfoUpdateVO.setWxuserDesc("desc");
        wxuserInfoUpdateVO.setWxuserName("name");
        wxuserService.updateUserInfo(wxuserInfoUpdateVO);
        Wxuser wxuser = wxuserService.findById(wxuserId);
        WxuserInfoUpdateVO updateVO = BeanMapper.map(wxuser, WxuserInfoUpdateVO.class);
        Assert.assertEquals("修改失败", updateVO, wxuserInfoUpdateVO);
    }

    /**
     * 添加用户地址
     */
    @Test
    public void addersCraete() {
        Long wxuserId = 1544168325763903L;
        AddersVO addersVO = new AddersVO();
        addersVO.setAppmodelId(appmodelId);
        addersVO.setPhone("15888888");
        String area = "地区地区" + DateUtil.currentSeconds() + RandomUtil.randomInt(1, 20);
        addersVO.setArea(area);
        addersVO.setAddress("地址地址");
        addersVO.setWxuserId(wxuserId);
        consigneeService.addersCraete(addersVO);
        Consignee consignee = consigneeService.findBy("area", area);
        AddersVO update = BeanMapper.map(consignee, AddersVO.class);
        Assert.assertEquals("数据添加不一致", addersVO, update);
    }

    /**
     * 删除用户地址
     */
    @Test
    public void addersDelete() {
        addersCraete();
        addersCraete();
        List<Consignee> all = consigneeService.findAll();
        Consignee consignee = all.get(0);
        consigneeService.deleteById(consignee.getConsigneeId());
        Consignee byId = consigneeService.findById(consignee.getConsigneeId());
        Assert.assertNull("数据未删除成功", byId);
    }

    /**
     * 更新用户地址|设置为默认
     */
    @Test
    public void addersUpdate() {
        addersCraete();
        addersCraete();
        List<Consignee> all = consigneeService.findAll();
        Consignee consignee = all.get(0);
        consignee.setDefaultAdderss(true);
        Consignee byId = consigneeService.findById(consignee.getConsigneeId());
        Assert.assertEquals("数据未删除成功", byId.getDefaultAdderss(), consignee.getDefaultAdderss());
    }

}