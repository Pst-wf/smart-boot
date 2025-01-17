package com.smart.file.dao;

import com.smart.entity.file.FileEntity;
import com.smart.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件中心
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Mapper
public interface FileDao extends BaseDao<FileEntity> {

}
