package com.smart.tools.exception;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.smart.common.utils.AuthUtil;
import com.smart.common.utils.IPUtil;
import com.smart.entity.system.ErrorLogEntity;
import com.smart.model.exception.SmartException;
import com.smart.model.response.r.Result;
import com.smart.service.system.ErrorLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 全局异常捕获类(作用是异常信息统一格式输出)
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@RestControllerAdvice
@Slf4j
public class SmartResponseBodyAdvice {
    @Autowired
    ErrorLogService errorLogService;

    public SmartResponseBodyAdvice() {
    }

    /**
     * 全局异常捕获类
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public String businessExceptionHandler(Exception e) {
        log.error("<全局异常> -  msg:{}", e.getMessage());
        e.printStackTrace();
        if (!(e instanceof SmartException)) {
            ErrorLogEntity errorLogEntity = new ErrorLogEntity();
            IdentifierGenerator identifierGenerator = new DefaultIdentifierGenerator();
            errorLogEntity.setErrorId(identifierGenerator.nextId(new Object()).toString());
            errorLogEntity.setExceptionClass(e.getClass().toString());
            errorLogEntity.setExceptionMessage(e.getMessage());
            StackTraceElement[] stackTraceArr = e.getStackTrace();
            if (stackTraceArr != null) {
                List<StackTraceElement> stackTraceElements = Arrays.asList(stackTraceArr);
                if (!stackTraceElements.isEmpty()) {
                    String stackTrace = JSON.toJSONString(stackTraceElements);
                    errorLogEntity.setStacktrace(stackTrace.getBytes(StandardCharsets.UTF_8));
                    StackTraceElement first = stackTraceElements.get(0);
                    String logMethod = first.getClassName() +
                            "." +
                            first.getMethodName() +
                            "(" +
                            first.getFileName() +
                            ":" +
                            first.getLineNumber() +
                            ")";
                    errorLogEntity.setLogMethod(logMethod);
                }
            }
            errorLogEntity.setUserId(AuthUtil.getUserId());
            errorLogEntity.setUserNickname(AuthUtil.getNickname());
            errorLogEntity.setUsername(AuthUtil.getUsername());
            errorLogEntity.setIp(AuthUtil.getRequest() == null ? null : IPUtil.getIpAddress(AuthUtil.getRequest()));
            errorLogService.saveEntity(errorLogEntity);
            return Result.error(errorLogEntity.getErrorId());
        } else {
            return Result.fail(((SmartException) e).getCode(), e.getMessage());
        }
    }
}
