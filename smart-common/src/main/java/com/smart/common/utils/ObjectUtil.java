package com.smart.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.NamedThreadLocal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 对象操作工具类，继承 org.apache.commons.lang3.ObjectUtil 类
 *
 * @author wf
 * @version 2020-3-29
 */
public class ObjectUtil extends org.apache.commons.lang3.ObjectUtils {

    private static final Logger logger = LoggerFactory.getLogger(ObjectUtil.class);

    /**
     * 根据属性名获取属性值
     */
    public static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter);
            return method.invoke(o);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 根据属性名设置属性值
     *
     * @param field 字段
     * @param o     对象
     * @param value 值
     */
    private static void setFieldValue(Field field, Object o, Object value) {
        try {
            field.setAccessible(true);
            if (field.getType().isInstance(value)) {
                field.set(o, field.getType().cast(value));
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 根据属性名设置属性值
     */
    public static void setFieldValueByName(String fieldName, Object o, Object value) {
        try {
            Field field = o.getClass().getField(fieldName);
            field.setAccessible(true);
            if (field.getType().isInstance(value)) {
                field.set(o, field.getType().cast(value));
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 验证是否存在字段
     *
     * @param entity    对象
     * @param fieldName 对象中某属性名称
     * @return boolean
     */
    public static boolean checkField(Object entity, String fieldName) {
        Field[] fields = getAllFields(entity.getClass());
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取本类及其父类的字段属性
     *
     * @param clazz 当前类对象
     * @return 字段数组
     */
    public static Field[] getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(ListUtil.newArrayList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        return fieldList.toArray(fields);
    }

    /**
     * 转换为 Double 类型
     */
    public static Double toDouble(final Object val) {
        if (val == null) {
            return 0D;
        }
        try {
            return NumberUtils.toDouble(StringUtil.trim(val.toString()));
        } catch (Exception e) {
            return 0D;
        }
    }

    /**
     * 转换为 Float 类型
     */
    public static Float toFloat(final Object val) {
        return toDouble(val).floatValue();
    }

    /**
     * 转换为 Long 类型
     */
    public static Long toLong(final Object val) {
        return toDouble(val).longValue();
    }

    /**
     * 转换为 Integer 类型
     */
    public static Integer toInteger(final Object val) {
        return toLong(val).intValue();
    }

    /**
     * 转换为 Boolean 类型 'true', 'on', 'y', 't', 'yes' or '1'
     * (case-insensitive) will return true. Otherwise, false is returned.
     */
    public static Boolean toBoolean(final Object val) {
        if (val == null) {
            return false;
        }
        return BooleanUtils.toBoolean(val.toString()) || "1".equals(val.toString());
    }

    /**
     * 转换为字符串
     */
    public static String toString(final Object obj) {
        return toString(obj, StringUtil.EMPTY);
    }

    /**
     * 转换为字符串，如果对象为空，则使用 defaultVal 值
     */
    public static String toString(final Object obj, final String defaultVal) {
        return obj == null ? defaultVal : obj.toString();
    }

    /**
     * 转换为字符串，忽略空值。如 null 字符串，则被认为空值，如下：
     * "" to "" ; null to "" ; "null" to "" ; "NULL" to "" ; "Null" to ""
     */
    public static String toStringIgnoreNull(final Object val) {
        return ObjectUtil.toStringIgnoreNull(val, StringUtil.EMPTY);
    }

    /**
     * 转换为字符串，忽略空值。如 null 字符串，则被认为空值，如下：
     * "" to defaultVal ; null to defaultVal ; "null" to defaultVal ; "NULL" to defaultVal ; "Null" to defaultVal
     */
    public static String toStringIgnoreNull(final Object val, String defaultVal) {
        String str = ObjectUtil.toString(val);
        return !"".equals(str) && !"null".equalsIgnoreCase(str.trim()) ? str : defaultVal;
    }

    /**
     * 拷贝一个对象（但是子对象无法拷贝）
     *
     * @param source           对象
     * @param ignoreProperties 忽略字段
     */
    public static Object copyBean(Object source, String... ignoreProperties) {
        if (source == null) {
            return null;
        }
        try {
            Object target = source.getClass().getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target, ignoreProperties);
            return target;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                 | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw ExceptionUtil.unchecked(e);
        }
    }

    /**
     * 克隆一个对象（完全拷贝）
     *
     * @param source 对象
     */
    public static Object cloneBean(Object source) {
        if (source == null) {
            return null;
        }
        byte[] bytes = ObjectUtil.serialize(source);
        return ObjectUtil.unSerialize(bytes);
    }

    /**
     * 序列化对象
     *
     * @param object 对象
     * @return byte[]
     */
    public static byte[] serialize(Object object) {
        return ObjectUtil.serializeFst(object);
    }

    /**
     * 反序列化对象
     *
     * @param bytes 字节数组
     * @return Object
     */
    public static Object unSerialize(byte[] bytes) {
        return ObjectUtil.unSerializeFst(bytes);
    }

    /**
     * 序列化对象
     *
     * @param object 对象
     * @return byte[]
     */
    public static byte[] serializeJava(Object object) {
        if (object == null) {
            return null;
        }
        long beginTime = System.currentTimeMillis();
        byte[] bytes = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream)) {
            oos.writeObject(object);
            bytes = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long totalTime = System.currentTimeMillis() - beginTime;
        if (totalTime > 3000) {
            logger.warn(object.getClass() + " serialize time: " + TimeUtil.formatDateAgo(totalTime));
        }
        return bytes;
    }

    /**
     * 反序列化对象
     *
     * @param bytes 字节数组
     * @return Object
     */
    public static Object unSerializeJava(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        long beginTime = System.currentTimeMillis();
        Object object = null;
        if (bytes.length > 0) {
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream)) {
                object = ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long totalTime = System.currentTimeMillis() - beginTime;
        if (totalTime > 3000) {
            logger.warn(Objects.requireNonNull(object).getClass() + " unSerialize time: " + TimeUtil.formatDateAgo(totalTime));
        }
        return object;
    }

    private static final ThreadLocal<FSTConfiguration> FST_CONFIGURATION =
            new NamedThreadLocal<FSTConfiguration>("FSTConfiguration") {
                @Override
                public FSTConfiguration initialValue() {
                    return FSTConfiguration.createDefaultConfiguration();
                }
            };

    /**
     * FST 序列化对象
     *
     * @param object 对象
     * @return byte[]
     */
    public static byte[] serializeFst(Object object) {
        if (object == null) {
            return null;
        }
        long beginTime = System.currentTimeMillis();
        byte[] bytes = FST_CONFIGURATION.get().asByteArray(object);
        long totalTime = System.currentTimeMillis() - beginTime;
        if (totalTime > 3000) {
            logger.warn(object.getClass() + " fst serialize time: " + TimeUtil.formatDateAgo(totalTime));
        }
        return bytes;
    }

    /**
     * FST 反序列化对象
     *
     * @param bytes 字节数组
     * @return Object
     */
    public static Object unSerializeFst(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        long beginTime = System.currentTimeMillis();
        Object object = FST_CONFIGURATION.get().asObject(bytes);
        long totalTime = System.currentTimeMillis() - beginTime;
        if (totalTime > 3000) {
            logger.warn(object.getClass() + " fst unSerialize time: " + TimeUtil.formatDateAgo(totalTime));
        }
        return object;
    }


    /**
     * 获取所有包含注解的属性
     *
     * @param o     目标对象
     * @param clazz 注解所属类
     * @return List
     */
    public static List<Class<?>> getObjectWithTableName(Object o, Class<? extends Annotation> clazz) {
        List<Class<?>> classes = new ArrayList<>();
        Field[] declaredFields = o.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getType() == List.class) {
                try {
                    field.setAccessible(true);
                    List<?> l = (List<?>) field.get(o);
                    if (l != null) {
                        if (!l.isEmpty()) {
                            Object o1 = l.get(0);
                            boolean hasAnnotation = o1.getClass().isAnnotationPresent(clazz);
                            if (hasAnnotation) {
                                classes.add(o1.getClass());
                                List<Class<?>> list = getObjectWithTableName(o1.getClass(), clazz);
                                classes.addAll(list);
                            }
                        }
                    }
                } catch (IllegalAccessException ignored) {
                }
            } else {
                boolean hasAnnotation = field.getType().isAnnotationPresent(clazz);
                if (hasAnnotation) {
                    classes.add(field.getType());
                    List<Class<?>> list = getObjectWithTableName(field.getType(), clazz);
                    classes.addAll(list);
                }
            }
        }
        return classes;
    }

    /**
     * 对象转换JSON
     *
     * @param o 对象
     * @return JSONObject
     */
    public static JSONObject objectToJson(Object o) {
        if (null != o) {
            // 将Long类型 转化String 防止丢失精度
            SerializeConfig serializeConfig = SerializeConfig.globalInstance;
            serializeConfig.put(Long.class, ToStringSerializer.instance);
            serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
            return JSON.parseObject(JSON.toJSONString(o, serializeConfig, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat));
        } else {
            return null;
        }
    }

    /**
     * 对象转换JSONArray
     *
     * @param o 对象
     * @return JSONObject
     */
    public static JSONArray objectToJsonArray(Object o) {
        if (null != o) {
            // 将Long类型 转化String 防止丢失精度
            SerializeConfig serializeConfig = SerializeConfig.globalInstance;
            serializeConfig.put(Long.class, ToStringSerializer.instance);
            serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
            return JSON.parseArray(JSON.toJSONString(o, serializeConfig, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat));
        } else {
            return null;
        }
    }

    /**
     * 对象转JSONString
     *
     * @param o 对象
     * @return String
     */
    public static String toJSONString(Object o) {
        if (null != o) {
            // 将Long类型 转化String 防止丢失精度
            SerializeConfig serializeConfig = SerializeConfig.globalInstance;
            serializeConfig.put(Long.class, ToStringSerializer.instance);
            serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
            return JSON.toJSONString(o, serializeConfig, SerializerFeature.SortField, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
        } else {
            return null;
        }
    }

    /**
     * 初始化对象
     *
     * @param o 对象
     * @return Object
     */
    public static Object initData(Object o) {
        if (null != o) {
            Map<Class<?>, List<Field>> result = getFiled(o.getClass(), new HashMap<>(0));
            for (Class<?> clazz : result.keySet()) {
                List<Field> list = result.get(clazz);
                for (Field field : list) {
                    if (field.getType().equals(List.class)) {
                        field.setAccessible(true);
                        setFieldValue(field, o, new ArrayList<>());
                    }
                }
            }
            return o;
        } else {
            throw new RuntimeException("初始化数据失败！");
        }
    }

    /**
     * 获取树形
     *
     * @param clazz 类
     * @param map   存储map
     * @return Map
     */
    private static Map<Class<?>, List<Field>> getFiled(Class<?> clazz, Map<Class<?>, List<Field>> map) {
        List<Field> list = new ArrayList<>();
        if (null != clazz.getSuperclass()) {
            list.addAll(Arrays.asList(clazz.getDeclaredFields()));
            map.put(clazz, list);
            return getFiled(clazz.getSuperclass(), map);
        } else {
            map.put(clazz, list);
            list.addAll(Arrays.asList(clazz.getDeclaredFields()));
            return map;
        }
    }

    /**
     * Object转map
     *
     * @param obj 对象
     * @return Map
     */
    public static Map<String, Object> objectToMap(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        return Arrays.stream(fields)
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toMap(Field::getName, field -> {
                    try {
                        return field.get(obj);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    /**
     * Object转map
     *
     * @param obj 对象
     * @return Map
     */
    public static <T> T transfer(Object obj, Class<T> clazz) {
        if (obj != null) {
            if (clazz.isInstance(obj)) {
                return clazz.cast(obj); // 安全地转换类型
            } else {
                throw new ClassCastException("无法将 " + obj.getClass().getName() + " 转换为 " + clazz.getName());
            }
        }
        return null;

    }
}
