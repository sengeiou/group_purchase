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

package com.mds.group.purchase.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.order.model.GoodsSortingOrder;
import com.mds.group.purchase.order.model.GoodsSortingOrderDetail;
import com.mds.group.purchase.order.service.GoodsSortingOrderService;
import com.mds.group.purchase.order.vo.GoodsSortingOrderViewVO;
import com.mds.group.purchase.utils.GoodsSortingOrderUtil;
import com.mds.group.purchase.utils.PageUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * The type Goods sorting order service.
 *
 * @author CodeGenerator
 * @date 2019 /02/23
 */
@Service
public class GoodsSortingOrderServiceImpl extends AbstractService<GoodsSortingOrder>
        implements GoodsSortingOrderService {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private GoodsSortingOrderUtil goodsSortingOrderUtil;

    @Override
    public List<GoodsSortingOrderViewVO> goodsSortingOrder(String appmodelId, Long sendBillId) {
        List<GoodsSortingOrderViewVO> goodsSortingOrderViewVOS = (List<GoodsSortingOrderViewVO>) redisTemplate
                .opsForValue().get(GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.GOODS_SORTING_ORDER_CACHE + sendBillId);
        if (goodsSortingOrderViewVOS == null) {
            goodsSortingOrderViewVOS = goodsSortingOrderUtil.generateGoodsSortOrder(sendBillId, appmodelId);
            redisTemplate.opsForValue()
                    .set(GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.GOODS_SORTING_ORDER_CACHE + sendBillId,
                            goodsSortingOrderViewVOS, 1, TimeUnit.HOURS);
        }
        return goodsSortingOrderViewVOS;
    }

    @Override
    public void export(String appmodelId, Integer pageNum, Integer pageSize, Long sendBillId,
                       HttpServletResponse response) {
        List<GoodsSortingOrderViewVO> goodsSortingOrderViewVOS = goodsSortingOrder(appmodelId, sendBillId);
        PageInfo<GoodsSortingOrderViewVO> pageInfo = PageUtil.pageUtil(pageNum, pageSize, goodsSortingOrderViewVOS);
        goodsSortingOrderViewVOS = pageInfo.getList();
        if (CollectionUtil.isEmpty(goodsSortingOrderViewVOS)) {
            throw new ServiceException("无导出数据");
        }
        try (ExcelWriter writer = ExcelUtil.getBigWriter(-1)) {
            //合并首行
            writer.getHeadCellStyle().setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            Font font = writer.createFont();
            font.setBold(true);
            writer.getHeadCellStyle().setFont(font);
            writer.merge(0, 1, 0, 3, goodsSortingOrderViewVOS.get(0).getSendBillName().replace("发货单", "商品分拣单"), false);
            writer.merge(2, 3, 0, 0, "编号", true);
            writer.merge(2, 3, 1, 1, "商品", true);
            writer.merge(2, 3, 2, 2, "总数量", true);
            writer.merge(2, 3, 3, 3, "分拣明细", true);
            writer.setColumnWidth(0, 10);
            writer.setColumnWidth(1, 30);
            writer.setColumnWidth(2, 8);
            writer.setColumnWidth(3, 50);
            writer.passRows(4);
            writer.getStyleSet().getCellStyle().setWrapText(true);
            int index = 1;
            for (GoodsSortingOrderViewVO vo : goodsSortingOrderViewVOS) {
                List<GoodsSortingOrderDetail> detailList = vo.getGoodsSortingOrderDetailList();
                StringBuilder builder = new StringBuilder();
                int i = 1;
                for (GoodsSortingOrderDetail sortingOrderDetail : detailList) {
                    builder.append(sortingOrderDetail.getLineName()).append(" ")
                            .append(sortingOrderDetail.getGoodsNumber()).append(" 件;");
                    if (i % 3 == 0) {
                        builder.append("\r\n");
                    }
                    i++;
                }
                builder.deleteCharAt(builder.lastIndexOf(";")).append("。");
                List<String> row1 = CollUtil
                        .newArrayList(String.valueOf(index++), vo.getGoodsName(), vo.getGoodsTotleSum().toString(),
                                builder.toString());
                writer.getStyleSet();
                writer.writeRow(row1);
                writer.setRowHeight(writer.getCurrentRow() - 1, 60);
            }
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            String filename = new String(goodsSortingOrderViewVOS.get(0).getSendBillName().replace("发货单", "商品发货单")
                    .getBytes(StandardCharsets.UTF_8), "gb2312");
            response.setHeader("Content-Disposition", "attachment;filename=".concat(filename));
            writer.flush(response.getOutputStream());
            response.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
