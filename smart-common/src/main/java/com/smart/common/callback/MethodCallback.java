package com.smart.common.callback;

/**
 * 方法回调接口
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
public interface MethodCallback {
    /**
     * 执行
     *
     * @param params 参数
     * @return Object
     */
    Object execute(Object... params);

}
