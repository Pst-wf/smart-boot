package com.smart.auth.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.auth.model.SmartFrontUser;
import com.smart.auth.model.SmartUser;
import com.smart.auth.utils.TokenUtil;
import com.smart.common.constant.AuthConstant;
import com.smart.common.constant.SmartConstant;
import com.smart.common.utils.AesCbcUtil;
import com.smart.common.utils.AuthUtil;
import com.smart.common.utils.ListUtil;
import com.smart.common.utils.StringUtil;
import com.smart.entity.system.FrontUserEntity;
import com.smart.entity.system.IdentityEntity;
import com.smart.entity.system.TenantEntity;
import com.smart.entity.system.UserEntity;
import com.smart.model.exception.SmartException;
import com.smart.model.response.r.ResultCode;
import com.smart.service.system.ConfigService;
import com.smart.service.system.FrontUserService;
import com.smart.service.system.TenantService;
import com.smart.service.system.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.smart.common.constant.AuthConstant.*;
import static com.smart.common.constant.SmartConstant.NO;
import static com.smart.common.constant.SmartConstant.SYSTEM_ID;
import static com.smart.model.response.r.MessageProperties.*;

/**
 * 用户信息
 *
 * @author wf
 */
@Slf4j
@Service
@AllArgsConstructor
public class SmartUserDetailsServiceImpl implements SmartUserDetailsService {
    @Autowired
    UserService userService;
    @Autowired
    FrontUserService frontUserService;
    @Autowired
    TenantService tenantService;
    @Autowired
    ConfigService configService;

    /**
     * 获取用户信息
     *
     * @param username 账号
     * @param userType 用户类型
     * @return 用户信息
     * @throws UsernameNotFoundException 异常
     */
    @Override
    public UserDetails loadUserByUsername(String username, String userType) throws UsernameNotFoundException {
        HttpServletRequest request = AuthUtil.getRequest();
        if (request == null) {
            throw new RuntimeException("获取request失败！");
        }
        // 获取租户ID
        String tenantId = request.getHeader(TENANT_HEADER_KEY);
        if (StringUtil.isBlank(tenantId)) {
            throw new UserDeniedAuthorizationException(TENANT_NOT_FOUND);
        }
        System.err.println("【登录的后台用户的tenantId: " + tenantId + "】");
        // 获取租户信息
        TenantEntity tenant = tenantService.getOne(new LambdaQueryWrapper<TenantEntity>().eq(TenantEntity::getTenantId, tenantId));
        if (TokenUtil.judgeTenant(tenant)) {
            throw new UserDeniedAuthorizationException(USER_HAS_NO_TENANT_PERMISSION);
        }
        if (StringUtil.isBlank(username)) {
            throw new SmartException("账号获取失败！");
        }
        if (StringUtil.isBlank(userType)) {
            throw new SmartException("用户类型获取失败！");
        }
        if (userType.equals(USER_TYPE_PC)) {
            return getUserByUsername(username, tenantId, request);
        } else if (userType.equals(USER_TYPE_APP)) {
            return getFrontUserByUsername(username, tenantId);
        } else {
            throw new SmartException("用户类型异常！");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByUsername(username, USER_TYPE_PC);
    }

    /**
     * 获取用户信息
     *
     * @param openId   微信OpenId
     * @param userType 用户类型
     * @return 用户信息
     * @throws UsernameNotFoundException 异常
     */
    @Override
    public UserDetails loadUserByOpenId(String openId, String userType) throws UsernameNotFoundException {
        HttpServletRequest request = AuthUtil.getRequest();
        if (request == null) {
            throw new RuntimeException("获取request失败！");
        }
        // 获取租户ID
        String tenantId = request.getHeader(TENANT_HEADER_KEY);
        if (StringUtil.isBlank(tenantId)) {
            throw new UserDeniedAuthorizationException(TENANT_NOT_FOUND);
        }
        System.err.println("【登录的后台用户的tenantId: " + tenantId + "】");
        // 获取租户信息
        TenantEntity tenant = tenantService.getOne(new LambdaQueryWrapper<TenantEntity>().eq(TenantEntity::getTenantId, tenantId));
        if (TokenUtil.judgeTenant(tenant)) {
            throw new UserDeniedAuthorizationException(USER_HAS_NO_TENANT_PERMISSION);
        }

        if (StringUtil.isBlank(openId)) {
            throw new SmartException("openId获取失败！");
        }
        if (StringUtil.isBlank(userType)) {
            throw new SmartException("用户类型获取失败！");
        }
        if (userType.equals(USER_TYPE_PC)) {
            return getUserByOpenId(openId, tenantId, request);
        } else if (userType.equals(USER_TYPE_APP)) {
            return getFrontUserByOpenId(openId, tenantId);
        } else {
            throw new SmartException("用户类型异常！");
        }
    }

    /**
     * 获取后台用户信息
     *
     * @param username 账号
     * @param tenantId 租户ID
     * @param request  请求
     * @return SmartUser
     */
    private SmartUser getUserByUsername(String username, String tenantId, HttpServletRequest request) {
        System.err.println("【登录的后台用户的username: " + username + "】");
        // 获取当前身份信息
        String identityId = request.getHeader(IDENTITY_ID);
        System.err.println("【登录的后台用户的identityId: " + identityId + "】");
        String deptId = null;
        String deptName = null;
        String postId = null;
        String postName = null;
        String roleId = null;
        String roleName = null;
        String organizationId = null;
        String organizationName = null;
        //根据用户名查询是否有用户
        UserEntity user = userService.getOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUsername, username));
        if (user == null) {
            throw new SmartException(ResultCode.USER_NOT_FOUND);
        }
        // 账号状态
        String userStatus = user.getUserStatus();
        if (StringUtil.notBlankAndEquals(userStatus, NO)) {
            throw new SmartException(ResultCode.USER_FROZEN);
        }
        List<IdentityEntity> identityList;
        if (SYSTEM_ID.equals(user.getId())) {
            IdentityEntity identity = new IdentityEntity();
            identityId = SYSTEM_ID;
            roleId = SYSTEM_ID;
            deptName = "无";
            organizationName = "无";
            postName = "无";
            roleName = "超级管理员";
            identity.setRoleId(SYSTEM_ID);
            identity.setUserId(SYSTEM_ID);
            identity.setPostName(postName);
            identity.setRoleName(roleName);
            identity.setOrganizationName(organizationName);
            identity.setDeptName(deptName);
            identityList = ListUtil.newArrayList(identity);
        } else {
            UserEntity userWithIdentity = userService.getUserWithIdentity(user.getId());
            identityList = userWithIdentity.getIdentityList();
            if (StringUtil.isNotBlank(identityId)) {
                String finalIdentityId = identityId;
                identityList = identityList.stream().filter(x -> finalIdentityId.equals(x.getId())).collect(Collectors.toList());
                if (!identityList.isEmpty()) {
                    deptId = identityList.get(0).getDeptId();
                    organizationId = identityList.get(0).getOrganizationId();
                    postId = identityList.get(0).getPostId();
                    roleId = identityList.get(0).getRoleId();
                    deptName = identityList.get(0).getDeptName();
                    organizationName = identityList.get(0).getOrganizationName();
                    postName = identityList.get(0).getPostName();
                    roleName = identityList.get(0).getRoleName();
                }
            } else {
                if (identityList.size() == 1) {
                    identityId = identityList.get(0).getId();
                    deptId = identityList.get(0).getDeptId();
                    organizationId = identityList.get(0).getOrganizationId();
                    postId = identityList.get(0).getPostId();
                    roleId = identityList.get(0).getRoleId();
                    deptName = identityList.get(0).getDeptName();
                    organizationName = identityList.get(0).getOrganizationName();
                    postName = identityList.get(0).getPostName();
                    roleName = identityList.get(0).getRoleName();
                }
            }

        }
        return new SmartUser(
                user.getUsername(),
                AuthConstant.ENCRYPT + user.getPassword(),
                user.getId(),
                user.getGender(),
                user.getPhone(),
                user.getEmail(),
                user.getAvatar(),
                SYSTEM_ID.equals(user.getId()),
                tenantId,
                user.getNickname(),
                identityId,
                deptId,
                deptName,
                postId,
                postName,
                roleId,
                roleName,
                organizationId,
                organizationName,
                identityList,
                user.getRemarks(),
                true,
                true,
                true,
                true, new ArrayList<>());
    }

    /**
     * 获取前台用户信息
     *
     * @param username 账号
     * @param tenantId 租户ID
     * @return SmartUser
     */
    private SmartFrontUser getFrontUserByUsername(String username, String tenantId) {
        System.err.println("【登录的前台用户的username: " + username + "】");
        System.err.println("【登录的前台用户的tenantId: " + tenantId + "】");
        //根据用户名查询是否有用户
        FrontUserEntity user = frontUserService.getOne(new LambdaQueryWrapper<FrontUserEntity>().eq(FrontUserEntity::getUsername, username));
        if (user == null) {
            throw new UserDeniedAuthorizationException(USER_NOT_FOUND);
        }
        // 账号状态
        String userStatus = user.getUserStatus();
        if (StringUtil.notBlankAndEquals(userStatus, NO)) {
            throw new UserDeniedAuthorizationException(USER_FROZEN);
        }
        return new SmartFrontUser(
                user.getUsername(),
                AuthConstant.ENCRYPT + user.getPassword(),
                user.getId(),
                user.getGender(),
                user.getPhone(),
                user.getAvatar(),
                tenantId,
                user.getNickname(),
                true,
                true,
                true,
                true, new ArrayList<>());
    }


    /**
     * 获取后台用户信息
     *
     * @param openId   账号
     * @param tenantId 租户ID
     * @param request  请求
     * @return SmartUser
     */
    private SmartUser getUserByOpenId(String openId, String tenantId, HttpServletRequest request) {
        System.err.println("【登录的后台用户的openId: " + openId + "】");
        // 获取当前身份信息
        String identityId = request.getHeader(IDENTITY_ID);
        System.err.println("【登录的后台用户的identityId: " + identityId + "】");
        String deptId = null;
        String deptName = null;
        String postId = null;
        String postName = null;
        String roleId = null;
        String roleName = null;
        String organizationId = null;
        String organizationName = null;
        //根据用户名查询是否有用户
        UserEntity user = userService.getOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getWxOpenid, openId));
        if (user == null) {
            return null;
        }
        // 账号状态
        String userStatus = user.getUserStatus();
        if (StringUtil.notBlankAndEquals(userStatus, NO)) {
            throw new UserDeniedAuthorizationException(USER_FROZEN);
        }
        List<IdentityEntity> identityList;
        if (SYSTEM_ID.equals(user.getId())) {
            IdentityEntity identity = new IdentityEntity();
            identityId = SYSTEM_ID;
            roleId = SYSTEM_ID;
            deptName = "无";
            organizationName = "无";
            postName = "无";
            roleName = "超级管理员";
            identity.setRoleId(SYSTEM_ID);
            identity.setUserId(SYSTEM_ID);
            identity.setPostName(postName);
            identity.setRoleName(roleName);
            identity.setOrganizationName(organizationName);
            identity.setDeptName(deptName);
            identityList = ListUtil.newArrayList(identity);
        } else {
            UserEntity userWithIdentity = userService.getUserWithIdentity(user.getId());
            identityList = userWithIdentity.getIdentityList();
            if (StringUtil.isNotBlank(identityId)) {
                String finalIdentityId = identityId;
                identityList = identityList.stream().filter(x -> finalIdentityId.equals(x.getId())).collect(Collectors.toList());
                if (!identityList.isEmpty()) {
                    deptId = identityList.get(0).getDeptId();
                    organizationId = identityList.get(0).getOrganizationId();
                    postId = identityList.get(0).getPostId();
                    roleId = identityList.get(0).getRoleId();
                    deptName = identityList.get(0).getDeptName();
                    organizationName = identityList.get(0).getOrganizationName();
                    postName = identityList.get(0).getPostName();
                    roleName = identityList.get(0).getRoleName();
                }
            } else {
                if (identityList.size() == 1) {
                    identityId = identityList.get(0).getId();
                    deptId = identityList.get(0).getDeptId();
                    organizationId = identityList.get(0).getOrganizationId();
                    postId = identityList.get(0).getPostId();
                    roleId = identityList.get(0).getRoleId();
                    deptName = identityList.get(0).getDeptName();
                    organizationName = identityList.get(0).getOrganizationName();
                    postName = identityList.get(0).getPostName();
                    roleName = identityList.get(0).getRoleName();
                }
            }

        }
        return new SmartUser(
                user.getUsername(),
                AuthConstant.ENCRYPT + user.getPassword(),
                user.getId(),
                user.getGender(),
                user.getPhone(),
                user.getEmail(),
                user.getAvatar(),
                SYSTEM_ID.equals(user.getId()),
                tenantId,
                user.getNickname(),
                identityId,
                deptId,
                deptName,
                postId,
                postName,
                roleId,
                roleName,
                organizationId,
                organizationName,
                identityList,
                user.getRemarks(),
                true,
                true,
                true,
                true, new ArrayList<>());
    }

    /**
     * 获取前台用户信息
     *
     * @param openId   openId
     * @param tenantId 租户ID
     * @return SmartUser
     */
    private SmartFrontUser getFrontUserByOpenId(String openId, String tenantId) {
        System.err.println("【登录的前台用户的openId: " + openId + "】");
        System.err.println("【登录的前台用户的tenantId: " + tenantId + "】");
        //根据用户名查询是否有用户
        FrontUserEntity user = frontUserService.getOne(new LambdaQueryWrapper<FrontUserEntity>().eq(FrontUserEntity::getWxOpenid, openId));
        if (user == null) {
            return null;
        }
        // 账号状态
        String userStatus = user.getUserStatus();
        if (StringUtil.notBlankAndEquals(userStatus, NO)) {
            throw new UserDeniedAuthorizationException(USER_FROZEN);
        }
        return new SmartFrontUser(
                user.getUsername(),
                AuthConstant.ENCRYPT + user.getPassword(),
                user.getId(),
                user.getGender(),
                user.getPhone(),
                user.getAvatar(),
                tenantId,
                user.getNickname(),
                true,
                true,
                true,
                true, new ArrayList<>());
    }

    /**
     * 创建前台用户
     *
     * @param params 参数
     * @return SmartUser
     */
    public SmartFrontUser createFrontUserByWechat(Map<String, String> params) {
        try {
            HttpServletRequest request = AuthUtil.getRequest();
            if (request == null) {
                throw new RuntimeException("获取request失败！");
            }
            String tenantId = request.getHeader(TENANT_HEADER_KEY);
            String encryptedData = params.get("encryptedData");
            String sessionKey = params.get("sessionKey");
            String userType = params.get("userType");
            String iv = params.get("iv");
            log.info("encryptedData = > {}", encryptedData);
            log.info("iv = > {}", iv);
            log.info("userType = > {}", userType);
            if (StringUtil.isBlank(userType)) {
                throw new SmartException("用户类型获取失败！");
            }
            String str = AesCbcUtil.decrypt(encryptedData, sessionKey, iv, "UTF-8");
            log.info("小程序 -> 解密结果 ====> " + str);
            JSONObject jsonObject = JSONObject.parseObject(str);
            // 通过手机号创建用户
            String phoneNumber = jsonObject.getString("phoneNumber");
            if (StringUtil.isBlank(phoneNumber)) {
                throw new SmartException("用户手机号获取失败！");
            }
            String nickName = StringUtil.isBlank(jsonObject.getString("nickName")) ? "游客" + System.currentTimeMillis() : jsonObject.getString("nickName");
            String gender = jsonObject.getString("gender");
            if (StringUtil.isNotBlank(gender)) {
                gender = gender.equals("0") ? "1" : "2";
            }
            String avatarUrl = jsonObject.getString("avatarUrl");
            String openId = params.get("openId");
            String unionid = params.get("unionId");

            if (userType.equals(USER_TYPE_PC)) {
                throw new SmartException("暂未开放PC端用户授权自主注册！");
            } else if (userType.equals(USER_TYPE_APP)) {
                FrontUserEntity user = frontUserService.createAppUserByWechat(phoneNumber, nickName, gender, avatarUrl, openId, unionid);
                if (user == null) {
                    throw new SmartException("用户注册失败！");
                }
                return new SmartFrontUser(
                        user.getUsername(),
                        AuthConstant.ENCRYPT + user.getPassword(),
                        user.getId(),
                        user.getGender(),
                        user.getPhone(),
                        user.getAvatar(),
                        tenantId,
                        user.getNickname(),
                        true,
                        true,
                        true,
                        true, new ArrayList<>());
            } else {
                throw new SmartException("用户类型异常！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SmartException(e.getMessage());
        }

    }

    /**
     * 检查是否开启自主注册
     *
     * @return boolean
     */
    @Override
    public boolean checkAutoSignUp() {
        String autoSignUp = configService.getConfig("auto_sign_up");
        return StringUtil.notBlankAndEquals(autoSignUp, SmartConstant.YES);
    }
}
