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

package com.mds.group.purchase.article.model;

import com.mds.group.purchase.article.valid.LeaveGroup;
import com.mds.group.purchase.article.valid.ReplyGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * The type Leave word.
 *
 * @author Created by wx on 2018/06/07.
 */
@Data
public class LeaveWord {

    @Id
    @ApiModelProperty(value = "留言id")
    private String leaveWordId;

    @ApiModelProperty(value = "文章id")
    private String articleId;

    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空")
    private Long wxuserId;

    @ApiModelProperty(value = "用户头像")
    private String wxuserImg;

    @ApiModelProperty(value = "用户名称")
    private String wxuserName;

    @ApiModelProperty(value = "留言时间")
    private String leaveTime;

    @ApiModelProperty(value = "留言信息")
    @NotBlank(groups = LeaveGroup.class, message = "留言信息不能为空")
    private String leaveInfo;

    @ApiModelProperty(value = "回复时间")
    private String replyTime;

    @ApiModelProperty(value = "回复内容")
    @NotBlank(groups = ReplyGroup.class, message = "回复内容不能为空")
    private String replyInfo;

    @ApiModelProperty(value = "回复状态(0-未回复 1-已回复)")
    private Integer replyType;

    @ApiModelProperty(value = "是否精选 0-普通 1-精选")
    private Integer choiceness;

    @ApiModelProperty(value = "置顶状态(0未置顶- 1-已置顶)")
    private Integer sortType;

    @ApiModelProperty(value = "排序值")
    private Long sortTime;

    @ApiModelProperty(value = "模板Id")
    @NotBlank(message = "模板Id不能为空")
    private String appmodelId;

    @ApiModelProperty(value = "当前用户留言点赞状态")
    private Boolean laudOrNot;

    @ApiModelProperty(value = "点赞数")
    private Integer laud;

}
