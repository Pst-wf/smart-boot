package com.smart.message.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.smart.common.constant.SmartConstant;
import com.smart.common.utils.AuthUtil;
import com.smart.common.utils.StringUtil;
import com.smart.entity.message.NoticeEntity;
import com.smart.entity.message.NoticeRefEntity;
import com.smart.message.dao.NoticeRefDao;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.message.NoticeRefService;
import com.smart.service.message.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知公告-用户 ServiceImpl
 *
 * @author wf
 * @since 2024-12-13 17:48:13
 */
@Service("noticeRefService")
@Transactional(rollbackFor = Exception.class)
public class NoticeRefServiceImpl extends BaseServiceImpl<NoticeRefDao, NoticeRefEntity> implements NoticeRefService {
    @Autowired
    NoticeService noticeService;

    /**
     * 分页
     *
     * @param entity bean实体
     * @return Page
     */
    @Override
    public Page<NoticeRefEntity> findPage(NoticeRefEntity entity) {
        QueryWrapper<NoticeRefEntity> wrapper = new QueryWrapper<>();
        wrapper = initWrapper(entity, wrapper);
        wrapper = initWrapperAfter(entity, wrapper);
        Page<NoticeRefEntity> page = PageHelper.startPage(entity.getCurrent(), entity.getSize());
        baseMapper.findList(wrapper);
        return page;
    }

    /**
     * 标记已读
     *
     * @param id 主键
     * @return boolean
     */
    @Override
    public boolean read(String id) {
        NoticeRefEntity one = super.getById(id);
        if (one == null) {
            throw new SmartException("发布数据不存在，标记已读失败!");
        }
        if (StringUtil.notBlankAndEquals(one.getIsRead(), SmartConstant.YES)) {
            return true;
        }
        one.setIsRead(SmartConstant.YES);
        one.setReadTime(new Date());
        return super.updateById(one);
    }

    /**
     * 撤销
     *
     * @param id 主键
     * @return boolean
     */
    @Override
    public boolean cancel(String id) {
        NoticeRefEntity one = super.getById(id);
        if (one == null) {
            throw new SmartException("发布数据不存在，撤销失败!");
        }
        if (StringUtil.notBlankAndEquals(one.getIsCancel(), SmartConstant.YES)) {
            throw new SmartException("发布数据已撤销，请勿重复操作!");
        }
        one.setIsCancel(SmartConstant.YES);
        one.setCancelTime(new Date());
        return super.updateById(one);
    }


    /**
     * 查询用户通知公告
     *
     * @param entity 参数0
     * @return com.github.pagehelper.Page<com.smart.entity.message.NoticeRefEntity>
     */
    @Override
    public Page<NoticeRefEntity> pageForUser(NoticeRefEntity entity) {
        entity.setDefaultOrder(false);
        entity.setIsCancel(SmartConstant.NO);
        if (!AuthUtil.isSuperAdmin()) {
            entity.setUserId(AuthUtil.getUserId());
        }
        QueryWrapper<NoticeRefEntity> wrapper = new QueryWrapper<>();
        wrapper = initWrapper(entity, wrapper);
        wrapper = initWrapperAfter(entity, wrapper);
        wrapper.orderByAsc("is_read");
        wrapper.orderByDesc("notice_type", "send_time");
        Page<NoticeRefEntity> page = PageHelper.startPage(entity.getCurrent(), entity.getSize());
        baseMapper.pageForUser(wrapper);
        return page;
    }
}