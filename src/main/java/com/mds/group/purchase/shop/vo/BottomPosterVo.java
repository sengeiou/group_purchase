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

package com.mds.group.purchase.shop.vo;

import com.mds.group.purchase.shop.model.BottomPoster;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 底部海报参数类
 *
 * @author shuke
 */
@Data
public class BottomPosterVo {

    @NotNull
    private Long id;

    /**
     * 海报链接
     */
    @NotBlank
    private String posterUrl;

    /**
     * 海报链接的电话号
     */
    @NotBlank
    private String phone;

    private String appmodelId;

    /**
     * 开启状态（1：开启，0：关闭）
     */
    @NotBlank
    private Integer status;

    /**
     * Vo to bottom poster bottom poster.
     *
     * @return the bottom poster
     */
    public BottomPoster voToBottomPoster() {
        BottomPoster bottomPoster = new BottomPoster();
        bottomPoster.setAppmodelId(appmodelId);
        bottomPoster.setId(id);
        bottomPoster.setPhone(phone);
        bottomPoster.setPosterUrl(posterUrl);
        bottomPoster.setStatus(status);
        return bottomPoster;
    }
}
