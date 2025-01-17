//package com.smart.system.controller;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.toolkit.StringUtils;
//import com.smart.auth.utils.AuthUtil;
//import com.smart.common.utils.CacheUtil;
//import com.smart.common.utils.IPUtil;
//import com.smart.common.utils.Md5Util;
//import com.smart.entity.system.*;
//import com.smart.model.response.r.Result;
//import com.smart.model.response.r.ResultCode;
//import com.smart.service.system.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.smart.common.constant.SmartConstant.*;
//
///**
// * 登录
// *
// * @author wf
// * @since 2022-07-26 00:00:00
// */
//@RestController
//@RequestMapping("/account")
//@Slf4j
//public class LoginController {
//    @Autowired
//    UserService userService;
//    @Autowired
//    MenuService menuService;
//    @Autowired
//    RoleService roleService;
//    @Autowired
//    ScopeService scopeService;
//    @Autowired
//    IdentityInfoService identityInfoService;
//    @Autowired
//    LoginLogService loginLogService;
//    @Autowired
//    ConfigService configService;
//
//    /**
//     * 登录验证
//     *
//     * @param userEntity 用户bean
//     * @return String
//     */
//    @PostMapping("/login")
//    public String login(@RequestBody UserEntity userEntity) {
//        //根据用户名查询是否有用户
//        UserEntity user = userService.getOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUsername, userEntity.getUsername()));
//        if (user == null) {
//            return Result.fail("用户不存在");
//        } else {
//            //页面输入的密码 经过MD5加密
//            String password = Md5Util.md5(userEntity.getPassword(), user.getUsername());
//            if (!user.getPassword().equals(password)) {
//                return Result.fail("密码不正确");
//            }
//            if (NO.equals(user.getUserStatus())) {
//                return Result.fail("用户已停用");
//            } else {
//                //获取角色及菜单路由
//                user = userService.getUserWithIdentity(user.getId());
//                if (SYSTEM_ID.equals(user.getId())) {
//                    //system账号
//                    List<IdentityEntity> list = new ArrayList<>();
//                    IdentityEntity identityEntity = new IdentityEntity();
//                    identityEntity.setId(SYSTEM_ID);
//                    identityEntity.setRoleId(SYSTEM_ID);
//                    identityEntity.setRoleName(SYSTEM_NAME);
//                    identityEntity.setDeptName("无");
//                    identityEntity.setPostName("无");
//                    identityEntity.setOrganizationName("无");
//                    list.add(identityEntity);
//                    user.setIdentityList(list);
//                }
//                return Result.data(user);
//            }
//        }
//    }
//
//    /**
//     * 获取身份
//     *
//     * @param userEntity 用户bean
//     * @param request    请求
//     * @return String
//     */
//    @PostMapping("/loginByIdentity")
//    public String loginByIdentity(@RequestBody UserEntity userEntity, HttpServletRequest request) {
//        UserEntity user;
//        List<IdentityEntity> identityList = new ArrayList<>();
//
//        if (SYSTEM_ID.equals(userEntity.getIdentityId())) {
//            user = userService.getById(SYSTEM_ID);
//            RoleEntity roleEntity = new RoleEntity();
//            roleEntity.setId(SYSTEM_ID);
//            roleEntity.setRoleName("超级管理员");
//            roleEntity.setMenuList(menuService.findMenuList());
//            IdentityEntity identityEntity = new IdentityEntity();
//            identityEntity.setId(SYSTEM_ID);
//            identityEntity.setRoleName("超级管理员");
//            identityEntity.setPostName("无");
//            identityEntity.setDeptName("无");
//            identityEntity.setOrganizationName("无");
//            identityEntity.setRoleEntity(roleEntity);
//            identityList.add(identityEntity);
//            user.setIdentityInfo("无");
//        } else {
//            IdentityEntity identityEntity = identityInfoService.getInfo(userEntity.getIdentityId());
//            //查询是否有用户
//            user = userService.getById(identityEntity.getUserId());
//            if (user == null) {
//                return Result.fail("用户不存在");
//            }
//            //获取角色及菜单路由
//            RoleEntity roleEntity = roleService.getById(identityEntity.getRoleId());
//            roleEntity.setMenuList(menuService.findMenuByRoleId(roleEntity.getId(), true));
//            identityEntity.setRoleEntity(roleEntity);
//            identityList.add(identityEntity);
//            if (identityEntity.getDeptId().equals(identityEntity.getOrganizationId())) {
//                user.setIdentityInfo(identityEntity.getOrganizationName() + " - " + identityEntity.getPostName());
//            } else {
//                user.setIdentityInfo(identityEntity.getOrganizationName() + " - " + identityEntity.getDeptName() + " - " + identityEntity.getPostName());
//            }
//            // 登录成功权限存入缓存
//            List<ScopeEntity> scopeList = scopeService.getByRoleId(identityEntity.getRoleId());
//            CacheUtil.put("scope", identityEntity.getId(), scopeList);
//        }
//        user.setIdentityList(identityList);
//        String ip = IPUtil.getIpAddress(request);
//        //更新登录IP和最后登录时间
//        userService.updateLoginInfo(ip, user.getId());
//        //根据用户和角色生成token
//        String token = userService.getToken(user);
//        user.setToken(token);
//        // 记录登录日志
//        LoginLogEntity loginLogEntity = new LoginLogEntity();
//        loginLogEntity.setUserId(user.getId());
//        loginLogEntity.setUserNickname(user.getNickname());
//        loginLogEntity.setUsername(user.getUsername());
//        loginLogEntity.setIp(ip);
//        loginLogEntity.setGrantType("PC");
//        loginLogService.saveEntity(loginLogEntity);
//        return Result.data(user);
//    }
//
//    /**
//     * 小程序端登录
//     *
//     * @param userEntity 用户bean
//     * @param request    请求
//     * @return String
//     */
//    @PostMapping("/loginByWechat")
//    public String loginByWechat(@RequestBody UserEntity userEntity, HttpServletRequest request) {
//        //根据用户名查询是否有用户
//        UserEntity user = userService.getOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUsername, userEntity.getUsername()));
//        if (user == null) {
//            return Result.fail("用户不存在");
//        } else {
//            //页面输入的密码 经过MD5加密
//            String password = Md5Util.md5(userEntity.getPassword(), user.getUsername());
//            if (!user.getPassword().equals(password)) {
//                return Result.fail("密码不正确");
//            }
//            if (NO.equals(user.getUserStatus())) {
//                return Result.fail("用户已停用");
//            } else {
//                List<IdentityEntity> list = identityInfoService.list(new LambdaQueryWrapper<IdentityEntity>().eq(IdentityEntity::getUserId, user.getId()));
//                if (list.isEmpty()) {
//                    return Result.fail("用户身份信息为空");
//                }
//                userEntity.setIdentityId(list.get(0).getId());
//                List<IdentityEntity> identityList = new ArrayList<>();
//                IdentityEntity identityEntity = identityInfoService.getInfo(userEntity.getIdentityId());
//                //查询是否有用户
//                user = userService.getById(identityEntity.getUserId());
//                if (user == null) {
//                    return Result.fail("用户不存在");
//                }
//                //获取角色及菜单路由
//                RoleEntity roleEntity = roleService.getById(identityEntity.getRoleId());
//                roleEntity.setMenuList(menuService.findMenuByRoleId(roleEntity.getId(), true));
//                identityEntity.setRoleEntity(roleEntity);
//                identityList.add(identityEntity);
//                if (identityEntity.getDeptId().equals(identityEntity.getOrganizationId())) {
//                    user.setIdentityInfo(identityEntity.getOrganizationName() + " - " + identityEntity.getPostName());
//                } else {
//                    user.setIdentityInfo(identityEntity.getOrganizationName() + " - " + identityEntity.getDeptName() + " - " + identityEntity.getPostName());
//                }
//                // 登录成功权限存入缓存
//                List<ScopeEntity> scopeList = scopeService.getByRoleId(identityEntity.getRoleId());
//                CacheUtil.put("scope", identityEntity.getId(), scopeList);
//                user.setIdentityList(identityList);
//                String ip = IPUtil.getIpAddress(request);
//                //更新登录IP和最后登录时间
//                userService.updateLoginInfo(ip, user.getId());
//                //根据用户和角色生成token
//                String token = userService.getToken(user);
//                user.setToken(token);
//                // 记录登录日志
//                LoginLogEntity loginLogEntity = new LoginLogEntity();
//                loginLogEntity.setUserId(user.getId());
//                loginLogEntity.setUserNickname(user.getNickname());
//                loginLogEntity.setUsername(user.getUsername());
//                loginLogEntity.setIp(ip);
//                loginLogEntity.setGrantType("WECHAT");
//                loginLogService.saveEntity(loginLogEntity);
//                return Result.data(user);
//            }
//        }
//    }
//
//    /**
//     * 获取登陆人信息及所属菜单
//     *
//     * @return String
//     */
//    @PostMapping("/getInfo")
//    public String getInfo() {
//        UserEntity user;
//        List<IdentityEntity> identityList = new ArrayList<>();
//        String identityId = AuthUtil.getIdentityId();
//        if (StringUtils.isBlank(identityId)) {
//            return Result.fail(ResultCode.OVER_TIME);
//        }
//        if (SYSTEM_ID.equals(identityId)) {
//            user = userService.getById("system");
//            RoleEntity roleEntity = new RoleEntity();
//            roleEntity.setId("system");
//            roleEntity.setRoleName("超级管理员");
//            roleEntity.setMenuList(menuService.findMenuList());
//            IdentityEntity identityEntity = new IdentityEntity();
//            identityEntity.setId("system");
//            identityEntity.setRoleName("超级管理员");
//            identityEntity.setPostName("无");
//            identityEntity.setDeptName("无");
//            identityEntity.setOrganizationName("无");
//            identityEntity.setRoleEntity(roleEntity);
//            identityList.add(identityEntity);
//            user.setIdentityInfo("无");
//        } else {
//            IdentityEntity identityEntity = identityInfoService.getInfo(identityId);
//            //查询是否有用户
//            user = userService.getById(identityEntity.getUserId());
//            if (user == null) {
//                return Result.fail("用户不存在");
//            }
//            //获取角色及菜单路由
//            RoleEntity roleEntity = roleService.getById(identityEntity.getRoleId());
//            roleEntity.setMenuList(menuService.findMenuByRoleId(roleEntity.getId(), true));
//            identityEntity.setRoleEntity(roleEntity);
//            identityList.add(identityEntity);
//            if (identityEntity.getDeptId().equals(identityEntity.getOrganizationId())) {
//                user.setIdentityInfo(identityEntity.getOrganizationName() + " - " + identityEntity.getPostName());
//            } else {
//                user.setIdentityInfo(identityEntity.getOrganizationName() + " - " + identityEntity.getDeptName() + " - " + identityEntity.getPostName());
//            }
//            // 登录成功权限存入缓存
//            List<ScopeEntity> scopeList = scopeService.getByRoleId(identityEntity.getRoleId());
//            CacheUtil.put("scope", identityEntity.getId(), scopeList);
//        }
//        user.setIdentityList(identityList);
//        return Result.data(user);
//    }
//
//    /**
//     * 退出登录
//     *
//     * @param request 请求
//     * @return String
//     */
//    @PostMapping("/logout")
//    public String logout(HttpServletRequest request) {
//        String identityId = AuthUtil.getIdentityId(request);
//        if (StringUtils.isNotBlank(identityId)) {
//            // 清除缓存
//            CacheUtil.evict("permission", identityId);
//            CacheUtil.evict("scope", identityId);
//        }
//        return Result.success("登出成功！");
//    }
//
//    /**
//     * 切换身份
//     *
//     * @param userEntity 用户bean
//     * @return String
//     */
//    @PostMapping("/change")
//    public String change(@RequestBody UserEntity userEntity) {
//        //获取角色及菜单路由
//        UserEntity user = userService.getUserWithIdentity(userEntity.getId());
//        if (user == null) {
//            return Result.fail("用户不存在！");
//        }
//        if (SYSTEM_ID.equals(user.getId())) {
//            //system账号
//            List<IdentityEntity> list = new ArrayList<>();
//            IdentityEntity identityEntity = new IdentityEntity();
//            identityEntity.setId("system");
//            identityEntity.setRoleId("system");
//            identityEntity.setRoleName("超级管理员");
//            identityEntity.setDeptName("无");
//            identityEntity.setPostName("无");
//            identityEntity.setOrganizationName("无");
//            list.add(identityEntity);
//            user.setIdentityList(list);
//        }
//        return Result.data(user);
//    }
//}
//
