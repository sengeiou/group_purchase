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

package com.mds.group.purchase.goods.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.mds.group.purchase.constant.Common;
import com.mds.group.purchase.constant.HandType;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.exception.GlobalException;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.goods.dao.GoodsClassMapper;
import com.mds.group.purchase.goods.model.Goods;
import com.mds.group.purchase.goods.model.GoodsClass;
import com.mds.group.purchase.goods.model.GoodsClassMapping;
import com.mds.group.purchase.goods.result.GoodsClassResult;
import com.mds.group.purchase.goods.service.GoodsClassMappingService;
import com.mds.group.purchase.goods.service.GoodsClassService;
import com.mds.group.purchase.goods.service.GoodsService;
import com.mds.group.purchase.goods.vo.ClassTwoVo;
import com.mds.group.purchase.goods.vo.GoodsClassVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * The type Goods class service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@Service
public class GoodsClassServiceImpl extends AbstractService<GoodsClass> implements GoodsClassService {

    @Resource
    private GoodsService goodsService;
    @Resource
    private GoodsClassMapper goodsClassDao;
    @Resource
    private GoodsClassMappingService goodsClassMappingService;

    @Override
    public List<GoodsClassResult> getGoodsClasses(String appmodelId) {
        return goodsClassDao.selectGoodsClassesByAppmodelId(appmodelId);
    }

    @Override
    public boolean isExists(Long goodsClassId, String appmodelId) {
        return goodsClassDao.isExists(goodsClassId, appmodelId) == 1;
    }

    @Override
    public boolean goodsClassIsExists(Long[] goodsClassIds, String appmodelId) {
        if (goodsClassIds == null || goodsClassIds.length <= 0) {
            throw new GlobalException(CodeMsg.NULL_PARAM);
        }
        for (Long goodsClassId : goodsClassIds) {
            if (goodsClassId == 0) {
                return true;
            }
            if (!isExists(goodsClassId, appmodelId)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delById(String goodsClassId) {
        List<Long> classIdList = Arrays.stream(goodsClassId.split(",")).map(Long::valueOf).collect(Collectors.toList());
        Condition condition = new Condition(GoodsClassMapping.class);
        condition.createCriteria().andIn("goodsClassId", classIdList);
        List<GoodsClassMapping> classMappings = goodsClassMappingService.findByCondition(condition);
        //排除掉已删除的商品
        if (CollectionUtil.isNotEmpty(classMappings)) {
            List<Long> goodsIds = classMappings.stream().map(GoodsClassMapping::getGoodsId)
                    .collect(Collectors.toList());
            List<Goods> byIds = goodsService.findByIdListNotDel(goodsIds);
            if (CollectionUtil.isNotEmpty(byIds)) {
                throw new ServiceException("分类下有商品,不能删除");
            }
        }
        //一、删除父id为此id的下级分类
        //1、得到所有下级分类，
        List<Long> list = goodsClassDao.selectIdByFatherId(classIdList);
        if (list != null) {
            list.addAll(classIdList);
            list = list.stream().distinct().collect(Collectors.toList());
            //2、删除所有属于该分类的商品的goodsclassMapping
            goodsClassMappingService.delGoodsClassMapping(list);
            goodsClassDao.delByFatherId(list);
        }
        //二、删除id对应分类
        goodsClassDao.delById(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void putGoodsClass(String appmodelId, GoodsClassVo classVo) {
        List<GoodsClass> goodsClasses = new ArrayList<>();
        if (classVo.getGoodsClassId() == -1) {
            //这个一级分类为新增
            GoodsClass goodsClass1 = new GoodsClass();
            goodsClass1.setAppmodelId(appmodelId);
            goodsClass1.setCreateTime(new Date());
            goodsClass1.setFatherId(0L);
            goodsClass1.setDelFlag(0);
            goodsClass1.setSort(0);
            goodsClass1.setGoodsClassName(classVo.getGoodsClassName());
            goodsClassDao.insertGoodsClass(goodsClass1);
            for (ClassTwoVo classTwo : classVo.getClassTwos()) {
                GoodsClass goodsClass2 = new GoodsClass();
                goodsClass2.setGoodsClassName(classTwo.getGoodsClassName());
                goodsClass2.setCreateTime(new Date());
                goodsClass2.setAppmodelId(appmodelId);
                goodsClass2.setFatherId(goodsClass1.getGoodsClassId());
                goodsClass2.setDelFlag(0);
                goodsClass2.setSort(classTwo.getSort());
                goodsClasses.add(goodsClass2);
            }
            goodsClassDao.insertList(goodsClasses);
            sort(1, goodsClass1.getGoodsClassId(), appmodelId);
        } else {
            //这个一级分类为已存在
            GoodsClass goodsClass1 = new GoodsClass();
            goodsClass1.setAppmodelId(appmodelId);
            goodsClass1.setFatherId(classVo.getFatherId());
            goodsClass1.setGoodsClassName(classVo.getGoodsClassName());
            goodsClass1.setGoodsClassId(classVo.getGoodsClassId());
            goodsClass1.setDelFlag(0);
            goodsClassDao.updateByPrimaryKeySelective(goodsClass1);
            for (ClassTwoVo classTwo : classVo.getClassTwos()) {
                GoodsClass goodsClass2 = new GoodsClass();
                goodsClass2.setGoodsClassName(classTwo.getGoodsClassName());
                goodsClass2.setFatherId(goodsClass1.getGoodsClassId());
                goodsClass2.setAppmodelId(appmodelId);
                goodsClass2.setSort(classTwo.getSort());
                goodsClass2.setGoodsClassName(classTwo.getGoodsClassName());
                goodsClass2.setDelFlag(0);
                if (classTwo.getGoodsClassId() == -1) {
                    //新增的二级分类
                    goodsClassDao.insert(goodsClass2);
                } else {
                    //修改的二级分类
                    goodsClass2.setGoodsClassId(classTwo.getGoodsClassId());
                    goodsClassDao.updateByPrimaryKeySelective(goodsClass2);
                }
            }
        }

    }

    @Override
    public List<GoodsClassResult> selectFirstClass(String appmodelId) {
        return goodsClassDao.selectGoodsClassesByAppmodelId(appmodelId);
    }

    @Override
    public List<GoodsClass> getGoodsClassesAndUnderClass(Long goodsClassId) {
        return goodsClassDao.selectGoodsClassesAndUnderClass(goodsClassId);
    }

    @Override
    public int sort(Integer handleType, Long goodsClassId, String appmodelId) {
        if (goodsClassId == null || goodsClassId <= 0) {
            throw new ServiceException("分类id错误");
        }
        GoodsClass goodsClass = goodsClassDao.selectByPrimaryKey(goodsClassId);
        if (goodsClass == null) {
            throw new ServiceException("无此分类");
        }
        Condition condition = new Condition(GoodsClass.class);
        condition.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("fatherId", goodsClass.getFatherId())
                .andEqualTo("delFlag", 0);
        condition.orderBy("sort").asc();
        List<GoodsClass> goodsClasses = goodsClassDao.selectByCondition(condition);
        for (int i = 0; i < goodsClasses.size(); i++) {
            if (goodsClasses.get(i).getGoodsClassId().equals(goodsClass.getGoodsClassId())) {
                if (handleType.equals(HandType.TOP)) {
                    goodsClasses.get(i).setSort(goodsClasses.get(0).getSort() - 1);
                }
                if (handleType.equals(HandType.UP)) {
                    goodsClasses.get(i).setSort(goodsClasses.get(i - 1).getSort() - 1);
                }
                if (handleType.equals(HandType.DOWN)) {
                    goodsClasses.get(i).setSort(goodsClasses.get(i + 1).getSort() + 1);
                }
                if (handleType.equals(HandType.FOOT)) {
                    goodsClasses.get(i).setSort(goodsClasses.get(goodsClasses.size() - 1).getSort() + 1);
                }
                break;
            }
        }
        goodsClasses.sort(Comparator.comparing(GoodsClass::getSort));
        for (int i = 0; i < goodsClasses.size(); i++) {
            goodsClasses.get(i).setSort(i);
            goodsClassDao.updateByPrimaryKeySelective(goodsClasses.get(i));
        }
        return handleType;
    }

    @Override
    public void sortNew(String classIds, String appmodelId) {
        List<String> classIdList = Arrays.asList(classIds.split(Common.REGEX));
        //更新分类的排序
        for (int i = 0; i < classIdList.size(); i++) {
            goodsClassDao.updateSortById(Long.valueOf(classIdList.get(i)), i + 1, appmodelId);
        }
    }

}
