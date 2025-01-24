package com.smart.system.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.common.utils.StringUtil;
import com.smart.entity.gen.GenTableEntity;
import com.smart.entity.system.ButtonsEntity;
import com.smart.entity.system.MenuEntity;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.system.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.smart.common.constant.SmartConstant.INT_ZERO;
import static com.smart.common.constant.SmartConstant.YES;
import static com.smart.system.constant.SystemConstant.MENU_TYPE_2;
import static com.smart.system.constant.SystemConstant.MENU_WEIGHT_DEFAULT;


/**
 * 菜单 Controller
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@RestController
@RequestMapping("/system/menu")
public class MenuController {
    @Autowired
    MenuService menuService;

    /**
     * 列表
     *
     * @param menuEntity 菜单bean
     * @return String
     */
    @GetMapping("/page")
    public String page(MenuEntity menuEntity) {
        return Result.data(menuService.findPage(menuEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        MenuEntity result = menuService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param menuEntity 菜单bean
     * @return String
     */
    @HasPermission("menu:add")
    @PostMapping("/save")
    @SaveLog(module = "菜单管理", type = LogType.ADD)
    public String save(@RequestBody MenuEntity menuEntity) {
        MenuEntity result = menuService.saveEntity(menuEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param menuEntity 菜单bean
     * @return String
     */
    @HasPermission("menu:update")
    @PostMapping("/update")
    @SaveLog(module = "菜单管理", type = LogType.UPDATE)
    public String update(@RequestBody MenuEntity menuEntity) {
        MenuEntity result = menuService.updateEntity(menuEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param menuEntity 菜单bean
     * @return String
     */
    @HasPermission("menu:delete")
    @PostMapping("/delete")
    @SaveLog(module = "菜单管理", type = LogType.DELETE)
    public String delete(@RequestBody MenuEntity menuEntity) {
        return Result.status(menuService.delete(menuEntity));
    }

    /**
     * 列表（不分页）
     *
     * @param menuEntity 菜单bean
     * @return String
     */
    @GetMapping("/list")
    public String list(MenuEntity menuEntity) {
        List<MenuEntity> list = menuService.findList(menuEntity);
        return Result.data(list);
    }

    /**
     * 树形结构
     *
     * @param menuEntity 菜单bean
     * @return String
     */
    @GetMapping("/tree")
    public String tree(MenuEntity menuEntity) {
        List<MenuEntity> list = menuService.getTree(menuEntity);
        return Result.data(list);
    }

    /**
     * 树形结构格式化
     *
     * @param menuEntity 菜单bean
     * @return String
     */
    @GetMapping("/treeFormat")
    public String treeFormat(MenuEntity menuEntity) {
        List<Map<String, Object>> list = menuService.getTreeFormat(menuEntity);
        return Result.data(list);
    }

    /**
     * 树形结构格式化带按钮
     *
     * @param menuEntity 菜单bean
     * @return String
     */
    @GetMapping("/buttonTreeFormat")
    public String buttonTreeFormat(MenuEntity menuEntity) {
        List<Map<String, Object>> list = menuService.getButtonTreeFormat(menuEntity);
        return Result.data(list);
    }


    /**
     * 树形结构格式化带数据权限
     *
     * @param menuEntity 菜单bean
     * @return String
     */
    @GetMapping("/scopeTreeFormat")
    public String scopeTreeFormat(MenuEntity menuEntity) {
        List<Map<String, Object>> list = menuService.getScopeTreeFormat(menuEntity);
        return Result.data(list);
    }


    /**
     * 生成菜单
     *
     * @param genTable bean
     * @return Object
     */
    @PostMapping("/saveMenu")
    public String saveMenu(@RequestBody GenTableEntity genTable) {
        String routeNamePrefix = "";
        String routePathPrefix = "";
        String parentId = INT_ZERO;
        if (StringUtil.isNotBlank(genTable.getMenuId())) {
            MenuEntity parent = menuService.getById(genTable.getMenuId());
            parentId = parent.getId();
            routeNamePrefix = parent.getRouteName() + "_";
            routePathPrefix = parent.getRoutePath();

        }
        MenuEntity menu = new MenuEntity();
        menu.setMenuType(MENU_TYPE_2);
        menu.setMenuName(genTable.getComments());
        String className = StrUtil.lowerFirst(genTable.getClassName());
        String routeName = StrUtil.toUnderlineCase(genTable.getClassName());
        String replace = routeName.replace("_", "-");
        menu.setRouteName(routeNamePrefix + replace);
        menu.setRoutePath(routePathPrefix + "/" + replace);
        menu.setParentId(parentId);
        if (menu.getParentId().equals(INT_ZERO)) {
            // 菜单设置component
            menu.setComponent("layout.base$view." + menu.getRouteName());
        } else {
            // 菜单设置component
            menu.setComponent("view." + menu.getRouteName());
        }
        menu.setI18nKey("route." + menu.getRouteName());
        menu.setIconType("1");
        menu.setStatus(YES);
        menu.setKeepAlive(true);
        menu.setConstant(false);
        menu.setMultiTab(false);
        menu.setOrder(menuService.findNextOrderByParentId(menu.getParentId()));
        // 权重
        menu.setWeight(MENU_WEIGHT_DEFAULT);
        List<ButtonsEntity> list = new ArrayList<>();
        boolean addStatus = true;
        boolean updateStatus = true;
        boolean deleteStatus = true;
        boolean importStatus = true;
        boolean exportStatus = true;
        JSONObject options = genTable.getOptions();
        if (options != null) {
            Boolean addValue = options.getBoolean("addStatus");
            Boolean updateValue = options.getBoolean("updateStatus");
            Boolean deleteValue = options.getBoolean("deleteStatus");
            Boolean importValue = options.getBoolean("importStatus");
            Boolean exportValue = options.getBoolean("exportStatus");
            if (addValue != null) {
                addStatus = addValue;
            }
            if (updateValue != null) {
                updateStatus = updateValue;
            }
            if (deleteValue != null) {
                deleteStatus = deleteValue;
            }
            if (importValue != null) {
                importStatus = importValue;
            }
            if (exportValue != null) {
                exportStatus = exportValue;
            }
        }
        if (addStatus) {
            ButtonsEntity addButton = new ButtonsEntity();
            addButton.setCode(className + ":add");
            addButton.setName(genTable.getComments() + "新增");
            list.add(addButton);
        }
        if (updateStatus) {
            ButtonsEntity updateButton = new ButtonsEntity();
            updateButton.setCode(className + ":update");
            updateButton.setName(genTable.getComments() + "修改");
            list.add(updateButton);
        }
        if (deleteStatus) {
            ButtonsEntity deleteButton = new ButtonsEntity();
            deleteButton.setCode(className + ":delete");
            deleteButton.setName(genTable.getComments() + "删除");
            list.add(deleteButton);
        }
        if (exportStatus) {
            ButtonsEntity exportButton = new ButtonsEntity();
            exportButton.setCode(className + ":export");
            exportButton.setName(genTable.getComments() + "导出");
            list.add(exportButton);
        }
        if (importStatus) {
            ButtonsEntity importButton = new ButtonsEntity();
            importButton.setCode(className + ":import");
            importButton.setName(genTable.getComments() + "导入");
            list.add(importButton);

        }
        menu.setButtons(list);
        MenuEntity result = menuService.saveEntity(menu);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }
}
