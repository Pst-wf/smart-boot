package com.smart.system.dao;

import com.smart.entity.system.UserConfigEntity;
import com.smart.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;


/**
 * 用户配置
 *
 * @author wf
 * @since 2024-11-05 10:18:28
 */
@Mapper
public interface UserConfigDao extends BaseDao<UserConfigEntity> {

}
