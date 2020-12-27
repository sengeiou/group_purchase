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

package com.mds.group.purchase.logistics.controller;

import cn.hutool.core.util.RandomUtil;
import com.mds.group.purchase.GroupPurchaseApplicationTests;
import com.mds.group.purchase.common.CommunityUtil;
import com.mds.group.purchase.logistics.controller.VO.ProvinceCityAreaStreetsVO;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.model.LineDetail;
import com.mds.group.purchase.logistics.result.LineDetailResult;
import com.mds.group.purchase.logistics.result.LineResult;
import com.mds.group.purchase.logistics.service.CommunityService;
import com.mds.group.purchase.logistics.service.LineService;
import com.mds.group.purchase.logistics.vo.CommunityVo;
import com.mds.group.purchase.logistics.vo.LineGetVo;
import com.mds.group.purchase.logistics.vo.LineVo;
import com.mds.group.purchase.utils.BeanMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Rollback
@SpringBootTest
@Transactional
@RunWith(SpringRunner.class)
public class CommunityControllerTest extends GroupPurchaseApplicationTests {


	@Autowired
	private CommunityService communityService;

	@Autowired
	private LineService lineService;

	@Autowired
	private CommunityUtil communityUtil;

	private static final String appmodelId = "S00050001wx17c66eb4da0ef6ab";

	/**
	 * 获取对应街道的所有小区信息
	 * 获取对应县区的所有小区信息
	 */
	@Test
	public void getCommunityIntoTest() {
		ProvinceCityAreaStreetsVO pcasV = communityUtil.createComminuty();
		//获取对应街道的所有小区信息
		List<Community> communitiesS = communityService
				.getCommunitysByStreetId(pcasV.getStreetsResults().get(0).getValue(), CommunityUtil.appmodelId);
		Assert.assertNotEquals("数据为空", 0, communitiesS.size());
		//获取对应县区的所有小区信息
		List<Community> communitiesA = communityService
				.getCommunitysByAreaId(pcasV.getAreaResult().getValue(), CommunityUtil.appmodelId);
		Assert.assertNotEquals("数据为空", 0, communitiesA.size());
	}


	/**
	 *更新一个小区信息
	 */
	@Test
	public void modifyCommunityTest() {
		ProvinceCityAreaStreetsVO pcasV = communityUtil.createComminuty();
		Community community = pcasV.getCommunity();
		CommunityVo communityVo = BeanMapper.map(community, CommunityVo.class);
		communityVo.setCommunityName(RandomUtil.randomString(10));
		communityService.updateCommunity(communityVo);
		community.setCommunityName(communityVo.getCommunityName());
		Community communityNew = communityService.findById(community.getCommunityId());
		Assert.assertEquals("更新失败", community, communityNew);
	}

	/**
	 *删除一个小区
	 *
	 */
	@Test
	public void delCommunityByIdTest() {
		ProvinceCityAreaStreetsVO pcasV = communityUtil.createComminuty();
		ProvinceCityAreaStreetsVO pcasV3 = communityUtil.createComminuty();
		StringBuilder sb = new StringBuilder();
		sb.append(pcasV.getCommunity().getCommunityId()).append(",");
		sb.append(pcasV3.getCommunity().getCommunityId());
		communityService.deleteCommunityByIds(sb.toString(),"1323");
		List<Community> communityList = communityService.findByIds(sb.toString());
		Assert.assertEquals("小区未删除成功", 0, communityList.size());
	}

	/**
	 *获取团长不可以选择申请的小区id
	 */
	@Test
	public void getGroupApplyCommunitiesTest() {
	}


	/**
	 * 新建线路
	 */
	@Test
	public void savaLineTest() {
		communityUtil.createLine();
	}

	/**
	 * 修改线路
	 */
	@Test
	public void modifyLineTest() {
		//创建一个线路信息并删除
		ProvinceCityAreaStreetsVO pcas = communityUtil.createLine();
		LineResult line = pcas.getLineResult();
		//把删除的线路的信息添加至其他地方
		lineService.deleteLineByIds(line.getLineId().toString(),appmodelId);

		ProvinceCityAreaStreetsVO line1 = communityUtil.createLine();
		//创建要修改的线路
		LineResult updateData = line1.getLineResult();
		LineVo lineVo = new LineVo();
		lineVo.setDriverPhone(updateData.getDriverPhone());
		lineVo.setDriverName(updateData.getDriverName());
		lineVo.setAppmodelId(updateData.getAppmodelId());
		lineVo.setLineId(updateData.getLineId());
		lineVo.setLineName(updateData.getLineName());
		lineVo.setProvinceId(line.getProvinceId());
		lineVo.setCityId(line.getCityId());
		lineVo.setAreaId(line.getAreaId());
		List<Long> communityIds = line.getLineDetailResults().get(0).getLineDetails().stream()
				.map(LineDetail::getCommunityId).collect(Collectors.toList());
		lineVo.setCommunities(communityIds);
		lineService.updateLine(lineVo);
		LineGetVo lineGetVo = new LineGetVo();
		lineGetVo.setPage(1);
		lineGetVo.setSize(1);
		lineGetVo.setLineId(lineVo.getLineId());
		Map<String, Object> line2 = lineService.getLine(lineGetVo, appmodelId);
		List<LineResult> lineResults = (List<LineResult>) line2.get("list");
		Assert.assertEquals("数据修改失败", 1, 1);
		LineResult lineResult = lineResults.get(0);
		List<LineDetailResult> lineDetailResults = lineResult.getLineDetailResults();
		List<LineDetail> lineDetails = lineDetailResults.get(0).getLineDetails();
		Map<Long, LineDetail> lineDetailMap = lineDetails.stream()
				.collect(Collectors.toMap(LineDetail::getCommunityId, v -> v));
		for (Long communityId : communityIds) {
			Assert.assertNotNull("数据未修改", lineDetailMap.get(communityId));
		}

	}


}