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

package com.mds.group.purchase.user.service.impl;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.util.crypt.WxMaCryptUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.configurer.WXPMServiceFactory;
import com.mds.group.purchase.constant.GroupLeaderStatus;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.logistics.dto.CommunityMoreDTO;
import com.mds.group.purchase.logistics.model.Cities;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.model.Provinces;
import com.mds.group.purchase.logistics.service.CitiesService;
import com.mds.group.purchase.logistics.service.CommunityService;
import com.mds.group.purchase.logistics.service.ProvincesService;
import com.mds.group.purchase.order.model.Comment;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.service.CommentService;
import com.mds.group.purchase.order.service.OrderService;
import com.mds.group.purchase.solitaire.service.SolitaireSettingService;
import com.mds.group.purchase.solitaire.vo.SolitaireSettingVo;
import com.mds.group.purchase.user.dao.WxuserMapper;
import com.mds.group.purchase.user.model.Consignee;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.ConsigneeService;
import com.mds.group.purchase.user.service.GroupLeaderService;
import com.mds.group.purchase.user.service.WxuserService;
import com.mds.group.purchase.user.vo.*;
import com.mds.group.purchase.utils.BeanMapper;
import com.mds.group.purchase.utils.IdGenerateUtils;
import com.mds.group.purchase.utils.UserUtil;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.open.api.WxOpenComponentService;
import me.chanjar.weixin.open.api.WxOpenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * The type Wxuser service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@Service
@Log4j2
public class WxuserServiceImpl extends AbstractService<Wxuser> implements WxuserService {

    @Resource
    private UserUtil userUtil;
    @Resource
    private OrderService orderService;
    @Resource
    private WxuserMapper tWxuserMapper;
    @Resource
    private WxOpenService wxOpenService;
    @Resource
    private SolitaireSettingService solitaireSettingService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private CitiesService citiesService;
    @Resource
    private ConsigneeService consigneeService;
    @Resource
    private CommunityService communityService;
    @Resource
    private ProvincesService provincesService;
    @Resource
    private CommentService commentService;
    @Resource
    private GroupLeaderService groupLeaderService;
    @Resource
    private WXPMServiceFactory wxpmServiceFactory;

    @Override
    public LoginResultVO wxLogin(String code, String appmodelId) {
        WxOpenComponentService componentService = wxOpenService.getWxOpenComponentService();
        try {
            WxMaJscode2SessionResult wxMaJscode2SessionResult = componentService
                    .miniappJscode2Session(appmodelId.substring(9), code);
            log.info("小程序登录获取到的用户信息" + wxMaJscode2SessionResult.toString());
            Wxuser wxuser;
            //每次登陆都算一次浏览量
            redisTemplate.opsForValue().increment(GroupMallProperties.getRedisPrefix().concat(appmodelId)
                    .concat(RedisPrefix.MANAGER_STATISTICS_PAGEVIEW));
            //当天的缓存中是否存在当前用户
            wxuser = (Wxuser) redisTemplate.opsForValue()
                    .get(GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.USER)
                            .concat(wxMaJscode2SessionResult.getOpenid()));

            //缓存中为空从数据库中查询
            if (wxuser == null) {
                wxuser = tWxuserMapper.selectByOpenId(wxMaJscode2SessionResult.getOpenid());
                //缓存中不存在用户则表示今天用户没有被加入到访客数
                redisTemplate.opsForValue().increment(GroupMallProperties.getRedisPrefix().concat(appmodelId)
                        .concat(RedisPrefix.MANAGER_STATISTICS_VISITORSUM));
            }
            if (wxuser == null && wxMaJscode2SessionResult.getUnionid() != null) {
                //先根据unionid查询用户
                wxuser = tWxuserMapper.selectByUnionid(wxMaJscode2SessionResult.getUnionid());
                //根据miniopenid查不到用户，但是根据unionid查询到了则表示用户第一次是通过h5页面进入的
                //更新用户的miniopenid
                wxuser.setMiniOpenId(wxMaJscode2SessionResult.getOpenid());
                wxuser.setSessionKey(wxMaJscode2SessionResult.getSessionKey());
                update(wxuser);
            }
            if (wxuser != null) {
                wxuser.setSessionKey(wxMaJscode2SessionResult.getSessionKey());
            }
            //数据库中也没有则是新用户
            if (wxuser == null) {
                wxuser = new Wxuser();
                wxuser.setCreateTime(DateUtil.date());
                wxuser.setMiniOpenId(wxMaJscode2SessionResult.getOpenid());
                wxuser.setSessionKey(wxMaJscode2SessionResult.getSessionKey());
                wxuser.setAppmodelId(appmodelId);
                wxuser.setUserStatus(1);
                wxuser.setWxuserId(IdGenerateUtils.getItemId());
                if (wxMaJscode2SessionResult.getUnionid() != null) {
                    wxuser.setUnionId(wxMaJscode2SessionResult.getUnionid());
                }
                tWxuserMapper.insertSelective(wxuser);
                //添加 访客数一个用户一天只能算一个
                redisTemplate.opsForValue().increment(GroupMallProperties.getRedisPrefix().concat(appmodelId)
                        .concat(RedisPrefix.MANAGER_STATISTICS_VISITORSUM));
            }
            userUtil.depositCache(wxuser, 1);
            LoginResultVO loginResultVO = BeanUtil.toBean(wxuser, LoginResultVO.class);
            //如果是团长查询团长状态
            if (wxuser.getUserStatus().equals(2)) {
                Condition condition = new Condition(GroupLeader.class);
                condition.createCriteria().andEqualTo("wxuserId", wxuser.getWxuserId()).andEqualTo("deleteState", 0);
                GroupLeader groupLeader = groupLeaderService.findByOneCondition(condition);
                if (groupLeader == null) {
                    throw new ServiceException("数据异常");
                }
                loginResultVO.setGroupLeaderId(groupLeader.getGroupLeaderId());
                loginResultVO.setGroupState(1);
                if (groupLeader.getStatus().equals(GroupLeaderStatus.STATUS_DISABLE)) {
                    loginResultVO.setGroupState(2);
                }
            }
            return loginResultVO;
        } catch (WxErrorException e) {
            throw new ServiceException("获取openId失败");
        }
    }

    @Override
    public Wxuser wxH5Login(String code, String appmodelId) {
        WxMpService wxMpService = wxpmServiceFactory.getWxMpService(appmodelId);
        WxMpUser wxMpUser;
        Wxuser wxuser = null;
        try {
            wxMpUser = wxMpService.oauth2getUserInfo(wxMpService.oauth2getAccessToken(code), "zh_CN");
            log.info("h5登录获取到的用户信息" + wxMpUser.toString());
//			wxMpUser = wxMpService.getUserService().userInfo(wxMpUser.getOpenId());
            //根据unionid获取用户
            wxuser = (Wxuser) redisTemplate.opsForValue()
                    .get(GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.USERUNION)
                            .concat(wxMpUser.getUnionId()));
            //缓存中为空从数据库中查询
            if (wxuser == null) {
                Condition condition = new Condition(Wxuser.class);
                condition.createCriteria().andEqualTo("appmodelId", appmodelId)
                        .andEqualTo("unionId", wxMpUser.getUnionId());
                wxuser = findByOneCondition(condition);
                //缓存中不存在用户则表示今天用户没有被加入到访客数
                redisTemplate.opsForValue().increment(GroupMallProperties.getRedisPrefix().concat(appmodelId)
                        .concat(RedisPrefix.MANAGER_STATISTICS_VISITORSUM));
            }

            //数据库中也没有则是新用户,保存用户数据
            if (wxuser == null) {
                wxuser = new Wxuser();
                wxuser.setCreateTime(DateUtil.date());
                wxuser.setMpOpenid(wxMpUser.getOpenId());
                wxuser.setAppmodelId(appmodelId);
                wxuser.setUserStatus(1);
                wxuser.setWxuserId(IdGenerateUtils.getItemId());
                wxuser.setUnionId(wxMpUser.getUnionId());
                wxuser.setIcon(wxMpUser.getHeadImgUrl());
                wxuser.setWxuserName(wxMpUser.getNickname());
                tWxuserMapper.insertSelective(wxuser);
                //添加 访客数一个用户一天只能算一个
                redisTemplate.opsForValue().increment(GroupMallProperties.getRedisPrefix().concat(appmodelId)
                        .concat(RedisPrefix.MANAGER_STATISTICS_VISITORSUM));
            } else if (wxuser.getMpOpenid() == null) {
                wxuser.setMpOpenid(wxMpUser.getOpenId());
                tWxuserMapper.updateMpOpenIdByPrimaryKey(wxuser.getWxuserId(), wxMpUser.getOpenId());
            }
            //比较用户昵称和头像，不同则更新
            log.info("wxuser信息" + wxuser.toString());
            log.info("wxMpuser信息" + wxMpUser.toString());
            log.info("名称" + wxMpUser.getNickname());
            log.info("头像" + wxMpUser.getHeadImgUrl());
            log.info("用户数据更新前", wxuser);
            if (!wxMpUser.getNickname().equals(wxuser.getWxuserName()) || !wxMpUser.getHeadImgUrl().equals(wxuser.getIcon())) {
                wxuser.setIcon(wxMpUser.getHeadImgUrl());
                wxuser.setWxuserName(wxMpUser.getNickname());
                tWxuserMapper.updateByPrimaryKeySelective(wxuser);
                //更新成功后在更新缓存
                userUtil.depositCache(wxuser, 2);
                log.info("用户数据已经更新", wxuser);
            }
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return wxuser;
    }


    @Override
    public Result wxH5LoginV13(String code, String appmodelId) {
        //1 用code拿到用户mpUser
        //2 mpUser的openId从数据库中查询wxuser表，或从缓存中获取wxuser，查询不到或者wxuser的miniOpenid为空则小程序用户数据和h5的用户信息未绑定
        //3 已绑定用户则返回wxuser信息
        //4 未绑定的mpUser，生成mdsUnionId返回到h5端
        //mdsUnionId包含用户nickname的標記值，可用于粗略校验是否是同一个微信号

        //step 1
        WxMpService wxMpService = wxpmServiceFactory.getWxMpService(appmodelId);
        WxMpUser wxMpUser;
        Wxuser wxuser = null;
        try {
            wxMpUser = wxMpService.oauth2getUserInfo(wxMpService.oauth2getAccessToken(code), "zh_CN");
            log.info("h5登录获取到的用户信息" + wxMpUser.toString());

            //step 2
            //根据mpOpenId从缓存中获取用户信息
            wxuser = (Wxuser) redisTemplate.opsForValue()
                    .get(GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.MPOPENID)
                            .concat(wxMpUser.getOpenId()));
            //缓存中为空从数据库中查询
            if (wxuser == null) {
                Condition condition = new Condition(Wxuser.class);
                condition.createCriteria().andEqualTo("appmodelId", appmodelId)
                        .andEqualTo("mpOpenid", wxMpUser.getOpenId());
                wxuser = findByOneCondition(condition);
                //数据库中也没有,用户未关联信息，生成mdsUnionId
                if (wxuser == null || wxuser.getMiniOpenId() == null) {
//					String mdsUnionId;
//					try {
//						 mdsUnionId=UserUtil.generateMdsUnionId(wxMpUser.getNickname());
//					}catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
//						badHanyuPinyinOutputFormatCombination.printStackTrace();
//						mdsUnionId = UserUtil.generateMdsUnionId();
//					}
                    JSONObject object = new JSONObject();
                    object.put("msg", "用户数据未与小程序绑定");
                    object.put("mpOpenid", wxMpUser.getOpenId());
                    return Result.error(CodeMsg.FAIL.fillArgs(object.toJSONString()));
                }
            } else {
                //添加 访客数一个用户一天只能算一个
                redisTemplate.opsForValue().increment(GroupMallProperties.getRedisPrefix().concat(appmodelId)
                        .concat(RedisPrefix.MANAGER_STATISTICS_VISITORSUM));
                //比较用户昵称和头像，不同则更新
                if (!wxMpUser.getNickname().equals(wxuser.getWxuserName()) || !wxMpUser.getHeadImgUrl().equals(wxuser.getIcon())) {
                    wxuser.setIcon(wxMpUser.getHeadImgUrl());
                    wxuser.setWxuserName(wxMpUser.getNickname());
                    tWxuserMapper.updateByPrimaryKeySelective(wxuser);
                    //更新成功后在更新缓存
                    userUtil.depositCache(wxuser, 2);
                    log.info("用户数据已经更新", wxuser);
                }
            }
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return Result.success(wxuser);
    }

    @Override
    public Result bindH5Mini(Long wxuserId, String mdsUnionId, String mpOpenid, String appmodelId) {
        //1 获取小程序的用户信息
        //2 验证mdsUnionId
        //3 将mdsUnionId插入绕小程序用户信息中
        Wxuser wxuser = tWxuserMapper.selectByWxuserIdAndAppmodelId(wxuserId, appmodelId);
        if (StringUtils.isBlank(wxuser.getMdsUnionId())) {
//			String h5NamePY = mdsUnionId.substring(12);
//			if (StringUtils.isNotBlank(h5NamePY)) {
//				try {
//					String pingYin = PinYinUtil.getPingYin(wxuser.getWxuserName());
//					if (!h5NamePY.equals(pingYin)) {
//						throw new ServiceException("h5用户信息与小程序用户信息不匹配");
//					}
//				} catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
//					badHanyuPinyinOutputFormatCombination.printStackTrace();
//				}
//			}
            wxuser.setMdsUnionId(mdsUnionId);
            wxuser.setMpOpenid(mpOpenid);
            tWxuserMapper.updateByPrimaryKeySelective(wxuser);
            userUtil.depositCache(wxuser, 2);
            return Result.success("绑定成功");
        } else {
            return Result.error(CodeMsg.FAIL.fillArgs("H5数据已绑定到小程序"));
        }
    }

    @Override
    public Result unBind(Long wxuserId, String appmodelId) {
        //1 获取小程序的用户信息
        //2 验证mdsUnionId
        //3 将mdsUnionId插入绕小程序用户信息中
        Wxuser wxuser = tWxuserMapper.selectByWxuserIdAndAppmodelId(wxuserId, appmodelId);
        String mpOpenid = wxuser.getMpOpenid();
        tWxuserMapper.unbind(wxuserId);
        if (StringUtils.isEmpty(mpOpenid)) {
            return Result.success("账号已解绑");
        }
        //当前日期
        DateTime currentDay = DateUtil.date();
        //结束日期
        DateTime endOfDay = DateUtil.endOfDay(DateUtil.date());
        redisTemplate.opsForValue()
                .set(GroupMallProperties.getRedisPrefix().concat(wxuser.getAppmodelId()).concat(RedisPrefix.MPOPENID)
                                .concat(mpOpenid), null, endOfDay.getTime() - currentDay.getTime(),
                        TimeUnit.MILLISECONDS);
        return Result.success();

    }

    @Override
    public String decodeUserInfo(DecodeUserInfoVO decodeUserInfoVO) {
        Wxuser wxuser = tWxuserMapper.selectByPrimaryKey(decodeUserInfoVO.getWxuserId());
        if (wxuser == null) {
            throw new ServiceException("用户不存在");
        }
        //缓存中的ssionkey为最新
        Wxuser wxuser1 = (Wxuser) redisTemplate.opsForValue()
                .get(GroupMallProperties.getRedisPrefix().concat(wxuser.getAppmodelId()).concat(RedisPrefix.USER)
                        .concat(wxuser.getMiniOpenId()));
        if (wxuser1 != null) {
            wxuser = wxuser1;
        }
        String jsonData = WxMaCryptUtils
                .decrypt(wxuser.getSessionKey(), decodeUserInfoVO.getEncryptedData(), decodeUserInfoVO.getIvStr());
        JSONObject jsonObject = JSON.parseObject(jsonData);
        //更新部分信息
        if (StringUtils.isBlank(jsonObject.getString("avatarUrl"))) {
            //该用户没有头像，则设置一个默认头像
            wxuser.setIcon("https://www.superprism.cn/resource/groupmall/wxuser_default_icon.jpg");
        } else {
            wxuser.setIcon(jsonObject.getString("avatarUrl"));
        }
        wxuser.setWxuserName(jsonObject.getString("nickName"));
        if (StringUtils.isBlank(wxuser.getUnionId())) {
            wxuser.setUnionId(jsonObject.getString("unionId"));
        }
        tWxuserMapper.updateByPrimaryKeySelective(wxuser);
        userUtil.depositCache(wxuser, 1);
        return wxuser.getPhone();
    }

    @Override
    public UserInfoVO getUserInfo(Long wxuserId, String appmodelId) {
        Wxuser wxuser = tWxuserMapper.selectByWxuserIdAndAppmodelId(wxuserId, appmodelId);
        if (wxuser == null) {
            throw new ServiceException("用户不存在");
        }
        UserInfoVO userInfoVO = BeanMapper.map(wxuser, UserInfoVO.class);
        if (userInfoVO.getUserStatus().equals(2)) {
            Condition condition = new Condition(GroupLeader.class);
            condition.createCriteria().andEqualTo("wxuserId", wxuser.getWxuserId()).andEqualTo("deleteState", 0);
            GroupLeader groupLeader = groupLeaderService.findByOneCondition(condition);
            if (groupLeader.getStatus().equals(1)) {
                userInfoVO.setGroupState(1);
            }
            if (groupLeader.getStatus().equals(3)) {
                userInfoVO.setGroupState(2);
            }
            userInfoVO.setGroupLeaderId(groupLeader.getGroupLeaderId());
            userInfoVO.setBrokerage(groupLeader.getBrokerage());
        }
        if (userInfoVO.getCommunityId() != null && userInfoVO.getCommunityId() > 0) {
            List<GroupLeader> groupLeaders = groupLeaderService.findByList("communityId", userInfoVO.getCommunityId());
            List<GroupLeader> notDeleteGroupLeaderList = groupLeaders.stream()
                    .filter(obj -> obj.getDeleteState().equals(false)).collect(Collectors.toList());
            Community community = communityService.findById(userInfoVO.getCommunityId());
            Cities cityid = citiesService.findBy("cityid", community.getCityId());
            userInfoVO.setCityId(cityid.getCityid());
            userInfoVO.setCityName(cityid.getCity().substring(0, cityid.getCity().length() - 1));
            //市辖区转换
            if ("110000".equals(cityid.getProvinceid()) || "120000".equals(cityid.getProvinceid()) || "310000"
                    .equals(cityid.getProvinceid()) || "500000".equals(cityid.getProvinceid())) {
                Provinces provinceid = provincesService.findBy("provinceid", cityid.getProvinceid());
                userInfoVO.setCityName(provinceid.getProvince().substring(0, cityid.getCity().length() - 1));
            }
            userInfoVO.setCommunityName(community.getCommunityName());
            if (CollectionUtil.isNotEmpty(notDeleteGroupLeaderList)) {
                notDeleteGroupLeaderList = notDeleteGroupLeaderList.stream()
                        .filter(obj -> obj.getStatus().equals(1) || obj.getStatus().equals(3))
                        .collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(notDeleteGroupLeaderList)) {
                    if (notDeleteGroupLeaderList.get(0).getStatus().equals(3)) {
                        userInfoVO.setCommunityState(1);
                    }
                    if (notDeleteGroupLeaderList.get(0).getStatus().equals(1)) {
                        userInfoVO.setCommunityState(0);
                    }
                } else {
                    userInfoVO.setCommunityState(null);
                }
            } else if (CollectionUtil.isNotEmpty(groupLeaders)) {
                userInfoVO.setCommunityState(1);
            }
        }
        return userInfoVO;
    }

    @Override
    public UserInfoVO getUserInfo2(Long wxuserId, String appmodelId) {
        Wxuser wxuser = tWxuserMapper.selectByWxuserIdAndAppmodelId(wxuserId, appmodelId);
        if (wxuser == null) {
            throw new ServiceException("用户不存在");
        }
        UserInfoVO userInfoVO = BeanMapper.map(wxuser, UserInfoVO.class);
//        String url = wxServiceUtils.createQrcode(wxuser.getWxuserId().toString(),
//        "pages/orderWriteOffDetail/orderWriteOffDetail", appmodelId);
//        userInfoVO.setReceiptCode(url);
        Condition condition = new Condition(GroupLeader.class);
        condition.createCriteria().andEqualTo("wxuserId", wxuser.getWxuserId()).andEqualTo("deleteState", 0)
                .andIn("status", Arrays.asList(1, 3));
        GroupLeader groupLeader = groupLeaderService.findByOneCondition(condition);
        if (groupLeader != null) {
            if (groupLeader.getStatus().equals(1)) {
                userInfoVO.setGroupState(1);
            }
            if (groupLeader.getStatus().equals(3)) {
                userInfoVO.setGroupState(2);
            }
            userInfoVO.setGroupLeaderId(groupLeader.getGroupLeaderId());
            userInfoVO.setBrokerage(groupLeader.getBrokerage());
        }
        if (userInfoVO.getCommunityId() != null && userInfoVO.getCommunityId() > 0) {
            List<GroupLeader> groupLeaders = groupLeaderService.findByList("communityId", userInfoVO.getCommunityId());
            List<GroupLeader> notDeleteGroupLeaderList = groupLeaders.stream()
                    .filter(obj -> obj.getDeleteState().equals(false)).collect(Collectors.toList());
            Community community = communityService.findById(userInfoVO.getCommunityId());
            Cities cityid = citiesService.findBy("cityid", community.getCityId());
            userInfoVO.setCityId(cityid.getCityid());
            userInfoVO.setCityName(cityid.getCity().substring(0, cityid.getCity().length() - 1));
            //市辖区转换
            if ("110000".equals(cityid.getProvinceid()) || "120000".equals(cityid.getProvinceid()) || "310000"
                    .equals(cityid.getProvinceid()) || "500000".equals(cityid.getProvinceid())) {
                Provinces provinceid = provincesService.findBy("provinceid", cityid.getProvinceid());
                userInfoVO.setCityName(provinceid.getProvince().substring(0, cityid.getCity().length() - 1));
            }
            userInfoVO.setCommunityName(community.getCommunityName());
            if (CollectionUtil.isNotEmpty(notDeleteGroupLeaderList)) {
                notDeleteGroupLeaderList = notDeleteGroupLeaderList.stream()
                        .filter(obj -> obj.getStatus().equals(1) || obj.getStatus().equals(3))
                        .collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(notDeleteGroupLeaderList)) {
                    if (notDeleteGroupLeaderList.get(0).getStatus().equals(3)) {
                        userInfoVO.setCommunityState(1);
                    }
                    if (notDeleteGroupLeaderList.get(0).getStatus().equals(1)) {
                        userInfoVO.setCommunityState(0);
                    }
                } else {
                    userInfoVO.setCommunityState(null);
                }
            } else if (CollectionUtil.isNotEmpty(groupLeaders)) {
                userInfoVO.setCommunityState(1);
            }
        }
        //h5接龙活动是否开启
        SolitaireSettingVo res = solitaireSettingService.getSolitaireInfo(appmodelId);
        userInfoVO.setSolitaireOpen(res.getStatus() != null && res.getStatus() == 1);
        return userInfoVO;
    }

    @Override
    public List<Wxuser> findCustomersByGroupLeaderId(String groupLeaderId, String search) {
        return tWxuserMapper.findCustomersByGroupLeaderId(groupLeaderId, search);
    }

    @Override
    public String finUserGroupById(Long wxuserId, String appmodelId) {
        JSONObject jsonObject = new JSONObject();
        Wxuser wxuser = tWxuserMapper.selectByWxuserIdAndAppmodelId(wxuserId, appmodelId);
        if (wxuser != null) {
            //根据用户的小区id获取未被删除的团长
            Long communityId = wxuser.getCommunityId();
            Condition condition = new Condition(GroupLeader.class);
            condition.createCriteria().andEqualTo("communityId", communityId).andEqualTo("deleteState", 0);
            List<GroupLeader> groupLeaders = groupLeaderService.findByCondition(condition);
            //团长信息不存在则返回空数据
            if (CollectionUtil.isNotEmpty(groupLeaders)) {
                GroupLeader groupLeader = groupLeaders.get(0);
                //获取该团长的微信用户信息
                Wxuser groupUser = tWxuserMapper
                        .selectByWxuserIdAndAppmodelId(groupLeader.getWxuserId(), groupLeader.getAppmodelId());
                //获取小区对象
                Community community = communityService.getCommunityById(communityId);
                //获取该团长的所有评价信息
                Condition condition1 = new Condition(Comment.class);
                condition1.createCriteria().andEqualTo("groupLeaderId", groupLeader.getGroupLeaderId())
                        .andEqualTo("delFlag", 0);
                List<Comment> byCondition = commentService.findByCondition(condition1);
                double groupScore;
                if (CollectionUtil.isNotEmpty(byCondition)) {
                    //取得团长评分的平均值
                    groupScore = byCondition.stream().mapToDouble(Comment::getGroupScore).average().getAsDouble();
                } else {
                    //团长没有被评价过则默认5分
                    groupScore = 5.0;
                }
                if (groupUser != null) {
                    jsonObject.put("nickName", groupUser.getWxuserName());
                    jsonObject.put("icon", groupUser.getIcon());
                }
                if (community != null) {
                    jsonObject.put("community", community.getCommunityName());
                }
                jsonObject.put("score", groupScore);
                jsonObject.put("name", groupLeader.getGroupName());
                jsonObject.put("phone", groupLeader.getGroupPhone());
                jsonObject.put("adress", groupLeader.getAddress());
            }
        }
        return jsonObject.toJSONString();
    }

    @Override
    public Integer updateUserInfo(WxuserInfoUpdateVO infoUpdateVO) {
        Wxuser wxuser = BeanUtil.toBean(infoUpdateVO, Wxuser.class);
        if (wxuser == null) {
            throw new ServiceException("更新数据为空");
        }
        if (tWxuserMapper.updateByPrimaryKeySelective(wxuser) > 0) {
            wxuser = tWxuserMapper.selectByPrimaryKey(infoUpdateVO.getWxuserId());
            userUtil.depositCache(wxuser, 1);
            return 1;
        }
        return 0;
    }

    @Override
    public int updateRemark(RemarkVO userRemarkVO) {
        List<Wxuser> wxusers = tWxuserMapper.selectByIds(userRemarkVO.getIds());
        AtomicInteger i = new AtomicInteger();
        if (userRemarkVO.getCoverType() == 0) {
            //不覆盖原有备注,过滤出非空备注的用户
            List<Wxuser> collect = wxusers.stream().filter(obj -> !StringUtils.isNotBlank(obj.getRemark()))
                    .collect(Collectors.toList());
            collect.forEach(obj -> {
                obj.setRemark(userRemarkVO.getRemark());
                i.set(tWxuserMapper.updateByPrimaryKeySelective(obj));
            });
        }
        if (userRemarkVO.getCoverType() == 1) {
            wxusers.forEach(obj -> {
                obj.setRemark(userRemarkVO.getRemark());
                i.set(tWxuserMapper.updateByPrimaryKeySelective(obj));
            });
        }
        return i.get();
    }

    @Override
    public List<WxuserManagerVO> searchWxuserManager(Integer pageNum, Integer pageSize, String nickName,
                                                     String community, String createTime, String appmodelId) {
        Map<String, Object> paramMap = new HashMap<>(8);
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(community)) {
            for (char c : community.toCharArray()) {
                sb.append(c).append("%");
            }
            sb.insert(0, "%");
            paramMap.put("community", sb.toString());
            sb.delete(0, sb.length() - 1);
        }
        if (StringUtils.isNotBlank(createTime)) {
            String[] split = createTime.split(",");
            paramMap.put("startTime", split[0]);
            if (StringUtils.isNotBlank(split[1])) {
                paramMap.put("endTime", split[1]);
            }
        }
        if (StringUtils.isNotBlank(nickName)) {
            for (char c : nickName.toCharArray()) {
                sb.append(c).append("%");
            }
            sb.insert(0, "%");
            paramMap.put("nickName", sb.toString());
            sb.delete(0, sb.length() - 1);
        }
        paramMap.put("appmodelId", appmodelId);
        PageHelper.startPage(pageNum, pageSize, "create_time desc");
        List<WxuserManagerVO> memberManagerVOS = tWxuserMapper.selectWxuserManager(paramMap);
        if (!CollUtil.isEmpty(memberManagerVOS)) {
            //封装用户信息 取货点和地址
            List<Long> communityIds = memberManagerVOS.stream().filter(obj -> obj.getCommunityId() != null)
                    .map(WxuserManagerVO::getCommunityId).collect(Collectors.toList());
            Map<Long, CommunityMoreDTO> moreDTOMap = new HashMap<>(pageSize);
            if (!CollUtil.isEmpty(communityIds)) {
                List<CommunityMoreDTO> communities = communityService.getCommunityAll(communityIds);
                moreDTOMap = communities.stream().collect(Collectors.toMap(Community::getCommunityId, v -> v));
            }
            List<Long> wxuserId = memberManagerVOS.stream().map(WxuserManagerVO::getWxuserId)
                    .collect(Collectors.toList());
            //查询用户默认收货地址
            Condition condition = new Condition(Consignee.class);
            condition.createCriteria().andIn("wxuserId", wxuserId).andEqualTo("defaultAdderss", true);
            Map<Long, Consignee> consigneeMap = consigneeService.findByCondition(condition).stream()
                    .collect(Collectors.toMap(Consignee::getWxuserId, v -> v));
            //消费金额 购次未写
            List<Order> orderList = orderService.findByWxuserIds(wxuserId);
            Map<Long, List<Order>> wxuserOrderMap = orderList.stream()
                    .collect(Collectors.groupingBy(Order::getWxuserId));
            for (WxuserManagerVO obj : memberManagerVOS) {
                Consignee consignee = consigneeMap.get(obj.getWxuserId());
                if (consignee != null) {
                    obj.setPhone(consignee.getPhone());
                }
                CommunityMoreDTO communityMoreDTO = moreDTOMap.get(obj.getCommunityId());
                if (communityMoreDTO != null) {
                    obj.setCommunityName(communityMoreDTO.getCommunityName());
                    obj.setCommunityAddres(communityMoreDTO.getPcaAdr());
                }
                List<Order> orders = wxuserOrderMap.get(obj.getWxuserId());
                if (orders != null) {
                    BigDecimal consume = BigDecimal.ZERO;
                    int sum = 0;
                    for (Order order : orders) {
                        consume = NumberUtil.add(consume, order.getPayFee());
                        sum = sum + 1;
                    }
                    obj.setConsume(NumberUtil.round(consume, 2).doubleValue());
                    obj.setBuySum(sum);
                    obj.setLastBuyTime(orders.get(0).getPayTime());
                } else {
                    obj.setConsume(0.0);
                    obj.setBuySum(0);
                }
            }
        }
        return memberManagerVOS;
    }

    @Override
    public Long qrcode(String code, String state, String appmodelId) {
        //网页应用
        String getToken = "https://api.weixin.qq.com/sns/oauth2/access_token?" + "appid=wxd4b3d4bd675361f2&"
                + "secret=7f21eef09154eac79a34dd4ffc0236d4&code=CODE" + "&grant_type=authorization_code";
        String xml = HttpUtil.get(getToken.replace("CODE", code));
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = WxMpOAuth2AccessToken.fromJson(xml);
        if (wxMpOAuth2AccessToken == null) {
            throw new ServiceException("数据为空");
        }
        Wxuser wxuser;
        if (wxMpOAuth2AccessToken.getUnionId() != null) {
            wxuser = tWxuserMapper.selectByUnionid(wxMpOAuth2AccessToken.getUnionId());
            if (wxuser == null) {
                wxuser = new Wxuser();
                wxuser.setCreateTime(DateUtil.date());
                wxuser.setUnionId(wxMpOAuth2AccessToken.getUnionId());
                wxuser.setAppmodelId(appmodelId);
                wxuser.setUserStatus(1);
                wxuser.setWxuserId(IdGenerateUtils.getItemId());
                tWxuserMapper.insertSelective(wxuser);
            }
        } else {
            wxuser = new Wxuser();
            wxuser.setPcOpenId(wxMpOAuth2AccessToken.getOpenId());
            wxuser = tWxuserMapper.selectOne(wxuser);
            if (wxuser.getWxuserId() == null) {
                wxuser = new Wxuser();
                wxuser.setCreateTime(DateUtil.date());
                wxuser.setAppmodelId(appmodelId);
                wxuser.setUserStatus(1);
                wxuser.setWxuserId(IdGenerateUtils.getItemId());
                tWxuserMapper.insertSelective(wxuser);
            }
        }
        wxuser.setPcOpenId(wxMpOAuth2AccessToken.getOpenId());
        String getInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
        String jsondata = HttpUtil.get(getInfo.replace("ACCESS_TOKEN", wxMpOAuth2AccessToken.getAccessToken())
                .replace("OPENID", wxMpOAuth2AccessToken.getOpenId()));
        JSONObject jsonObject = JSON.parseObject(jsondata);
        wxuser.setIcon(jsonObject.getString("headimgurl"));
        wxuser.setWxuserName(jsonObject.getString("nickname"));
        tWxuserMapper.updateByPrimaryKeySelective(wxuser);
        userUtil.depositCache(wxuser, 1);
        return wxuser.getWxuserId();
    }

    @Override
    public Wxuser findByGroupleaderId(String groupLeaderId) {
        return tWxuserMapper.selectByGroupleaderId(groupLeaderId);
    }

    @Override
    public void updateByComminuty(String appmodelId, Long communityId) {
        tWxuserMapper.updateByComminuty(appmodelId, communityId);
    }
}
