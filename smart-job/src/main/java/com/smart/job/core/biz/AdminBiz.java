package com.smart.job.core.biz;


import com.smart.job.core.biz.model.HandleCallbackParam;
import com.smart.job.core.biz.model.RegistryParam;
import com.smart.job.core.biz.model.ReturnT;

import java.util.List;

/**
 * @author xuxueli 2017-07-27 21:52:49
 */
public interface AdminBiz {

    // ---------------------- callback ----------------------

    /**
     * callback
     *
     * @param callbackParamList 参数
     * @return ReturnT
     */
    ReturnT<String> callback(List<HandleCallbackParam> callbackParamList);


    // ---------------------- registry ----------------------

    /**
     * registry
     *
     * @param registryParam 参数
     * @return ReturnT
     */
    ReturnT<String> registry(RegistryParam registryParam);

    /**
     * registry remove
     *
     * @param registryParam 参数
     * @return ReturnT
     */
    ReturnT<String> registryRemove(RegistryParam registryParam);
}
