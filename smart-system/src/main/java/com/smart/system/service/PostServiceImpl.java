package com.smart.system.service;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.smart.common.constant.SmartConstant;
import com.smart.common.utils.StringUtil;
import com.smart.entity.system.IdentityEntity;
import com.smart.entity.system.PostEntity;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.system.PostService;
import com.smart.system.dao.PostDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.smart.common.constant.SmartConstant.NO;
import static com.smart.common.constant.SmartConstant.YES;

/**
 * 岗位 ServiceImpl
 *
 * @author wf
 * @since 2022-03-12 08:42:03
 */
@Service("postService")
@Transactional(rollbackFor = Exception.class)
public class PostServiceImpl extends BaseServiceImpl<PostDao, PostEntity> implements PostService {

    /**
     * 保存之前处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    @Override
    public void beforeSaveOrUpdate(PostEntity entity, boolean isAdd) {
        PostEntity postEntity = Db.lambdaQuery(PostEntity.class).eq(PostEntity::getPostCode, entity.getPostCode()).one();
        if (postEntity != null) {
            if (isAdd) {
                // 新增
                throw new SmartException("岗位编码已存在！");
            } else {
                // 编辑
                if (!entity.getId().equals(postEntity.getId())) {
                    throw new SmartException("岗位编码已存在！");
                }
            }
        }

        if (!isAdd) {
            if (StringUtil.notBlankAndEquals(entity.getStatus(), SmartConstant.NO)) {
                //验证该部门是否有人使用
                long count = Db.lambdaQuery(IdentityEntity.class).eq(IdentityEntity::getPostId, entity.getId()).count();
                if (count > 0) {
                    throw new SmartException("要禁用的岗位下有用户存在，不可禁用！");
                }
            }
        }
    }

    /**
     * 删除之前处理
     *
     * @param entity bean 实体
     * @param isReal 是否物理删除
     */
    @Override
    public void beforeDelete(PostEntity entity, boolean isReal) {
        //验证该部门是否有人使用
        long count = Db.lambdaQuery(IdentityEntity.class).in(IdentityEntity::getPostId, entity.getDeleteIds()).count();
        if (count > 0) {
            throw new SmartException("要删除的岗位下有用户存在，不可删除！");
        }
    }

    /**
     * 启用/停用
     *
     * @param entity bean实体
     * @return boolean
     */
    @Override
    public boolean updateStatus(PostEntity entity) {
        if (StringUtil.notBlankAndEquals(entity.getStatus(), SmartConstant.NO)) {
            //验证该部门是否有人使用
            long count = Db.lambdaQuery(IdentityEntity.class).eq(IdentityEntity::getPostId, entity.getId()).count();
            if (count > 0) {
                throw new SmartException("要禁用的岗位下有用户存在，不可禁用！");
            }
        }
        return Db.lambdaUpdate(PostEntity.class)
                .set(PostEntity::getStatus, StringUtil.notBlankAndEquals(entity.getStatus(), YES) ? YES : NO)
                .eq(PostEntity::getId, entity.getId())
                .update();
    }
}