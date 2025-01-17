package com.smart.common.utils;

import com.github.pagehelper.PageInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * 手动分页转化成pageInfo
 *
 * @author wf
 * @since 2023-03-28 17:28:00
 */
public class PageInfoUtils {

    public static <T> PageInfo<T> getPageInfo(List<T> arrayList, int current, int size) {
        //实现list分页
        int pageStart = current == 1 ? 0 : (current - 1) * size;
        int pageEnd = Math.min(arrayList.size(), size * current);
        List<T> pageResult = new LinkedList<T>();
        if (arrayList.size() > pageStart) {
            pageResult = arrayList.subList(pageStart, pageEnd);
        }
        PageInfo<T> pageInfo = new PageInfo<T>(pageResult);
        //获取PageInfo其他参数
        pageInfo.setTotal(arrayList.size());
        long endRow = pageInfo.getEndRow() == 0 ? 0 : (long) (current - 1) * size + pageInfo.getEndRow() + 1;
        pageInfo.setEndRow(endRow);
        boolean hasNextPage = arrayList.size() > size * current;
        pageInfo.setHasNextPage(hasNextPage);
        boolean hasPreviousPage = current != 1;
        pageInfo.setHasPreviousPage(hasPreviousPage);
        pageInfo.setIsFirstPage(!hasPreviousPage);
        boolean isLastPage = arrayList.size() > size * (current - 1) && arrayList.size() <= size * current;
        pageInfo.setIsLastPage(isLastPage);
        int pages = arrayList.size() % size == 0 ? arrayList.size() / size : (arrayList.size() / size) + 1;
        pageInfo.setNavigateLastPage(pages);
        int[] navigatePageNums = new int[pages];
        for (int i = 1; i < pages; i++) {
            navigatePageNums[i - 1] = i;
        }
        pageInfo.setNavigatepageNums(navigatePageNums);
        int nextPage = current < pages ? current + 1 : 0;
        pageInfo.setNextPage(nextPage);
        pageInfo.setPageNum(current);
        pageInfo.setPageSize(size);
        pageInfo.setPages(pages);
        pageInfo.setPrePage(current - 1);
        pageInfo.setSize(pageInfo.getList().size());
        int starRow = arrayList.size() < size * current ? 1 + size * (current - 1) : 0;
        pageInfo.setStartRow(starRow);
        return pageInfo;
    }

    public static <T> PageInfo<T> getPageInfo(List<T> pageResult, int current, int size, int total) {
        PageInfo<T> pageInfo = new PageInfo<>(pageResult);
        //获取PageInfo其他参数
        pageInfo.setTotal(total);
        long endRow = pageInfo.getEndRow() == 0 ? 0 : (long) (current - 1) * size + pageInfo.getEndRow() + 1;
        pageInfo.setEndRow(endRow);
        boolean hasNextPage = total > size * current;
        pageInfo.setHasNextPage(hasNextPage);
        boolean hasPreviousPage = current != 1;
        pageInfo.setHasPreviousPage(hasPreviousPage);
        pageInfo.setIsFirstPage(!hasPreviousPage);
        boolean isLastPage = total > size * (current - 1) && total <= size * current;
        pageInfo.setIsLastPage(isLastPage);
        int pages = total % size == 0 ? total / size : (total / size) + 1;
        pageInfo.setNavigateLastPage(pages);
        int[] navigatePageNums = new int[pages];
        for (int i = 1; i < pages; i++) {
            navigatePageNums[i - 1] = i;
        }
        pageInfo.setNavigatepageNums(navigatePageNums);
        int nextPage = current < pages ? current + 1 : 0;
        pageInfo.setNextPage(nextPage);
        pageInfo.setPageNum(current);
        pageInfo.setPageSize(size);
        pageInfo.setPages(pages);
        pageInfo.setPrePage(current - 1);
        pageInfo.setSize(pageInfo.getList().size());
        int starRow = total < size * current ? 1 + size * (current - 1) : 0;
        pageInfo.setStartRow(starRow);
        return pageInfo;
    }
}