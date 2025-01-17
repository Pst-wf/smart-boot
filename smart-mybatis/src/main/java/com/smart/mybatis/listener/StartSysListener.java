package com.smart.mybatis.listener;

import com.smart.mybatis.handler.FastJSONArrayTypeHandler;
import com.smart.mybatis.interceptor.QueryInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用监听器的方式添加拦截器
 *
 * @author wf
 * @apiNote 由于PageHelper修改了拦截参数个数 导致后面添加的mybatis拦截器都会无效
 * @since 2022-07-26 00:00:00
 */
@Component
@Slf4j
public class StartSysListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private QueryInterceptor queryInterceptor;
    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.addMyInterceptor();
    }

    private void addMyInterceptor() {
        log.error(">>>>>>>>>>> 添加Mybatis自定义拦截器");
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            sqlSessionFactory.getConfiguration().addInterceptor(queryInterceptor);
            sqlSessionFactory.getConfiguration().getTypeHandlerRegistry().register(FastJSONArrayTypeHandler.class);
        }
        log.error(">>>>>>>>>>> Mybatis自定义拦截器添加结束");
    }
}