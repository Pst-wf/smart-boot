package com.smart.system.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.smart.common.utils.ObjectUtil;
import com.smart.common.utils.StringUtil;
import com.smart.entity.system.IdentityEntity;
import com.smart.entity.system.UserEntity;
import com.smart.model.response.r.ResultWrapper;
import com.smart.mybatis.entity.BaseEntity;
import com.smart.service.system.DeptService;
import com.smart.service.system.IdentityInfoService;
import com.smart.service.system.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.smart.common.constant.SmartConstant.SYSTEM_ID;
import static com.smart.common.constant.SmartConstant.SYSTEM_NAME;


/**
 * 数据创建身份信息填充工具类
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Service("resultWrapper")
public class ResultWrapperImpl implements ResultWrapper {
    @Autowired
    IdentityInfoService identityInfoService;
    @Autowired
    UserService userService;
    @Autowired
    DeptService deptService;

    @Override
    public <E> E setObjectIdentityInfo(Object data, Class<E> clazz) {
        if (data != null) {
            String str = ObjectUtil.toJSONString(data);
            try {
                JSONObject jsonObject = JSONObject.parseObject(str);
                setIdentityByKeyValue(jsonObject);
                return JSONObject.parseObject(ObjectUtil.toJSONString(jsonObject), clazz);
            } catch (JSONException ex) {
                return (E) data;
            }
        } else {
            return null;
        }
    }

    @Override
    public <E> List<E> setListIdentityInfo(Object data, Class<E> clazz) {
        if (data != null) {
            String str = ObjectUtil.toJSONString(data);
            JSONArray array = JSONArray.parseArray(str);
            for (Object o : array) {
                setIdentityByKeyValue(o);
            }
            return JSONArray.parseArray(ObjectUtil.toJSONString(array), clazz);
        } else {
            return null;
        }
    }

    /**
     * 填充信息 key:value
     *
     * @param o 修改对象
     */
    private void setIdentityByKeyValue(Object o) {
        if (o instanceof JSONObject) {
            setCreateUserNameByKeyValue(o);
            setCreateUserAccountByKeyValue(o);
            setCreateDeptNameByKeyValue(o);
            setCreateOrganizationNameByKeyValue(o);
            // 递归
            for (String key : ((JSONObject) o).keySet()) {
                if (((JSONObject) o).get(key) instanceof JSONArray) {
                    if (((JSONObject) o).getJSONArray(key) != null) {
                        for (int i = 0; i < ((JSONObject) o).getJSONArray(key).size(); i++) {
                            setIdentityByKeyValue(((JSONObject) o).getJSONArray(key).get(i));
                        }
                    }
                }
                if (((JSONObject) o).get(key) instanceof JSONObject) {
                    if (((JSONObject) o).getJSONObject(key) != null) {
                        setIdentityByKeyValue(((JSONObject) o).get(key));
                    }
                }
            }
        }
    }

    /**
     * 填充创建人姓名  key:value
     *
     * @param o 填充对象
     */
    private void setCreateUserNameByKeyValue(Object o) {
        if (((JSONObject) o).containsKey(BaseEntity.Fields.createUserName)) {
            String createUserName = ((JSONObject) o).getString(BaseEntity.Fields.createUserName);
            // 若无创建人姓名，则需要获取
            if (StringUtil.isBlank(createUserName)) {
                String createBy = ((JSONObject) o).getString(BaseEntity.Fields.createBy);
                String createUser = ((JSONObject) o).getString(BaseEntity.Fields.createUser);
                if (StringUtil.isNotBlank(createBy)) {
                    // 若是超级管理员，则手动赋值
                    if (SYSTEM_ID.equals(createBy)) {
                        ((JSONObject) o).put(BaseEntity.Fields.createUserName, SYSTEM_NAME);
                    } else {
                        if (StringUtil.isNotBlank(createUser)) {
                            UserEntity userEntity = userService.getCacheUser(createUser);
                            if (userEntity != null) {
                                ((JSONObject) o).put(BaseEntity.Fields.createUserName, userEntity.getNickname());
                            }
                        } else {
                            // 先获取身份信息
                            IdentityEntity identityEntity = identityInfoService.getCacheIdentity(createBy);
                            if (identityEntity != null) {
                                // 通过用户ID获取用户信息
                                UserEntity userEntity = userService.getCacheUser(identityEntity.getUserId());
                                if (userEntity != null) {
                                    ((JSONObject) o).put(BaseEntity.Fields.createUserName, userEntity.getNickname());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 填充创建人账号 key:value
     *
     * @param o 填充对象
     */
    private void setCreateUserAccountByKeyValue(Object o) {
        if (((JSONObject) o).containsKey(BaseEntity.Fields.createUserAccount)) {
            String createUserAccount = ((JSONObject) o).getString(BaseEntity.Fields.createUserAccount);
            // 若无创建人账号，则需要获取
            if (StringUtil.isBlank(createUserAccount)) {
                String createBy = ((JSONObject) o).getString(BaseEntity.Fields.createBy);
                String createUser = ((JSONObject) o).getString(BaseEntity.Fields.createUser);
                if (StringUtil.isNotBlank(createBy)) {
                    // 若是超级管理员，则手动赋值
                    if (SYSTEM_ID.equals(createBy)) {
                        ((JSONObject) o).put(BaseEntity.Fields.createUserAccount, "system");
                    } else {
                        if (StringUtil.isNotBlank(createUser)) {
                            UserEntity userEntity = userService.getCacheUser(createUser);
                            if (userEntity != null) {
                                ((JSONObject) o).put(BaseEntity.Fields.createUserAccount, userEntity.getUsername());
                            }
                        } else {
                            // 先获取身份信息
                            IdentityEntity identityEntity = identityInfoService.getCacheIdentity(createBy);
                            if (identityEntity != null) {
                                // 通过用户ID获取用户信息
                                UserEntity userEntity = userService.getCacheUser(identityEntity.getUserId());
                                if (userEntity != null) {
                                    ((JSONObject) o).put(BaseEntity.Fields.createUserAccount, userEntity.getUsername());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * 填充创建人部门名称  key:value
     *
     * @param o 填充对象
     */
    private void setCreateDeptNameByKeyValue(Object o) {
        if (((JSONObject) o).containsKey(BaseEntity.Fields.createDeptName)) {
            String createDeptName = ((JSONObject) o).getString(BaseEntity.Fields.createDeptName);
            // 若无创建人部门，则需要获取
            if (StringUtil.isBlank(createDeptName)) {
                String createBy = ((JSONObject) o).getString(BaseEntity.Fields.createBy);
                String createDept = ((JSONObject) o).getString(BaseEntity.Fields.createDept);
                if (StringUtil.isNotBlank(createBy)) {
                    // 若是超级管理员，则手动赋值
                    if (SYSTEM_ID.equals(createBy)) {
                        ((JSONObject) o).put(BaseEntity.Fields.createDeptName, null);
                    } else {
                        if (StringUtil.isNotBlank(createDept)) {
                            // 通过部门ID获取部门信息
                            ((JSONObject) o).put(BaseEntity.Fields.createDeptName, deptService.getAncestorsDeptName(createDept));
                        } else {
                            // 先获取身份信息
                            IdentityEntity identityEntity = identityInfoService.getCacheIdentity(createBy);
                            if (identityEntity != null) {
                                ((JSONObject) o).put(BaseEntity.Fields.createDeptName, deptService.getAncestorsDeptName(identityEntity.getDeptId()));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 填充创建人机构名称  key:value
     *
     * @param o 填充对象
     */
    private void setCreateOrganizationNameByKeyValue(Object o) {
        if (((JSONObject) o).containsKey(BaseEntity.Fields.createOrganizationName)) {
            String createOrganizationName = ((JSONObject) o).getString(BaseEntity.Fields.createOrganizationName);
            // 若无创建人公司，则需要获取
            if (StringUtil.isBlank(createOrganizationName)) {
                String createBy = ((JSONObject) o).getString(BaseEntity.Fields.createBy);
                String createOrganization = ((JSONObject) o).getString(BaseEntity.Fields.createOrganization);
                if (StringUtil.isNotBlank(createBy)) {
                    // 若是超级管理员，则手动赋值
                    if (SYSTEM_ID.equals(createBy)) {
                        ((JSONObject) o).put(BaseEntity.Fields.createOrganizationName, null);
                    } else {
                        if (StringUtil.isNotBlank(createOrganization)) {
                            // 通过部门ID获取部门信息
                            ((JSONObject) o).put(BaseEntity.Fields.createOrganizationName, deptService.getAncestorsDeptName(createOrganization));
                        } else {
                            // 先获取身份信息
                            IdentityEntity identityEntity = identityInfoService.getCacheIdentity(createBy);
                            if (identityEntity != null) {
                                ((JSONObject) o).put(BaseEntity.Fields.createOrganizationName, deptService.getAncestorsDeptName(identityEntity.getOrganizationId()));
                            }
                        }
                    }
                }
            }
        }
    }
}
