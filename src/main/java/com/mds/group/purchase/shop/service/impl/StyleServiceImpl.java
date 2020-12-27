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
import com.mds.group.purchase.shop.dao.StyleMapper;
import com.mds.group.purchase.shop.model.Style;
import com.mds.group.purchase.shop.service.StyleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * The type Style service.
 *
 * @author CodeGenerator
 * @date 2018 /12/13
 */
@Service
public class StyleServiceImpl extends AbstractService<Style> implements StyleService {

    @Resource
    private StyleMapper tStyleMapper;

}
