package com.smart.system.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.smart.common.utils.*;
import com.smart.entity.system.*;
import com.smart.model.exception.SmartException;
import com.smart.model.route.Button;
import com.smart.model.route.MenuRouter;
import com.smart.model.route.RouterMeta;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.system.ButtonsService;
import com.smart.service.system.MenuService;
import com.smart.service.system.RoleButtonsService;
import com.smart.service.system.ScopeService;
import com.smart.system.dao.MenuDao;
import com.smart.system.dao.RoleMenuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.smart.common.constant.SmartConstant.*;
import static com.smart.system.constant.SystemConstant.MENU_WEIGHT_DEFAULT;


/**
 * 菜单表 ServiceImpl
 *
 * @author wf
 * @since 2021-12-31 10:05:07
 */
@Service("menuService")
@Transactional(rollbackFor = Exception.class)
public class MenuServiceImpl extends BaseServiceImpl<MenuDao, MenuEntity> implements MenuService {

    @Autowired
    ButtonsService buttonsService;
    @Autowired
    ScopeService scopeService;
    @Autowired
    RoleButtonsService roleButtonsService;
    @Resource
    RoleMenuDao roleMenuDao;

    /**
     * 获取树形结构
     *
     * @param menuEntity bean
     * @return List
     */
    @Override
    public List<MenuEntity> getTree(MenuEntity menuEntity) {
        if (StringUtil.isNotBlank(AuthUtil.getUserId()) && !AuthUtil.getUserId().equals(SYSTEM_ID)) {
            menuEntity.setWeight(MENU_WEIGHT_DEFAULT);
        }
        List<MenuEntity> list = super.findList(menuEntity);
        // 查询所有按钮
        List<ButtonsEntity> buttons = buttonsService.list();
        Map<String, List<ButtonsEntity>> buttonsMap = buttons.stream().collect(Collectors.groupingBy(ButtonsEntity::getMenuId));
        list.forEach(x -> {
            List<ButtonsEntity> buttonsEntities = buttonsMap.get(x.getId());
            if (buttonsEntities != null && !buttonsEntities.isEmpty()) {
                buttonsEntities.sort(Comparator.comparing(ButtonsEntity::getSort));
                x.setButtons(buttonsEntities);
            }
        });
        return TreeUtil.buildTree(list);
    }

    /**
     * 保存之前处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    @Override
    public void beforeSaveOrUpdate(MenuEntity entity, boolean isAdd) {
        if (StringUtil.isBlank(entity.getRouteName())) {
            throw new SmartException("路由名称不能为空！");
        }
        if (StringUtil.isBlank(entity.getWeight())) {
            // 默认权重是2
            entity.setWeight(MENU_WEIGHT_DEFAULT);
        }
        MenuEntity one = super.getOne(new LambdaQueryWrapper<MenuEntity>().eq(MenuEntity::getRouteName, entity.getRouteName()).eq(MenuEntity::getIsDeleted, "0"));
        // 校验按钮唯一
        List<ButtonsEntity> buttons = new ArrayList<>();
        if (ListUtil.isNotEmpty(entity.getButtons())) {
            Set<String> collect = entity.getButtons().stream().map(ButtonsEntity::getCode).collect(Collectors.toSet());
            if (!collect.isEmpty()) {
                buttons = buttonsService.list(new LambdaQueryWrapper<ButtonsEntity>().in(ButtonsEntity::getCode, collect));
            }
        }
        if (isAdd) {
            // 新增
            if (one != null) {
                throw new SmartException("当前路由已存在！");
            }
            if (!buttons.isEmpty()) {
                String collect = buttons.stream().map(ButtonsEntity::getCode).collect(Collectors.joining(","));
                throw new SmartException("按钮【" + collect + "】已存在，新增失败！");
            }
        } else {
            // 修改
            if (one != null && !one.getId().equals(entity.getId())) {
                throw new SmartException("当前路由已存在！");
            }
            List<ButtonsEntity> filterButtons = buttons.stream().filter(x -> !x.getMenuId().equals(entity.getId())).collect(Collectors.toList());
            if (!filterButtons.isEmpty()) {
                String collect = filterButtons.stream().map(ButtonsEntity::getCode).collect(Collectors.joining(","));
                throw new SmartException("按钮【" + collect + "】已存在，保存失败！");
            }
        }
        if (StringUtil.isBlank(entity.getParentId()) || entity.getParentId().equals(INT_ZERO)) {
            entity.setParentId(INT_ZERO);
        } else {
            String parentId = entity.getParentId();
            if (StringUtil.notBlankAndEquals(parentId, entity.getId())) {
                throw new SmartException("父级和本级不能为同一条数据！");
            }
            if (!isAdd) {
                // 判断是否自己的父级原来是自己的子集
                List<MenuEntity> children = baseMapper.selectList(new LambdaQueryWrapper<MenuEntity>().eq(MenuEntity::getParentId, entity.getId()));
                List<String> childrenIds = children.stream().map(MenuEntity::getId).collect(Collectors.toList());
                if (childrenIds.contains(parentId)) {
                    //如果修改自己的parentId为自己的子集 则需要更新子集
                    MenuEntity menu = baseMapper.selectById(entity.getId());
                    if (menu != null) {
                        LambdaUpdateChainWrapper<MenuEntity> updateChainWrapper = new LambdaUpdateChainWrapper<>(baseMapper);
                        updateChainWrapper.set(MenuEntity::getParentId, menu.getParentId()).eq(MenuEntity::getId, parentId).update();
                    }
                }
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
    public void afterSaveOrUpdate(MenuEntity entity, boolean isAdd) {
        if (!isAdd) {
            // 获取原按钮
            List<ButtonsEntity> list = buttonsService.list(new LambdaQueryWrapper<ButtonsEntity>().eq(ButtonsEntity::getMenuId, entity.getId()));
            // 原的按钮集合
            Set<String> oldCodes = list.stream().map(ButtonsEntity::getCode).filter(StringUtil::isNotBlank).collect(Collectors.toSet());
            // 新的按钮集合
            Set<String> newCodes = new HashSet<>();
            if (ListUtil.isNotEmpty(entity.getButtons())) {
                newCodes = entity.getButtons().stream().map(ButtonsEntity::getCode).filter(StringUtil::isNotBlank).collect(Collectors.toSet());
            }
            // 计算差集
            List<String> deleteCodes = ListUtil.subtractCollection(oldCodes, newCodes);
            if (!deleteCodes.isEmpty()) {
                roleButtonsService.remove(new LambdaQueryWrapper<RoleButtonsEntity>().in(RoleButtonsEntity::getButtonCode, deleteCodes));
            }
            // 处理query
            if (entity.getQuery() == null) {
                LambdaUpdateChainWrapper<MenuEntity> updateChainWrapper = new LambdaUpdateChainWrapper<>(baseMapper);
                updateChainWrapper.set(MenuEntity::getQuery, null).eq(MenuEntity::getId, entity.getId()).update();
            }
        }

        buttonsService.remove(new LambdaQueryWrapper<ButtonsEntity>().eq(ButtonsEntity::getMenuId, entity.getId()));
        if (ListUtil.isNotEmpty(entity.getButtons())) {
            ListUtil.forEach(entity.getButtons(), (index, item) -> {
                item.setId(null);
                item.setSort(index + 1);
                item.setMenuId(entity.getId());
            });
            buttonsService.saveBatch(entity.getButtons());
        }
        // 清空按钮缓存
        CacheUtil.clear("buttons");
    }

    /**
     * 删除之前处理
     *
     * @param entity bean 实体
     * @param isReal 是否物理删除
     */
    @Override
    public void beforeDelete(MenuEntity entity, boolean isReal) {
        // 查询对应的子菜单 补全deleteIds
//        List<MenuEntity> children = baseMapper.findChildrenByParentIds(entity.getDeleteIds());
//        Set<String> collect = children.stream().map(MenuEntity::getId).collect(Collectors.toSet());
//        List<String> deleteIds = entity.getDeleteIds();
//        deleteIds.addAll(collect);

        // 优化兼容行，原方法只适配mysql8，修改为查全表后循环匹配
        List<String> deleteIds = entity.getDeleteIds();
        if (ListUtil.isNotEmpty(entity.getDeleteIds())) {
            List<MenuEntity> list = super.list();
            List<MenuEntity> tree = TreeUtil.buildTree(list);
            getChildrenId(tree, deleteIds);
        }
        entity.setDeleteIds(deleteIds);
    }

    /**
     * 删除之后处理
     *
     * @param entity bean 实体
     * @param isReal 是否物理删除
     */
    @Override
    public void afterDelete(MenuEntity entity, boolean isReal) {
        //删除中间表
        roleMenuDao.delete(new LambdaQueryWrapper<RoleMenuEntity>().in(RoleMenuEntity::getMenuId, entity.getDeleteIds()));
        //查询对应的按钮
        List<ButtonsEntity> buttons = buttonsService.list(new LambdaQueryWrapper<ButtonsEntity>().in(ButtonsEntity::getMenuId, entity.getDeleteIds()));
        if (!buttons.isEmpty()) {
            // 按钮codes集合
            Set<String> buttonCodes = buttons.stream().map(ButtonsEntity::getCode).filter(StringUtil::isNotBlank).collect(Collectors.toSet());
            // 删除按钮
            buttonsService.removeBatchByIds(buttons);
            // 删除按钮角色中间表
            roleButtonsService.remove(new LambdaQueryWrapper<RoleButtonsEntity>().in(RoleButtonsEntity::getButtonCode, buttonCodes));
        }
    }

    /**
     * 根据角色ID获取对应路由
     *
     * @param roleId 角色ID
     * @return List
     */
    @Override
    public List<MenuRouter> findRouterByRoleId(String roleId) {
        List<MenuEntity> menuList;
        List<ButtonsEntity> buttonList;
        if (SYSTEM_ID.equals(roleId)) {
            MenuEntity menuEntity = new MenuEntity();
            menuEntity.setStatus(YES);
            menuList = super.findList(menuEntity);
            buttonList = buttonsService.list();
        } else {
            menuList = baseMapper.findMenuByRoleId(roleId);
            buttonList = buttonsService.findButtonsByRoleId(roleId);
        }
        // 过滤path为空的菜单 前端会报错
        menuList = menuList.stream().filter(x -> StringUtil.isNotBlank(x.getRoutePath())).collect(Collectors.toList());
        List<MenuRouter> routers = new ArrayList<>();
        menuList.forEach(x -> {
            MenuRouter router = new MenuRouter();
            router.setId(x.getId());
            router.setParentId(x.getParentId());
            router.setName(x.getRouteName());
            router.setPath(x.getRoutePath());
            if (StringUtil.isNotBlank(x.getProps())) {
                try {
                    router.setProps(JSON.parseObject(x.getProps()));
                } catch (Exception e) {
                    router.setProps(new JSONObject());
                }
            }
            router.setRedirect(x.getRedirect());
            router.setComponent(x.getComponent());
            RouterMeta meta = new RouterMeta();
            BeanUtil.copyProperties(x, meta, "icon");
            meta.setTitle(x.getMenuName());
            if (StringUtil.notBlankAndEquals(x.getIconType(), "1")) {
                meta.setIcon(x.getIcon());
            } else if (StringUtil.notBlankAndEquals(x.getIconType(), "2")) {
                meta.setLocalIcon(x.getIcon());
            }
            // 按钮权限
            List<ButtonsEntity> collect = buttonList.stream().filter(buttons -> buttons.getMenuId().equals(x.getId()))
                    .sorted(Comparator.comparing(ButtonsEntity::getSort)).collect(Collectors.toList());
            List<Button> buttons = new ArrayList<>();
            collect.forEach(b -> {
                Button button = new Button();
                button.setCode(b.getCode());
                button.setName(b.getName());
                button.setSort(b.getSort());
                buttons.add(button);
            });
            meta.setButtons(buttons);
            router.setMeta(meta);
            routers.add(router);
        });
        return TreeUtil.buildTree(routers);
    }

    /**
     * 根据角色ID获取菜单
     *
     * @param roleId 角色ID
     * @return List
     */
    @Override
    public List<MenuEntity> findMenuByRoleId(String roleId) {
        return baseMapper.findMenuByRoleId(roleId);
    }

    /**
     * 获取树形格式化结构
     *
     * @param menuEntity bean
     * @return List
     */
    @Override
    public List<Map<String, Object>> getTreeFormat(MenuEntity menuEntity) {
        if (StringUtil.isNotBlank(AuthUtil.getUserId()) && !AuthUtil.getUserId().equals(SYSTEM_ID)) {
            menuEntity.setWeight(MENU_WEIGHT_DEFAULT);
        }
        List<MenuEntity> list = super.findList(menuEntity);
        List<Map<String, Object>> maps = new ArrayList<>();
        list.forEach(x -> {
            Map<String, Object> map = new HashMap<>(0);
            map.put("id", x.getId());
            map.put("pId", x.getParentId());
            map.put("label", x.getMenuName());
            map.put("i18nKey", x.getI18nKey());
            map.put("disabled", x.getStatus().equals(NO));
            maps.add(map);
        });
        return TreeUtil.buildTreeMap(maps, "id", "pId");
    }

    /**
     * 树形结构格式化带按钮
     *
     * @param menuEntity 菜单bean
     * @return String
     */
    @Override
    public List<Map<String, Object>> getButtonTreeFormat(MenuEntity menuEntity) {
        if (StringUtil.isNotBlank(AuthUtil.getUserId()) && !AuthUtil.getUserId().equals(SYSTEM_ID)) {
            menuEntity.setWeight(MENU_WEIGHT_DEFAULT);
        }
        // 查询所有菜单
        List<MenuEntity> list = super.findList(menuEntity);
        Set<String> ids = list.stream().map(MenuEntity::getId).collect(Collectors.toSet());
        // 查询所有按钮
        List<ButtonsEntity> buttons = buttonsService.list();
        List<Map<String, Object>> maps = new ArrayList<>();
        list.forEach(x -> {
            Map<String, Object> map = new HashMap<>(0);
            map.put("id", x.getId());
            map.put("pId", x.getParentId());
            map.put("label", x.getMenuName());
            map.put("disabled", x.getStatus().equals(NO));
            map.put("checkboxDisabled", true);
            maps.add(map);
        });
        buttons.forEach(x -> {
            if (ids.contains(x.getMenuId())) {
                Map<String, Object> map = new HashMap<>(0);
                map.put("id", x.getId());
                map.put("pId", x.getMenuId());
                map.put("label", x.getName() + " - 【按钮】");
                maps.add(map);
            }
        });
        return TreeUtil.buildTreeMap(maps, "id", "pId");
    }

    /**
     * 树形结构格式化带数据权限
     *
     * @param menuEntity 菜单bean
     * @return String
     */
    @Override
    public List<Map<String, Object>> getScopeTreeFormat(MenuEntity menuEntity) {
        if (StringUtil.isNotBlank(AuthUtil.getUserId()) && !AuthUtil.getUserId().equals(SYSTEM_ID)) {
            menuEntity.setWeight(MENU_WEIGHT_DEFAULT);
        }
        // 查询所有菜单
        List<MenuEntity> list = super.findList(menuEntity);
        Set<String> ids = list.stream().map(MenuEntity::getId).collect(Collectors.toSet());

        // 查询所有数据权限
        List<ScopeEntity> scopes = scopeService.list();
        List<Map<String, Object>> maps = new ArrayList<>();
        list.forEach(x -> {
            Map<String, Object> map = new HashMap<>(0);
            map.put("id", x.getId());
            map.put("pId", x.getParentId());
            map.put("label", x.getMenuName());
            map.put("disabled", x.getStatus().equals(NO));
            map.put("checkboxDisabled", true);
            maps.add(map);
        });
        scopes.forEach(x -> {
            if (ids.contains(x.getMenuId())) {
                Map<String, Object> map = new HashMap<>(0);
                map.put("id", x.getId());
                map.put("pId", x.getMenuId());
                map.put("label", x.getScopeName() + " - 【数据权限】");
                maps.add(map);
            }
        });

        return TreeUtil.buildTreeMap(maps, "id", "pId");
    }

    /**
     * 根据父级ID获取子级下一个排序号 最大排序号
     *
     * @param parentId 父级ID
     * @return int
     */
    @Override
    public int findNextOrderByParentId(String parentId) {
        List<MenuEntity> list = baseMapper.selectList(new LambdaQueryWrapper<MenuEntity>().eq(MenuEntity::getParentId, parentId).orderByDesc(MenuEntity::getOrder));
        return !list.isEmpty() ? list.get(0).getOrder() + 1 : 0;
    }

    /**
     * 获取所有子集IDS
     */
    private void getChildrenId(List<MenuEntity> list, List<String> deleteIds) {
        for (MenuEntity menuEntity : list) {
            if (deleteIds.contains(menuEntity.getParentId())) {
                deleteIds.add(menuEntity.getId());
            }
            if (menuEntity.getChildren() != null && !menuEntity.getChildren().isEmpty()) {
                getChildrenId(menuEntity.getChildren(), deleteIds);
            }
        }
    }
}

