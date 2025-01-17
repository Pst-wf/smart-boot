package com.smart.system.dao;

import com.smart.entity.system.ConfigEntity;
import com.smart.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;


/**
 * 系统配置
 *
 * @author wf
 * @since 2023-05-30 15:29:15
 */
@Mapper
public interface ConfigDao extends BaseDao<ConfigEntity> {
    /**
     * 获取端
     *
     * @return java.util.LinkedHashMap<java.lang.String, java.lang.Object>
     */
    List<LinkedHashMap<String, Object>> getClientByClientId(@Param("clientId") String clientId);

    void updateTokenExpirationByClientId(@Param("key") String key, @Param("time") Integer time, @Param("clientId") String clientId);
}
