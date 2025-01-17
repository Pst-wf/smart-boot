package com.smart.message.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.common.constant.SmartConstant;
import com.smart.common.utils.ListUtil;
import com.smart.common.utils.StringUtil;
import com.smart.entity.message.NoticeRecordEntity;
import com.smart.entity.message.NoticeRefEntity;
import com.smart.entity.system.IdentityEntity;
import com.smart.entity.system.UserEntity;
import com.smart.message.constant.MessageConstant;
import com.smart.message.dao.NoticeRecordDao;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.message.NoticeRecordService;
import com.smart.service.message.NoticeRefService;
import com.smart.service.system.IdentityInfoService;
import com.smart.service.system.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 通知公告发布记录 ServiceImpl
 *
 * @author wf
 * @since 2024-12-13 17:46:05
 */
@Service("noticeRecordService")
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class NoticeRecordServiceImpl extends BaseServiceImpl<NoticeRecordDao, NoticeRecordEntity> implements NoticeRecordService {

    private final IdentityInfoService identityInfoService;
    private final UserService userService;
    private final NoticeRefService noticeRefService;

    /**
     * 保存之前处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    @Override
    public void beforeSaveOrUpdate(NoticeRecordEntity entity, boolean isAdd) {
        if (isAdd) {
            entity.setReleaseTime(new Date());
        }
    }

    /**
     * 保存之后处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    @Override
    public void afterSaveOrUpdate(NoticeRecordEntity entity, boolean isAdd) {
        if (StringUtil.isNotBlank(entity.getReleaseType()) && StringUtil.isNotBlank(entity.getReleaseValue())) {
            List<String> userIds = new ArrayList<>();
            List<String> list = ListUtil.newArrayList(entity.getReleaseValue().split(","));
            switch (entity.getReleaseType()) {
                case MessageConstant.NOTICE_RELEASE_TYPE_DEPT:
                case MessageConstant.NOTICE_RELEASE_TYPE_POST:
                case MessageConstant.NOTICE_RELEASE_TYPE_ROLE:
                    List<IdentityEntity> identities = new ArrayList<>();
                    if (entity.getReleaseType().equals(MessageConstant.NOTICE_RELEASE_TYPE_DEPT)) {
                        // 通过部门查找用户
                        identities = identityInfoService.list(new LambdaQueryWrapper<IdentityEntity>().in(IdentityEntity::getDeptId, list));
                    }
                    if (entity.getReleaseType().equals(MessageConstant.NOTICE_RELEASE_TYPE_POST)) {
                        // 通过岗位查找用户
                        identities = identityInfoService.list(new LambdaQueryWrapper<IdentityEntity>().in(IdentityEntity::getPostId, list));
                    }
                    if (entity.getReleaseType().equals(MessageConstant.NOTICE_RELEASE_TYPE_ROLE)) {
                        // 通过角色查找用户
                        identities = identityInfoService.list(new LambdaQueryWrapper<IdentityEntity>().in(IdentityEntity::getRoleId, list));
                    }
                    Set<String> set = identities.stream().map(IdentityEntity::getUserId).collect(Collectors.toSet());
                    if (!set.isEmpty()) {
                        //查询有效用户
                        List<UserEntity> userList = userService.list(new LambdaQueryWrapper<UserEntity>()
                                .eq(UserEntity::getUserStatus, SmartConstant.YES)
                                .in(UserEntity::getId, set));
                        userIds = userList.stream().map(UserEntity::getId).collect(Collectors.toList());
                    }
                    break;

                case MessageConstant.NOTICE_RELEASE_TYPE_USER:
                    userIds = list;
                    break;
                default:
                    throw new SmartException("发布类型异常！");
            }
            if (!userIds.isEmpty()) {
                // 生成发布关联表数据
                List<NoticeRefEntity> data = new ArrayList<>();
                for (String userId : userIds) {
                    NoticeRefEntity ref = new NoticeRefEntity();
                    ref.setNoticeId(entity.getNoticeId());
                    ref.setUserId(userId);
                    ref.setIsRead(SmartConstant.NO);
                    ref.setIsCancel(SmartConstant.NO);
                    ref.setReleaseId(entity.getId());
                    ref.setSendTime(entity.getReleaseTime());
                    data.add(ref);
                }
                boolean b = noticeRefService.saveBatch(data);
                if (!b) {
                    throw new SmartException("发布失败！");
                }
            }
        }
    }
}