package com.smart.job.core.executor.impl;

import com.smart.job.core.executor.XxlJobExecutor;
import com.smart.job.core.handler.annotation.XxlJob;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * xxl-job executor (for frameless)
 *
 * @author xuxueli 2020-11-05
 */
public class XxlJobSimpleExecutor extends XxlJobExecutor {
    @Value("${job.openStatus}")
    private boolean openStatus;

    @Getter
    private List<Object> xxlJobBeanList = new ArrayList<>();

    public void setXxlJobBeanList(List<Object> xxlJobBeanList) {
        this.xxlJobBeanList = xxlJobBeanList;
    }


    @Override
    public void start() {

        // init JobHandler Repository (for method)
        initJobHandlerMethodRepository(xxlJobBeanList);
        if (openStatus) {
            // super start
            try {
                super.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }


    private void initJobHandlerMethodRepository(List<Object> xxlJobBeanList) {
        if (xxlJobBeanList == null || xxlJobBeanList.isEmpty()) {
            return;
        }

        // init job handler from method
        for (Object bean : xxlJobBeanList) {
            // method
            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method executeMethod : methods) {
                XxlJob xxlJob = executeMethod.getAnnotation(XxlJob.class);
                // registry
                registJobHandler(xxlJob, bean, executeMethod);
            }

        }

    }

}
