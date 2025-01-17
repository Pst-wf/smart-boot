package com.smart.system.service;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.system.dao.ErrorLogDao;
import com.smart.entity.system.ErrorLogEntity;
import com.smart.service.system.ErrorLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 错误日志 ServiceImpl
 *
 * @author wf
 * @since 2022-07-27 14:27:15
 */
@Service("errorLogService")
@Transactional(rollbackFor = Exception.class)
public class ErrorLogServiceImpl extends BaseServiceImpl<ErrorLogDao, ErrorLogEntity> implements ErrorLogService {
    @Override
    public Page<ErrorLogEntity> findPage(ErrorLogEntity entity) {
        Page<ErrorLogEntity> page = super.findPage(entity);
        for (ErrorLogEntity errorLogEntity : page.getResult()) {
            String str = new String(errorLogEntity.getStacktrace(), StandardCharsets.UTF_8);
            List<StackTraceElement> stackTraceElements = JSONArray.parseArray(str, StackTraceElement.class);
            errorLogEntity.setStacktraceList(stackTraceElements);
        }
        return page;
    }
}