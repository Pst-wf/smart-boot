package com.smart.system.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.smart.common.utils.*;
import com.smart.common.vo.UserVO;
import com.smart.entity.system.DeptEntity;
import com.smart.entity.system.IdentityEntity;
import com.smart.entity.system.UserEntity;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.system.ConfigService;
import com.smart.service.system.IdentityInfoService;
import com.smart.service.system.UserService;
import com.smart.system.dao.DeptDao;
import com.smart.system.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.smart.common.constant.SmartConstant.*;
import static com.smart.system.constant.SystemConstant.HAS_DEPT_POST;

/**
 * 用户表 ServiceImpl
 *
 * @author wf
 * @since 2021-12-30 21:03:52
 */
@Slf4j
@Service("userService")
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends BaseServiceImpl<UserDao, UserEntity> implements UserService {

    @Resource
    DeptDao deptDao;
    @Autowired
    IdentityInfoService identityInfoService;
    @Autowired
    ConfigService configService;
    /**
     * token有效期
     */
    @Value("${token_expire}")
    public long expireTime;


    @Override
    public Page<UserEntity> findPage(UserEntity entity) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper = initWrapper(entity, wrapper);
        //过滤SYSTEM账号
        wrapper.ne("id", "system");
        Page<UserEntity> page = PageHelper.startPage(entity.getCurrent(), entity.getSize());
        //根据条件查询
        baseMapper.findPageWithIdentity(wrapper, entity);
        return page;
    }

    @Override
    public List<UserEntity> findList(UserEntity entity) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper = initWrapper(entity, wrapper);
        //过滤SYSTEM账号
        wrapper.ne("id", "system");
        //根据条件查询
        return baseMapper.findPageWithIdentity(wrapper, entity);
    }

    @Override
    public UserEntity getUserWithIdentity(String id) {
        // 需要再次查询只有角色id的
        UserEntity user = baseMapper.getUserWithIdentity(id);
        Set<String> roleIds = user.getIdentityList().stream().map(IdentityEntity::getRoleId).filter(StringUtil::isNotBlank).collect(Collectors.toSet());
        user.setRole(roleIds.stream().findFirst().orElse(null));
        return user;
    }

    /**
     * 更新登录IP和最后登录时间
     *
     * @param userEntity 用户信息
     */
    @Override
    public void updateLoginInfo(UserEntity userEntity) {
        baseMapper.updateLoginInfo(userEntity.getLastLoginIp(), userEntity.getId(), userEntity.getLastLoginDate());

    }

    /**
     * 保存之前处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    @Override
    public void beforeSaveOrUpdate(UserEntity entity, boolean isAdd) {
        if (isAdd) {
            // 新增
            long count = Db.lambdaQuery(UserEntity.class).eq(UserEntity::getUsername, entity.getUsername()).count();
            if (count > 0) {
                throw new SmartException("该账号已被使用");
            }
            String password = DigestUtil.md5Hex(entity.getPassword());
            String passwordBase = CryptoUtil.encrypt(entity.getPassword());
            entity.setPassword(password);
            entity.setPasswordBase(passwordBase);
        } else {
            //修改
            // 验证账号是否有人使用过
            UserEntity user = baseMapper.getUserById(entity.getId());
            String oldUsername = user.getUsername();
            String oldPassword = user.getPassword();
            //判断是否修改账号
            if (StringUtil.isNotBlank(entity.getUsername())) {
                if (!oldUsername.equals(entity.getUsername())) {
                    long count = Db.lambdaQuery(UserEntity.class).eq(UserEntity::getUsername, entity.getUsername()).count();
                    if (count > 0) {
                        throw new SmartException("该账号已被使用");
                    }
                }
            }
            //判断是否修改密码
            if (StringUtil.isNotBlank(entity.getPassword())) {
                if (!oldPassword.equals(entity.getPassword())) {
                    String password = DigestUtil.md5Hex(entity.getPassword());
                    String passwordBase = CryptoUtil.encrypt(entity.getPassword());
                    entity.setPassword(password);
                    entity.setPasswordBase(passwordBase);
                }
            }
        }
    }

    @Override
    public UserEntity saveEntity(UserEntity entity) {
        UserEntity result = super.saveEntity(entity);
        if (result != null) {
            String hasDeptPost = configService.getConfig(HAS_DEPT_POST);
            if (StringUtil.notBlankAndEquals(hasDeptPost, NO)) {
                //保存身份
                String role = entity.getRole();
                if (StringUtil.isNotBlank(role)) {
                    IdentityEntity identityEntity = new IdentityEntity();
                    identityEntity.setRoleId(role);
                    identityEntity.setUserId(entity.getId());
                    identityInfoService.saveEntity(identityEntity);
                }
            } else {
                //保存身份
                List<IdentityEntity> identityList = entity.getIdentityList();
                if (ListUtil.isNotEmpty(identityList)) {
                    for (IdentityEntity identityEntity : identityList) {
                        //获取公司ID
                        if (StringUtil.isNotBlank(identityEntity.getDeptId())) {
                            DeptEntity deptEntity = deptDao.selectById(identityEntity.getDeptId());
                            if (deptEntity != null && StringUtil.isNotBlank(deptEntity.getAncestors())) {
                                List<String> organizationIds = deptDao.getOrganizationIds(deptEntity.getAncestors() + "," + deptEntity.getId());
                                if (!organizationIds.isEmpty()) {
                                    identityEntity.setOrganizationId(organizationIds.get(organizationIds.size() - 1));
                                }
                            }
                        }
                        identityEntity.setUserId(entity.getId());
                        identityEntity.setId(null);
                    }
                    identityInfoService.saveBatch(identityList);
                }
            }
        }
        return result;
    }

    @Override
    @CacheEvict(cacheNames = "user", key = "#entity.id")
    public UserEntity updateEntity(UserEntity entity) {
        UserEntity result = super.updateEntity(entity);
        if (result != null) {
            String hasDeptPost = configService.getConfig(HAS_DEPT_POST);
            if (StringUtil.notBlankAndEquals(hasDeptPost, NO)) {
                // 传过来的角色id
                String role = entity.getRole();
                List<IdentityEntity> list = Db.lambdaQuery(IdentityEntity.class).eq(IdentityEntity::getUserId, entity.getId()).list();
                if (StringUtil.isNotBlank(role)) {
                    // 若是单选模式 应该是 直接更新
                    list.forEach(x -> {
                        x.setRoleId(role);
                    });
                    identityInfoService.updateBatchById(list);
                }
            } else {
                //保存身份
                List<IdentityEntity> identityList = entity.getIdentityList();
                if (ListUtil.isNotEmpty(identityList)) {
                    for (IdentityEntity identityEntity : identityList) {
                        //获取公司ID
                        if (StringUtil.isNotBlank(identityEntity.getDeptId())) {
                            DeptEntity deptEntity = deptDao.selectById(identityEntity.getDeptId());
                            if (deptEntity != null && StringUtil.isNotBlank(deptEntity.getAncestors())) {
                                List<String> organizationIds = deptDao.getOrganizationIds(deptEntity.getAncestors() + "," + deptEntity.getId());
                                if (!organizationIds.isEmpty()) {
                                    identityEntity.setOrganizationId(organizationIds.get(organizationIds.size() - 1));
                                }
                            }
                        }
                        identityEntity.setUserId(entity.getId());
                        if (StringUtil.notBlankAndContains(identityEntity.getId(), IS_NEW)) {
                            identityEntity.setId(null);
                        } else if (StringUtil.isNotBlank(identityEntity.getId())) {
                            // 删除对应的缓存
                            CacheUtil.evict("identity", identityEntity.getId());
                        }
                    }
                    identityInfoService.saveOrUpdateBatch(identityList);
                }
                //删除无效身份
                if (ListUtil.isNotEmpty(entity.getDeleteIds())) {
                    identityInfoService.removeByIds(entity.getDeleteIds());
                    // 删除对应的缓存
                    CacheUtil.evictKeys("identity", entity.getDeleteIds());
                }
            }
        }
        return result;
    }

    @Override
    @CacheEvict(cacheNames = "user", key = "#entity.id")
    public boolean updateUserStatus(UserEntity entity) {
        String identityId = AuthUtil.getIdentityId();
        String userStatus = entity.getUserStatus();
        if (YES.equals(userStatus)) {
            return baseMapper.updateUserStatus(YES, null, entity.getId(), identityId);
        } else {
            return baseMapper.updateUserStatus(NO, new Date(), entity.getId(), identityId);
        }
    }

    @Override
    public boolean delete(UserEntity entity) {
        boolean b = super.delete(entity);
        if (b) {
            //删除身份
            Db.remove(Db.lambdaQuery(IdentityEntity.class).in(IdentityEntity::getUserId, entity.getDeleteIds()).getWrapper());
            // 清除所有身份缓存
            CacheUtil.clear("identity");
            // 清除用户缓存
            CacheUtil.evictKeys("user", entity.getDeleteIds());
        }
        return b;
    }

    /**
     * 更新个人信息
     *
     * @param entity 用户bean
     * @param userId 当前登陆人ID
     * @return boolean
     */
    @Override
    @CachePut(cacheNames = "user", key = "#userId", unless = "#result == null")
    public UserEntity updateInfo(UserEntity entity, String userId) {
        UserEntity user = super.getById(userId);
        user.setNickname(entity.getNickname());
        user.setPhone(entity.getPhone());
        user.setAvatar(entity.getAvatar());
        user.setRemarks(entity.getRemarks());
        return super.updateEntity(user);
    }

    /**
     * 更新个人信息
     *
     * @param jsonObject 密码信息
     * @param userId     当前登陆人ID
     * @return boolean
     */
    @Override
    @CachePut(cacheNames = "user", key = "#userId", unless = "#result == null")
    public UserEntity updatePassword(JSONObject jsonObject, String userId) {
        String oldPassword = jsonObject.getString("oldPassword");
        String newPassword = jsonObject.getString("newPassword");
        UserEntity entity = super.getById(userId);
        //判断旧密码是否正确
        if (DigestUtil.md5Hex(oldPassword).equals(entity.getPassword())) {
            String password = DigestUtil.md5Hex(newPassword);
            String passwordBase = CryptoUtil.encrypt(newPassword);
            entity.setPassword(password);
            entity.setPasswordBase(passwordBase);
            boolean update = super.updateById(entity);
            if (update) {
                // 更新缓存
                UserEntity userEntity = CacheUtil.get("user", userId, UserEntity.class);
                if (userEntity != null) {
                    userEntity.setPassword(password);
                    userEntity.setPasswordBase(passwordBase);
                    CacheUtil.put("user", userId, userEntity);
                }
                //生成新的token
                String token = this.updateToken(AuthUtil.getUser(), password);
                entity.setToken(token);
            } else {
                throw new SmartException("操作失败!");
            }
            return entity;
        } else {
            throw new SmartException("原密码错误！");
        }

    }

    /**
     * 获取缓存用户信息
     *
     * @param id 用户ID
     * @return UserEntity
     */
    @Override
    @Cacheable(cacheNames = "user", key = "#id", unless = "#result == null")
    public UserEntity getCacheUser(String id) {
        return super.get(id);
    }

    /**
     * 通过IDS获取用户集合
     *
     * @param ids 主键IDS
     * @return List
     */
    @Override
    public List<UserEntity> getUserListByIds(String ids) {
        if (StringUtil.isNotBlank(ids)) {
            String[] idsArr = ids.split(",");
            List<UserEntity> list = new ArrayList<>();
            // 需要查询的id
            List<String> selectIds = new ArrayList<>();
            for (String id : idsArr) {
                UserEntity user = CacheUtil.get("user", id, UserEntity.class);
                if (user != null) {
                    list.add(user);
                } else {
                    selectIds.add(id);
                }
            }
            if (ListUtil.isNotEmpty(selectIds)) {
                List<UserEntity> selectUsers = Db.lambdaQuery(UserEntity.class).in(UserEntity::getId, selectIds).list();
                selectUsers.forEach(user -> {
                    // 存入缓存
                    CacheUtil.put("user", user.getId(), user);
                });
                list.addAll(selectUsers);
            }
            // 按原顺序返回
            List<UserEntity> result = new ArrayList<>();
            Arrays.asList(idsArr).forEach(id -> {
                List<UserEntity> collect = list.stream().filter(e -> e.getId().equals(id)).collect(Collectors.toList());
                if (ListUtil.isNotEmpty(collect)) {
                    result.add(collect.get(0));
                }
            });
            return result;
        }
        return new ArrayList<>();
    }

    /**
     * 获取用户身份信息集合
     *
     * @param userId 用户ID
     * @return List
     */
    @Override
    public List<IdentityEntity> findIdentityList(String userId) {
        return baseMapper.findIdentityList(userId);
    }

    /**
     * 修改密码后更新token
     *
     * @param user     当前登录用户
     * @param password 密码
     * @return String
     */
    private String updateToken(UserVO user, String password) {
        if (user != null) {
            Date expiresAt = new Date(System.currentTimeMillis() + expireTime);
            // 以 password 作为 token 的密钥
            return JWT.create()
                    .withClaim("user", JSON.toJSONString(user))
                    .withExpiresAt(expiresAt)
                    .sign(Algorithm.HMAC256(password));
        } else {
            throw new SmartException("获取当前登录人信息失败！");
        }
    }
}