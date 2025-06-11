package com.smart.gateway.interceptor;

import com.smart.common.utils.AuthUtil;
import com.smart.common.utils.CacheUtil;
import com.smart.common.utils.StringUtil;
import com.smart.common.vo.UserVO;
import com.smart.entity.system.FrontUserEntity;
import com.smart.entity.system.UserEntity;
import com.smart.common.gateway.provider.AuthProvider;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.system.FrontUserService;
import com.smart.service.system.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static com.smart.common.constant.AuthConstant.USER_TYPE_APP;
import static com.smart.common.constant.AuthConstant.USER_TYPE_PC;
import static com.smart.common.constant.SmartConstant.NO;


/**
 * 拦截器
 *
 * @author wf
 * @since 2022-08-06
 */
@Slf4j
@Component
public class SmartInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;
    @Autowired
    FrontUserService frontUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String url = request.getRequestURI();
        if (isSkip(url)) {
            return true;
        }
        UserVO user = AuthUtil.getUser(request);
        PrintWriter out;
        String r;
        if (user == null) {
            r = Result.fail(ResultCode.NOT_LOGIN,url);
        } else {
            if (StringUtil.isBlank(user.getUserType())) {
                r = Result.fail(ResultCode.USER_TYPE_ERROR);
            } else {
                if (user.getUserType().equals(USER_TYPE_PC)) {
                    if (StringUtil.isBlank(user.getIdentityId())) {
                        r = Result.fail(ResultCode.UN_CHOOSE_IDENTITY);
                    } else {
                        UserEntity cacheUser = userService.getCacheUser(user.getUserId());
                        if (cacheUser != null) {
                            // 账号状态
                            String userStatus = cacheUser.getUserStatus();
                            if (StringUtil.notBlankAndEquals(userStatus, NO)) {
                                //账号已停用
                                r = Result.fail(ResultCode.USER_FROZEN);
                            } else {
                                return true;
                            }
                        } else {
                            // 获取用户信息失败
                            log.error("获取PC端用户信息失败 -> {}", user.getUserId());
                            r = Result.fail(ResultCode.USER_INFO_ERROR);
                        }
                        CacheUtil.evict("user", user.getUserId());
                    }
                } else if (user.getUserType().equals(USER_TYPE_APP)) {
                    FrontUserEntity cacheUser = frontUserService.getCacheUser(user.getUserId());
                    if (cacheUser != null) {
                        // 账号状态
                        String userStatus = cacheUser.getUserStatus();
                        if (StringUtil.notBlankAndEquals(userStatus, NO)) {
                            //账号已停用
                            r = Result.fail(ResultCode.USER_FROZEN);
                        } else {
                            return true;
                        }
                    } else {
                        // 获取用户信息失败
                        log.error("获取App端用户信息失败 -> {}", user.getUserId());
                        r = Result.fail(ResultCode.USER_INFO_ERROR);
                    }
                    CacheUtil.evict("app_user", user.getUserId());
                } else {
                    r = Result.fail(ResultCode.USER_TYPE_ERROR);
                }
            }
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        out = response.getWriter();
        out.append(r);
        return false;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    /**
     * 验证是否跳过鉴权
     *
     * @param path 需验证的路由
     */
    private boolean isSkip(String path) {
        return AuthProvider.getAllSkipUrl().stream().map(url -> url.replace(AuthProvider.TARGET, AuthProvider.REPLACEMENT)).anyMatch(path::contains);
    }
}