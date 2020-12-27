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

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.result.Community4GroupApply;
import com.mds.group.purchase.logistics.result.CommunityCanPick;
import com.mds.group.purchase.logistics.result.CommunityHaveGroupLeaderInfo;
import com.mds.group.purchase.logistics.service.CommunityService;
import com.mds.group.purchase.logistics.vo.CommunityGetVo;
import com.mds.group.purchase.logistics.vo.CommunityVo;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.GroupLeaderService;
import com.mds.group.purchase.user.service.WxuserService;
import com.mds.group.purchase.utils.GeoCodeUtil;
import com.mds.group.purchase.utils.PageUtil;
import com.mds.group.purchase.utils.PinYinUtil;
import io.swagger.annotations.*;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.*;


/**
 * 小区管理相关接口
 *
 * @author shuke
 */
@RestController
@RequestMapping("/community")
@Api(tags = "所有接口")
@Validated
public class CommunityController {

    @Resource
    private WxuserService wxuserService;
    @Resource
    private CommunityService communityService;
    @Resource
    private GroupLeaderService groupLeaderService;

    /**
     * 获取未分组小区
     *
     * @param appmodelId the appmodel id
     * @return the communities by street id
     */
    @ApiOperation(value = "获取未分组小区", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = Community.class),})
    @GetMapping("/v1/no/group")
    public Result getCommunitiesByStreetId(@RequestHeader @NotBlank String appmodelId) {
        List<Community> communities = communityService.findNotGroupCommunity(appmodelId);
        return Result.success(communities);
    }

    /**
     * 获取对应街道的所有小区信息
     *
     * @param streetId   the street id
     * @param page       the page
     * @param size       the size
     * @param appmodelId the appmodel id
     * @return the communities by street id
     */
    @ApiOperation(value = "获取对应街道的所有小区信息", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = Community.class),})
    @GetMapping("/v1/streetId")
    public Result getCommunitiesByStreetId(
            @ApiParam(value = "街道id", required = true) @NotNull @RequestParam String streetId,
            @ApiParam(value = "当前页码，默认为0") @RequestParam(defaultValue = "0") Integer page,
            @ApiParam(value = "页面数据数量，默认为0查询所有") @RequestParam(defaultValue = "0") Integer size,
            @RequestHeader @NotBlank String appmodelId) {
        PageHelper.startPage(page, size);
        List<Community> communities = communityService.getCommunitysByStreetId(Long.parseLong(streetId), appmodelId);
        PageInfo<Community> pageInfo = new PageInfo<>(communities);
        return Result.success(pageInfo);
    }

    /**
     * 获取对应县区的所有小区信息
     *
     * @param areaId     the area id
     * @param page       the page
     * @param size       the size
     * @param appmodelId the appmodel id
     * @return the communities by area id
     */
    @ApiOperation(value = "获取对应县区的所有小区信息", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = Community.class),})
    @GetMapping("/v1/areaId")
    public Result getCommunitiesByAreaId(
            @ApiParam(value = "县区id", required = true) @NotNull @RequestParam String areaId,
            @ApiParam(value = "当前页码，默认为0") @RequestParam(defaultValue = "0") Integer page,
            @ApiParam(value = "页面数据数量，默认为0查询所有") @RequestParam(defaultValue = "0") Integer size,
            @RequestHeader @NotBlank String appmodelId) {
        PageHelper.startPage(page, size);
        List<Community> communities = communityService.getCommunitysByAreaId(areaId, appmodelId);
        PageInfo<Community> pageInfo = new PageInfo<>(communities);
        return Result.success(pageInfo);
    }

    /**
     * 获取对应城市的所有含有团长的小区信息
     *
     * @param cityId     the city id
     * @param appmodelId the appmodel id
     * @return the communities by city id
     */
    @ApiOperation(value = "获取对应城市的所有含有团长的小区信息", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = Community.class),})
    @GetMapping("/wx/v1/city")
    public Result getCommunitiesByCityId(
            @ApiParam(value = "城市id", required = true) @NotNull @RequestParam String cityId,
            @RequestHeader @NotBlank String appmodelId) {
        List<Community> communities = communityService.getCommunitysHaveGroupByCityId(cityId, appmodelId);
        return Result.success(communities);
    }

    /**
     * 获取对应城市的所有含有团长的小区信息,小区按照字母排序
     *
     * @param cityId     the city id
     * @param appmodelId the appmodel id
     * @return the communities by city id v 2
     * @throws BadHanyuPinyinOutputFormatCombination the bad hanyu pinyin output format combination
     */
    @ApiOperation(value = "获取对应城市的所有含有团长的小区信息,小区按照字母排序", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = Community.class),})
    @GetMapping("/wx/v2/byCity")
    public Result getCommunitiesByCityIdV2(
            @ApiParam(value = "城市id", required = true) @NotNull @RequestParam String cityId,
            @RequestHeader @NotBlank String appmodelId) throws BadHanyuPinyinOutputFormatCombination {
        List<Community> communities = communityService.getCommunitysHaveGroupByCityId(cityId, appmodelId);
        if (communities == null) {
            return Result.success();
        }
        // 降序排序
        Map<String, List<Community>> cMap = new TreeMap<>(String::compareTo);
        for (Community community : communities) {
            String pingYin = PinYinUtil.getPingYinFirst(community.getCommunityName());
            List<Community> communities1 = cMap.get(pingYin);
            if (communities1 == null) {
                communities1 = new ArrayList<>();
            }
            communities1.add(community);
            cMap.put(pingYin, communities1);
        }
        return Result.success(cMap);
    }

    /**
     * 根据小区名称搜索小区信息
     *
     * @param communityName the community name
     * @param appmodelId    the appmodel id
     * @param userLocation  the user location
     * @return the result
     */
    @ApiOperation(value = "根据小区名称搜索小区信息", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = Community.class),})
    @GetMapping("/wx/v1/search/name")
    @Deprecated
    public Result searchCommunitiesByName(@ApiParam(value = "小区名称") @NotBlank @RequestParam String communityName,
                                          @RequestHeader @NotBlank String appmodelId,
                                          @ApiParam("用户坐标") @RequestParam String userLocation) {
        List<Community> communities = communityService.searchCommunitysByName(communityName, appmodelId, null);
        List<CommunityCanPick> canPick = getDistance(userLocation, communities);
        Collections.sort(canPick);
        return Result.success(canPick);
    }

    /**
     * 没有位置信息时根据小区名称搜索小区信息
     *
     * @param communityName the community name
     * @param appmodelId    the appmodel id
     * @return the result
     */
    @ApiOperation(value = "没有位置信息时根据小区名称搜索小区信息", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = Community.class),})
    @Deprecated
    @GetMapping("/wx/v2/search/name")
    public Result searchCommunitiesByName(@ApiParam(value = "小区名称") @NotBlank @RequestParam String communityName,
                                          @RequestHeader @NotBlank String appmodelId) {
        List<Community> communities = communityService.searchCommunitysByName(communityName, appmodelId, null);
        return Result.success(communities);
    }

    /**
     * 根据小区名称搜索小区信息
     *
     * @param communityName the community name
     * @param appmodelId    the appmodel id
     * @param userLocation  the user location
     * @param cityName      the city name
     * @return the result
     */
    @ApiOperation(value = "根据小区名称搜索小区信息", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = Community.class),})
    @GetMapping("/wx/v3/search/name")
    @Deprecated
    public Result searchCommunitiesByName(@ApiParam(value = "小区名称") @NotBlank @RequestParam String communityName,
                                          @RequestHeader @NotBlank String appmodelId,
                                          @ApiParam("用户坐标") @RequestParam(required = false) String userLocation,
                                          @ApiParam("指定城市") @RequestParam(required = false) String cityName) {
        List<Community> communities = communityService.searchCommunitysByName(communityName, appmodelId, cityName);
        if (StringUtils.isNotBlank(userLocation)) {
            List<CommunityCanPick> canPick = getDistance(userLocation, communities);
            Collections.sort(canPick);
            return Result.success(canPick);
        } else {
            return Result.success(communities);
        }
    }

    private List<CommunityCanPick> getDistance(String userLocation, List<Community> communities) {
        List<CommunityCanPick> canPick = new ArrayList<>();
        communities.forEach(obj -> {
            double distance = GeoCodeUtil.distance(userLocation, obj.getLocation());
            BigDecimal bd = new BigDecimal(distance);
            distance = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            CommunityCanPick communityCanPick = new CommunityCanPick(obj);
            communityCanPick.setDistance(distance);
            canPick.add(communityCanPick);
        });
        return canPick;
    }

    /**
     * 获取小区信息
     *
     * @param communityVo the community vo
     * @param appmodelId  the appmodel id
     * @return the communities by street id
     */
    @ApiOperation(value = "获取小区信息", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = Community.class),})
    @PostMapping("/v1/community")
    public Result<PageInfo<CommunityHaveGroupLeaderInfo>> getCommunitiesByStreetId(@ApiParam(value = "请求的参数") @RequestBody @Valid CommunityGetVo communityVo,
                                                                                   @RequestHeader @NotBlank String appmodelId) {
        if (communityVo.getPage() == null) {
            communityVo.setPage(0);
        }
        if (communityVo.getSize() == null) {
            communityVo.setSize(0);
        }
        List<CommunityHaveGroupLeaderInfo> infos = communityService.getCommunitiesByStreetId(communityVo, appmodelId);
        PageInfo<CommunityHaveGroupLeaderInfo> pageInfo;
        if (infos.size() == 0) {
            pageInfo = new PageInfo<>();
            return Result.success(pageInfo);
        }
        if (communityVo.getPage() == 0 || communityVo.getSize() == 0) {
            pageInfo = new PageInfo<>(infos);
            pageInfo.setTotal(infos.size());
            return Result.success(pageInfo);
        } else {
            pageInfo = PageUtil.pageUtil(communityVo.getPage(), communityVo.getSize(), infos);
            return Result.success(pageInfo);
        }
    }

    /**
     * 新建一个小区信息
     *
     * @param communityVo the community vo
     * @param appmodelId  the appmodel id
     * @return the result
     */
    @ApiOperation(value = "新建一个小区信息", tags = "新增接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @Deprecated
    @PostMapping(value = "/v1/save")
    public Result saveCommunity(@Valid @RequestBody CommunityVo communityVo,
                                @RequestHeader @NotBlank String appmodelId) {
        communityService.saveCommunityV2(communityVo, appmodelId);
        return Result.success(true);
    }

    /**
     * 新建一个小区信息
     *
     * @param communityVo 新建小区参数类
     * @param appmodelId  模板id
     * @return 创建是否成功 result
     * @since v1.2
     */
    @ApiOperation(value = "新建一个小区信息,v1.2版本接口", tags = "v1.2版本接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @PostMapping(value = "/v2/save")
    public Result saveCommunityV2(@ApiParam("新建小区所需的参数对象") @Valid @RequestBody CommunityVo communityVo,
                                  @ApiParam("小程序模板id") @RequestHeader @NotBlank String appmodelId) {
        communityService.saveCommunityV2(communityVo, appmodelId);
        return Result.success(true);
    }

    /**
     * 更新一个小区信息
     *
     * @param communityVo the community vo
     * @return the result
     */
    @ApiOperation(value = "更新一个小区信息", tags = "更新接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @PutMapping(value = "/v1/update")
    public Result modifyCommunity(@Valid @RequestBody CommunityVo communityVo) {
        communityService.updateCommunity(communityVo);
        return Result.success(true);
    }

    /**
     * 删除一个小区
     *
     * @param appmodelId   the appmodel id
     * @param communityIds the community ids
     * @return the result
     */
    @ApiOperation(value = "删除一个小区", tags = "删除接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @DeleteMapping(value = "/v1/delete")
    public Result delCommunityById(@RequestHeader @NotBlank String appmodelId,
                                   @RequestParam(name = "communityIds") @ApiParam(value = "需要删除的小区id", required =
                                           true) @NotBlank String communityIds) {
        communityService.deleteCommunityByIds(communityIds, appmodelId);
        return Result.success(true);
    }

    /**
     * 获取团长可以选择申请的小区
     *
     * @param appmodelId the appmodel id
     * @param streetId   the street id
     * @return the group apply communities
     */
    @ApiResponse(code = 200, message = "success", response = Community4GroupApply.class)
    @ApiOperation(value = "获取团长可以选择申请的小区", tags = "查询接口")
    @GetMapping("/wx/v1/group/apply/community")
    public Result<List<Community4GroupApply>> getGroupApplyCommunities(@RequestHeader @NotBlank String appmodelId,
                                                                       @RequestParam String streetId) {
        List<Community4GroupApply> community4GroupApplyList = communityService
                .getGroupApplyCommunities(Long.parseLong(streetId), appmodelId);
        return Result.success(community4GroupApplyList);
    }

    /**
     * 用户可供选择的小区列表，按距离远近排序,
     *
     * @param appmodelId   小程序模板id
     * @param userLocation 用户坐标
     * @param cityName     城市名
     * @return 用户可供选择小区列表 result
     */
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = CommunityCanPick.class)})
    @ApiOperation(value = "用户可供选择小区列表，按距离远近排序", tags = "查询接口")
    @GetMapping("/wx/v1/userCanPickCommunities")
    public Result userCanPickCommunities(@RequestHeader @NotBlank String appmodelId,
                                         @ApiParam("用户坐标") @RequestParam String userLocation,
                                         @ApiParam("城市名") @RequestParam(required = false) String cityName) {
        List<Community> communityList = communityService.userCanPickCommunities(appmodelId, cityName);
        if (communityList == null || communityList.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        List<CommunityCanPick> canPick = this.getDistance(userLocation, communityList);
        if (CollectionUtil.isNotEmpty(canPick)) {
            Collections.sort(canPick);
        }
        return Result.success(canPick);
    }

    /**
     * 用户选择小区
     *
     * @param wxuserId    用户id
     * @param communityId 小区id
     * @return null result
     */
    @ApiResponse(code = 200, message = "success")
    @ApiOperation(value = "用户选择小区", tags = "更新接口")
    @GetMapping("/wx/v1/userPickCommunity")
    public Result userPickCommunity(@RequestParam Long wxuserId, @RequestParam Long communityId) {
        Wxuser wxuser = wxuserService.findById(wxuserId);
        if (wxuser == null) {
            return Result.error(CodeMsg.SERVER_ERROR.fillArgs("用户不存在"));
        }
        wxuser.setCommunityId(communityId);
        wxuserService.update(wxuser);
        return Result.success();
    }

    /**
     * 获取用户的默认小区
     *
     * @param wxuserId 用户id
     * @return 用户的默认小区 result
     */
    @ApiResponse(code = 200, message = "success")
    @ApiOperation(value = "获取用户的默认小区", tags = "查询接口")
    @GetMapping("/wx/v1/default")
    public Result defaultCommunity(@RequestParam @NotNull Long wxuserId) {
        Wxuser wxuser = wxuserService.findById(wxuserId);
        if (wxuser == null) {
            return Result.error(CodeMsg.SERVER_ERROR.fillArgs("用户不存在"));
        }
        Community community = communityService.getCommunityById(wxuser.getCommunityId());
        return Result.success(community);
    }

    /**
     * 确认订单时，获取取货地址
     *
     * @param wxuserId 用户id
     * @return 团长对象 pickup location
     */
    @ApiResponse(code = 200, message = "success")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = GroupLeader.class),})
    @ApiOperation(value = "确认订单时，获取取货地址", tags = "查询接口")
    @GetMapping("/wx/v1/pickupLocation")
    public Result getPickupLocation(@RequestParam Long wxuserId) {
        Wxuser wxuser = wxuserService.findById(wxuserId);
        if (wxuser == null) {
            return Result.error(CodeMsg.SERVER_ERROR.fillArgs("用户不存在"));
        }
        GroupLeader groupLeader = groupLeaderService.findBySoleGroupLeader(wxuser.getCommunityId());
        return Result.success(groupLeader);
    }
}
