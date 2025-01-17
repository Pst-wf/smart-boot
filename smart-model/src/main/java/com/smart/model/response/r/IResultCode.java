package com.smart.model.response.r;

import java.io.Serializable;

/**
 * 业务代码接口
 *
 * @author Chill
 */
public interface IResultCode extends Serializable {

    /**
     * 获取消息
     *
     * @return String
     */
    String getMessage();

    /**
     * 获取状态码
     *
     * @return int
     */
    int getCode();

}
