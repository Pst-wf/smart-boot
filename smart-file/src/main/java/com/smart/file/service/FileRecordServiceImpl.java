package com.smart.file.service;


import com.smart.entity.file.FileRecordEntity;
import com.smart.file.dao.FileRecordDao;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.file.FileRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文件中心 ServiceImpl
 *
 * @author wf
 * @since 2023-04-26 16:02:37
 */
@Service("fileRecordService")
@Transactional(rollbackFor = Exception.class)
public class FileRecordServiceImpl extends BaseServiceImpl<FileRecordDao, FileRecordEntity> implements FileRecordService {

}