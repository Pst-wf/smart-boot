package com.smart.job.dao;

import com.smart.entity.job.JobRegistryEntity;
import com.smart.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 任务注册
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Mapper
public interface JobRegistryDao extends BaseDao<JobRegistryEntity> {

    List<String> findDead(@Param("timeout") int timeout, @Param("nowTime") Date nowTime);

    void removeDead(@Param("ids") List<String> ids);

    List<JobRegistryEntity> findAll(@Param("timeout") int timeout, @Param("nowTime") Date nowTime);


    int registryUpdate(@Param("registryGroup") String registryGroup,
                       @Param("registryKey") String registryKey,
                       @Param("registryValue") String registryValue,
                       @Param("updateTime") Date updateTime);

    int registrySave(@Param("registryGroup") String registryGroup,
                     @Param("registryKey") String registryKey,
                     @Param("registryValue") String registryValue,
                     @Param("updateTime") Date updateTime);

    int registryDelete(@Param("registryGroup") String registryGroup,
                       @Param("registryKey") String registryKey,
                       @Param("registryValue") String registryValue);

}
