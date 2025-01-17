package com.smart.gen.service;

import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.gen.dao.GenDemoDao;
import com.smart.entity.gen.GenDemoEntity;
import com.smart.service.gen.GenDemoService;
import com.smart.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 生成案例 ServiceImpl
 *
 * @author wf
 * @since 2024-08-19 00:28:40
 */
@Service("genDemoService")
@Transactional(rollbackFor = Exception.class)
public class GenDemoServiceImpl extends BaseServiceImpl<GenDemoDao, GenDemoEntity> implements GenDemoService {

    @Autowired
    FileService fileService;

    /**
     * 详情
     *
     * @param id 主键ID
     * @return bean
     */
    @Override
    public GenDemoEntity get(String id) {
      GenDemoEntity entity = super.get(id);
      if(entity != null){
        entity.setTableColumn12List(fileService.getListByIds(entity.getTableColumn12()));
      }
      return entity;
    }

}