package com.smart.aop.log;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.smart.common.utils.AuthUtil;
import com.smart.common.utils.IPUtil;
import com.smart.common.utils.ObjectUtil;
import com.smart.common.utils.StringUtil;
import com.smart.entity.system.LogEntity;
import com.smart.model.response.r.ResultCode;
import com.smart.service.system.LogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 生成日志切面
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Aspect
@Component
@Slf4j
public class SaveLogAspect {
    @Autowired
    LogService logService;
    private static final String CODE = "code";

    /**
     * 设置操作日志切入点 记录操作日志 在注解的位置切入代码
     */
    @Pointcut("@annotation(com.smart.aop.log.SaveLog)")
    public void logPointCut() {
    }

    /**
     * 正常返回通知,拦截用户操作日志,连接点正常执行完成后执行, 如果连接点抛出异常,则不会执行
     *
     * @param joinPoint 切入点
     * @param result    返回值
     */
    @AfterReturning(value = "logPointCut()", returning = "result")
    public void saveLog(JoinPoint joinPoint, Object result) {
        try {
            if (result != null) {
                JSONObject jsonObject = JSONObject.parseObject(result.toString(), Feature.OrderedField);
                if (jsonObject.getIntValue(CODE) == ResultCode.SUCCESS.getCode()) {
                    LogEntity log = new LogEntity();
                    // 从切面织入点处通过反射机制获取织入点处的方法
                    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                    // 获取切入点所在的方法
                    Method method = signature.getMethod();
                    // 获取入参
                    Object[] args = joinPoint.getArgs();
                    String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
                    // 获取注解
                    SaveLog saveLog = method.getAnnotation(SaveLog.class);
                    if (saveLog != null) {
                        String module = saveLog.module();
                        LogType type = saveLog.type();
                        log.setLogModule(module);
                        log.setLogType(type.getValue());
                        JSONObject logResult = jsonObject.getJSONObject("data") == null ? null : jsonObject.getJSONObject("data");
                        log.setLogDesc(getDesc(type, args, logResult));
                        log.setLogParams(getParams(parameterNames, args));
                        log.setLogResult(ObjectUtil.toJSONString(jsonObject));
                    }
                    // 获取请求的类名
                    String className = joinPoint.getTarget().getClass().getName();
                    // 获取请求的方法名
                    String methodName = method.getName();
                    methodName = className + ":  【 " + methodName + " 】";
                    log.setLogMethod(methodName);
                    log.setUserId(AuthUtil.getUserId());
                    log.setUserNickname(AuthUtil.getNickname());
                    log.setUsername(AuthUtil.getUsername());
                    log.setIp(AuthUtil.getRequest() == null ? null : IPUtil.getIpAddress(AuthUtil.getRequest()));
                    logService.saveEntity(log);
                }
            }
        } catch (Exception e) {
            log.error("操作日志记录失败 -> {}", e.getMessage());
        }

    }

    /**
     * 从响应中获取数据
     *
     * @param logType 类型
     * @param args    入参
     * @param obj     返回
     */
    private String getDesc(LogType logType, Object[] args, JSONObject obj) {
        if (logType == LogType.ADD || logType == LogType.UPDATE) {
            if (obj != null) {
                String value = obj.getString("id");
                if (StringUtil.isNotBlank(value)) {
                    return logType.getValue() + " [" + value + "]";
                }
            }
        } else if (logType == LogType.DELETE || logType == LogType.REAL_DELETE ) {
            List<?> deleteIds = null;
            for (Object arg : args) {
                Object param = ObjectUtil.getFieldValueByName("deleteIds", arg);
                if (param != null) {
                    deleteIds = (List<?>) param;
                }
            }
            if (deleteIds != null) {
                if (!deleteIds.isEmpty()) {
                    String value = deleteIds.stream().map(String::valueOf).collect(Collectors.joining(","));
                    return logType.getValue() + " [" + value + "]";
                }
            }
        }
        return null;
    }

    /**
     * 获取参数
     *
     * @param parameterNames 参数名
     * @param args           入参
     */
    private String getParams(String[] parameterNames, Object[] args) {
        JSONObject jsonObject = new JSONObject(true);
        for (int i = 0; i < parameterNames.length; i++) {
            jsonObject.put(parameterNames[i], args[i]);
        }
        return ObjectUtil.toJSONString(jsonObject);
    }
}