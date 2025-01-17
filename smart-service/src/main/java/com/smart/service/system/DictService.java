package com.smart.service.system;

import com.smart.entity.system.DictEntity;
import com.smart.mybatis.service.BaseService;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;

/**
 * 字典 Service
 *
 * @author wf
 * @since 2022-01-17 07:42:32
 */
public interface DictService extends BaseService<DictEntity> {
    /**
     * 通过Codes获取字典
     *
     * @param codes code字符串
     * @return String
     */
    Map<String, List<DictEntity>> getDictByCodes(String codes);

    /**
     * 通过code 获取字典
     *
     * @param dictCode code
     * @return list
     */
    List<DictEntity> getDictByCode(String dictCode);

    /**
     * 通过父级 获取字典
     *
     * @param parentId 父级ID
     * @return list
     */
    List<DictEntity> getDictByParentId(String parentId);


    /**
     * 获取字典名称
     *
     * @param dictCode     字典code
     * @param dictValue    字典值
     * @param defaultValue 默认值
     * @return String
     */
    String getDictName(@NonNull String dictCode, @NonNull String dictValue, String defaultValue);

    /**
     * 获取字典值
     *
     * @param dictCode     字典code
     * @param dictName     字典名称
     * @param defaultValue 默认值
     * @return String
     */
    String getDictValue(@NonNull String dictCode, @NonNull String dictName, String defaultValue);


    /**
     * 获取字典树
     *
     * @param entity bean对象
     * @return list
     */
    List<DictEntity> getTree(DictEntity entity);

    /**
     * 获取字典数据集
     *
     * @param id 主键ID
     * @return List
     */
    List<DictEntity> getValues(String id);
}
