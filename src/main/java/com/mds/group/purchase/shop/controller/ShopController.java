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

package com.mds.group.purchase.shop.controller;

import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.shop.model.AppPage;
import com.mds.group.purchase.shop.model.Shop;
import com.mds.group.purchase.shop.model.ShopFunction;
import com.mds.group.purchase.shop.model.Style;
import com.mds.group.purchase.shop.service.AppPageService;
import com.mds.group.purchase.shop.service.ShopFunctionService;
import com.mds.group.purchase.shop.service.ShopService;
import com.mds.group.purchase.shop.service.StyleService;
import com.mds.group.purchase.shop.vo.GeneralSituationVO;
import com.mds.group.purchase.shop.vo.ShopCreateUpdateVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * The type Shop controller.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@RestController
@RequestMapping("/shop")
@Api(tags = "所有接口")
public class ShopController {

    @Resource
    private ShopService shopService;
    @Resource
    private StyleService styleService;
    @Resource
    private AppPageService appPageService;
    @Resource
    private ShopFunctionService shopFunctionService;

    /**
     * Find shop result.
     *
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "查询店铺信息", tags = "查询接口")
    @GetMapping("/v1/info")
    public Result<Shop> findShop(
            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId) {
        Shop shopInfo = shopService.getByAppmodelId(appmodelId);
        return Result.success(shopInfo);
    }

    /**
     * Save or update shop result.
     *
     * @param shop       the shop
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "保存或更新店铺信息", tags = "添加接口")
    @PostMapping("/v1/edit/info")
    public Result saveOrUpdateShop(@RequestBody Shop shop,
                                   @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27,
                                           message = "商品标示错误") String appmodelId) {
        shop.setAppmodelId(appmodelId);
        shopService.saveOrUpdate(shop);
        return Result.success("保存信息成功");
    }


    /**
     * Shop create update setting result.
     *
     * @param shopCreateUpdateVO the shop create update vo
     * @param appmodelId         the appmodel id
     * @return the result
     */
    @PostMapping("/v1/edit/setting")
    @ApiOperation(value = "保存或更新店铺设置", tags = "添加接口")
    public Result shopCreateUpdateSetting(@RequestBody @Valid ShopCreateUpdateVO shopCreateUpdateVO,
                                          @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min =
                                                  27, message = "商品标示错误") String appmodelId) {
        shopCreateUpdateVO.setAppmodelId(appmodelId);
        int num = shopFunctionService.shopCreateUpdate(shopCreateUpdateVO);
        if (num == 0) {
            return Result.error(new CodeMsg("无修改操作"));
        }
        return Result.success();
    }

    /**
     * Detail result.
     *
     * @param appmodelId the appmodel id
     * @return the result
     */
    @GetMapping("/v1/setting/info")
    @ApiOperation(value = "获取店铺设置", tags = "查询接口")
    public Result<ShopCreateUpdateVO> detail(
            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId) {
        ShopCreateUpdateVO shopCreateUpdateVO = shopFunctionService.findByAppmodelId(appmodelId);
        return Result.success(shopCreateUpdateVO);
    }

    /**
     * Styles result.
     *
     * @param appmodelId the appmodel id
     * @return the result
     */
    @GetMapping("/v1/styles")
    @ApiOperation(value = "获取店铺风格", tags = "查询接口")
    public Result<List<Style>> styles(
            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId) {
        List<Style> all = styleService.findAll();
        ShopFunction shopFunction = shopFunctionService.findBy("appmodelId", appmodelId);
        all.forEach(obj -> obj.setDef(obj.getStyleId().equals(shopFunction.getShopStyleId())));
        return Result.success(all);
    }

    /**
     * Page detail result.
     *
     * @return the result
     */
    @GetMapping("/v1/page/info")
    @ApiOperation(value = "查询页面", tags = "查询接口")
    public Result<List<AppPage>> pageDetail() {
        List<AppPage> appPage = appPageService.findAll();
        return Result.success(appPage);
    }

    /**
     * General situation result.
     *
     * @param appmodelId the appmodel id
     * @return the result
     */
    @GetMapping("/v1/general/situation")
    @ApiOperation(value = "后台首页概况", tags = "查询接口")
    public Result<GeneralSituationVO> generalSituation(
            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId) {
        GeneralSituationVO generalSituation = shopService.generalSituation(appmodelId);
        return Result.success(generalSituation);
    }

}
