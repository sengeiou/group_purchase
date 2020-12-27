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

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.core.ResultGenerator;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.user.model.Consignee;
import com.mds.group.purchase.user.service.ConsigneeService;
import com.mds.group.purchase.user.service.WxuserService;
import com.mds.group.purchase.user.vo.*;
import com.mds.group.purchase.utils.GeoCodeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The type Wxuser controller.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@RestController
@Validated
@RequestMapping("/wxuser")
@Api(tags = {"所有接口"})
public class WxuserController {

    @Resource
    private WxuserService wxuserService;
    @Resource
    private RedisTemplate<String, LoginResultVO> redisTemplate;
    @Resource
    private ConsigneeService consigneeService;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;

    /**
     * Wx login result.
     *
     * @param code       the code
     * @param appmodelId the appmodel id
     * @return the result
     */
    @GetMapping("/v1/login")
    @ApiOperation(value = "用户登录", notes = "返回用户id", tags = "查询接口")
    public Result<LoginResultVO> wxLogin(@ApiParam(value = "微信code") @RequestParam @NotBlank String code,
                                         @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min =
                                                 27, message = "商品标示错误") String appmodelId) {
        LoginResultVO wxuser = wxuserService.wxLogin(code, appmodelId);
        return Result.success(wxuser);
    }


    /**
     * Wx h 5 login result.
     *
     * @param code       the code
     * @param appmodelId the appmodel id
     * @return the result
     */
    @GetMapping("/h5/login")
    @ApiOperation(value = "用户登录", notes = "返回用户id", tags = "查询接口")
    public Result wxH5Login(@ApiParam(value = "微信code") @RequestParam @NotBlank String code,
                            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message =
                                    "商品标示错误") String appmodelId) {
        //每次登陆记录一次浏览量,异步操作
        activeDelaySendJobHandler.savaTask(appmodelId, ActiviMqQueueName.H5_PAGE_VIEW, 0L, appmodelId, false);
        return wxuserService.wxH5LoginV13(code, appmodelId);
    }

    /**
     * Bind h 5 mini result.
     *
     * @param mdsUnionId the mds union id
     * @param wxuserId   the wxuser id
     * @param mpOpenid   the mp openid
     * @param appmodelId the appmodel id
     * @return the result
     */
    @GetMapping("/bind")
    @ApiOperation(value = "绑定h5用户到小程序", notes = "", tags = "绑定接口")
    public Result bindH5Mini(@ApiParam(value = "微信code") @RequestParam @NotBlank String mdsUnionId,
                             @RequestParam @NotNull Long wxuserId,
                             @ApiParam(value = "公总号openid") @RequestParam @NotBlank String mpOpenid,
                             @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message =
                                     "商品标示错误") String appmodelId) {
        return wxuserService.bindH5Mini(wxuserId, mdsUnionId, mpOpenid, appmodelId);
    }

    /**
     * Unbind h 5 mini result.
     *
     * @param wxuserId   the wxuser id
     * @param appmodelId the appmodel id
     * @return the result
     */
    @GetMapping("/unbind")
    @ApiOperation(value = "解绑h5用户到小程序", notes = "", tags = "解绑接口")
    public Result unbindH5Mini(@RequestParam @NotNull Long wxuserId,
                               @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27,
                                       message = "商品标示错误") String appmodelId) {
        return wxuserService.unBind(wxuserId, appmodelId);
    }

    /**
     * Decode user info result.
     *
     * @param decodeUserInfoVO the decode user info vo
     * @return the result
     */
    @PutMapping("/v1/decode")
    @ApiOperation(value = "用户加密数据解密", tags = "解密接口")
    public Result decodeUserInfo(@RequestBody @Valid DecodeUserInfoVO decodeUserInfoVO) {
        String phone = wxuserService.decodeUserInfo(decodeUserInfoVO);
        return Result.success(phone);
    }

    /**
     * Gets user info.
     *
     * @param wxuserId   the wxuser id
     * @param appmodelId the appmodel id
     * @return the user info
     */
    @GetMapping("/v1/user/info")
    @Deprecated
    @ApiOperation(value = "获取用户最新信息", tags = "查询接口")
    public Result<UserInfoVO> getUserInfo(@NotNull @RequestParam Long wxuserId,
                                          @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min =
                                                  27, message = "商品标示错误") String appmodelId) {
        UserInfoVO wxuser = wxuserService.getUserInfo(wxuserId, appmodelId);
        return Result.success(wxuser);
    }

    /**
     * Gets user info v 2.
     *
     * @param wxuserId   the wxuser id
     * @param appmodelId the appmodel id
     * @return the user info v 2
     */
    @GetMapping("/v2/user/info")
    @ApiOperation(value = "获取用户最新信息", tags = "查询接口")
    public Result<UserInfoVO> getUserInfoV2(@NotNull @RequestParam Long wxuserId,
                                            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min
                                                    = 27, message = "商品标示错误") String appmodelId) {
        UserInfoVO wxuser = wxuserService.getUserInfo2(wxuserId, appmodelId);
        return Result.success(wxuser);
    }

    /**
     * Update user info result.
     *
     * @param infoUpdateVO the info update vo
     * @return the result
     */
    @PutMapping("/v1/user/modify")
    @ApiOperation(value = "更新微信用户信息", notes = "更新用户头像|昵称|是否可用", tags = "更新接口")
    public Result updateUserInfo(@RequestBody @ApiParam(value = "需要更新的信息") @Valid WxuserInfoUpdateVO infoUpdateVO) {
        Integer num = wxuserService.updateUserInfo(infoUpdateVO);
        if (num > 0) {
            return Result.success();
        }
        return ResultGenerator.genFailResult("更新失败");
    }

    /**
     * Update remark result.
     *
     * @param userRemarkVO the user remark vo
     * @return the result
     */
    @PutMapping("/v1/user/remark")
    @ApiOperation(value = "普通用户批量备注", tags = "更新接口")
    public Result updateRemark(@RequestBody @Valid RemarkVO userRemarkVO) {
        int num = wxuserService.updateRemark(userRemarkVO);
        if (num > 0) {
            return Result.success();
        }
        return Result.success();
    }

    /**
     * Search wxuser manager result.
     *
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @param nickName   the nick name
     * @param community  the community
     * @param createTime the create time
     * @param appmodelId the appmodel id
     * @return the result
     */
    @GetMapping("/v1/user/manager")
    @ApiOperation(value = "会员管理", tags = "查询接口")
    public Result<PageInfo<WxuserManagerVO>> searchWxuserManager(
            @RequestParam @NotNull(message = "pageNum不能为空") Integer pageNum,
            @RequestParam @NotNull(message = "pageSize不能为空") Integer pageSize,
            @RequestParam(required = false) @ApiParam(value = "昵称") String nickName,
            @RequestParam(required = false) @ApiParam(value = "小区名") String community,
            @RequestParam(required = false) @ApiParam(value = "注册时间 开始时间和结束时间逗号分隔") String createTime,
            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId) {
        List<WxuserManagerVO> list = wxuserService
                .searchWxuserManager(pageNum, pageSize, nickName, community, createTime, appmodelId);
        return Result.success(new PageInfo<>(list));
    }

    /**
     * Qrcode result.
     *
     * @param code       the code
     * @param state      the state
     * @param appmodelId the appmodel id
     * @return the result
     */
//@GetMapping("/v1/qrcode") 无法联通过时
    @Deprecated
    @ApiOperation(value = "pc端微信扫码回调(网站应用)", tags = "回调接口")
    public Result<LoginResultVO> qrcode(@RequestParam String code, @RequestParam String state,
                                        @RequestHeader String appmodelId) {
        String key = GroupMallProperties.getRedisPrefix().concat(appmodelId)
                .concat(RedisPrefix.WXCHAT_SCAN_QRCODE_NOTIFY).concat(code);
        LoginResultVO loginResultVO = redisTemplate.opsForValue().get(key);
        if (loginResultVO != null) {
            return Result.success(loginResultVO);
        }
        Long wxuserId = wxuserService.qrcode(code, state, appmodelId);
        loginResultVO = new LoginResultVO();
        loginResultVO.setUserStatus(1);
        loginResultVO.setWxuserId(wxuserId);
        redisTemplate.opsForValue().set(key, loginResultVO, 2, TimeUnit.SECONDS);
        return Result.success(loginResultVO);
    }

    /**
     * Area result.
     *
     * @param longitude the longitude
     * @param latitude  the latitude
     * @return the result
     */
    @GetMapping("/v1/area")
    @ApiOperation(value = "获取经纬度位置", tags = "查询接口")
    public Result area(@RequestParam @ApiParam(value = "经度", required = true) String longitude,
                       @RequestParam @ApiParam(value = "纬度", required = true) String latitude) {
        String list = GeoCodeUtil.getArea(longitude, latitude, false);
        JSONObject jsonObject = JSONObject.parseObject(list);
        return Result.success(jsonObject);
    }

    /**
     * Adders craete result.
     *
     * @param addersVO   the adders vo
     * @param appmodelId the appmodel id
     * @return the result
     */
    @PostMapping("/v1/adders")
    @ApiOperation(value = "添加用户地址", tags = "添加接口")
    public Result addersCraete(@RequestBody AddersVO addersVO,
                               @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27,
                                       message = "商品标示错误") String appmodelId) {
        addersVO.setAppmodelId(appmodelId);
        int num = consigneeService.addersCraete(addersVO);
        if (num > 0) {
            return Result.success();
        }
        return Result.error(new CodeMsg("添加失败"));
    }

    /**
     * Adders get result.
     *
     * @param wxuserId the wxuser id
     * @return the result
     */
    @GetMapping("/v1/adders")
    @ApiOperation(value = "获取用户地址", tags = "查询接口")
    public Result<List<Consignee>> addersGet(@NotNull @RequestParam Long wxuserId) {
        List<Consignee> list = consigneeService.addersGet(wxuserId);
        return Result.success(list);
    }

    /**
     * Adders delete result.
     *
     * @param consigneeId the consignee id
     * @param wxuserId    the wxuser id
     * @return the result
     */
    @DeleteMapping("/v1/adders")
    @ApiOperation(value = "删除用户地址", tags = "删除接口")
    public Result addersDelete(@RequestParam @ApiParam(value = "id") @NotNull Long consigneeId,
                               @ApiParam(value = "用户id") @RequestParam(required = false) Long wxuserId) {
        int num = consigneeService.addersDelete(consigneeId, wxuserId);

        if (num > 0) {
            return Result.success();
        }
        return Result.error(new CodeMsg("删除失败"));
    }

    /**
     * Adders update result.
     *
     * @param consignee the consignee
     * @return the result
     */
    @PutMapping("/v1/adders")
    @ApiOperation(value = "更新用户地址|设置为默认", tags = "更新接口")
    public Result addersUpdate(@RequestBody @Valid Consignee consignee) {
        int num = consigneeService.addersUpdate(consignee);
        if (num > 0) {
            return Result.success();
        }
        return Result.error(new CodeMsg("更新失败"));
    }

    /**
     * Gets user group.
     *
     * @param wxuserId   the wxuser id
     * @param appmodelId the appmodel id
     * @return the user group
     */
    @GetMapping("/v1.2.2/group")
    @ApiOperation(value = "用户获取自己的团长", tags = "v1.2.2接口")
    public Result getUserGroup(@RequestParam @NotNull Long wxuserId, @RequestHeader @NotBlank String appmodelId) {
        String groupInfo = wxuserService.finUserGroupById(wxuserId, appmodelId);
        return Result.success(groupInfo);
    }
}
