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

import com.mds.group.purchase.article.valid.ReplyGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * The type Reply vo.
 *
 * @author Created by wx on 2018/06/07.
 */
@Data
public class ReplyVO {

    @ApiModelProperty(value = "留言id")
    @NotBlank(message = "留言id不能为空")
    private String leaveWordId;

    @NotBlank(groups = ReplyGroup.class, message = "留言id不能为空")
    @ApiModelProperty(value = "回复内容")
    private String replyInfo;

    @ApiModelProperty(value = "精选状态")
    private Integer choiceness;

    @ApiModelProperty(value = "置顶状态")
    private Integer sortType;

}
