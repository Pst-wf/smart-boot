package com.smart.aop.permission;

import com.smart.common.utils.AuthUtil;
import com.smart.common.utils.StringUtil;
import com.smart.entity.system.ButtonsEntity;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.system.RoleService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限验证切面
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Aspect
@Component
public class PermissionAspect {
    @Autowired
    RoleService roleService;

    /**
     * 设置鉴权切入点 在注解的位置切入代码
     */
    @Pointcut("@annotation(com.smart.aop.permission.HasPermission)")
    public void tokenPointCut() {
    }

    /**
     * 环绕切入，验证权限通过后再进去接口
     *
     * @param joinPoint 切入点
     */
    @Around(value = "tokenPointCut()")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        String userId = AuthUtil.getUserId();
        if (StringUtil.isBlank(userId)) {
            return Result.fail(ResultCode.OVER_TIME);
        }
        if (!AuthUtil.isSuperAdmin()) {
            List<ButtonsEntity> buttons = roleService.getButtons(AuthUtil.getRoleId());
            if (buttons == null) {
                return Result.fail(ResultCode.UN_AUTH);
            }
            Set<String> codes = buttons.stream().map(ButtonsEntity::getCode).collect(Collectors.toSet());

            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            // 获取操作
            HasPermission hasPermission = method.getAnnotation(HasPermission.class);
            String[] value = hasPermission.value();
            boolean b = false;
            for (String item : value) {
                if (codes.contains(item)) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                return Result.fail(ResultCode.NO_PERMISSION);
            }
        }
        return joinPoint.proceed();
    }
}
