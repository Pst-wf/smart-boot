package com.smart.mybatis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import java.util.Collection;
import java.util.List;

/**
 * Service
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
public interface BaseService<T> extends IService<T> {
    /**
     * 分页查询
     *
     * @param entity bean实体
     * @return Page
     */
    Page<T> findPage(T entity);

    /**
     * 分页查询 (返回pageInfo)
     *
     * @param entity bean实体
     * @return PageInfo
     */
    PageInfo<T> findPageInfo(T entity);

    /**
     * 查询
     *
     * @param entity bean实体
     * @return List
     */
    List<T> findList(T entity);

    /**
     * 查询一条数据
     *
     * @param id 主键ID
     * @return 实体
     */
    T get(String id);

    /**
     * 新增
     *
     * @param entity bean实体
     * @return 实体
     */
    T saveEntity(T entity);

    /**
     * 修改
     *
     * @param entity bean实体
     * @return 实体
     */
    T updateEntity(T entity);

    /**
     * 删除
     *
     * @param entity bean实体
     * @return boolean
     */
    boolean delete(T entity);

    /**
     * 物理删除
     *
     * @param entity 参数
     * @return boolean
     */
    boolean realDelete(T entity);

    /**
     * 通过ID删除数据
     *
     * @param id 主键
     * @return boolean
     */
    boolean deleteById(String id);

    /**
     * 批量新增或修改
     *
     * @param list 参数
     * @return boolean
     */
    boolean saveOrUpdateBatch(Collection<T> list);

    /**
     * 通过IDS获取集合
     *
     * @param ids 主键IDS
     * @return List
     */
    List<T> getListByIds(String ids);

    /**
     * 通过IDS集合获取集合
     *
     * @param idList 主键IDS集合
     * @return List
     */
    List<T> getListByIdList(Collection<String> idList);

}

