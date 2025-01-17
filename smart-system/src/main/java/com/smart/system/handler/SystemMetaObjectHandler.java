package com.smart.system.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.smart.common.utils.ObjectUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 填充器
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Component
public class SystemMetaObjectHandler implements MetaObjectHandler {

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
            // 对象存储启用状态
            this.strictInsertFill(metaObject, "ossStatus", String.class, "0");
            // 用户状态
            this.strictInsertFill(metaObject, "userStatus", String.class, "1");
            // 是否系统用户
            this.strictInsertFill(metaObject, "isSys", String.class, "0");
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
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