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

package com.mds.group.purchase.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.user.model.Wxuser;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * The type User util.
 *
 * @author whh
 */
@Component
public class UserUtil {

    @Resource
    private RedisTemplate<String, Wxuser> redisTemplate;

    /**
     * 生成mdsUnionId
     *
     * @param nickName the nick name
     * @return the string
     * @throws BadHanyuPinyinOutputFormatCombination the bad hanyu pinyin output format combination
     */
    public static String generateMdsUnionId(String nickName) throws BadHanyuPinyinOutputFormatCombination {
        return RandomUtil.randomString(12) + PinYinUtil.getPingYin(nickName);
    }

    /**
     * Generate mds union id string.
     *
     * @return the string
     */
    public static String generateMdsUnionId() {
        return RandomUtil.randomString(12);
    }

    /**
     * 更新用户缓存中的数据
     *
     * @param wxuser the wxuser
     * @param type   the type
     */
    public void depositCache(Wxuser wxuser, Integer type) {
        if (wxuser != null) {
            //当前日期
            DateTime currentDay = DateUtil.date();
            //结束日期
            DateTime endOfDay = DateUtil.endOfDay(DateUtil.date());
            redisTemplate.opsForValue()
                    .set(GroupMallProperties.getRedisPrefix().concat(wxuser.getAppmodelId()).concat(RedisPrefix.USER)
                                    .concat(wxuser.getMiniOpenId()), wxuser, endOfDay.getTime() - currentDay.getTime(),
                            TimeUnit.MILLISECONDS);
            if (wxuser.getUnionId() != null && type.equals(1)) {
                redisTemplate.opsForValue().set(GroupMallProperties.getRedisPrefix().concat(wxuser.getAppmodelId())
                                .concat(RedisPrefix.USERUNION).concat(wxuser.getUnionId()), wxuser,
                        endOfDay.getTime() - currentDay.getTime(), TimeUnit.MILLISECONDS);
            }
            if (wxuser.getMpOpenid() != null && type.equals(2)) {
                redisTemplate.opsForValue()
                        .set(GroupMallProperties.getRedisPrefix().concat(wxuser.getAppmodelId()).concat(RedisPrefix.MPOPENID)
                                        .concat(wxuser.getMpOpenid()), wxuser, endOfDay.getTime() - currentDay.getTime(),
                                TimeUnit.MILLISECONDS);
            }
        }
    }


}
