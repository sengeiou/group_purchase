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

package com.mds.group.purchase.order.result;

import com.mds.group.purchase.order.model.ReceiptBill;
import com.mds.group.purchase.order.model.ReceiptBillDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * The type Receipt bill result.
 *
 * @author pavawi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReceiptBillResult extends ReceiptBill {

    /**
     * The Receipt bill detail list.
     */
    @ApiModelProperty("签收单详情")
    List<ReceiptBillDetail> receiptBillDetailList;
}
