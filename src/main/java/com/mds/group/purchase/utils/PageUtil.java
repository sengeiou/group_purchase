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

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 分页
 *
 * @author shuke
 */
public class PageUtil {

    /**
     * Page util page info.
     *
     * @param <T>  the type parameter
     * @param page the page
     * @param size the size
     * @param list the list
     * @return the page info
     */
    public static <T> PageInfo<T> pageUtil(Integer page, Integer size, List<T> list) {
        page = page == null ? 0 : page;
        size = size == null ? 0 : size;
        if (list == null || list.size() == 0) {
            return new PageInfo<>();
        }
        //如果size==0 或者page ==0返回所有数据
        if (size == 0 || page == 0) {
            return new PageInfo<>(list);
        }
        int pages = (list.size() + size - 1) / size;
        int total = list.size();
        if (page > pages) {
            return new PageInfo<>();
        }
        int fromIndex = (page - 1) * size;
        int toIndex = page * size > list.size() ? list.size() : page * size;
        PageInfo<T> pageInfo = new PageInfo<>(list.subList(fromIndex, toIndex));
        pageInfo.setTotal(total);
        pageInfo.setPages(pages);
        pageInfo.setPageNum(page);
        pageInfo.setHasNextPage(pageInfo.getPageNum() < pageInfo.getPages());
        if (pageInfo.isHasNextPage()) {
            pageInfo.setNextPage(page + 1);
        }
        return pageInfo;
    }

    /**
     * Page info page info.
     *
     * @param <T>      the type parameter
     * @param total    the total
     * @param page     the page
     * @param pageSize the page size
     * @param list     the list
     * @return the page info
     */
    public static <T> PageInfo<T> pageInfo(int total, int page, int pageSize, List<T> list) {
        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setList(list);
        pageInfo.setTotal(total);
        if (total == 0 || page <= 0 || pageSize <= 0) {
            return pageInfo;
        }
        //总页数
        int pages = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        if (page > pages) {
            return pageInfo;
        }
        //是否首页
        boolean isFirstPage = page == 1;
        //是否最后一页
        boolean isLastPage = page == pages;
        //是否有下一页
        boolean hasNextPage = page < pages;
        //前一页页码
        int prePage = isFirstPage ? 1 : page - 1;
        //后一页页码
        int nexPage = hasNextPage ? page + 1 : page;
        //当前页的数量
        int size = isLastPage ? total % pageSize : pageSize;
        pageInfo.setIsFirstPage(isFirstPage);
        pageInfo.setIsLastPage(isLastPage);
        pageInfo.setHasPreviousPage(isFirstPage);
        pageInfo.setPrePage(prePage);
        pageInfo.setNextPage(nexPage);
        pageInfo.setPages(pages);
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        pageInfo.setSize(size);
        pageInfo.setHasNextPage(hasNextPage);
        return pageInfo;
    }


}