package com.smart.model.response.r;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.smart.common.annotation.ExcludeKeys;
import com.smart.common.utils.AuthUtil;
import com.smart.common.utils.JsonUtil;
import com.smart.common.utils.SpringUtil;
import com.smart.common.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;


/**
 * Web接口统一返回结果
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
public class Result implements Serializable {

    private static final long serialVersionUID = 1L;

    private static ResultWrapper resultWrapper;

    private static ResultWrapper getResultWrapper() {
        if (resultWrapper == null) {
            resultWrapper = SpringUtil.getBean(ResultWrapper.class);
        }
        return resultWrapper;
    }

    /**
     * 成功
     *
     * @return String
     */
    public static String success() {
        return success(ResultCode.SUCCESS.getMessage());
    }

    /**
     * 成功带消息
     *
     * @param message 返回消息
     * @return String
     */
    public static String success(String message) {
        Map<String, Object> result = new LinkedHashMap<>(0);
        result.put("code", ResultCode.SUCCESS.getCode());
        result.put("msg", message);
        return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
    }

    /**
     * 成功带参数
     *
     * @param data 返回数据
     * @return String
     */
    public static String data(Object data) {
        return data(data, true, 0, null);
    }

    /**
     * 成功带参数 (是否校验当前页数超过总页数)
     *
     * @param data         返回数据
     * @param checkCurrent 是否校验当前页数超过总页数
     * @return String
     */
    public static String data(Object data, int checkCurrent) {
        return data(data, true, checkCurrent, null);
    }

    /**
     * 成功带参数 (过滤器)
     *
     * @param data       返回数据
     * @param filterList 过滤器
     * @return String
     */
    public static String data(Object data, List<SerializeFilter> filterList) {
        return data(data, true, 0, filterList);
    }

    /**
     * 成功带参数 (是否处理创建人信息数据)
     *
     * @param data           返回数据
     * @param isIdentityInfo 是否处理创建人信息数据
     * @return String
     */
    public static String data(Object data, boolean isIdentityInfo) {
        return data(data, isIdentityInfo, 0, null);
    }

    /**
     * 成功带参数
     *
     * @param data               返回数据
     * @param isIdentityInfo     是否处理创建人信息数据
     * @param checkCurrentStatus 是否校验当前页数超过总页数
     * @param filterList         过滤器
     * @return String
     */
    public static String data(Object data, boolean isIdentityInfo, int checkCurrentStatus, List<SerializeFilter> filterList) {
        if (filterList == null) {
            filterList = new ArrayList<>(0);
        }
        Map<String, Object> result = new LinkedHashMap<>(0);
        result.put("code", ResultCode.SUCCESS.getCode());
        result.put("msg", ResultCode.SUCCESS.getMessage());
        Map<String, Object> record = new LinkedHashMap<>(0);
        if (data != null) {
            // 填充身份信息
            if (data.getClass() == Page.class) {
                Integer current = ((Page<?>) data).getPageNum();
                if (!((Page<?>) data).getResult().isEmpty()) {
                    Map<String, Object> map = checkCurrent(((Page<?>) data).getPages(), checkCurrentStatus);
                    boolean checkCurrent = (boolean) map.get("result");
                    current = map.get("current") == null ? current : (Integer) map.get("current");
                    if (checkCurrent) {
                        Object o = ((Page<?>) data).getResult().get(0);
                        filterList.addAll(JsonUtil.getFilterList(o, TableName.class, "excludeProperty"));
                        filterList.addAll(JsonUtil.getFilterList(o, ExcludeKeys.class, "value"));
                        if (isIdentityInfo) {
                            record.put("records", getResultWrapper().setListIdentityInfo(((Page<?>) data).getResult(), o.getClass()));
                        } else {
                            record.put("records", ((Page<?>) data).getResult());
                        }
                    } else {
                        record.put("records", new ArrayList<>());
                    }
                } else {
                    record.put("records", ((Page<?>) data).getResult());
                }
                record.put("total", new Long(((Page<?>) data).getTotal()).intValue());
                record.put("pages", ((Page<?>) data).getPages());
                record.put("current", current);
                record.put("size", ((Page<?>) data).getPageSize());
                result.put("data", record);
            } else if (data.getClass() == PageInfo.class) {
                Integer current = ((PageInfo<?>) data).getPageNum();
                if (!((PageInfo<?>) data).getList().isEmpty()) {
                    Map<String, Object> map = checkCurrent(((PageInfo<?>) data).getPages(), checkCurrentStatus);
                    boolean checkCurrent = (boolean) map.get("result");
                    current = map.get("current") == null ? current : (Integer) map.get("current");
                    if (checkCurrent) {
                        Object o = ((PageInfo<?>) data).getList().get(0);
                        filterList.addAll(JsonUtil.getFilterList(o, TableName.class, "excludeProperty"));
                        filterList.addAll(JsonUtil.getFilterList(o, ExcludeKeys.class, "value"));
                        if (isIdentityInfo) {
                            record.put("records", getResultWrapper().setListIdentityInfo(((PageInfo<?>) data).getList(), o.getClass()));
                        } else {
                            record.put("records", ((PageInfo<?>) data).getList());
                        }
                    } else {
                        record.put("records", new ArrayList<>());
                    }
                } else {
                    record.put("records", ((PageInfo<?>) data).getList());
                }
                record.put("total", new Long(((PageInfo<?>) data).getTotal()).intValue());
                record.put("pages", ((PageInfo<?>) data).getPages());
                record.put("current", current);
                record.put("size", ((PageInfo<?>) data).getPageSize());
                result.put("data", record);
            } else if (data.getClass() == ArrayList.class) {
                if (!((ArrayList<?>) data).isEmpty()) {
                    Object o = ((ArrayList<?>) data).get(0);
                    filterList.addAll(JsonUtil.getFilterList(o, TableName.class, "excludeProperty"));
                    filterList.addAll(JsonUtil.getFilterList(o, ExcludeKeys.class, "value"));
                    if (isIdentityInfo) {
                        result.put("data", getResultWrapper().setListIdentityInfo(data, o.getClass()));
                    } else {
                        result.put("data", data);
                    }
                } else {
                    result.put("data", data);
                }
            } else {
                filterList.addAll(JsonUtil.getFilterList(data, TableName.class, "excludeProperty"));
                filterList.addAll(JsonUtil.getFilterList(data, ExcludeKeys.class, "value"));
                if (isIdentityInfo) {
                    result.put("data", getResultWrapper().setObjectIdentityInfo(data, data.getClass()));
                } else {
                    result.put("data", data);
                }
            }
        }
        SerializeFilter[] filterArr = filterList.toArray(new SerializeFilter[0]);
        return JSON.toJSONString(result, filterArr, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
    }

    /**
     * 失败
     *
     * @return String
     */
    public static String fail() {
        return fail(ResultCode.FAIL.getCode(), ResultCode.FAIL.getMessage());
    }

    /**
     * 失败
     *
     * @return String
     */
    public static String fail(IResultCode resultCode) {
        return fail(resultCode.getCode(), resultCode.getMessage());
    }

    /**
     * 失败
     *
     * @return String
     */
    public static String fail(IResultCode resultCode, Object object) {
        return fail(resultCode.getCode(), resultCode.getMessage(), object);
    }

    /**
     * 失败
     *
     * @param message 返回消息
     * @return String
     */
    public static String fail(String message) {
        return fail(ResultCode.FAIL.getCode(), message);
    }

    /**
     * 失败
     *
     * @param message 返回消息
     * @return String
     */
    public static String fail(String message, Object object) {
        return fail(ResultCode.FAIL.getCode(), message, object);
    }

    /**
     * 失败
     *
     * @param code    返回编码
     * @param message 返回消息
     * @return String
     */
    public static String fail(int code, String message) {
        Map<String, Object> result = new LinkedHashMap<>(0);
        result.put("code", code);
        result.put("msg", message);
        return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
    }

    /**
     * 失败
     *
     * @param code    返回编码
     * @param message 返回消息
     * @return String
     */
    public static String fail(int code, String message, Object data) {
        Map<String, Object> result = new LinkedHashMap<>(0);
        result.put("code", code);
        result.put("msg", message);
        result.put("data", data);
        return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
    }

    /**
     * 系统异常
     *
     * @param errorId 异常ID
     * @return String
     */
    public static String error(String errorId) {
        Map<String, Object> result = new LinkedHashMap<>(0);
        result.put("code", ResultCode.ERROR.getCode());
        result.put("msg", ResultCode.ERROR.getMessage());
        result.put("errorId", errorId);
        return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
    }

    /**
     * 根据状态返回
     *
     * @param b 状态
     * @return String
     */
    public static String status(boolean b) {
        return b ? success() : fail();
    }

    /**
     * 校验页数是否超过总页数
     *
     * @param pages              总页数
     * @param checkCurrentStatus 是否校验当前页数超过总页数
     * @return boolean
     */
    private static Map<String, Object> checkCurrent(int pages, int checkCurrentStatus) {
        Map<String, Object> res = new HashMap<>(0);
        boolean check = true;
        if (checkCurrentStatus == 1) {
            // 获取入参页数
            HttpServletRequest request = AuthUtil.getRequest();
            if (request != null) {
                // 从request中获取current
                String query = request.getQueryString();
                if (StringUtil.isNotBlank(query)) {
                    List<String> queries = Arrays.asList(query.split("&"));
                    String currentParam = queries.stream().filter(item -> item.contains("current=")).findFirst().orElse(null);
                    if (StringUtil.isNotBlank(currentParam)) {
                        String current = currentParam.replace("current=", "");
                        check = Integer.parseInt(current) <= pages;
                        res.put("current", Integer.parseInt(current));
                    }
                }
            }
        }
        res.put("result", check);
        return res;
    }
}