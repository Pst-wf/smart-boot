package com.smart.mybatis.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FastJSONArrayTypeHandler extends BaseTypeHandler<JSONArray> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONArray parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.parseJson(rs.getString(columnName));
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.parseJson(rs.getString(columnIndex));
    }

    @Override
    public JSONArray getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.parseJson(cs.getString(columnIndex));
    }

    private String toJson(JSONArray array) {
        if (array == null || array.isEmpty()) {
            return null;
        }
        try {
            SerializeConfig serializeConfig = SerializeConfig.globalInstance;
            serializeConfig.put(Long.class, ToStringSerializer.instance);
            serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
            return JSON.toJSONString(array, serializeConfig, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JSONArray parseJson(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return JSONArray.parseArray(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}