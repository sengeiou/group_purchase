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

package com.mds.group.purchase.article.controller;


import com.mds.group.purchase.article.model.LeaveWord;
import com.mds.group.purchase.article.service.LeaveWordService;
import com.mds.group.purchase.article.valid.LeaveGroup;
import com.mds.group.purchase.article.valid.ReplyGroup;
import com.mds.group.purchase.article.vo.ArticleSelectVO;
import com.mds.group.purchase.article.vo.LeaveWordLaudVO;
import com.mds.group.purchase.article.vo.LeaveWordVO;
import com.mds.group.purchase.article.vo.ReplyVO;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.core.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


/**
 * 留言
 *
 * @author Created by wx on 2018/06/07.
 */
@Api(tags = "所有接口")
@Validated
@RestController
@RequestMapping("/leaveWord")
public class LeaveWordController {

    @Resource
    private LeaveWordService leaveWordService;

    /**
     * Add result.
     *
     * @param leaveWord the leave word
     * @return the result
     */
    @ApiOperation(value = "留言添加", tags = "添加接口")
    @PostMapping("/v1/add")
    public Result add(@RequestBody @Validated({LeaveGroup.class}) LeaveWord leaveWord) {
        LeaveWord result = leaveWordService.save(leaveWord);
        if (result != null) {
            return ResultGenerator.genSuccessResult("留言成功");
        } else {
            return ResultGenerator.genFailResult("保存失败");
        }
    }

    /**
     * Delete result.
     *
     * @param leaveWordId the leave word id
     * @param articleId   the article id
     * @return the result
     */
    @ApiOperation(value = "留言删除", tags = "删除接口")
    @DeleteMapping("/v1/delete")
    public Result delete(
            @ApiParam(value = "留言id") @RequestParam @NotBlank String leaveWordId,
            @ApiParam(value = "文章id") @RequestParam @NotBlank String articleId) {
        leaveWordService.delete(leaveWordId, articleId);
        return ResultGenerator.genSuccessResult("删除成功");
    }

    /**
     * Update to choiceness result.
     *
     * @param replyVO the reply vo
     * @return the result
     */
    @ApiOperation(value = "设置留言是否精选", tags = "更新接口")
    @PutMapping("/v1/update")
    public Result updateToChoiceness(@RequestBody @Validated ReplyVO replyVO) {
        LeaveWord update = leaveWordService.updateToChoiceness(replyVO);
        if (update == null) {
            return ResultGenerator.genFailResult("更新失败");
        }
        return ResultGenerator.genSuccessResult();
    }

    /**
     * Delete choiceness result.
     *
     * @param leaveWordId the leave word id
     * @return the result
     */
    @ApiOperation(value = "删除回复", tags = "更新接口")
    @DeleteMapping("/v1/reply/delete")
    public Result deleteChoiceness(@ApiParam(value = "留言id") @RequestParam @NotBlank String leaveWordId) {
        LeaveWord update = leaveWordService.deleteChoiceness(leaveWordId);
        if (update == null) {
            return ResultGenerator.genFailResult("更新失败");
        }
        return ResultGenerator.genSuccessResult();
    }

    /**
     * Reply choiceness result.
     *
     * @param replyVO the reply vo
     * @return the result
     */
    @ApiOperation(value = "回复留言", tags = "更新接口")
    @PutMapping("/v1/reply")
    public Result replyChoiceness(@RequestBody @Validated({ReplyGroup.class}) ReplyVO replyVO) {
        LeaveWord update = leaveWordService.replyChoiceness(replyVO.getReplyInfo(), replyVO.getLeaveWordId());
        if (update == null) {
            return ResultGenerator.genFailResult("更新失败");
        }
        return ResultGenerator.genSuccessResult();
    }

    /**
     * Sort result.
     *
     * @param replyVO the reply vo
     * @return the result
     */
    @ApiOperation(value = "留言置顶/取消", tags = "更新接口")
    @PutMapping("/v1/sort")
    public Result sort(@RequestBody @Valid ReplyVO replyVO) {
        leaveWordService.sort(replyVO);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * Select later result.
     *
     * @param articleId the article id
     * @return the result
     */
    @ApiOperation(value = "文章留言查询后台", tags = "查询接口")
    @GetMapping("/v1/selectLater")
    public Result<List<LeaveWord>> selectLater(@NotBlank @RequestParam @ApiParam(value = "文章id") String articleId) {
        List<LeaveWord> leaveWords = leaveWordService.findAll(articleId);
        return ResultGenerator.genSuccessResult(leaveWords);
    }

    /**
     * Select before result.
     *
     * @param articleSelectVO the article select vo
     * @return the result
     */
    @ApiOperation(value = "小程序端文章留言查询", tags = "查询接口")
    @GetMapping("/v1/selectBefore")
    public Result<Page<LeaveWord>> selectBefore(@Valid ArticleSelectVO articleSelectVO) {
        Page<LeaveWord> leaveWords = leaveWordService
                .getByChoicenessType(articleSelectVO.getWxuserId(), articleSelectVO.getArticleId(),
                        articleSelectVO.getPageNum(), articleSelectVO.getPageSize());
        return ResultGenerator.genSuccessResult(leaveWords);
    }

    /**
     * Select mine result.
     *
     * @param articleSelectVO the article select vo
     * @return the result
     */
    @ApiOperation(value = "查询用户自己留言", tags = "查询接口")
    @GetMapping("/v1/selectMine")
    public Result<Page<LeaveWord>> selectMine(@Valid ArticleSelectVO articleSelectVO) {
        Page<LeaveWord> words = leaveWordService
                .findByWxuserIdAndArticleId(articleSelectVO.getWxuserId(), articleSelectVO.getArticleId(),
                        articleSelectVO.getPageNum(), articleSelectVO.getPageSize());
        return ResultGenerator.genSuccessResult(words);
    }

    /**
     * Find leave word result.
     *
     * @param findWord   the find word
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "根据输入条件查询留言", tags = "查询接口")
    @GetMapping("/v1/findLeaveWord")
    public Result<List<LeaveWordVO>> findLeaveWord(
            @ApiParam(value = "查询条件") @RequestParam @NotNull(message = "条件不能为空") String findWord,
            @RequestParam @NotNull(message = "appmodelId不能为空")
            @Size(max = 27, min = 27, message = "appmodelId错误") String appmodelId) {
        List<LeaveWordVO> leaveWords = leaveWordService.findLeaveWord(findWord, appmodelId);
        return ResultGenerator.genSuccessResult(leaveWords);
    }

    /**
     * Update leave word laud result.
     *
     * @param leaveWordLaudVO the leave word laud vo
     * @return the result
     */
    @ApiOperation(value = "留言点赞", tags = "更新接口")
    @PutMapping("/v1/updateLeaveWordLaud")
    public Result updateLeaveWordLaud(@RequestBody @Valid LeaveWordLaudVO leaveWordLaudVO) {
        leaveWordService.updateLeaveWordLaud(leaveWordLaudVO);
        return ResultGenerator.genSuccessResult();
    }
}
