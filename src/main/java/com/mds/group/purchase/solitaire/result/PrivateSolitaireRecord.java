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

package com.mds.group.purchase.solitaire.result;

import com.mds.group.purchase.solitaire.model.SolitaireRecord;
import com.mds.group.purchase.user.model.GroupLeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The type Private solitaire record.
 *
 * @author pavawi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PrivateSolitaireRecord extends SolitaireRecord {

    private GroupLeader groupLeader;

    /**
     * Instantiates a new Private solitaire record.
     */
    public PrivateSolitaireRecord() {

    }

    /**
     * Instantiates a new Private solitaire record.
     *
     * @param solitaireRecord the solitaire record
     * @param groupLeader     the group leader
     */
    public PrivateSolitaireRecord(SolitaireRecord solitaireRecord, GroupLeader groupLeader) {
        this.setGroupLeader(groupLeader);
        this.setAppmodelId(solitaireRecord.getAppmodelId());
        this.setBuyerIcon(solitaireRecord.getBuyerIcon());
        this.setBuyerId(solitaireRecord.getBuyerId());
        this.setBuyerName(solitaireRecord.getBuyerName());
        this.setCreateTime(solitaireRecord.getCreateTime());
        this.setGroupAddress(solitaireRecord.getGroupAddress());
        this.setGroupLeaderId(solitaireRecord.getGroupLeaderId());
        this.setId(solitaireRecord.getId());
        this.setOrderNo(solitaireRecord.getOrderNo());
        this.setRecordDetail(solitaireRecord.getRecordDetail());
        this.setTotalPrice(solitaireRecord.getTotalPrice());
    }

}
