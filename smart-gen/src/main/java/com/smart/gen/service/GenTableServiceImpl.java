package com.smart.gen.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.smart.common.utils.ListUtil;
import com.smart.common.utils.StringUtil;
import com.smart.common.utils.TreeUtil;
import com.smart.entity.gen.GenTableColumnEntity;
import com.smart.entity.gen.GenTableEntity;
import com.smart.gen.dao.GenTableColumnDao;
import com.smart.gen.dao.GenTableDao;
import com.smart.gen.utils.GenUtils;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.gen.GenTableColumnService;
import com.smart.service.gen.GenTableService;
import com.smart.service.system.MenuService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成表 ServiceImpl
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Service("genTableService")
@Transactional(rollbackFor = Exception.class)
public class GenTableServiceImpl extends BaseServiceImpl<GenTableDao, GenTableEntity> implements GenTableService {

    @Resource
    GenTableColumnDao genTableColumnDao;
    @Autowired
    GenTableColumnService genTableColumnService;
    @Autowired
    MenuService menuService;

    @Override
    public byte[] generatorCode(List<String> ids) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (String id : ids) {
            //查询表信息
            GenTableEntity table = baseMapper.selectById(id);
            if (table == null) {
                throw new SmartException("表不存在");
            }
            //查询列信息
            List<GenTableColumnEntity> columns = genTableColumnDao.selectList(new LambdaQueryWrapper<GenTableColumnEntity>().eq(GenTableColumnEntity::getTableId, table.getId()).orderByAsc(GenTableColumnEntity::getColumnSort));
            if (StringUtil.isNotBlank(table.getMenuId())) {
                table.setMenu(menuService.getById(table.getMenuId()));
            }
            //生成代码
            GenUtils.generatorCode(table, columns, zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    @Override
    public List<GenTableEntity> findTables() {
        Configuration config = GenUtils.getConfig();
        String mainPath = StringUtils.isBlank(config.getString("mainPath")) ? "com.smart" : config.getString("mainPath");
        String moduleName = StringUtils.isBlank(config.getString("moduleName")) ? "business" : config.getString("moduleName");
        String author = StringUtils.isBlank(config.getString("author")) ? "wf" : config.getString("author");
        List<GenTableEntity> tables = baseMapper.findTables();
        for (GenTableEntity genTableEntity : tables) {
            genTableEntity.setClassName(GenUtils.tableToJava(genTableEntity.getTableName(), config.getStringArray("tablePrefix")));
            genTableEntity.setPackageName(mainPath);
            genTableEntity.setFunctionName(genTableEntity.getClassName());
            genTableEntity.setFunctionAuthor(author);
            genTableEntity.setModuleName(moduleName);
        }
        return tables;
    }

    /**
     * 保存之后处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    @Override
    public void afterSaveOrUpdate(GenTableEntity entity, boolean isAdd) {
        if (isAdd) {
            ListUtil.forEach(entity.getColumns(), (index, column) -> {
                column.setId(null);
                column.setColumnSort(new BigDecimal(index + 1));
                column.setTableId(entity.getId());
            });
            if (!entity.getColumns().isEmpty()) {
                genTableColumnService.saveBatch(entity.getColumns());
            }
        } else {
            if (StringUtil.isBlank(entity.getMenuId())) {
                LambdaUpdateChainWrapper<GenTableEntity> updateChainWrapper = new LambdaUpdateChainWrapper<>(baseMapper);
                updateChainWrapper.set(GenTableEntity::getMenuId, null).eq(GenTableEntity::getId, entity.getId()).update();
            }

            // 先清空所有字段的查询方式以及字典编码
            LambdaUpdateChainWrapper<GenTableColumnEntity> updateChainWrapper = new LambdaUpdateChainWrapper<>(genTableColumnDao);
            updateChainWrapper
                    .set(GenTableColumnEntity::getQueryType, null)
                    .set(GenTableColumnEntity::getDictCode, null)
                    .eq(GenTableColumnEntity::getTableId, entity.getId()).update();

            ListUtil.forEach(entity.getColumns(), (index, column) -> {
                column.setColumnSort(new BigDecimal(index + 1));
            });
            genTableColumnService.updateBatchById(entity.getColumns());
        }
    }


    /**
     * 预览代码
     *
     * @param id 表编号
     * @return List
     */
    @Override
    public List<Map<String, Object>> previewCode(String id) {
        //查询表信息
        GenTableEntity table = baseMapper.selectById(id);
        if (table == null) {
            throw new SmartException("表不存在");
        }
        //查询列信息
        List<GenTableColumnEntity> columns = genTableColumnDao.selectList(new LambdaQueryWrapper<GenTableColumnEntity>().eq(GenTableColumnEntity::getTableId, table.getId()).orderByAsc(GenTableColumnEntity::getColumnSort));
        if (StringUtil.isNotBlank(table.getMenuId())) {
            table.setMenu(menuService.getById(table.getMenuId()));
        }
        List<Map<String, Object>> maps = GenUtils.previewCode(table, columns);
        return TreeUtil.buildTreeMap(maps, "id", "pId");
    }
}