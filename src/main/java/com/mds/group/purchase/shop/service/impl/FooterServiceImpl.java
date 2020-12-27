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

package com.mds.group.purchase.shop.service.impl;

import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.shop.dao.FooterMapper;
import com.mds.group.purchase.shop.model.AppPage;
import com.mds.group.purchase.shop.model.Footer;
import com.mds.group.purchase.shop.service.AppPageService;
import com.mds.group.purchase.shop.service.FooterService;
import com.mds.group.purchase.shop.vo.FooterInfoVO;
import com.mds.group.purchase.utils.BeanMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * The type Footer service.
 *
 * @author Created by wx on 2018/06/04.
 */
@Service
public class FooterServiceImpl extends AbstractService<Footer> implements FooterService {

    @Resource
    private FooterMapper tFooterMapper;
    @Resource
    private AppPageService appPageService;

    @Override
    public List<FooterInfoVO> findByAppmoedelId(String appmodelId) {
        List<Footer> footers = tFooterMapper.findByAppmoedelId(appmodelId);
        String pageid = footers.stream().map(obj -> obj.getAppPageId().toString()).collect(Collectors.joining(","));
        if ("".equalsIgnoreCase(pageid)) {
            return new ArrayList<>();
        }
        Map<Integer, AppPage> appPageMap = appPageService.findByIds(pageid).stream()
                .collect(Collectors.toMap(AppPage::getAppPageId, v -> v));
        List<FooterInfoVO> footerInfoVOS = BeanMapper.mapList(footers, FooterInfoVO.class);
        footerInfoVOS.forEach(obj -> {
            AppPage appPage = appPageMap.get(obj.getAppPageId());
            obj.setPagePath(appPage.getPagePath());
        });
        footerInfoVOS.sort(Comparator.comparing(FooterInfoVO::getSort));
        return footerInfoVOS;
    }

    @Override
    public int updateSort(String footerIds) {
        List<Integer> footerIdList =
                Arrays.stream(footerIds.split(",")).map(Integer::valueOf).collect(Collectors.toList());
        List<Footer> footers = tFooterMapper.listByIds(footerIdList);
        if (1 == footers.get(0).getAppPageId() && 5 == footers.get(footers.size() - 1).getAppPageId()) {
            int index = 1;
            for (Footer footer : footers) {
                footer.setSort(index++);
                this.update(footer);
            }
            return 1;
        } else {
            return 0;
        }

    }
}
