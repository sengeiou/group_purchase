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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;

/**
 * The type Article.
 *
 * @author Created by wx on 2018/06/07.
 */
@Data
public class Article {

    @Id
    @ApiModelProperty(value = "文章id")
    private String articleId;

    @ApiModelProperty(value = "文章封面")
    private String coverUrl;

    @ApiModelProperty(value = "文章标题")
    @NotBlank(message = "不能为空")
    private String articleTitle;

    @ApiModelProperty(value = "所属分类Id字符串")
    private String categoryIds;

    @ApiModelProperty(value = "视频")
    private String videoUrl;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

    @ApiModelProperty(value = "文章内容")
    private String centent;

    @ApiModelProperty(value = "点赞数量")
    private Integer laud;

    @ApiModelProperty(value = "排序值")
    private Integer sort;

    @ApiModelProperty(value = "阅读量")
    private Integer lookSum;

    @ApiModelProperty(value = "评论量")
    private Integer discussSum;

    @ApiModelProperty(value = "模板Id",hidden = true)
    private String appmodelId;

    @ApiModelProperty(value = "链接商品Id")
    private Long productId;

    @ApiModelProperty(value = "链接商品名称")
    private String productName;

    @ApiModelProperty(value = "所属分类名称字符串")
    private String categoryNames;

    /**
     * Gets centent.
     *
     * @return the centent
     */
    public String getCentent() {
        return centent.replaceAll("&amp;", "&");
    }

    /**
     * Sets centent.
     *
     * @param centent the centent
     */
    public void setCentent(String centent) {
        this.centent = centent.replaceAll("&amp;", "&");
    }
}
