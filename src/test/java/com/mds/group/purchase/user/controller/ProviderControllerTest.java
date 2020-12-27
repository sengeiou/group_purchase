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
import com.mds.group.purchase.GroupPurchaseApplicationTests;
import com.mds.group.purchase.user.model.Provider;
import com.mds.group.purchase.user.service.ProviderService;
import com.mds.group.purchase.user.vo.*;
import com.mds.group.purchase.utils.BeanMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
public class ProviderControllerTest extends GroupPurchaseApplicationTests {

    @Autowired
    private ProviderService providerService;

    private static String appmodelId = "S00050001wx4c0a3c2e450c2c7d";

    @Test
    public void test() {
        providerApplyRegister();
        providerApplyRegister();
        providerApplyRegister();
    }

    /**
     * 资源入驻申请
     */
    @Test
    public void providerApplyRegister() {
        ProviderApplyRegisterVO providerApplyRegisterVO = new ProviderApplyRegisterVO();
        providerApplyRegisterVO.setAppmodelId(appmodelId);
        providerApplyRegisterVO.setGoodsClass("产品分类");
        String name = "供应商姓名" + DateUtil.currentSeconds();
        String phone = DateUtil.currentSeconds() + "";
        providerApplyRegisterVO.setProviderName(name);
        providerApplyRegisterVO.setProviderPhone(phone);
        try {
            //添加供应商
            providerService.providerApplyRegister(providerApplyRegisterVO);
        } catch (Exception e) {
            Assert.assertEquals("非指定异常", e.getMessage(), "该手机号已在申请中");
            throw e;
        }
    }

    /**
     * 同意
     */
    @Test
    public void providerApply1() {
        providerApplyRegister();
        providerApplyRegister();
        providerApplyRegister();
        ProviderManagerVO providerManagerVO = providerService.providerApplySearch(3, appmodelId, "", "", null, 1, 1)
                .get(0);
        Assert.assertNotNull("查询出错", providerManagerVO);
        ProviderApplyVO providerApplyVO = BeanMapper.map(providerManagerVO, ProviderApplyVO.class);
        providerApplyVO.setOptionType(1);
        providerApplyVO.setId(providerManagerVO.getProviderId());
        providerApplyVO.setProviderAddress("地址");
        providerApplyVO.setProviderArea("地区");
        try {
            providerService.providerApply(providerApplyVO);
        }catch (Exception e){
            Assert.assertEquals("未知异常","该手机号已注册为团长",e.getMessage());
            return;
        }
        ProviderManagerVO provider = providerService
                .providerApplySearch(1, appmodelId, null, null, providerManagerVO.getProviderId(), 1, 1).get(0);
        Assert.assertNotNull("对象为空", provider);
        Assert.assertEquals("非指定对象", provider.getProviderId(), providerApplyVO.getId());
        Assert.assertEquals("注册状态未修改成功", Integer.valueOf(1), provider.getApplyState());
    }

    /**
     * 拒绝入驻申请
     */
    @Test
    public void providerApply2() {
        providerApplyRegister();
        providerApplyRegister();
        providerApplyRegister();
        ProviderManagerVO providerManagerVO = providerService.providerApplySearch(3, appmodelId, "", "", null, 1, 1)
                .get(0);
        Assert.assertNotNull("查询出错", providerManagerVO);
        ProviderApplyVO providerApplyVO = BeanMapper.map(providerManagerVO, ProviderApplyVO.class);
        providerApplyVO.setOptionType(0);
        providerApplyVO.setId(providerManagerVO.getProviderId());
        providerApplyVO.setProviderAddress("地址");
        providerApplyVO.setProviderArea("地区");
        providerService.providerApply(providerApplyVO);
        ProviderManagerVO provider = providerService
                .providerApplySearch(4, appmodelId, null, null, providerManagerVO.getProviderId(), 1, 1).get(0);
        Assert.assertNotNull("对象为空", provider);
        Assert.assertEquals("非指定对象", provider.getProviderId(), providerApplyVO.getId());
        Assert.assertEquals("注册状态未修改成功", Integer.valueOf(2), provider.getApplyState());
    }

    /**
     * 新增供应商
     */
    @Test
    public void providerApply3() {
        ProviderApplyVO providerApplyVO = new ProviderApplyVO();
        providerApplyVO.setOptionType(2);
        providerApplyVO.setId(null);
        providerApplyVO.setProviderAddress("地址");
        providerApplyVO.setProviderArea("地区");
        providerApplyVO.setAppmodelId(appmodelId);
        providerApplyVO.setGoodsClass("类目");
        String name = "供应商姓名" + DateUtil.currentSeconds();
        String phone = DateUtil.currentSeconds() + "";
        providerApplyVO.setProviderPhone(phone);
        providerApplyVO.setProviderName(name);
        providerService.providerApply(providerApplyVO);
        ProviderManagerVO provider = providerService.providerApplySearch(1, appmodelId, name, phone, null, 1, 1).get(0);
        Assert.assertNotNull("对象为空", provider);
        Assert.assertEquals("非指定对象", provider.getProviderName(), providerApplyVO.getProviderName());

    }

    /**
     * 批量删除单个/多个供应商|
     */
    @Test
    public void providerApplyDelete1() {
        providerApply3();
        //1-供应商管理 2-禁用中  3-待审核  4-已关闭
        //新增供应商删除
        ProviderManagerVO providerManagerVO = providerService.providerApplySearch(1, appmodelId, "", "", null, 1, 1)
                .get(0);
        DeleteVO deleteVO = new DeleteVO();
        deleteVO.setIds(providerManagerVO.getProviderId());
        providerService.providerDelete(deleteVO);
        Provider provider = providerService.findById(providerManagerVO.getProviderId());
        Assert.assertNotNull("数据错误", provider);
        Assert.assertEquals("状态未正确修改", true, provider.getDeleteState());
    }

    /**
     * 批量删除单个/多个申请记录
     */
    @Test
    public void providerApplyDelete2() {
        providerApply1();
        int size = 2;
        List<ProviderManagerVO> providerManagerVOS = providerService
                .providerApplySearch(3, appmodelId, "", "", null, 1, size);
        String ids = providerManagerVOS.stream().map(obj -> obj.getProviderId()).collect(Collectors.joining(","));
        DeleteVO deleteVO = new DeleteVO();
        deleteVO.setIds(ids);
        providerService.providerDelete(deleteVO);
        Condition condition = new Condition(Provider.class);
        condition.createCriteria().andIn("providerId", Arrays.asList(ids.split(",")));
        List<Provider> providers = providerService.findByCondition(condition);
        Assert.assertNotNull("数据错误", providers);
        Assert.assertEquals("数据不一致", size, providers.size());
        for (Provider provider : providers) {
            Assert.assertEquals("状态未正确修改", true, provider.getDeleteState());
        }
    }

    /**
     * 禁用/开启供应商
     */
    @Test
    public void providerStatu() {
        providerApply1();
        ProviderManagerVO providerManagerVO = providerService.providerApplySearch(1, appmodelId, "", "", null, 1, 1)
                .get(0);
        ProviderStatuVO providerStatuVO = new ProviderStatuVO();
        providerStatuVO.setOptionType(0);
        providerStatuVO.setId(providerManagerVO.getProviderId());
        providerService.providerStatu(providerStatuVO);
        Provider provider = providerService.findById(providerManagerVO.getProviderId());
        Assert.assertEquals("未禁用成功", Integer.valueOf(0), provider.getProviderStatus());

        providerStatuVO.setOptionType(1);
        providerService.providerStatu(providerStatuVO);
        provider = providerService.findById(providerManagerVO.getProviderId());
        Assert.assertEquals("未禁用成功", Integer.valueOf(1), provider.getProviderStatus());
    }
}