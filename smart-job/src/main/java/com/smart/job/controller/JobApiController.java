package com.smart.job.controller;

import com.smart.job.core.biz.AdminBiz;
import com.smart.job.core.biz.model.HandleCallbackParam;
import com.smart.job.core.biz.model.RegistryParam;
import com.smart.job.core.biz.model.ReturnT;
import com.smart.job.core.conf.XxlJobAdminConfig;
import com.smart.job.core.util.GsonTool;
import com.smart.job.core.util.XxlJobRemotingUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author xxl
 * @since 2017/5/10
 */
@Controller
@RequestMapping("/api")
public class JobApiController {

    @Resource
    AdminBiz adminBiz;

    /**
     * api
     *
     * @param uri  接口地址
     * @param data 参数
     * @return ReturnT
     */
    @RequestMapping("/{uri}")
    @ResponseBody
    public ReturnT<String> api(HttpServletRequest request, @PathVariable("uri") String uri, @RequestBody(required = false) String data) {

        // valid
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "invalid request, HttpMethod not support.");
        }
        if (uri == null || uri.trim().isEmpty()) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "invalid request, uri-mapping empty.");
        }
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken() != null
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().isEmpty()
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN))) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }

        // services mapping
        switch (uri) {
            case "callback":
                List<HandleCallbackParam> callbackParamList = GsonTool.fromJson(data, List.class, HandleCallbackParam.class);
                return adminBiz.callback(callbackParamList);
            case "registry": {
                RegistryParam registryParam = GsonTool.fromJson(data, RegistryParam.class);
                return adminBiz.registry(registryParam);
            }
            case "registryRemove": {
                RegistryParam registryParam = GsonTool.fromJson(data, RegistryParam.class);
                return adminBiz.registryRemove(registryParam);
            }
            default:
                return new ReturnT<>(ReturnT.FAIL_CODE, "invalid request, uri-mapping(" + uri + ") not found.");
        }

    }

}
