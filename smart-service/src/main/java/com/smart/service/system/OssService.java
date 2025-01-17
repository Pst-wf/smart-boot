package com.smart.service.system;

import com.smart.entity.system.OssEntity;
import com.smart.mybatis.service.BaseService;

/**
 * 对象存储 Service
 *
 * @author wf
 * @since 2022-03-27 12:25:02
 */
public interface OssService extends BaseService<OssEntity> {

    /**
     * 启用/停用
     *
     * @param ossEntity 对象存储bean
     * @return boolean
     */
    boolean updateOssStatus(OssEntity ossEntity);

    /**
     * 获取当前配置
     *
     * @return OssEntity
     */
    OssEntity getCurrent();
}

