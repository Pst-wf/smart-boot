package com.smart.system.dao;

import com.smart.mybatis.dao.BaseDao;
import com.smart.entity.system.DictEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字典
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Mapper
public interface DictDao extends BaseDao<DictEntity> {

}
