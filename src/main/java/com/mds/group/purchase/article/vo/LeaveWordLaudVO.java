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

package com.mds.group.purchase.article.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * The type Leave word laud vo.
 *
 * @author Created by wx on 2018/06/07.
 */
@Data
public class LeaveWordLaudVO {

    @ApiModelProperty(value = "用户id")
    @NotNull
    private Long wxuserId;

    @ApiModelProperty(value = "留言id")
    @NotBlank
    private String leaveWordId;

}
