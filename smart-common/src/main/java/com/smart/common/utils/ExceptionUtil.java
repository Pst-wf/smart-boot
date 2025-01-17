package com.smart.common.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 关于异常的工具类.
 *
 * @author wf
 * @version 2022-01-01 17:28:23
 */
public class ExceptionUtil {

    /**
     * 在request中获取异常类
     *
     * @param request 请求
     * @return Throwable
     */
    public static Throwable getThrowable(HttpServletRequest request) {
        Throwable ex = null;
        if (request.getAttribute("exception") != null) {
            ex = (Throwable) request.getAttribute("exception");
        } else if (request.getAttribute("javax.servlet.error.exception") != null) {
            ex = (Throwable) request.getAttribute("javax.servlet.error.exception");
        }
        return ex;
    }

    public static String getExceptionMessage(Throwable ex) {
        String message = null;
        Throwable e = ex;
        while (true) {
            if (e == null) {
                break;
            }
            if (StringUtil.startsWith(e.getMessage(), "msg:")) {
                message = StringUtil.replace(e.getMessage(), "msg:", "");
                break;
            } else if ("com.smart.model.exception.SmartException".equals(e.getClass().getName())) {
                message = e.getMessage();
                break;
            } else {
                e = e.getCause();
            }
        }
        return message;
    }

    /**
     * 将ErrorStack转化为String.
     */
    public static String getStackTraceAsString(Throwable e) {
        if (e == null) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    /**
     * 判断异常是否由某些底层的异常引起.
     */
    @SuppressWarnings("unchecked")
    public static boolean isCausedBy(Exception ex, Class<? extends Exception>... causeExceptionClasses) {
        Throwable cause = ex.getCause();
        while (cause != null) {
            for (Class<? extends Exception> causeClass : causeExceptionClasses) {
                if (causeClass.isInstance(cause)) {
                    return true;
                }
            }
            cause = cause.getCause();
        }
        return false;
    }

    /**
     * 将CheckedException转换为UncheckedException.
     */
    public static RuntimeException unchecked(Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new RuntimeException(e);
        }
    }

}
