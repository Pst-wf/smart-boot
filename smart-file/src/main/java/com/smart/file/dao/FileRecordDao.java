package com.smart.file.dao;

import com.smart.entity.file.FileRecordEntity;
import com.smart.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;


/**
 * 文件中心
 *
 * @author wf
 * @since 2023-04-26 16:02:37
 */
@Mapper
public interface FileRecordDao extends BaseDao<FileRecordEntity> {

}
