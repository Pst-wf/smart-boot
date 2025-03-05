package com.smart.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.smart.common.constant.SmartConstant;
import com.smart.common.utils.CacheUtil;
import com.smart.common.utils.ListUtil;
import com.smart.common.utils.StringUtil;
import com.smart.common.utils.TreeUtil;
import com.smart.entity.system.DeptEntity;
import com.smart.entity.system.IdentityEntity;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.system.DeptService;
import com.smart.service.system.IdentityInfoService;
import com.smart.system.dao.DeptDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.smart.common.constant.SmartConstant.*;

/**
 * 机构 ServiceImpl
 *
 * @author wf
 * @since 2022-01-15 00:21:17
 */
@Service("deptService")
@Transactional(rollbackFor = Exception.class)
public class DeptServiceImpl extends BaseServiceImpl<DeptDao, DeptEntity> implements DeptService {
    @Autowired
    IdentityInfoService identityInfoService;

    /**
     * 集合
     *
     * @param entity bean实体
     * @return List
     */
    @Override
    public List<DeptEntity> findList(DeptEntity entity) {
        QueryWrapper<DeptEntity> wrapper = new QueryWrapper<>();
        wrapper = initWrapper(entity, wrapper);
        wrapper = initWrapperAfter(entity, wrapper);
        return baseMapper.findList(wrapper);
    }

    /**
     * 获取树形结构
     *
     * @param entity 部门bean
     * @return List
     */
    @Override
    public List<DeptEntity> getTree(DeptEntity entity) {
        return TreeUtil.buildTree(super.findList(entity));
    }

    /**
     * 获取树形格式化结构
     *
     * @param deptEntity bean
     * @return List
     */
    @Override
    public List<Map<String, Object>> getTreeFormat(DeptEntity deptEntity) {
        List<DeptEntity> list = super.findList(deptEntity);
        List<Map<String, Object>> maps = new ArrayList<>();
        list.forEach(x -> {
            Map<String, Object> map = new HashMap<>(0);
            map.put("id", x.getId());
            map.put("pId", x.getParentId());
            map.put("label", x.getDeptName());
            map.put("value", x.getId());
            map.put("key", x.getId());
            maps.add(map);
        });
        return TreeUtil.buildTreeMap(maps, "id", "pId");
    }

    /**
     * 获取树形结构 (MAP)
     *
     * @param entity 部门bean
     * @return List
     */
    @Override
    public List<Map<String, Object>> getTreeMap(DeptEntity entity) {
        List<DeptEntity> list = super.findList(entity);
        List<Map<String, Object>> formatList = new ArrayList<>();
        list.forEach(item -> {
            Map<String, Object> map = new HashMap<>(0);
            map.put("value", item.getId());
            map.put("parentId", item.getParentId());
            map.put("label", item.getDeptName());
            map.put("children", item.getChildren());
            formatList.add(map);
        });
        return TreeUtil.buildTreeMap(formatList, "value", "parentId");
    }

    /**
     * 保存之前处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    @Override
    public void beforeSaveOrUpdate(DeptEntity entity, boolean isAdd) {
        if (StringUtil.isBlank(entity.getParentId())) {
            entity.setParentId(INT_ZERO);
        }
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
    private void beforeAdd(DeptEntity entity) {
        String parentId = entity.getParentId();
        if (StringUtil.isBlank(entity.getDeptCode())) {
            throw new SmartException("机构编码不能为空！");
        }
        DeptEntity one = super.getOne(new LambdaQueryWrapper<DeptEntity>().eq(DeptEntity::getDeptCode, entity.getDeptCode()));
        if (one != null) {
            throw new SmartException("机构编码已存在！");
        }
        if (INT_ZERO.equals(parentId)) {
            entity.setAncestors(parentId);
        } else {
            DeptEntity parent = super.getById(parentId);
            String ancestors = parent.getAncestors() + "," + parentId;
            entity.setAncestors(ancestors);
        }
    }

    /**
     * 修改前执行
     *
     * @param entity bean对象
     */
    private void beforeUpdate(DeptEntity entity) {
        if (StringUtil.notBlankAndEquals(entity.getStatus(), SmartConstant.NO)) {
            //验证该部门是否有人使用
            QueryWrapper<DeptEntity> wrapper = new QueryWrapper<>();
            wrapper.and(x-> x.like("ancestors", entity.getId()).or().eq("id", entity.getId()));
            long count = baseMapper.checkDeptIdCanDisabled(wrapper);
            if (count > 0) {
                throw new SmartException("要禁用的机构下有用户存在，不可禁用！");
            }
        }
        if (StringUtil.isBlank(entity.getDeptCode())) {
            throw new SmartException("机构编码不能为空！");
        }
        DeptEntity one = super.getOne(new LambdaQueryWrapper<DeptEntity>().eq(DeptEntity::getDeptCode, entity.getDeptCode()));
        if (one != null && !one.getId().equals(entity.getId())) {
            throw new SmartException("机构编码已存在！");
        }
        DeptEntity dept = baseMapper.selectById(entity.getId());
        String parentId = entity.getParentId();
        String oldParentId = dept.getParentId();
        if (!oldParentId.equals(parentId)) {
            entity.setIsChangeParentId(true);
            // 修改了父级部门
            if (StringUtil.notBlankAndEquals(parentId, entity.getId())) {
                throw new SmartException("父级和本级不能为同一条数据！");
            }
            List<DeptEntity> children = baseMapper.selectList(new LambdaQueryWrapper<DeptEntity>().like(DeptEntity::getAncestors, entity.getId()));
            List<String> childrenIds = children.stream().map(DeptEntity::getId).collect(Collectors.toList());
            if (childrenIds.contains(parentId)) {
                //如果修改自己的parentId为自己的子集 则需要更新子集
                LambdaUpdateChainWrapper<DeptEntity> updateChainWrapper = new LambdaUpdateChainWrapper<>(baseMapper);
                updateChainWrapper
                        .set(DeptEntity::getParentId, dept.getParentId())
                        .set(DeptEntity::getAncestors, dept.getAncestors())
                        .eq(DeptEntity::getId, parentId)
                        .update();
            }
        }
    }

    @Override
    @CachePut(cacheNames = "dept", key = "#entity.id", unless = "#result == null")
    public DeptEntity updateEntity(DeptEntity entity) {
        return super.updateEntity(entity);
    }

    /**
     * 保存之后处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    @Override
    public void afterSaveOrUpdate(DeptEntity entity, boolean isAdd) {
        if (!isAdd && entity.getIsChangeParentId() != null && entity.getIsChangeParentId()) {
            DeptEntity parent = null;
            if (!INT_ZERO.equals(entity.getParentId())) {
                parent = super.getById(entity.getParentId());
            }
            // 父级
//            List<DeptEntity> children1 = baseMapper.findChildrenByParentIds(ListUtil.newArrayList(entity.getParentId()));
            // 优化兼容行，原方法只适配mysql8，修改为查全表后循环匹配(若组织机构较多 需先考虑性能在考虑兼容性)
            List<DeptEntity> list = super.list();
            List<DeptEntity> tree = TreeUtil.buildTree(list);
            List<DeptEntity> children = new ArrayList<>();
            getChildrenIdAndChildrenDept(tree, ListUtil.newArrayList(entity.getParentId()), children);
            List<DeptEntity> deptEntities = TreeUtil.buildTree(children);
            handleList(deptEntities, parent);
            List<DeptEntity> format = TreeUtil.toList(deptEntities);
            if (ListUtil.isNotEmpty(format)) {
                super.updateBatchById(format);
            }
        }
    }

    private void handleList(List<DeptEntity> list, DeptEntity parent) {
        list.forEach(x -> {
            if (parent == null) {
                x.setAncestors(INT_ZERO);
            } else {
                x.setAncestors(parent.getAncestors() + "," + parent.getId());
            }
            if (x.getChildren() != null && !x.getChildren().isEmpty()) {
                handleList(x.getChildren(), x);
            }
        });
    }

    /**
     * 删除之前处理
     *
     * @param entity bean 实体
     * @param isReal 是否物理删除
     */
    @Override
    public void beforeDelete(DeptEntity entity, boolean isReal) {
        // 查询对应的子菜单 补全deleteIds
//        List<DeptEntity> children = baseMapper.findChildrenByParentIds(entity.getDeleteIds());
//        Set<String> collect = children.stream().map(DeptEntity::getId).collect(Collectors.toSet());
//        List<String> deleteIds = entity.getDeleteIds();
//        deleteIds.addAll(collect);

        // 优化兼容行，原方法只适配mysql8，修改为查全表后循环匹配(若组织机构较多 需先考虑性能在考虑兼容性)
        List<String> deleteIds = entity.getDeleteIds();
        if (ListUtil.isNotEmpty(entity.getDeleteIds())) {
            List<DeptEntity> list = super.list();
            List<DeptEntity> tree = TreeUtil.buildTree(list);
            getChildrenIdAndChildrenDept(tree, deleteIds, new ArrayList<>());
        }
        entity.setDeleteIds(deleteIds);

        //验证该部门是否有人使用
        List<IdentityEntity> identityEntities = identityInfoService.list();
        // 获取所有身份使用的部门ID
        Set<String> set = identityEntities.stream().map(IdentityEntity::getDeptId).collect(Collectors.toSet());
        if (!set.isEmpty()) {
            List<DeptEntity> deptEntities = super.list(new LambdaQueryWrapper<DeptEntity>().in(DeptEntity::getId, set));
            deptEntities.forEach(item -> {
                List<String> ancestor = new ArrayList<>(Arrays.asList(item.getAncestors().split(",")));
                ancestor.add(item.id);
                List<String> ins = ListUtil.intersectionCollection(entity.getDeleteIds(), ancestor);
                if (!ins.isEmpty()) {
                    throw new SmartException("要删除的部门下有用户存在，不可删除！");
                }
            });
        }
    }

    /**
     * 删除之后处理
     *
     * @param entity bean 实体
     * @param isReal 是否物理删除
     */
    @Override
    public void afterDelete(DeptEntity entity, boolean isReal) {
        CacheUtil.evictKeys("dept", entity.getDeleteIds());
    }

    @Override
    public List<DeptEntity> findExportList(DeptEntity entity) {
        List<DeptEntity> deptList = findList(entity);
        // 重新格式化
        return formatDeptForExport(ListUtil.newArrayList(), deptList);
    }

    /**
     * 递归处理部门（导出）
     *
     * @param list     要追加的集合
     * @param deptList 所有的部门List
     * @return DeptEntity
     */
    public List<DeptEntity> formatDeptForExport(List<DeptEntity> list, List<DeptEntity> deptList) {
        //遍历传入的list
        for (DeptEntity dept : deptList) {
            list.add(dept);
            //所有菜单的父id与传入的根节点id比较，若相等则且类型为菜单为该级菜单的子菜单
            if (dept.getChildren() != null && !dept.getChildren().isEmpty()) {
                for (DeptEntity child : dept.getChildren()) {
                    child.setParentName(dept.getDeptName());
                }
                formatDeptForExport(list, dept.getChildren());
            }
        }
        return list;
    }

    /**
     * 获取缓存部门
     *
     * @param id 部门ID
     * @return DeptEntity
     */
    @Override
    public DeptEntity getCacheDept(String id) {
        DeptEntity dept = null;
        if (StringUtil.isNotBlank(id) && !INT_ZERO.equals(id)) {
            dept = CacheUtil.get("dept", id, DeptEntity.class);
            if (dept == null) {
                dept = super.get(id);
                if (dept != null) {
                    CacheUtil.put("dept", id, dept);
                }
            }
        }
        return dept;
    }

    /**
     * 通过IDS获取部门集合
     *
     * @param ids 主键IDS
     * @return List
     */
    @Override
    public List<DeptEntity> getDeptListByIds(String ids) {
        if (StringUtil.isNotBlank(ids)) {
            String[] idsArr = ids.split(",");
            List<DeptEntity> list = new ArrayList<>();
            // 需要查询的id
            List<String> selectIds = new ArrayList<>();
            for (String id : idsArr) {
                DeptEntity dept = CacheUtil.get("dept", id, DeptEntity.class);
                if (dept != null) {
                    list.add(dept);
                } else {
                    selectIds.add(id);
                }
            }
            if (ListUtil.isNotEmpty(selectIds)) {
                List<DeptEntity> selectDept = super.list(new LambdaQueryWrapper<DeptEntity>().in(DeptEntity::getId, selectIds));
                selectDept.forEach(dept -> {
                    // 存入缓存
                    CacheUtil.put("dept", dept.getId(), dept);
                });
                list.addAll(selectDept);
            }
            // 按原顺序返回
            List<DeptEntity> result = new ArrayList<>();
            Arrays.asList(idsArr).forEach(id -> {
                List<DeptEntity> collect = list.stream().filter(e -> e.getId().equals(id)).collect(Collectors.toList());
                if (ListUtil.isNotEmpty(collect)) {
                    result.add(collect.get(0));
                }
            });
            return result;
        }
        return new ArrayList<>();
    }

    /**
     * 通过部门ID获取部门所属的所有父级名称
     *
     * @param deptId 部门ID
     * @return String
     */
    @Override
    public String getAncestorsDeptName(String deptId) {
        StringBuilder sb = new StringBuilder();
        DeptEntity deptEntity = getCacheDept(deptId);
        if (deptEntity != null) {
            //循环获取祖级部门
            Arrays.asList(deptEntity.getAncestors().split(",")).forEach(x -> {
                DeptEntity cacheDept = getCacheDept(x);
                if (cacheDept != null) {
                    sb.append(cacheDept.getDeptName()).append("/");
                }
            });
            sb.append(deptEntity.getDeptName());
        }
        return sb.toString();
    }

    /**
     * 获取所有子集IDS和子集实体
     */
    private void getChildrenIdAndChildrenDept(List<DeptEntity> list, List<String> ids, List<DeptEntity> res) {
        for (DeptEntity deptEntity : list) {
            if (ids.contains(deptEntity.getParentId())) {
                ids.add(deptEntity.getId());
                res.add(deptEntity);
            }
            if (deptEntity.getChildren() != null && !deptEntity.getChildren().isEmpty()) {
                getChildrenIdAndChildrenDept(deptEntity.getChildren(), ids, res);
            }
        }
    }

    /**
     * 启用/停用
     *
     * @param entity bean实体
     * @return boolean
     */
    @Override
    @CacheEvict(cacheNames = "dept", key = "#entity.id")
    public boolean updateStatus(DeptEntity entity) {
        if (StringUtil.notBlankAndEquals(entity.getStatus(), SmartConstant.NO)) {
            //验证该部门是否有人使用
            QueryWrapper<DeptEntity> wrapper = new QueryWrapper<>();
            wrapper.and(x-> x.like("ancestors", entity.getId()).or().eq("id", entity.getId()));
            long count = baseMapper.checkDeptIdCanDisabled(wrapper);
            if (count > 0) {
                throw new SmartException("要禁用的机构下有用户存在，不可禁用！");
            }
        }
        LambdaUpdateChainWrapper<DeptEntity> updateChainWrapper = new LambdaUpdateChainWrapper<>(baseMapper);
        return updateChainWrapper
                .set(DeptEntity::getStatus, StringUtil.notBlankAndEquals(entity.getStatus(), YES) ? YES : NO)
                .eq(DeptEntity::getId, entity.getId())
                .update();
    }
}