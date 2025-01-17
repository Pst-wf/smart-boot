package com.smart.auth.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.common.constant.FileConstant;
import com.smart.common.gateway.provider.AuthProvider;
import com.smart.common.utils.ListUtil;
import com.smart.entity.system.OssEntity;
import com.smart.service.system.OssService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义登录成功配置
 * 白名单1
 *
 * @author wf
 */
@Configuration
@AllArgsConstructor
@EnableResourceServer
@Slf4j
public class SmartResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    /**
     * 自定义登录成功处理器
     */
    private AuthenticationSuccessHandler appLoginInSuccessHandler;
    private OssService ossService;

    @Override
    @SneakyThrows
    public void configure(HttpSecurity http) {
        String[] defaultSkip = AuthProvider.getDefaultSkipUrl().toArray(new String[0]);
        List<String> skipUrls = ListUtil.newArrayList(defaultSkip);
        List<OssEntity> local = ossService.list(new LambdaQueryWrapper<OssEntity>().eq(OssEntity::getOssType, FileConstant.OSS_TYPE_0));
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
                skipUrls.add(prefix + "/**");
            }
        });
        List<String> allSkipUrls = AuthProvider.initAllSkipUrl(skipUrls);
        log.error(">>>>>>>>>>> 免登录接口");
        allSkipUrls.forEach(log::error);

        http.headers().frameOptions().disable();
        http.formLogin()
                .successHandler(appLoginInSuccessHandler)
                .and()
                .authorizeRequests()
                .antMatchers(allSkipUrls.toArray(new String[0]))
                .permitAll()
                .anyRequest().authenticated().and()
                .csrf().disable();
    }

}
