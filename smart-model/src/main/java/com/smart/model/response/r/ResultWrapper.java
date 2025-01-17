package com.smart.model.response.r;

import java.util.List;

/**
 * 数据创建身份信息填充工具类
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
public interface ResultWrapper {
    <E> E setObjectIdentityInfo(Object data, Class<E> clazz);

    <E> List<E> setListIdentityInfo(Object data, Class<E> clazz);
}
