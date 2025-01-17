package com.smart.system.dao;

import com.smart.mybatis.dao.BaseDao;
import com.smart.entity.system.LoginLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Mapper
public interface LoginLogDao extends BaseDao<LoginLogEntity> {

}