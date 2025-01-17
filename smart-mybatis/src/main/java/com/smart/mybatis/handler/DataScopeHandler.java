package com.smart.mybatis.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.smart.common.utils.AuthUtil;
import com.smart.common.utils.CacheUtil;
import com.smart.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.smart.mybatis.constant.SqlConstant.*;

/**
 * 数据权限处理器
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Component
@Slf4j
public class DataScopeHandler {

    /**
     * 数据隔离
     */
    public String dataScope(MappedStatement ms, String sql) {
        String userId = AuthUtil.getUserId();
        if (StringUtils.isNotBlank(userId) && !AuthUtil.isSuperAdmin()) {
            // 登陆人部门ID
            String deptId = AuthUtil.getDeptId();
            // 登陆人角色ID
            String roleId = AuthUtil.getRoleId();
            // 登陆人机构ID
            String organizationId = AuthUtil.getOrganizationId();
            // 验证是否登录， 若未登录则无法获取数据权限，默认执行原SQL
            if (StringUtils.isNotBlank(roleId)) {
                Object obj = CacheUtil.get("scope", roleId);
                if (obj != null) {
                    JSONArray scopeArray = JSONArray.parseArray(JSON.toJSONString(obj));
                    if (scopeArray != null) {
                        StringBuilder sqlBuilder = new StringBuilder(sql);
                        for (int i = 0; i < scopeArray.size(); i++) {
                            JSONObject useScope = scopeArray.getJSONObject(i);
                            if (useScope.getString("scopeClass").equals(ms.getId())) {
                                String scopeType = useScope.getString("scopeType");
                                String scopeField = useScope.getString("scopeField");
                                String scopeSql = useScope.getString("scopeSql");
                                String visibilityIds = useScope.getString("visibilityIds");
                                List<String> idList = new ArrayList<>();
                                if (StringUtil.isNotBlank(visibilityIds)) {
                                    //自定义人员可见
                                    String[] ids = visibilityIds.split(",");
                                    for (String id : ids) {
                                        idList.add("'" + id + "'");
                                    }
                                }
                                // 替换参数
                                if (StringUtils.isNotBlank(scopeSql)) {
                                    scopeSql = scopeSql.replace("${Auth.userId}", "'" + userId + "'")
                                            .replace("${Auth.deptId}", "'" + deptId + "'")
                                            .replace("${Auth.organizationId}", "'" + organizationId + "'");
                                }
                                String subSql = "";
                                // 处理结束符号
                                if (sqlBuilder.toString().contains(SQL_END)) {
                                    sqlBuilder = new StringBuilder(sqlBuilder.toString().replace(SQL_END, ""));
                                }
                                if (StringUtils.isBlank(scopeField)) {
                                    scopeField = "create_by";
                                }
                                if (SQL_TYPE_6.equals(scopeType)) {
                                    //自定义sql
                                    subSql = scopeSql;
                                } else if (SQL_TYPE_1.equals(scopeType) && StringUtil.isNotBlank(userId)) {
                                    //本人可见
                                    subSql = "LEFT JOIN sys_identity identity ON main." + scopeField + " = identity.id WHERE identity.user_id = '" + userId + "'";
                                } else if (SQL_TYPE_2.equals(scopeType) && StringUtil.isNotBlank(deptId)) {
                                    //本部门可见
                                    subSql = "LEFT JOIN sys_identity identity ON main." + scopeField + " = identity.id WHERE identity.dept_id = '" + deptId + "'";
                                } else if (SQL_TYPE_3.equals(scopeType) && StringUtil.isNotBlank(organizationId)) {
                                    //本机构可见
                                    subSql = "LEFT JOIN sys_identity identity ON main." + scopeField + " = identity.id WHERE identity.organization_id = '" + organizationId + "'";
                                } else if (SQL_TYPE_4.equals(scopeType) && !idList.isEmpty()) {
                                    //指定用户可见
                                    subSql = "WHERE " + scopeField + " IN ( SELECT id FROM sys_identity WHERE user_id IN (" + StringUtil.join(idList, ",") + ") )";
                                } else if (SQL_TYPE_5.equals(scopeType) && !idList.isEmpty()) {
                                    //指定部门可见
                                    subSql = "WHERE " + scopeField + " IN ( SELECT id FROM sys_identity WHERE dept_id IN (" + StringUtil.join(idList, ",") + ") )";
                                }
                                sqlBuilder = new StringBuilder("SELECT main.* FROM ( " + sqlBuilder + " ) main " + subSql);
                            }
                        }
                        sql = sqlBuilder.toString();
                    }
                }
            }
        }
        return sql;
    }
}
