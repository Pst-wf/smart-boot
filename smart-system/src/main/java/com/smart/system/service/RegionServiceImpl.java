package com.smart.system.service;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.smart.common.constant.SmartConstant;
import com.smart.common.utils.CacheUtil;
import com.smart.common.utils.ListUtil;
import com.smart.common.utils.StringUtil;
import com.smart.common.utils.TreeUtil;
import com.smart.entity.system.RegionEntity;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.system.RegionService;
import com.smart.system.dao.RegionDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.smart.common.constant.SmartConstant.*;

/**
 * 行政区域 ServiceImpl
 *
 * @author wf
 * @since 2025-02-20 16:59:05
 */
@Service("regionService")
@Transactional(rollbackFor = Exception.class)
public class RegionServiceImpl extends BaseServiceImpl<RegionDao, RegionEntity> implements RegionService {
    /**
     * 异步加载
     */
    @Override
    public List<RegionEntity> asyncLoading(RegionEntity regionEntity) {
        if (StringUtil.isBlank(regionEntity.getParentId()) && StringUtil.isBlank(regionEntity.getAreaCode()) && StringUtil.isBlank(regionEntity.getName())) {
            regionEntity.setParentId(SmartConstant.INT_ZERO);
        }
        List<RegionEntity> list = super.findList(regionEntity);
        list.forEach(one -> one.setIsLeaf(false));
        return TreeUtil.buildTree(list);
    }

    /**
     * 获取全部行政区域（缓存）
     */
    public List<RegionEntity> getCacheAll() {
        List<RegionEntity> list = CacheUtil.get("region", "all", List.class);
        if (list == null) {
            list = super.findList(new RegionEntity());
            CacheUtil.put("region", "all", list);
        }
        return list;
    }

    /**
     * 树形结构
     */
    @Override
    public List<RegionEntity> tree(RegionEntity regionEntity) {
        List<RegionEntity> list;
        if (StringUtil.isBlank(regionEntity.getParentId()) &&
                StringUtil.isBlank(regionEntity.getAreaCode()) &&
                StringUtil.isBlank(regionEntity.getName())
        ) {
            // 查询全部
            list = getCacheAll();
        } else {
            list = super.findList(regionEntity);
        }
        return TreeUtil.buildTree(list);
    }

    /**
     * 获取树形格式化结构
     *
     * @param regionEntity bean
     * @return List
     */
    @Override
    public List<Map<String, Object>> getTreeFormat(RegionEntity regionEntity) {
        List<RegionEntity> list;
        if (StringUtil.isBlank(regionEntity.getParentId()) &&
                StringUtil.isBlank(regionEntity.getAreaCode()) &&
                StringUtil.isBlank(regionEntity.getName())
        ) {
            // 查询全部
            list = getCacheAll();
        } else {
            list = super.findList(regionEntity);
        }
        List<Map<String, Object>> maps = new ArrayList<>();
        list.forEach(x -> {
            Map<String, Object> map = new HashMap<>(0);
            map.put("id", x.getId());
            map.put("pId", x.getParentId());
            map.put("label", x.getName());
            map.put("value", x.getId());
            map.put("key", x.getId());
            maps.add(map);
        });
        return TreeUtil.buildTreeMap(maps, "id", "pId");
    }

    /**
     * 启用/停用
     *
     * @param entity bean实体
     * @return boolean
     */
    @Override
    public boolean updateStatus(RegionEntity entity) {
        return Db.lambdaUpdate(RegionEntity.class)
                .set(RegionEntity::getStatus, StringUtil.notBlankAndEquals(entity.getStatus(), YES) ? YES : NO)
                .eq(RegionEntity::getId, entity.getId())
                .update();
    }

    /**
     * 保存之前处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    @Override
    public void beforeSaveOrUpdate(RegionEntity entity, boolean isAdd) {
        if (StringUtil.isBlank(entity.getParentId())) {
            entity.setParentId(SmartConstant.INT_ZERO);
//            entity.setLevel(1);
        }

//
//        else {
//            RegionEntity parent = super.getById(entity.getParentId());
//            if (parent == null) {
//                throw new SmartException("查询上级异常！");
//            }
//            entity.setLevel(parent.getLevel() + 1);
//        }
        if (isAdd) {
            beforeAdd(entity);
        } else {
            beforeUpdate(entity);
        }
    }


    /**
     * 新增前执行
     *
     * @param entity bean对象
     */
    private void beforeAdd(RegionEntity entity) {
        String parentId = entity.getParentId();
        if (INT_ZERO.equals(parentId)) {
            entity.setAncestors(parentId);
            entity.setLevel(1);
        } else {
            RegionEntity parent = super.getById(parentId);
            if (parent == null) {
                throw new SmartException("查询上级异常！");
            }
            String ancestors = parent.getAncestors() + "," + parentId;
            entity.setAncestors(ancestors);
            entity.setLevel(parent.getLevel() + 1);
        }
    }

    /**
     * 修改前执行
     *
     * @param entity bean对象
     */
    private void beforeUpdate(RegionEntity entity) {
        RegionEntity region = baseMapper.selectById(entity.getId());
        String parentId = entity.getParentId();
        String oldParentId = region.getParentId();
        if (!oldParentId.equals(parentId)) {
            entity.setIsChangeParentId(true);
            // 修改了父级部门
            if (StringUtil.notBlankAndEquals(parentId, entity.getId())) {
                throw new SmartException("父级和本级不能为同一条数据！");
            }
            List<RegionEntity> children = Db.lambdaQuery(RegionEntity.class).like(RegionEntity::getAncestors, entity.getId()).list();
            List<String> childrenIds = children.stream().map(RegionEntity::getId).collect(Collectors.toList());
            if (childrenIds.contains(parentId)) {
                //如果修改自己的parentId为自己的子集 则需要更新子集
                Db.lambdaUpdate(RegionEntity.class)
                        .set(RegionEntity::getParentId, region.getParentId())
                        .set(RegionEntity::getAncestors, region.getAncestors())
                        .set(RegionEntity::getLevel, region.getLevel())
                        .eq(RegionEntity::getId, parentId)
                        .update();
            }
        }
    }

    /**
     * 保存之后处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    @Override
    public void afterSaveOrUpdate(RegionEntity entity, boolean isAdd) {
        CacheUtil.evict("region", "all");
        if (!isAdd && entity.getIsChangeParentId() != null && entity.getIsChangeParentId()) {
            RegionEntity parent = null;
            if (!INT_ZERO.equals(entity.getParentId())) {
                parent = super.getById(entity.getParentId());
            }
            // 优化兼容行，原方法只适配mysql8，修改为查全表后循环匹配(若组织机构较多 需先考虑性能在考虑兼容性)
            List<RegionEntity> list = super.list();
            List<RegionEntity> tree = TreeUtil.buildTree(list);
            List<RegionEntity> children = new ArrayList<>();
            getChildrenIdAndChildrenRegion(tree, ListUtil.newArrayList(entity.getParentId()), children);
            List<RegionEntity> regionEntities = TreeUtil.buildTree(children);
            handleList(regionEntities, parent);
            List<RegionEntity> format = TreeUtil.toList(regionEntities);
            if (ListUtil.isNotEmpty(format)) {
                super.updateBatchById(format);
            }
        }
    }

    /**
     * 删除之前处理
     *
     * @param entity bean 实体
     * @param isReal 是否物理删除
     */
    @Override
    public void beforeDelete(RegionEntity entity, boolean isReal) {
        // 优化兼容行，原方法只适配mysql8，修改为查全表后循环匹配(若组织机构较多 需先考虑性能在考虑兼容性)
        List<String> deleteIds = entity.getDeleteIds();
        if (ListUtil.isNotEmpty(entity.getDeleteIds())) {
            List<RegionEntity> list = super.list();
            List<RegionEntity> tree = TreeUtil.buildTree(list);
            getChildrenIdAndChildrenRegion(tree, deleteIds, new ArrayList<>());
        }
        entity.setDeleteIds(deleteIds);
    }

    /**
     * 批量删除之后处理
     *
     * @param entity bean 实体
     * @param isReal 是否物理删除
     */
    @Override
    public void afterDelete(RegionEntity entity, boolean isReal) {
        CacheUtil.evict("region", "all");
    }

    /**
     * 通过主键ID删除之后处理
     *
     * @param id 主键
     */
    @Override
    public void afterDeleteById(String id) {
        CacheUtil.evict("region", "all");
    }


    /**
     * 获取所有子集IDS和子集实体
     */
    private void getChildrenIdAndChildrenRegion(List<RegionEntity> list, List<String> ids, List<RegionEntity> res) {
        for (RegionEntity regionEntity : list) {
            if (ids.contains(regionEntity.getParentId())) {
                ids.add(regionEntity.getId());
                res.add(regionEntity);
            }
            if (regionEntity.getChildren() != null && !regionEntity.getChildren().isEmpty()) {
                getChildrenIdAndChildrenRegion(regionEntity.getChildren(), ids, res);
            }
        }
    }

    private void handleList(List<RegionEntity> list, RegionEntity parent) {
        list.forEach(x -> {
            if (parent == null) {
                x.setAncestors(INT_ZERO);
                x.setLevel(1);
            } else {
                x.setAncestors(parent.getAncestors() + "," + parent.getId());
                x.setLevel(parent.getLevel() + 1);
            }
            if (x.getChildren() != null && !x.getChildren().isEmpty()) {
                handleList(x.getChildren(), x);
            }
        });
    }
}