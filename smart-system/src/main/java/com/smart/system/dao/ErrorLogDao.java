package com.smart.system.dao;

import com.smart.mybatis.dao.BaseDao;
import com.smart.entity.system.ErrorLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 错误日志
 *
 * @author wf
 * @since 2022-07-27 14:27:15
 */
@Mapper
public interface ErrorLogDao extends BaseDao<ErrorLogEntity> {

}
