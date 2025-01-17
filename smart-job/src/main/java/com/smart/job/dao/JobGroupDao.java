package com.smart.job.dao;

import com.smart.entity.job.JobGroupEntity;
import com.smart.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 执行器
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Mapper
public interface JobGroupDao extends BaseDao<JobGroupEntity> {

    List<JobGroupEntity> findByAddressType(@Param("addressType") int addressType);

    /**
     * 修改状态
     *
     * @param onlineStatus 状态
     * @param appName      appName
     */
    void changeStatus(@Param("onlineStatus") String onlineStatus, @Param("appName") String appName);
}
