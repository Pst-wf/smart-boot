package com.smart.auth.service;

import lombok.SneakyThrows;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 客户端信息
 *
 * @author wf
 */
@Component
public class SmartClientDetailsServiceImpl extends JdbcClientDetailsService {

    public SmartClientDetailsServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * 缓存客户端信息
     *
     * @param clientId 客户端id
     */
    @Override
    @SneakyThrows
    public ClientDetails loadClientByClientId(String clientId) {
        return super.loadClientByClientId(clientId);
    }
}
