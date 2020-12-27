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

package com.mds.group.purchase.article.service.impl;


import com.mds.group.purchase.article.dao.LeaveWordLaudRespository;
import com.mds.group.purchase.article.model.LeaveWordLaud;
import com.mds.group.purchase.article.service.LeaveWordLaudService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * The type Leave word laud service.
 *
 * @author Created by wx on 2018/06/07.
 */
@Service
public class LeaveWordLaudServiceImpl implements LeaveWordLaudService {

    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private LeaveWordLaudRespository leaveWordLaudRespository;

    @Override
    public LeaveWordLaud getByWxuserIdAndLeaveWordId(Long wxuserId, String leaveWordId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("wxuserId").is(wxuserId));
        query.addCriteria(Criteria.where("leaveWordId").is(leaveWordId));
        return mongoTemplate.findOne(query, LeaveWordLaud.class);
    }

    @Override
    public void save(LeaveWordLaud leaveWordLaudNew) {
        leaveWordLaudRespository.save(leaveWordLaudNew);
    }
}
