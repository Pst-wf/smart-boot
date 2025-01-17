package com.smart.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.smart.common.utils.AuthUtil;
import com.smart.common.utils.ObjectUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 填充器
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Primary
@Component
public class BaseMetaObjectHandler implements MetaObjectHandler {
    @Autowired(required = false)
    private List<MetaObjectHandler> handlers;

    @Override
    public void insertFill(MetaObject metaObject) {
        boolean auto = true;
        boolean exist = ObjectUtil.checkField(metaObject.getOriginalObject(), "auto");
        if (exist) {
            Object value = ObjectUtil.getFieldValueByName("auto", metaObject.getOriginalObject());
            if (value != null) {
                auto = (Boolean) value;
            }
        }
        if (auto) {
            this.strictInsertFill(metaObject, "createBy", String.class, AuthUtil.getIdentityId() == null ? "" : AuthUtil.getIdentityId());
            this.strictInsertFill(metaObject, "updateBy", String.class, AuthUtil.getIdentityId() == null ? "" : AuthUtil.getIdentityId());
            this.strictInsertFill(metaObject, "createUser", String.class, AuthUtil.getUserId() == null ? "" : AuthUtil.getUserId());
            this.strictInsertFill(metaObject, "createDept", String.class, AuthUtil.getDeptId() == null ? "" : AuthUtil.getDeptId());
            this.strictInsertFill(metaObject, "createOrganization", String.class, AuthUtil.getOrganizationId() == null ? "" : AuthUtil.getOrganizationId());
            this.strictInsertFill(metaObject, "createDate", Date.class, new Date());
            this.strictInsertFill(metaObject, "updateDate", Date.class, new Date());
            this.strictInsertFill(metaObject, "isDeleted", String.class, "0");
            // 多个填充器循环执行 可能会被覆盖
            if (handlers != null) {
                handlers.stream().filter(o -> !o.equals(this)).forEach(o -> o.insertFill(metaObject));
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        boolean auto = true;
        boolean exist = ObjectUtil.checkField(metaObject.getOriginalObject(), "auto");
        if (exist) {
            Object value = ObjectUtil.getFieldValueByName("auto", metaObject.getOriginalObject());
            if (value != null) {
                auto = (Boolean) value;
            }
        }
        if (auto) {
            this.strictUpdateFill(metaObject, "updateBy", String.class, AuthUtil.getIdentityId() == null ? "" : AuthUtil.getIdentityId());
            this.strictUpdateFill(metaObject, "updateDate", Date.class, new Date());
            // 多个填充器循环执行 可能会被覆盖
            if (handlers != null) {
                handlers.stream().filter(o -> !o.equals(this)).forEach(o -> o.updateFill(metaObject));
            }
        }
    }

    /**
     * 重新父类方法 ，因为原方法中会判断是否有值，若有值则不覆盖 导致填充失效
     */
    @Override
    public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
        Object obj = fieldVal.get();
        if (Objects.nonNull(obj)) {
            metaObject.setValue(fieldName, obj);
        }
        return this;
    }
}