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

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.order.model.LineSortingOrder;
import com.mds.group.purchase.order.model.LineSortingOrderGoodsDetail;
import com.mds.group.purchase.order.service.GoodsSortingOrderService;
import com.mds.group.purchase.order.service.LineSortingOrderService;
import com.mds.group.purchase.order.service.ReceiptBillService;
import com.mds.group.purchase.order.vo.LineSortingOrderGoodsVO;
import com.mds.group.purchase.order.vo.LineSortingOrderViewVo;
import com.mds.group.purchase.utils.LineSortingOrderUtil;
import com.mds.group.purchase.utils.PageUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * The type Line sorting order service.
 *
 * @author CodeGenerator
 * @date 2018 /12/19
 */
@Service
public class LineSortingOrderServiceImpl extends AbstractService<LineSortingOrder> implements LineSortingOrderService {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private ReceiptBillService receiptBillService;
    @Resource
    private LineSortingOrderUtil lineSortingOrderUtil;
    @Resource
    private GoodsSortingOrderService goodsSortingOrderService;

    @Override
    public List<LineSortingOrderViewVo> sortingOrder(String appmodelId, Long sendBillId) {
        List<LineSortingOrderViewVo> lineSortingOrderViewVos = (List<LineSortingOrderViewVo>) redisTemplate
                .opsForValue().get(GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.LINE_SORTING_ORDER_CACHE + sendBillId);
        if (lineSortingOrderViewVos == null) {
            lineSortingOrderViewVos = lineSortingOrderUtil.generateLineSortOrder(sendBillId, appmodelId);
            redisTemplate.opsForValue()
                    .set((GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.LINE_SORTING_ORDER_CACHE + sendBillId),
                            lineSortingOrderViewVos, 1, TimeUnit.HOURS);
        }
        return lineSortingOrderViewVos;
    }

    @Override
    public void export(String appmodelId, Integer type, Integer pageNum, Integer pageSize, Long sendBillId, Long lineId,
                       HttpServletResponse response) {
        if (sendBillId == 0) {
            throw new ServiceException("请先选择分拣单");
        }
        if (type == 1) {
            goodsSortingOrderService.export(appmodelId, pageNum, pageSize, sendBillId, response);
        }
        //线路分拣单导出
        if (type == 2) {
            List<LineSortingOrderViewVo> listResult = sortingOrder(appmodelId, sendBillId);
            PageInfo<LineSortingOrderViewVo> pageInfo = PageUtil.pageUtil(pageNum, pageSize, listResult);
            lineSortingOrderExport(pageInfo.getList(), response);
        }
        //团长签收单导出
        if (type == 3) {
            receiptBillService.export(appmodelId, pageNum, pageSize, lineId, sendBillId, response);
        }
    }

    private void lineSortingOrderExport(List<LineSortingOrderViewVo> list, HttpServletResponse response) {
        try (ExcelWriter writer = ExcelUtil.getBigWriter()) {
            writer.getWorkbook().setSheetName(0, list.get(0).getLineName() + "0");
            int i = 0;
            for (LineSortingOrderViewVo lineSortingOrderViewVo : list) {
                if (i != 0) {
                    writer.setSheet(lineSortingOrderViewVo.getLineName().concat(i + ""));
                }
                i++;
                //设置行首样式
                writer.getHeadCellStyle().setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                Font font = writer.createFont();
                font.setBold(true);
                writer.getHeadCellStyle().setFont(font);
                //设置自动换行
                writer.getStyleSet().getCellStyle().setWrapText(true);
                //合并首行
                writer.merge(0, 1, 0, 3, list.get(0).getSendBillName().replace("发货单", "线路分拣单"), false);
                writer.merge(0, 1, 4, 7, "线路名称:".concat(lineSortingOrderViewVo.getLineName()), false);
                writer.merge(0, 1, 8, 10, "司机姓名:".concat(lineSortingOrderViewVo.getDriverName()), false);
                writer.merge(0, 1, 11, 13, "司机电话:".concat(lineSortingOrderViewVo.getPhone()), false);
                writer.merge(2, 3, 0, 0, "编号", true);
                writer.merge(2, 3, 1, 2, "商品", true);
                writer.merge(2, 3, 3, 3, "总数量", true);
                writer.merge(2, 3, 4, 13, "分拣明细", true);
                writer.setColumnWidth(1, 15);
                writer.setColumnWidth(2, 15);
                writer.passRows(4);
                int j = 1;
                for (LineSortingOrderGoodsVO lineSortingOrderGoodsVO : lineSortingOrderViewVo
                        .getLineSortingOrderGoodsVOS()) {
                    int currentRow = writer.getCurrentRow();
                    Row orCreateRow = writer.getOrCreateRow(currentRow);
                    Cell cell0 = orCreateRow.createCell(0);
                    Cell cell3 = orCreateRow.createCell(3);
                    //合并商品名称列
                    writer.merge(currentRow, currentRow, 1, 2, lineSortingOrderGoodsVO.getGoodsName(), false);
                    cell0.setCellValue(String.valueOf(j));
                    cell3.setCellValue(lineSortingOrderGoodsVO.getGoodsSum().toString().concat("件"));
                    List<LineSortingOrderGoodsDetail> lineSortingOrderDetailList = lineSortingOrderGoodsVO
                            .getLineSortingOrderDetailList();
                    StringBuilder detail = new StringBuilder();
                    int sum = 1;
                    for (LineSortingOrderGoodsDetail lineSortingOrderGoodsDetail : lineSortingOrderDetailList) {
                        detail.append(lineSortingOrderGoodsDetail.getCommunityName().concat(" ")
                                .concat(lineSortingOrderGoodsDetail.getGoodsNumber().toString()).concat(" 件")
                                .concat(";"));
                        if (sum % 5 == 0) {
                            detail.append("\r\n");
                        }
                        sum++;
                    }
                    int i1 = detail.lastIndexOf(";");
                    if (i1 != -1) {
                        detail.deleteCharAt(i1).append("。");
                    }
                    writer.merge(currentRow, currentRow, 4, 13, detail.toString(), false);
                    writer.writeCellValue(cell0.getColumnIndex(), cell0.getRowIndex(), cell0);
                    writer.writeCellValue(cell3.getColumnIndex(), cell3.getRowIndex(), cell3);
                    writer.passRows(1);
                    writer.setRowHeight(currentRow, 75);
                    j++;
                }
                int currentRow = writer.getCurrentRow();
                writer.merge(currentRow, currentRow + 3, 0, 4, "司机签收:________________", false);
                writer.merge(currentRow, currentRow + 3, 5, 13, "签收日期:________________", false);
            }

            String fileName = list.get(0).getSendBillName().concat(".xlsx");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8),
                            StandardCharsets.ISO_8859_1));
            writer.flush(response.getOutputStream());
            response.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
