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

package com.mds.group.purchase.shop.dao;

import com.mds.group.purchase.core.Mapper;
import com.mds.group.purchase.shop.model.Footer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * The interface Footer mapper.
 *
 * @author Created by medusawx on 2018/06/04.
 */
public interface FooterMapper extends Mapper<Footer> {

    /**
     * 根据appmodelId底部导航
     *
     * @param appmodelId the appmodel id
     * @return List<Footer>  list
     */
    List<Footer> findByAppmoedelId(String appmodelId);

    /**
     * List by ids list.
     *
     * @param footerIdList the footer id list
     * @return the list
     */
    List<Footer> listByIds(@Param("footerIdList") List<Integer> footerIdList);
}