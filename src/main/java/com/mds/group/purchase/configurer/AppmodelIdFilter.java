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

import cn.hutool.core.util.HashUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * The type Appmodel id filter.
 *
 * @author pavawi
 */
public class AppmodelIdFilter implements Filter {

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("------------test appmodelId filter doFilter --------------");
        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            //取得http请求header中的appmodelId
            String appmodelId = req.getHeader("appmodelId");
            //如果该请求头没有包含appmodelId，表示为公共接口，则不过滤
            if (StringUtils.isNotBlank(appmodelId)) {
                System.out.println("appmodelId = " + appmodelId);
                //从redis缓存中获取该appmodelId对应的小程序开启关闭信息
                String key = GroupMallProperties.getProjectTokenPrefix();
                String hashKey = String.valueOf(HashUtil.bkdrHash(key.concat(":").concat(appmodelId)));
                Boolean isRun = (Boolean) redisTemplate.opsForHash().get(key, hashKey);
                //缓存中没有该appmodelId的开启关闭信息，不过滤
                if (isRun != null) {
                    if (isRun) {
                        System.out.println("通过了");
                    } else {
                        System.out.println("没通过");
                        throw new ServletException("服务已到期");
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
