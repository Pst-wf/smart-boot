package com.smart.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.smart.common.utils.CacheUtil;
import com.smart.common.utils.StringUtil;
import com.smart.entity.system.OssEntity;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.system.OssService;
import com.smart.system.dao.OssDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.smart.common.constant.SmartConstant.NO;
import static com.smart.common.constant.SmartConstant.YES;

/**
 * 对象存储 ServiceImpl
 *
 * @author wf
 * @since 2022-03-27 12:25:02
 */
@Service("ossService")
@Transactional(rollbackFor = Exception.class)
public class OssServiceImpl extends BaseServiceImpl<OssDao, OssEntity> implements OssService {
    /**
     * 分页
     *
     * @param entity bean实体
     * @return Page
     */
    @Override
    public Page<OssEntity> findPage(OssEntity entity) {
        QueryWrapper<OssEntity> wrapper = new QueryWrapper<>();
        if (StringUtil.isBlank(entity.getSortField()) && StringUtil.isBlank(entity.getSortOrder())) {
            entity.setSortField(OssEntity.Fields.ossType);
            entity.setSortOrder("ASC");
        }
        wrapper = initWrapper(entity, wrapper);
        wrapper = initWrapperAfter(entity, wrapper);
        Page<OssEntity> page = PageHelper.startPage(entity.getCurrent(), entity.getSize());
        super.list(wrapper);
        return page;
    }

    /**
     * 保存之后处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    @Override
    public void afterSaveOrUpdate(OssEntity entity, boolean isAdd) {
        CacheUtil.put("oss", "config", entity);
    }

    /**
     * 删除之后处理
     *
     * @param entity bean 实体
     * @param isReal 是否物理删除
     */
    @Override
    public void afterDelete(OssEntity entity, boolean isReal) {
        CacheUtil.clear("oss");
    }

    @Override
    public boolean updateOssStatus(OssEntity entity) {
        boolean b;
        String ossStatus = entity.getOssStatus();
        if (YES.equals(ossStatus)) {
            long count = Db.lambdaQuery(OssEntity.class).eq(OssEntity::getOssStatus, YES).count();
            if (count > 0) {
                throw new SmartException("同时最多启用一个对象存储方式");
            }
            b = baseMapper.updateOssStatus(YES, entity.getId());
            if (b) {
                CacheUtil.put("oss", "config", super.get(entity.getId()));
            }
        } else {
            b = baseMapper.updateOssStatus(NO, entity.getId());
            if (b) {
                CacheUtil.clear("oss");
            }
        }
        return b;
    }

    @Override
    public OssEntity getCurrent() {
        Object o = CacheUtil.get("oss", "config");
        if (o == null) {
            return Db.lambdaQuery(OssEntity.class).eq(OssEntity::getOssStatus, YES).one();
        } else {
            return (OssEntity) o;
        }
    }
}