package com.smart.auth.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.smart.common.utils.AuthUtil;
import com.smart.common.utils.IOUtil;
import com.smart.common.utils.ListUtil;
import com.smart.common.utils.StringUtil;
import com.smart.model.response.r.Result;
import com.smart.model.route.ConstantRouter;
import com.smart.model.route.MenuRouter;
import com.smart.service.system.MenuService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

/**
 * 路由
 *
 * @author wf
 * @version 1.0.0
 * @since 2024/6/16
 */
@Slf4j
@RestController
@RequestMapping("/route")
@AllArgsConstructor
public class RouterController {
    MenuService menuService;

    /**
     * 获取用户路由
     *
     * @return String
     */
    @GetMapping("/getUserRoutes")
    public String getUserRoutes() {
        String roleId = AuthUtil.getRoleId();
        if (StringUtil.isBlank(roleId)) {
            return Result.fail("获取用户角色失败");
        }
        List<MenuRouter> routers = menuService.findRouterByRoleId(roleId);
        String value = IOUtil.getValue("json/router.json");
        JSONObject json = JSONObject.parseObject(value);
        List<MenuRouter> all = ListUtil.newArrayList(routers);
        List<MenuRouter> userRoute = JSONArray.parseArray(json.getString("userRoute"), MenuRouter.class);
        if (userRoute != null) {
            all.addAll(userRoute);
        }
        all.sort(Comparator.comparing(x -> x.getMeta().getOrder()));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("home", "home");
        jsonObject.put("routes", all);
        return Result.data(jsonObject);
    }

    /**
     * 路由是否存在
     *
     * @return String
     */
    @GetMapping("/isRouteExist")
    public String isRouteExist(String routeName) {
        return Result.data(true);
    }

    /**
     * 获取常量路由
     *
     * @return String
     */
    @GetMapping("/getConstantRoutes")
    public String getConstantRoutes() {
        String value = IOUtil.getValue("json/router.json");
        JSONObject json = JSONObject.parseObject(value);
        return Result.data(JSONArray.parseArray(json.getString("constantRoute"), ConstantRouter.class));
    }

}
