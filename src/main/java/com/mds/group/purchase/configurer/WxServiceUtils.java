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

package com.mds.group.purchase.configurer;

import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateData;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import cn.hutool.core.date.DateUtil;
import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.EntPayService;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.EntPayServiceImpl;
import com.github.binarywang.wxpay.util.SignUtils;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.utils.FileUploadUtil;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.api.WxOpenMaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * The type Wx service utils.
 *
 * @author whh
 */
@Component
@Log4j2
public class WxServiceUtils {

    @Resource
    private WxConfiguration wxPayConfiguration;

    /**
     * Query order wx pay order query result.
     *
     * @param transactionId 支付成功之后返回的交易id
     * @param appmodelId    the appmodel id
     * @return wx pay order query result
     * @throws WxPayException the wx pay exception
     */
    public WxPayOrderQueryResult queryOrder(String transactionId,String appmodelId) throws WxPayException {
        WxPayService wxPayService = wxPayConfiguration.init(appmodelId);
        return wxPayService.queryOrder(transactionId,"");
    }

    /**
     * 微信小程序生成二维码
     *
     * @param wxuserId   the wxuser id
     * @param page       the page
     * @param appmodelId the appmodel id
     * @return string string
     * @throws WxErrorException
     */
    public String createWxaCodeUnlimit(String wxuserId, String page, String appmodelId) {

        String scene = "u=" + wxuserId + "&t=" + DateUtil.offsetMinute(new Date(), 30).getTime() / 1000;
        WxOpenMaService msgService = wxPayConfiguration.initWxOpenService(appmodelId);
        String url = "";
        try {
            File file = msgService.getQrcodeService().createWxaCodeUnlimit(scene, page);
            url = FileUploadUtil.uploadImage(file, appmodelId);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Create qrcode string.
     *
     * @param wxuserId   the wxuser id
     * @param page       the page
     * @param appmodelId the appmodel id
     * @return the string
     */
    public String createQrcode(String wxuserId, String page, String appmodelId) {

        String path = page + "?wxuserId=" + wxuserId + "&time=" + DateUtil.offsetMinute(new Date(), 30).getTime();
        WxOpenMaService msgService = wxPayConfiguration.initWxOpenService(appmodelId);
        String url = "";
        try {
            File file = msgService.getQrcodeService().createQrcode(path);
            url = FileUploadUtil.uploadImage(file, appmodelId);
        } catch (WxErrorException e) {
            e.printStackTrace();
            //throw new ServiceException(e.getError().toString());
        }
        return url;
    }


    /**
     * Wx jsapi pay request map.
     *
     * @param body          商品描述
     * @param outTradeNo    订单号
     * @param payOutTradeNo 支付订单号
     * @param totalFee      订单总金额，单位为分
     * @param openid        用户在商户appid下的唯一标识
     * @param notify_url    通知地址
     * @param appmodelId    the appmodel id
     * @return map map
     * @throws Exception the exception
     */
    public Map<String, String> wxJsapiPayRequest(String body, String outTradeNo, String payOutTradeNo, String totalFee, String openid,
                                                 String notify_url, String appmodelId) throws Exception {
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody(body);
        orderRequest.setOutTradeNo(payOutTradeNo);
        orderRequest.setTotalFee(BaseWxPayRequest.yuanToFen(totalFee));
        orderRequest.setOpenid(openid);
        orderRequest.setSpbillCreateIp("192.168.1.1");
        orderRequest.setNonceStr(UUID.randomUUID().toString().substring(0,31));
        orderRequest.setTradeType("JSAPI");
        orderRequest.setNotifyUrl(notify_url);
        orderRequest.setAttach(outTradeNo);
        WxPayService wxPayService = wxPayConfiguration.init(appmodelId);
        WxPayUnifiedOrderResult wxPayUnifiedOrderResult = wxPayService.unifiedOrder(orderRequest);
        Map<String, String> resutlMap = new HashMap<>(5);
        if ("SUCCESS".equals(wxPayUnifiedOrderResult.getReturnCode())) {
            resutlMap.put("returnCode", wxPayUnifiedOrderResult.getReturnCode());
            String timeStamp = System.currentTimeMillis() / 1000 + "";
            String nonceStr = UUID.randomUUID().toString().substring(0,31);
            Map<String, String> map = new HashMap<>(5);
            map.put("appId", orderRequest.getAppid());
            map.put("nonceStr", nonceStr);
            map.put("package", "prepay_id=" + wxPayUnifiedOrderResult.getPrepayId());
            map.put("signType", "MD5");
            map.put("timeStamp", timeStamp);
            //再次签名
            String paySign = SignUtils.createSign(map, "MD5", wxPayService.getConfig().getMchKey(),null);
            resutlMap.put("timeStamp", timeStamp);
            resutlMap.put("paySign", paySign);
            resutlMap.put("package", "prepay_id=" + wxPayUnifiedOrderResult.getPrepayId());
            resutlMap.put("nonceStr", nonceStr);
            return resutlMap;
        } else {
            resutlMap.put("returnCode", wxPayUnifiedOrderResult.getReturnCode());
            resutlMap.put("returnMsg", wxPayUnifiedOrderResult.getReturnMsg());
            resutlMap.put("errorCode", wxPayUnifiedOrderResult.getErrCode());
            resutlMap.put("errCodeDes", wxPayUnifiedOrderResult.getErrCodeDes());
        }
        return resutlMap;
    }

    /**
     * Wx jsapi pay request h 5 map.
     *
     * @param body          商品描述
     * @param outTradeNo    订单号
     * @param payOutTradeNo 支付订单号
     * @param totalFee      订单总金额，单位为分
     * @param openid        用户在商户appid下的唯一标识
     * @param notifyUrl     通知地址
     * @param appmodelId    the appmodel id
     * @return map map
     * @throws Exception the exception
     */
    public Map<String, String> wxJsapiPayRequestH5(String body, String outTradeNo, String payOutTradeNo, String totalFee, String openid,
                                                   String notifyUrl, String appmodelId) throws Exception {
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody(body);
        orderRequest.setOutTradeNo(payOutTradeNo);
        orderRequest.setTotalFee(BaseWxPayRequest.yuanToFen(totalFee));
        orderRequest.setOpenid(openid);
        orderRequest.setSpbillCreateIp("192.168.1.1");
        orderRequest.setNonceStr(UUID.randomUUID().toString().substring(0,31));
        orderRequest.setTradeType("JSAPI");
        orderRequest.setNotifyUrl(notifyUrl);
        orderRequest.setAttach(outTradeNo);
        WxPayService wxPayService = wxPayConfiguration.initH5(appmodelId);
        log.info("mpopenid:"+wxPayService.getConfig().getAppId()+openid);
        WxPayUnifiedOrderResult wxPayUnifiedOrderResult = wxPayService.unifiedOrder(orderRequest);
        Map<String, String> resutlMap = new HashMap<>(5);
        if ("SUCCESS".equals(wxPayUnifiedOrderResult.getReturnCode())) {
            resutlMap.put("returnCode", wxPayUnifiedOrderResult.getReturnCode());
            String timeStamp = System.currentTimeMillis() / 1000 + "";
            String nonceStr = UUID.randomUUID().toString().substring(0,31);
            Map<String, String> map = new HashMap<>(5);
            map.put("appId", wxPayService.getConfig().getAppId());
            map.put("nonceStr", nonceStr);
            map.put("package", "prepay_id=" + wxPayUnifiedOrderResult.getPrepayId());
            map.put("signType", "MD5");
            map.put("timeStamp", timeStamp);
            //再次签名
            String paySign = SignUtils.createSign(map, "MD5", wxPayService.getConfig().getMchKey(),null);
            resutlMap.put("timeStamp", timeStamp);
            resutlMap.put("paySign", paySign);
            resutlMap.put("package", "prepay_id=" + wxPayUnifiedOrderResult.getPrepayId());
            resutlMap.put("nonceStr", nonceStr);
            return resutlMap;
        } else {
            resutlMap.put("returnCode", wxPayUnifiedOrderResult.getReturnCode());
            resutlMap.put("returnMsg", wxPayUnifiedOrderResult.getReturnMsg());
            resutlMap.put("errorCode", wxPayUnifiedOrderResult.getErrCode());
            resutlMap.put("errCodeDes", wxPayUnifiedOrderResult.getErrCodeDes());
        }
        return resutlMap;
    }

    /**
     * 微信扫码支付 模式2
     *
     * @param body       the body
     * @param outTradeNo the out trade no
     * @param totalFee   the total fee
     * @param productId  the product id
     * @param notify_url the notify url
     * @param appmodelId the appmodel id
     * @return byte [ ]
     * @throws WxPayException the wx pay exception
     */
    public byte[] wxNativePayRequest(String body, String outTradeNo, String totalFee, String productId,
                                     String notify_url, String appmodelId) throws WxPayException {
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody(body);
        orderRequest.setOutTradeNo(outTradeNo);
        orderRequest.setTotalFee(WxPayRefundRequest.yuanToFen(totalFee));
        orderRequest.setSpbillCreateIp("127.0.0.1");
        orderRequest.setNonceStr(UUID.randomUUID().toString().substring(0,31));
        orderRequest.setTradeType("NATIVE");
        orderRequest.setNotifyUrl(notify_url);
        orderRequest.setProductId(productId);
        WxPayService wxPayService = wxPayConfiguration.init(appmodelId);
        WxPayUnifiedOrderResult wxPayUnifiedOrderResult = wxPayService.unifiedOrder(orderRequest);
        if ("SUCCESS".equals(wxPayUnifiedOrderResult.getReturnCode())) {
            return wxPayService.createScanPayQrcodeMode2(wxPayUnifiedOrderResult.getCodeURL(), null, null);
        }
        return null;
    }

    /**
     * 微信退款
     *
     * @param out_trade_no  商户订单号
     * @param out_refund_no 商户退款单号
     * @param all_total_fee 订单金额
     * @param refund_fee    退款金额
     * @param appmodelId    the appmodel id
     * @return boolean boolean
     * @throws Exception
     */
    public Boolean wechatRefund(String out_trade_no, String out_refund_no, String all_total_fee, String refund_fee,
                                String appmodelId) {
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
        wxPayRefundRequest.setOutTradeNo(out_trade_no);
        wxPayRefundRequest.setOutRefundNo(out_refund_no);
        wxPayRefundRequest.setTotalFee(BaseWxPayRequest.yuanToFen(all_total_fee));
        wxPayRefundRequest.setRefundFee(BaseWxPayRequest.yuanToFen(refund_fee));
        WxPayService wxPayService = wxPayConfiguration.init(appmodelId);
        Boolean res = false;
        WxPayRefundResult refund = null;
        try {
            refund = wxPayService.refund(wxPayRefundRequest);
        } catch (WxPayException e) {
            e.printStackTrace();
            log.error("发起微信退款请求失败");
            log.error("入参: ");
            log.error(wxPayRefundRequest.toString());
            log.error("错误内容: ");
            log.error(e.getClass().toString());
            log.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
        String success="SUCCESS";
        if (success.equalsIgnoreCase(refund.getReturnCode())) {
            if (success.equalsIgnoreCase(refund.getResultCode())) {
                res = true;
            } else {
                log.error("发起微信退款请求失败");
                log.error("错误内容: ");
                log.error(refund.getErrCodeDes());
            }
        } else {
            log.error("发起微信退款请求失败");
            log.error("错误内容: ");
            log.error(refund.getReturnMsg());
        }
        return res;
    }

    /**
     * 第三方平台代小程序发模板消息
     *
     * @param keywords        the keywords
     * @param templateId      the template id
     * @param openId          the open id
     * @param formId          the form id
     * @param authorizerAppid the authorizer appid
     */
    public void platSendMiniProgramTemplateMessage(List<String> keywords, String templateId, String openId,
                                                   String formId, String authorizerAppid) {
        WxMaService wxMaServiceByAppid = wxPayConfiguration.getWxOpenService().getWxOpenComponentService()
                .getWxMaServiceByAppid(authorizerAppid);
        WxMaMsgService msgService = wxMaServiceByAppid.getMsgService();
        List<WxMaTemplateData> data = new ArrayList<>();
        int i = 1;
        for (String keyword : keywords) {
            data.add(new WxMaTemplateData("keyword" + i, keyword));
            i++;
        }
        WxMaTemplateMessage message = new WxMaTemplateMessage(openId, templateId, "", formId, data, "", "");
        try {
            log.info("参数主体"+message.toJson());
            msgService.sendTemplateMsg(message);
        } catch (WxErrorException e) {
            log.warn("----------小程序发模板消息发送失败------------");
            log.warn("小程序appId: " + authorizerAppid);
            log.warn("消息内容: " + keywords.toString());
            log.warn("微信报错: " + e.toString());
        }
    }

    /**
     * 企业付款
     *
     * @param appmodelId     the appmodel id
     * @param openId         the open id
     * @param partnerTradeNo the partner trade no
     * @param userName       the user name
     * @param amount         the amount
     * @param description    the description
     * @param appId          the app id
     * @return string string
     */
    public String enterprisePayment(String appmodelId, String openId, String partnerTradeNo,
                                    String userName, String amount, String description, String appId) {
        WxPayService wxPayService = wxPayConfiguration.init(appmodelId);
        //微信支付沙箱
//        wxPayService.getConfig().setUseSandboxEnv(true);

        wxPayService.getConfig().setAppId(appId);
        EntPayService entPayService = new EntPayServiceImpl(wxPayService);
        EntPayRequest entPayRequest = new EntPayRequest();
        entPayRequest.setOpenid(openId);
        entPayRequest.setPartnerTradeNo(partnerTradeNo);
        entPayRequest.setCheckName(WxPayConstants.CheckNameOption.NO_CHECK);
        if(StringUtils.isNotBlank(userName)){
            entPayRequest.setCheckName(WxPayConstants.CheckNameOption.FORCE_CHECK);
            entPayRequest.setReUserName(userName);
        }
        entPayRequest.setAmount(WxPayRefundRequest.yuanToFen(amount));
        entPayRequest.setDescription(description);
        entPayRequest.setSpbillCreateIp("127.0.0.1");
        try {
            EntPayResult entPayResult = entPayService.entPay(entPayRequest);
            if ("FAIL".equals(entPayResult.getReturnCode())) {
                log.error("企业支付失败:" + entPayResult.getReturnMsg());
                if ("NO_AUTH".equalsIgnoreCase(entPayResult.getErrCode())) {
                    throw new ServiceException(
                            "无法使用该功能,原因:1.用户账号被冻结，无法付款,2. 产品权限没有开通或者被风控冻结,3. 此IP地址不允许调用接口，如有需要请登录微信支付商户平台更改配置");
                } else if ("AMOUNT_LIMIT".equalsIgnoreCase(entPayResult.getErrCode())) {
                    throw new ServiceException("金额超限");
                } else if ("NAME_MISMATCH".equalsIgnoreCase(entPayResult.getErrCode())) {
                    throw new ServiceException("姓名校验出错");
                } else if ("MONEY_LIMIT".equalsIgnoreCase(entPayResult.getErrCode())) {
                    throw new ServiceException("已经达到今日付款总额上限/已达到付款给此用户额度上限");
                } else if ("V2_ACCOUNT_SIMPLE_BAN".equalsIgnoreCase(entPayResult.getErrCode())) {
                    throw new ServiceException("无法给非实名用户付款");
                } else if ("SENDNUM_LIMIT".equalsIgnoreCase(entPayResult.getErrCode())) {
                    throw new ServiceException("该用户今日付款次数超过限制,如有需要请登录微信支付商户平台更改API安全配置");
                } else if ("PAY_CHANNEL_NOT_ALLOWED".equalsIgnoreCase(entPayResult.getErrCode())) {
                    throw new ServiceException("本商户号未配置API发起能力");
                }
                throw new ServiceException("支付异常");
            }
            return entPayResult.getResultCode();
        } catch (WxPayException e) {
            if(e.getMessage().contains("证书文件")){
                throw new ServiceException("支付证书文件不存在,请上传!");
            }
            if("NOTENOUGH".equalsIgnoreCase(e.getErrCode())){
                throw new ServiceException("商家账户余额不足");
            }
            throw new ServiceException(e.getMessage(),e);
        }
    }
}
