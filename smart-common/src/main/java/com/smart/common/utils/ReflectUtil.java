package com.smart.common.utils;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * 反射工具类.
 * 提供调用getter/setter方法, 访问私有变量, 调用私有方法, 获取泛型类型Class, 被AOP过的真实类等工具函数.
 *
 * @author wf
 * @version 2022-01-01 17:28:23
 */
@SuppressWarnings("rawtypes")
public class ReflectUtil {

    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    private static final String CGLIB_CLASS_SEPARATOR = "$$";

    private static final Logger logger = LoggerFactory.getLogger(ReflectUtil.class);

    /**
     * 调用Getter方法，
     * 支持多级，如：对象名.对象名.方法，
     * 支持静态类及方法调用，
     * 支持Map
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeGetter(Object obj, String propertyName) {
        Object object = obj;
        for (String name : StringUtil.split(propertyName, ".")) {
            if (obj instanceof Map) {
                object = ((Map) obj).get(name);
            } else {
                String methodName = GETTER_PREFIX + StringUtil.capitalize(name);
                object = invokeMethod(object, methodName, new Class[]{}, new Object[]{});
            }
        }
        return (E) object;
    }

    /**
     * 调用Setter方法，仅匹配方法名，
     * 支持多级，如：对象名.对象名.方法，
     * 支持静态类及方法调用，
     * 支持Map
     */
    @SuppressWarnings("unchecked")
    public static <E> void invokeSetter(Object obj, String propertyName, E value) {
        Object object = obj;
        String[] names = StringUtil.split(propertyName, ".");
        for (int i = 0; i < names.length; i++) {
            if (i < names.length - 1) {
                if (obj instanceof Map) {
                    object = ((Map) obj).get(names[i]);
                } else {
                    String methodName = GETTER_PREFIX + StringUtil.capitalize(names[i]);
                    object = invokeMethod(object, methodName, new Class[]{}, new Object[]{});
                }
            } else {
                if (obj instanceof Map) {
                    ((Map) obj).put(names[i], value);
                } else {
                    String methodName = SETTER_PREFIX + StringUtil.capitalize(names[i]);
                    invokeMethodByName(object, methodName, new Object[]{value});
                }
            }
        }
    }

    /**
     * 注解调用Getter方法，
     * 支持多级，如：对象名.对象名.方法，
     * 支持静态类及方法调用，
     * 支持Map
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeAnnotationGetter(Object obj, String propertyName) {
        Object object = obj;
        for (String name : StringUtil.split(propertyName, ".")) {
            object = invokeMethod(object, name, new Class[]{}, new Object[]{});
        }
        return (E) object;
    }

    /**
     * 直接读取对象属性值，无视private/protected修饰符，不经过getter函数
     */
    @SuppressWarnings("unchecked")
    public static <E> E getFieldValue(final Object obj, final String fieldName) {
        Field field = getAccessibleField(obj, fieldName);
        if (field == null) {
            //throw new IllegalArgumentException("在 [" + obj.getClass() + "] 中，没有找到 [" + fieldName + "] 字段 ");
            if (obj != null) {
                logger.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + fieldName + "] 字段 ");
            }
            return null;
        }
        E result = null;
        try {
            result = (E) field.get(obj);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常: {}", e.getMessage());
        }
        return result;
    }

    /**
     * 直接设置对象属性值，无视private/protected修饰符，不经过setter函数
     */
    public static <E> void setFieldValue(final Object obj, final String fieldName, final E value) {
        Field field = getAccessibleField(obj, fieldName);
        if (field == null) {
            //throw new IllegalArgumentException("在 [" + obj.getClass() + "] 中，没有找到 [" + fieldName + "] 字段 ");
            logger.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + fieldName + "] 字段 ");
            return;
        }
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常: {}", e.getMessage());
        }
    }

    /**
     * 直接调用对象方法，无视private/protected修饰符，
     * 用于一次性调用的情况，否则应使用getAccessibleMethod()函数获得Method后反复调用，
     * 同时匹配方法名+参数类型，
     * 支持静态类及方法调用
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
                                     final Object[] args) {
        if (obj == null || methodName == null) {
            return null;
        }
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            //throw new IllegalArgumentException("在 [" + obj.getClass() + "] 中，没有找到 [" + methodName + "] 方法 ");
            logger.debug("在 [" + (obj.getClass() == Class.class ? obj : obj.getClass()) + "] 中，没有找到 [" + methodName + "] 方法 ");
            return null;
        }
        try {
            return (E) method.invoke(obj.getClass() == Class.class ? null : obj, args);
        } catch (Exception e) {
            String msg = "method: " + method + ", obj: " + obj + ", args: " + Arrays.toString(args) + "";
            throw convertReflectionExceptionToUnchecked(msg, e);
        }
    }

    /**
     * 直接调用对象方法，无视private/protected修饰符，
     * 用于一次性调用的情况，否则应使用getAccessibleMethodByName()函数获得Method后反复调用，
     * 只匹配函数名，如果有多个同名函数调用第一个，
     * 支持静态类及方法调用
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
        Method method = getAccessibleMethodByName(obj, methodName, args.length);
        if (method == null) {
            // 如果为空不报错，直接返回空。
//			throw new IllegalArgumentException("在 [" + obj.getClass() + "] 中，没有找到 [" + methodName + "] 方法 ");
            if (obj != null) {
                logger.debug("在 [" + (obj.getClass() == Class.class ? obj : obj.getClass()) + "] 中，没有找到 [" + methodName + "] 方法 ");
            }
            return null;
        }
        try {
            // 类型转换（将参数数据类型转换为目标方法参数类型）
            Class<?>[] cs = method.getParameterTypes();
            for (int i = 0; i < cs.length; i++) {
                if (args[i] != null && !args[i].getClass().equals(cs[i])) {
                    if (cs[i] == String.class) {
                        args[i] = ObjectUtil.toString(args[i]);
                        if (StringUtil.endsWith((String) args[i], ".0")) {
                            args[i] = StringUtil.substringBefore((String) args[i], ".0");
                        }
                    } else if (cs[i] == Integer.class) {
                        args[i] = ObjectUtil.toInteger(args[i]);
                    } else if (cs[i] == Long.class) {
                        args[i] = ObjectUtil.toLong(args[i]);
                    } else if (cs[i] == Double.class) {
                        args[i] = ObjectUtil.toDouble(args[i]);
                    } else if (cs[i] == Float.class) {
                        args[i] = ObjectUtil.toFloat(args[i]);
                    } else if (cs[i] == Date.class) {
                        if (args[i] instanceof String) {
                            args[i] = DateUtil.parseDate(args[i]);
                        } else {
                            // POI Excel 日期格式转换
                            args[i] = org.apache.poi.ss.usermodel.DateUtil.getJavaDate((Double) args[i]);
                        }
                    }
                }
            }
            return (E) method.invoke(obj.getClass() == Class.class ? null : obj, args);
        } catch (Exception e) {
            String msg = "method: " + method + ", obj: " + obj + ", args: " + Arrays.toString(args) + "";
            throw convertReflectionExceptionToUnchecked(msg, e);
        }
    }

    /**
     * 循环向上转型，获取对象的DeclaredField，并强制设置为可访问，
     * 如向上转型到Object仍无法找到，返回null
     */
    public static Field getAccessibleField(final Object obj, final String fieldName) {
        // 为空不报错。直接返回 null
        // Validate.notNull(obj, "object can't be null");
        if (obj == null) {
            return null;
        }
        Validate.notBlank(fieldName, "fieldName can't be blank");
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                makeAccessible(field);
                return field;
            } catch (NoSuchFieldException e) {
                // Field不在当前类定义,继续向上转型
                // new add
            }
        }
        return null;
    }

    /**
     * 循环向上转型，获取对象的DeclaredMethod,并强制设置为可访问，
     * 如向上转型到Object仍无法找到，返回null，
     * 匹配函数名+参数类型。
     * 用于方法需要被多次调用的情况，先使用本函数先取得Method，然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethod(final Object obj, final String methodName,
                                             final Class<?>... parameterTypes) {
        // 为空不报错。直接返回 null
        // Validate.notNull(obj, "object can't be null");
        if (obj == null) {
            return null;
        }
        Class<?> clazz = obj.getClass();
        if (clazz == Class.class) {
            clazz = (Class) obj;
        }
        Validate.notBlank(methodName, "methodName can't be blank");
        for (Class<?> searchType = clazz; searchType != Object.class; searchType = searchType.getSuperclass()) {
            try {
                Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
                makeAccessible(method);
                return method;
            } catch (NoSuchMethodException e) {
                // Method不在当前类定义,继续向上转型
                // new add
            }
        }
        return null;
    }

    /**
     * 循环向上转型，获取对象的DeclaredMethod，并强制设置为可访问，
     * 如向上转型到Object仍无法找到，返回null，
     * 只匹配函数名。
     * 用于方法需要被多次调用的情况，先使用本函数先取得Method，然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethodByName(final Object obj, final String methodName, int argsNum) {
        // 为空不报错。直接返回 null
        // Validate.notNull(obj, "object can't be null");
        if (obj == null) {
            return null;
        }
        Class<?> clazz = obj.getClass();
        if (clazz == Class.class) {
            clazz = (Class) obj;
        }
        Validate.notBlank(methodName, "methodName can't be blank");
        for (Class<?> searchType = clazz; searchType != Object.class; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName) && method.getParameterTypes().length == argsNum) {
                    makeAccessible(method);
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 改变private/protected的方法为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers())
                || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers())
                || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
                || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 通过反射，获得Class定义中声明的泛型参数的类型，注意泛型必须定义在父类处，
     * 如无法找到，返回Object.class，
     * 如 public UserDao extends CrudDao<User>
     *
     * @param clazz The class to introspect
     * @return Object.class
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClassGenericType(final Class clazz) {
        return getClassGenericType(clazz, 0);
    }

    /**
     * 通过反射，获得Class定义中声明的父类的泛型参数的类型，
     * 如无法找到，返回Object.class，
     * 如 public UserDao extends CrudDao<User, Long>
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic declaration,start from 0.
     * @return Object.class
     */
    public static Class getClassGenericType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            logger.debug(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            logger.debug("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
                    + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            logger.debug(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }
        return (Class) params[index];
    }

    /**
     * 获取类的Class，如果为内部类，则返回上级类Class
     */
    public static Class<?> getUserClass(Object instance) {
        if (instance == null) {
            throw new RuntimeException("Instance must not be null");
        }
        Class clazz = instance.getClass();
        if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !Object.class.equals(superClass)) {
                return superClass;
            }
        }
        return clazz;

    }

    /**
     * 将反射时的checked exception转换为unchecked exception
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(String msg, Exception e) {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(msg, e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException(msg, ((InvocationTargetException) e).getTargetException());
        }
        return new RuntimeException(msg, e);
    }

    public static <T extends Annotation> List<T> getAnnotationFromSuperclass(Class<?> clazz, Class<T> annotationClass) {
        List<T> list = new ArrayList<>();
        collect(clazz, annotationClass, list);
        return list;
    }

    public static <T extends Annotation> void collect(Class<?> clazz, Class<T> annotationClass, List<T> list) {
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == annotationClass) {
                list.add((T) annotation);
            }
        }
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            collect(superclass, annotationClass, list);
        }
    }
}
