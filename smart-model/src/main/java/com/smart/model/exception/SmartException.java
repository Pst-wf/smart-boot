package com.smart.model.exception;

import com.smart.model.response.r.IResultCode;
import lombok.Getter;

/**
 * 自定义异常
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Getter
public class SmartException extends RuntimeException {
    public Integer code;

    public SmartException(String message) {
        this(400, message);
    }

    public SmartException(int code, String message) {
        super(message);
        this.code = code;
    }

    public SmartException(IResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }
}
