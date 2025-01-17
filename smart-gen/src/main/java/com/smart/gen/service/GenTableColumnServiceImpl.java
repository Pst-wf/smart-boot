package com.smart.gen.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.smart.entity.gen.GenTableColumnEntity;
import com.smart.gen.dao.GenTableColumnDao;
import com.smart.gen.utils.GenUtils;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.gen.GenTableColumnService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.smart.gen.constant.GenConstant.DEFAULT_COLUMNS;


/**
 * 代码生成表列 ServiceImpl
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Service("genTableColumnService")
@Transactional(rollbackFor = Exception.class)
public class GenTableColumnServiceImpl extends BaseServiceImpl<GenTableColumnDao, GenTableColumnEntity> implements GenTableColumnService {


    @Override
    public List<GenTableColumnEntity> findColumns(GenTableColumnEntity genTableColumnEntity) {
        Configuration config = GenUtils.getConfig();
        List<GenTableColumnEntity> list = baseMapper.findColumns(genTableColumnEntity.getTableName());
        List<GenTableColumnEntity> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            GenTableColumnEntity columnEntity = list.get(i);
            columnEntity.setId(Integer.toString(i + 1));
            String attrName = StringUtils.uncapitalize(GenUtils.columnToJava(columnEntity.getColumnName()));
            columnEntity.setAttrName(attrName);
            String attrType = config.getString(columnEntity.getColumnType(), GenUtils.columnToJava(columnEntity.getColumnType()));
            columnEntity.setAttrType(attrType);
            // 默认设置
            switch (attrType) {
                case "String":
                    columnEntity.setComponents("INPUT");
                    break;
                case "Boolean":
                    columnEntity.setComponents("CHECKBOX");
                    break;
                case "Integer":
                case "Long":
                case "Float":
                case "Double":
                case "BigDecimal":
                    columnEntity.setComponents("INPUT-NUMBER");
                    break;
                case "Date":
                    columnEntity.setComponents("DATEPICKER");
                    break;
                default:
                    throw new SmartException("字段类型未匹配！");
            }
            if ("1".equals(columnEntity.getIsPk())) {
                columnEntity.setIsNotNull("1");
                columnEntity.setIsList("0");
                columnEntity.setIsForm("0");
            }
            if (Arrays.asList(DEFAULT_COLUMNS).contains(columnEntity.getColumnName())) {
                columnEntity.setIsForm("0");
                if ("is_deleted".equals(columnEntity.getColumnName()) ||
                        "create_user".equals(columnEntity.getColumnName()) ||
                        "create_dept".equals(columnEntity.getColumnName()) ||
                        "create_organization".equals(columnEntity.getColumnName()) ||
                        "update_by".equals(columnEntity.getColumnName()) ||
                        "update_date".equals(columnEntity.getColumnName()) ||
                        "remarks".equals(columnEntity.getColumnName())
                ) {
                    columnEntity.setIsList("0");
                }
            }
            result.add(columnEntity);
        }
        return result;
    }

    /**
     * 初始化wrapper之后执行
     *
     * @param entity  bean 实体
     * @param wrapper 查询条件
     * @return QueryWrapper
     */
    @Override
    public QueryWrapper<GenTableColumnEntity> initWrapperAfter(GenTableColumnEntity entity, QueryWrapper<GenTableColumnEntity> wrapper) {
        wrapper.orderByAsc("column_sort");
        return wrapper;
    }
}