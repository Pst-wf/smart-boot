package com.smart.system.service;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.smart.common.utils.*;
import com.smart.entity.system.DictEntity;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.system.DictService;
import com.smart.system.dao.DictDao;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.smart.common.constant.SmartConstant.INT_ZERO;
import static com.smart.common.constant.SmartConstant.IS_NEW;

/**
 * 字典 ServiceImpl
 *
 * @author wf
 * @since 2022-01-17 07:42:32
 */
@Service("dictService")
@Transactional(rollbackFor = Exception.class)
public class DictServiceImpl extends BaseServiceImpl<DictDao, DictEntity> implements DictService {

    /**
     * 新增
     *
     * @param entity bean实体
     * @return bean
     */
    @Override
    public DictEntity saveDb(DictEntity entity) {
        // 先获取字典的code
        DictEntity parent = null;
        if (StringUtil.isBlank(entity.getDictCode())) {
            // 若是子集则获取顶级父级
            String[] ancestors = entity.getAncestors().split(",");
            if (ancestors.length > 1) {
                parent = super.getById(ancestors[1]);
            }
        }
        boolean b = save(entity);
        // 新增成功后 若是父级字典则修改缓存
        if (b) {
            if (parent != null) {
                CacheUtil.evict("dict", parent.getDictCode());
                CacheUtil.put("dict", parent.getDictCode(), getDictByParentId(parent.getId()));
            }
        }
        return entity;
    }

    @Override
    public DictEntity updateDb(DictEntity entity) {
        // 先获取字典的code
        DictEntity parent;
        boolean isParent = false;
        if (StringUtil.isBlank(entity.getDictCode())) {
            // 若是子集则获取顶级父级
            String[] ancestors = entity.getAncestors().split(",");
            if (ancestors.length > 1) {
                parent = super.getById(ancestors[1]);
            } else {
                throw new SmartException("获取所属字典失败！");
            }
        } else {
            parent = super.getById(entity.getId());
            isParent = true;
        }
        String code = parent.getDictCode();
        boolean b = updateById(entity);
        // 修改成功后 若是父级字典则修改缓存
        if (b) {
            CacheUtil.evict("dict", code);
            if (isParent) {
                code = entity.getDictCode();
            }
            List<DictEntity> values = getDictByParentId(parent.getId());
            if (values.isEmpty()) {
                CacheUtil.put("dict", code, values);
            }
        }
        return entity;
    }

    /**
     * 保存之前处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    @Override
    public void beforeSaveOrUpdate(DictEntity entity, boolean isAdd) {
        if (!isAdd) {
            if (StringUtil.notBlankAndEquals(entity.getParentId(), entity.getId())) {
                throw new SmartException("父级和本级不能为同一条数据！");
            }
        }
        if (StringUtil.isNotBlank(entity.getDictCode())) {
            DictEntity dictEntity = Db.lambdaQuery(DictEntity.class).eq(DictEntity::getDictCode, entity.getDictCode()).one();
            if (dictEntity != null) {
                if (isAdd) {
                    // 新增
                    throw new SmartException("字典编码已存在！");
                } else {
                    // 编辑
                    if (!entity.getId().equals(dictEntity.getId())) {
                        throw new SmartException("字典编码已存在！");
                    }
                }
            }
        }
        if (StringUtil.notBlankAndContains(entity.getId(), IS_NEW)) {
            entity.setId(null);
        }
        if (StringUtil.isBlank(entity.getParentId()) || StringUtil.notBlankAndEquals(entity.getParentId(), INT_ZERO)) {
            entity.setParentId(INT_ZERO);
            entity.setAncestors(INT_ZERO);
        } else {
            DictEntity dictEntity = super.getById(entity.getParentId());
            if (dictEntity == null) {
                throw new SmartException("父级字典为空！");
            }
            entity.setAncestors(dictEntity.getAncestors() + "," + dictEntity.getId());
        }
    }

    /**
     * 删除
     *
     * @param entity bean实体
     * @return boolean
     */
    @Override
    public boolean delete(DictEntity entity) {
        List<DictEntity> list = Db.lambdaQuery(DictEntity.class).in(DictEntity::getId, entity.getDeleteIds()).list();
        List<String> codes = new ArrayList<>();
        for (DictEntity dictEntity : list) {
            if (StringUtil.isBlank(dictEntity.getDictCode())) {
                // 若是子集则获取顶级父级
                String[] ancestors = dictEntity.getAncestors().split(",");
                if (ancestors.length > 1) {
                    DictEntity parent = super.getById(ancestors[1]);
                    if (parent != null) {
                        codes.add(parent.getDictCode());
                    }
                }
            } else {
                codes.add(dictEntity.getDictCode());
            }
        }
        boolean b = super.delete(entity);
        if (b) {
            //删除子集
            entity.getDeleteIds().forEach(d -> {
                Db.remove(Db.lambdaQuery(DictEntity.class).like(DictEntity::getAncestors, d).getWrapper());
            });
            // 清楚缓存
            CacheUtil.evictKeys("dict", codes);
        }
        return b;
    }

    @Override
    public List<DictEntity> getDictByCode(String code) {
        List<DictEntity> dictList = CacheUtil.get("dict", code, List.class);
        if (ListUtil.isEmpty(dictList)) {
            List<DictEntity> list = Db.lambdaQuery(DictEntity.class).eq(DictEntity::getDictCode, code).orderByAsc(DictEntity::getSort).list();
            if (ListUtil.isNotEmpty(list)) {
                dictList = getDictByParentId(list.get(0).getId());
                CacheUtil.put("dict", code, dictList);
            }
        }
        return dictList;
    }

    @Override
    public Map<String, List<DictEntity>> getDictByCodes(String codes) {
        Map<String, List<DictEntity>> map = MapUtil.newHashMap();
        if (StringUtil.isNotBlank(codes)) {
            List<String> codeList = ListUtil.newArrayList(codes.split(","));
            // 去重
            Set<String> codeSet = SetUtil.newHashSet(codeList);
            for (String code : codeSet) {
                map.put(code, getDictByCode(code));
            }
            return map;
        }
        return null;
    }

    /**
     * 通过父级 获取字典
     *
     * @param parentId 父级ID
     * @return list
     */
    @Override
    public List<DictEntity> getDictByParentId(String parentId) {
        List<DictEntity> list = Db.lambdaQuery(DictEntity.class).like(DictEntity::getAncestors, parentId).orderByAsc(DictEntity::getSort).list();
        DictEntity parent = super.getById(parentId);
        if (parent == null) {
            throw new SmartException("获取字典为空！");
        }
        DictEntity dictEntity = formatDict(parent, list);
        return dictEntity.getChildren();
    }

    /**
     * 递归处理字典
     *
     * @param dictEntity 要处理的字典bean
     * @param dictList   所有的字典List
     * @return DictEntity
     */
    public DictEntity formatDict(DictEntity dictEntity, List<DictEntity> dictList) {
        List<DictEntity> list = new ArrayList<>();
        for (DictEntity dict : dictList) {
            if (String.valueOf(dict.getParentId()).equals(dictEntity.getId())) {
                list.add(dict);
            }
        }
        List<DictEntity> children = new ArrayList<>();
        List<DictEntity> collect = list.stream().sorted(Comparator.comparing(DictEntity::getSort)).collect(Collectors.toList());
        for (DictEntity dict : collect) {
            dict = formatDict(dict, dictList);
            children.add(dict);
        }
        dictEntity.setChildren(children);
        return dictEntity;
    }


    /**
     * 获取字典树
     *
     * @param entity bean对象
     * @return list
     */
    @Override
    public List<DictEntity> getTree(DictEntity entity) {
        return TreeUtil.buildTree(super.findList(entity));
    }

    /**
     * 获取字典名称
     *
     * @param dictCode     字典code
     * @param dictValue    字典值
     * @param defaultValue 默认值
     * @return String
     * todo 只支持一级字典
     */
    @Override
    public String getDictName(@NotNull String dictCode, @NotNull String dictValue, String defaultValue) {
        List<DictEntity> list = getDictByCode(dictCode);
        if (ListUtil.isNotEmpty(list)) {
            for (DictEntity dict : list) {
                if (StringUtil.notBlankAndEquals(dict.getDictValue(), dictValue)) {
                    return dict.getDictName();
                }
            }
        }
        return defaultValue;
    }

    /**
     * 获取字典值
     *
     * @param dictCode     字典code
     * @param dictName     字典名称
     * @param defaultValue 默认值
     * @return String
     * todo 只支持一级字典
     */
    @Override
    public String getDictValue(@NotNull String dictCode, @NotNull String dictName, String defaultValue) {
        List<DictEntity> list = getDictByCode(dictCode);
        if (ListUtil.isNotEmpty(list)) {
            for (DictEntity dict : list) {
                if (StringUtil.notBlankAndEquals(dict.getDictName(), dictName)) {
                    return dict.getDictValue();
                }
            }
        }
        return defaultValue;
    }

    /**
     * 获取字典数据集
     *
     * @param id 主键ID
     * @return List
     */
    @Override
    public List<DictEntity> getValues(String id) {
        List<DictEntity> list = Db.lambdaQuery(DictEntity.class).like(DictEntity::getAncestors, id).orderByAsc(DictEntity::getSort).list();
        list.forEach(x -> {
            x.setOldDictName(x.getDictName());
            x.setOldDictValue(x.getDictValue());
            x.setOldSort(x.getSort());
        });
        return TreeUtil.buildTree(list);
    }
}