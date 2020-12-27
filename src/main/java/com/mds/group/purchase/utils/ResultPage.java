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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * The type Result page.
 *
 * @param <T> the type parameter
 * @author pavawi *
 */
@Data
public class ResultPage<T> implements Serializable {

    private static final long serialVersionUID = -7037053583982683460L;
    @ApiModelProperty("返回的数组对象")
    private T list;

    @ApiModelProperty("总记录数")
    private Long totle;
}
