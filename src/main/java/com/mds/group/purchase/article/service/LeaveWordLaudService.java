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

package com.mds.group.purchase.article.service;

import com.mds.group.purchase.article.model.LeaveWordLaud;

/**
 * The interface Leave word laud service.
 *
 * @author Created by wx on 2018/06/07.
 */
public interface LeaveWordLaudService {

    /***
     * 根据用户id和留言id查询留言是否点赞
     *
     * @param wxuserId the wxuser id
     * @param leaveWordId the leave word id
     * @return LeaveWordLaud by wxuser id and leave word id
     */
    LeaveWordLaud getByWxuserIdAndLeaveWordId(Long wxuserId, String leaveWordId);

    /**
     * 更新或保存文章留言点赞
     *
     * @param leaveWordLaudNew the leave word laud new
     */
    void save(LeaveWordLaud leaveWordLaudNew);

}
