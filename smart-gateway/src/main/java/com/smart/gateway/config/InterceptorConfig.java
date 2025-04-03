package com.smart.gateway.config;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.smart.common.constant.FileConstant;
import com.smart.common.utils.ListUtil;
import com.smart.entity.system.OssEntity;
import com.smart.gateway.interceptor.SmartInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 拦截器注册
 *
 * @author wf
 * @since 2022-08-06
 */
@Component
@Slf4j
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    SmartInterceptor interceptor;

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        log.error(">>>>>>>>>>> 本地上传文件路径代理");
        List<OssEntity> local = Db.lambdaQuery(OssEntity.class).eq(OssEntity::getOssType, FileConstant.OSS_TYPE_0).list();
        local.stream().collect(Collectors.groupingBy(OssEntity::getBucket)).forEach((key, value) -> {
            if (ListUtil.isNotEmpty(value)) {
                // 相同访问路径取第一个
                OssEntity ossEntity = value.get(0);
                String prefix = ossEntity.getBucket();
                if (!prefix.startsWith("/")) {
                    prefix = "/" + prefix;
                }
                if (prefix.endsWith("/")) {
                    prefix = prefix.substring(0, prefix.length() - 1);
                }
                log.error("【{}】 -> 【{}】", prefix + "/**", ossEntity.getOssDir());
                // 本地文件上传路径
                registry.addResourceHandler(prefix + "/**")
                        .addResourceLocations("file:" + ossEntity.getOssDir() + "/");
            }
        });
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/**");
    }

}