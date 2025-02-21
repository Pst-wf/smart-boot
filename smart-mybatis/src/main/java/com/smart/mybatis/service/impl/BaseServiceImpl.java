package com.smart.mybatis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smart.common.utils.ListUtil;
import com.smart.common.utils.ObjectUtil;
import com.smart.common.utils.StringUtil;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.dao.BaseDao;
import com.smart.mybatis.entity.BaseIdEntity;
import com.smart.mybatis.enums.QueryType;
import com.smart.mybatis.service.BaseService;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.smart.mybatis.constant.PatternConstant.IS_MAIL_PATTERN;
import static com.smart.mybatis.constant.PatternConstant.IS_NAN_PATTERN;
import static com.smart.mybatis.constant.SqlConstant.ORDER_ASC;
import static com.smart.mybatis.constant.SqlConstant.ORDER_DESC;

/**
 * Service实现
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Transactional(rollbackFor = Exception.class)
public class BaseServiceImpl<D extends BaseDao<T>, T> extends ServiceImpl<D, T> implements BaseService<T> {
    @Resource
    D baseDao;

    /**
     * 分页
     *
     * @param entity bean实体
     * @return Page
     */
    @Override
    public Page<T> findPage(T entity) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper = initWrapper(entity, wrapper);
        wrapper = initWrapperAfter(entity, wrapper);
        int current = (int) ObjectUtil.getFieldValueByName("current", entity);
        int size = (int) ObjectUtil.getFieldValueByName("size", entity);
        Page<T> page = PageHelper.startPage(current, size);
        super.list(wrapper);
        return page;
    }

    /**
     * 分页查询 (返回pageInfo)
     *
     * @param entity bean实体
     * @return PageInfo
     */
    @Override
    public PageInfo<T> findPageInfo(T entity) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper = initWrapper(entity, wrapper);
        wrapper = initWrapperAfter(entity, wrapper);
        int current = (int) ObjectUtil.getFieldValueByName("current", entity);
        int size = (int) ObjectUtil.getFieldValueByName("size", entity);
        PageHelper.startPage(current, size);
        return new PageInfo<>(super.list(wrapper));
    }

    /**
     * 集合
     *
     * @param entity bean实体
     * @return List
     */
    @Override
    public List<T> findList(T entity) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper = initWrapper(entity, wrapper);
        wrapper = initWrapperAfter(entity, wrapper);
        return super.list(wrapper);
    }

    /**
     * 详情
     *
     * @param id 主键ID
     * @return bean
     */
    @Override
    public T get(String id) {
        return super.getById(id);
    }

    /**
     * 新增
     *
     * @param entity bean实体
     * @return bean
     */
    @Override
    public T saveEntity(T entity) {
        validate(entity);
        beforeSaveOrUpdate(entity, true);
        T result = saveDb(entity);
        if (result != null) {
            afterSaveOrUpdate(entity, true);
        }
        return result;
    }

    /**
     * 修改
     *
     * @param entity bean实体
     * @return bean
     */
    @Override
    public T updateEntity(T entity) {
        validate(entity);
        beforeSaveOrUpdate(entity, false);
        T result = updateDb(entity);
        if (result != null) {
            afterSaveOrUpdate(entity, false);
        }
        return result;
    }

    /**
     * 删除
     *
     * @param entity bean实体
     * @return boolean
     */
    @Override
    public boolean delete(T entity) {
        beforeDelete(entity, false);
        List<String> ids = (List<String>) ObjectUtil.getFieldValueByName(BaseIdEntity.Fields.deleteIds, entity);
        if (ListUtil.isEmpty(ids)) {
            return false;
        }
        boolean b = super.removeByIds(ids);
        if (b) {
            afterDelete(entity, false);
        }
        return b;
    }

    /**
     * 物理删除
     *
     * @param entity 参数
     * @return boolean
     */
    @Override
    public boolean realDelete(T entity) {
        beforeDelete(entity, true);
        List<String> ids = (List<String>) ObjectUtil.getFieldValueByName(BaseIdEntity.Fields.deleteIds, entity);
        if (ListUtil.isEmpty(ids)) {
            return false;
        }
        String tableName = getTableName(entity.getClass());
        baseDao.realDelete(tableName, ids);
        afterDelete(entity, true);
        return true;
    }

    /**
     * 通过ID删除数据
     *
     * @param id 主键
     * @return boolean
     */
    @Override
    public boolean deleteById(String id) {
        beforeDeleteById(id);
        if (StringUtil.isBlank(id)) {
            return false;
        }
        boolean b = super.removeById(id);
        if (b) {
            afterDeleteById(id);
        }
        return b;
    }

    /**
     * 批量新增或修改
     *
     * @param list 参数
     * @return boolean
     */
    @Override
    public boolean saveOrUpdateBatch(Collection<T> list) {
        boolean b1 = true;
        boolean b2 = true;
        List<T> updateList = list.stream().filter(i -> ObjectUtil.getFieldValueByName(BaseIdEntity.Fields.id, i) != null).collect(Collectors.toList());
        List<T> insertList = list.stream().filter(i -> ObjectUtil.getFieldValueByName(BaseIdEntity.Fields.id, i) == null).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(insertList)) {
            b1 = super.saveBatch(insertList);
        }
        if (ListUtil.isNotEmpty(updateList)) {
            b2 = super.updateBatchById(updateList);
        }
        return b1 && b2;
    }

    /**
     * 通过IDS获取集合
     *
     * @param ids 主键IDS
     * @return List
     */
    @Override
    public List<T> getListByIds(String ids) {
        if (StringUtil.isNotBlank(ids)) {
            String[] idsArr = ids.split(",");
            QueryWrapper<T> wrapper = new QueryWrapper<>();
            wrapper.in(BaseIdEntity.Fields.id, Arrays.asList(idsArr));
            List<T> list = super.list(wrapper);
            // 按原顺序返回
            List<T> result = new ArrayList<>();
            Arrays.asList(idsArr).forEach(id -> {
                List<T> collect = list.stream().filter(e -> ObjectUtil.getFieldValueByName(BaseIdEntity.Fields.id, e).equals(id)).collect(Collectors.toList());
                if (ListUtil.isNotEmpty(collect)) {
                    result.add(collect.get(0));
                }
            });
            return result;
        }
        return new ArrayList<>();
    }

    /**
     * 通过IDS集合获取集合
     *
     * @param idList 主键IDS集合
     * @return List
     */
    @Override
    public List<T> getListByIdList(Collection<String> idList) {
        if (ListUtil.isNotEmpty(idList)) {
            QueryWrapper<T> wrapper = new QueryWrapper<>();
            wrapper.in(BaseIdEntity.Fields.id, idList);
            List<T> list = super.list(wrapper);
            // 按原顺序返回
            List<T> result = new ArrayList<>();
            idList.forEach(id -> {
                List<T> collect = list.stream().filter(e -> ObjectUtil.getFieldValueByName(BaseIdEntity.Fields.id, e).equals(id)).collect(Collectors.toList());
                if (ListUtil.isNotEmpty(collect)) {
                    result.add(collect.get(0));
                }
            });
            return result;
        }
        return new ArrayList<>();
    }

    /**
     * 验证所有字段
     *
     * @param entity bean 实体
     */
    public void validate(T entity) {
        BeanWrapper bw = new BeanWrapperImpl(entity);
        //获取本类及父类所有注解标记的字段
        Field[] fields = FieldUtils.getFieldsWithAnnotation(entity.getClass(), Column.class);
        for (Field f : fields) {
            String value = "";
            if (bw.getPropertyValue(f.getName()) != null) {
                value = String.valueOf(bw.getPropertyValue(f.getName()));
            }
            //获取注解对象
            Column v = f.getAnnotation(Column.class);
            //获取是否要求不为null或者''
            boolean isNull = v.isNull();
            //是否是数字
            boolean isNumber = v.isNumber();
            //获取要求长度
            long length = v.length();
            //获取要求最大值
            double max = v.max();
            //获取要求最小值
            double min = v.min();
            //小数点后保留位数
            int pointCount = v.pointCount();
            //验证手机号
            boolean tel = v.tel();
            //验证手机号
            boolean idCard = v.idCard();
            //验证手机号
            boolean email = v.email();
            //小于某个值
            String lessThan = v.lessThan();
            //大于某个值
            String greaterThan = v.greaterThan();
            //验证是否为空
            if (!isNull) {
                if (StringUtil.isBlank(value)) {
                    throw new SmartException("[" + f.getName() + "]不能为NULL或空白字符");
                }
            }
            //验证是否是数字
            if (isNumber) {
                if (StringUtil.isNotBlank(value)) {
                    boolean isNaN = IS_NAN_PATTERN.matcher(value).matches();
                    if (!isNaN) {
                        throw new SmartException("[" + f.getName() + "]必须为数字");
                    }
                }
            }
            //验证长度
            if (length != 255) {
                if (StringUtil.isNotBlank(value)) {
                    if (value.length() > v.length()) {
                        throw new SmartException("[" + f.getName() + "]长度不能超过" + v.length());
                    }
                }
            }
            //验证手机号格式
            if (tel) {
                if (StringUtil.isNotBlank(value)) {
                    if (value.length() != 11) {
                        throw new SmartException("手机号格式不正确");
                    }
                }
            }
            //验证身份证(只验证长度)
            if (idCard) {
                if (StringUtil.isNotBlank(value)) {
                    if (value.length() != 15 && value.length() != 18) {
                        throw new SmartException("请输入15位或18位身份证");
                    }
                }
            }
            //验证邮箱(只验证长度)
            if (email) {
                if (StringUtil.isNotBlank(value)) {
                    boolean isMatched = IS_MAIL_PATTERN.matcher(value).matches();
                    if (!isMatched) {
                        throw new SmartException("邮箱格式不正确");
                    }
                }
            }
            if (max != -1 || min != -1 || pointCount != -1 || StringUtil.isNotBlank(lessThan) || StringUtil.isNotBlank(greaterThan)) {
                if (StringUtil.isNotBlank(value)) {
                    //正则判断是否为数字
                    boolean isNaN = IS_NAN_PATTERN.matcher(value).matches();
                    if (isNaN) {
                        BigDecimal bValue = new BigDecimal(value);

                        //验证最大值
                        if (max != -1) {
                            if (bValue.compareTo(new BigDecimal(max)) > 0) {
                                throw new SmartException("[" + f.getName() + "]最大值不能超过" + max);
                            }
                        }
                        //验证最小值
                        if (min != -1) {
                            if (bValue.compareTo(new BigDecimal(min)) < 0) {
                                throw new SmartException("[" + f.getName() + "]最小值不能低于" + min);
                            }
                        }
                        //验证小数点后保留位数
                        if (pointCount != -1) {
                            //判断是否包含小数点
                            int num = value.indexOf(".");
                            if (num > 0) {
                                //小数点后有多少位
                                int differ = (value.length() - num) - 1;
                                if (differ > pointCount) {
                                    throw new SmartException("[" + f.getName() + "]小数点后最多保留" + pointCount + "位");
                                }
                            }
                        }
                        try {
                            //验证是否小于
                            if (StringUtil.isNotBlank(lessThan)) {
                                Field field = entity.getClass().getDeclaredField(lessThan);
                                String target;
                                if (bw.getPropertyValue(field.getName()) != null) {
                                    target = String.valueOf(bw.getPropertyValue(field.getName()));
                                    if (StringUtil.isNotBlank(target)) {
                                        boolean targetNaN = IS_NAN_PATTERN.matcher(target).matches();
                                        if (targetNaN) {
                                            //转化成数字
                                            if (bValue.compareTo(new BigDecimal(target)) >= 0) {
                                                throw new SmartException("[" + f.getName() + "]应小于" + target);
                                            }
                                        } else {
                                            throw new SmartException("[" + f.getName() + "]小于的值必须是数字类型");
                                        }
                                    }
                                }
                            }
                            //验证是否小于
                            if (StringUtil.isNotBlank(greaterThan)) {
                                Field field = entity.getClass().getDeclaredField(greaterThan);
                                String target;
                                if (bw.getPropertyValue(field.getName()) != null) {
                                    target = String.valueOf(bw.getPropertyValue(field.getName()));
                                    if (StringUtil.isNotBlank(target)) {
                                        boolean targetNaN = IS_NAN_PATTERN.matcher(target).matches();
                                        if (targetNaN) {
                                            //转化成数字
                                            if (bValue.compareTo(new BigDecimal(target)) <= 0) {
                                                throw new SmartException("[" + f.getName() + "]应大于" + target);
                                            }
                                        } else {
                                            throw new SmartException("[" + f.getName() + "]大于的值必须是数字类型");
                                        }
                                    }
                                }
                            }
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                            throw new RuntimeException("系统错误");
                        }
                    } else {
                        if (StringUtil.isNotBlank(value)) {
                            throw new SmartException("[" + f.getName() + "]必须为数字");
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化Wrapper
     *
     * @param entity  bean 实体
     * @param wrapper 查询条件
     * @return QueryWrapper
     */
    public QueryWrapper<T> initWrapper(T entity, QueryWrapper<T> wrapper) {
        BeanWrapper bw = new BeanWrapperImpl(entity);
        //获取本类及父类所有注解标记的字段
        Field[] fields = FieldUtils.getFieldsWithAnnotation(entity.getClass(), Column.class);
        for (Field f : fields) {
            Object value = bw.getPropertyValue(f.getName());
            if (value != null && StringUtil.isNotBlank(value.toString().trim())) {
                //获取值 String类型
                //获取注解对象
                Column v = f.getAnnotation(Column.class);
                //字段名
                ColumnCache columnCache = LambdaUtils.getColumnMap(entity.getClass()).get(f.getName().toUpperCase(Locale.ROOT));
                String column = StringUtil.isNotBlank(v.name()) ? v.name() : (columnCache != null ? columnCache.getColumn() : f.getName());
                //查询条件
                QueryType queryType = v.queryType();
                if (queryType != null && StringUtil.isNotBlank(column)) {
                    switch (queryType) {
                        case EQ:
                            wrapper.eq(column, value);
                            break;
                        case LIKE:
                            wrapper.like(column, value);
                            break;
                        case NE:
                            wrapper.ne(column, value);
                            break;
                        case GT:
                            wrapper.gt(column, value);
                            break;
                        case GE:
                            wrapper.ge(column, value);
                            break;
                        case LT:
                            wrapper.lt(column, value);
                            break;
                        case LE:
                            wrapper.le(column, value);
                            break;
                        case IN:
                            if (value.getClass().equals(ArrayList.class)) {
                                if (!((ArrayList<?>) value).isEmpty()) {
                                    List<Object> list = new ArrayList<>(((ArrayList<?>) value));
                                    wrapper.in(column, list);
                                    break;
                                }
                            } else if (value.getClass().equals(String.class)) {
                                List<String> list = Arrays.asList(((String) value).split(v.separator()));
                                if (!list.isEmpty()) {
                                    wrapper.in(column, list);
                                    break;
                                }
                            }
                        case NOT_IN:
                            if (value.getClass().equals(ArrayList.class)) {
                                if (!((ArrayList<?>) value).isEmpty()) {
                                    List<Object> list = new ArrayList<>(((ArrayList<?>) value));
                                    wrapper.notIn(column, list);
                                    break;
                                }
                            } else if (value.getClass().equals(String.class)) {
                                List<String> list = Arrays.asList(((String) value).split(v.separator()));
                                if (!list.isEmpty()) {
                                    wrapper.notIn(column, list);
                                    break;
                                }
                            }
                        case BETWEEN:
                            if (value.getClass().equals(ArrayList.class)) {
                                if (((ArrayList<?>) value).size() == 2) {
                                    wrapper.between(column, ((ArrayList<?>) value).get(0), ((ArrayList<?>) value).get(1));
                                    break;
                                }
                            } else if (value.getClass().equals(String.class)) {
                                String[] params = ((String) value).split(v.separator());
                                if (params.length == 2) {
                                    wrapper.between(column, params[0], params[1]);
                                    break;
                                }
                            }
                        default:
                    }
                }
            }
        }
        Boolean defaultOrderStatus = (Boolean) ObjectUtil.getFieldValueByName("defaultOrder", entity);
        String sortField = (String) ObjectUtil.getFieldValueByName("sortField", entity);
        String sortOrder = (String) ObjectUtil.getFieldValueByName("sortOrder", entity);
        if (StringUtil.isNotBlank(sortField) && StringUtil.isNotBlank(sortOrder)) {
            String[] fieldArray = sortField.split(",");
            List<String> columns = new ArrayList<>();
            for (String filed : fieldArray) {
                //获取实体对应数据库的字段名
                Map<String, ColumnCache> columnMap = LambdaUtils.getColumnMap(entity.getClass());
                ColumnCache columnCache = columnMap.get(filed.toUpperCase(Locale.ROOT));
                if (columnCache != null) {
                    String column = columnCache.getColumn();
                    if (StringUtil.isNotBlank(column)) {
                        columns.add(column);
                    } else {
                        throw new SmartException("排序字段异常");
                    }
                } else {
                    // 用户自定义字段
                    columns.add(filed);
                }

            }
            if (ListUtil.isNotEmpty(columns)) {
                if (ORDER_ASC.equals(sortOrder.toLowerCase(Locale.ROOT))) {
                    wrapper.orderBy(true, true, StringUtil.join(columns, ","));
                } else if (ORDER_DESC.equals(sortOrder.toLowerCase(Locale.ROOT))) {
                    wrapper.orderBy(true, false, StringUtil.join(columns, ","));
                } else {
                    throw new SmartException("排序类型异常");
                }
            }
        } else {
            if (defaultOrderStatus != null && defaultOrderStatus) {
                // 默认排序规则
                defaultOrder(entity, wrapper);
            }
        }
        return wrapper;
    }

    /**
     * 默认排序规则
     *
     * @param entity  bean 实体
     * @param wrapper 查询条件
     */
    public void defaultOrder(T entity, QueryWrapper<T> wrapper) {
        if (ObjectUtil.checkField(entity, "sort")) {
            wrapper.orderBy(true, true, "sort");
        }
        if (ObjectUtil.checkField(entity, "order")) {
            wrapper.orderBy(true, true, "`order`");
        }
        if (ObjectUtil.checkField(entity, "createDate")) {
            wrapper.orderBy(true, false, "create_date");
        }
    }

    /**
     * 初始化wrapper之后执行
     *
     * @param entity  bean 实体
     * @param wrapper 查询条件
     * @return QueryWrapper
     */
    public QueryWrapper<T> initWrapperAfter(T entity, QueryWrapper<T> wrapper) {
        return wrapper;
    }

    /**
     * 保存
     *
     * @param entity bean 实体
     */
    public T saveDb(T entity) {
        return super.save(entity) ? entity : null;
    }

    /**
     * 修改
     *
     * @param entity bean 实体
     */
    public T updateDb(T entity) {
        return super.updateById(entity) ? entity : null;
    }

    /**
     * 保存之前处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    public void beforeSaveOrUpdate(T entity, boolean isAdd) {
    }

    /**
     * 删除之前处理
     *
     * @param entity bean 实体
     * @param isReal 是否物理删除
     */
    public void beforeDelete(T entity, boolean isReal) {
    }

    /**
     * 删除之前处理
     *
     * @param id 主键
     */
    public void beforeDeleteById(String id) {
    }

    /**
     * 保存之后处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    public void afterSaveOrUpdate(T entity, boolean isAdd) {
    }

    /**
     * 批量删除之后处理
     *
     * @param entity bean 实体
     * @param isReal 是否物理删除
     */
    public void afterDelete(T entity, boolean isReal) {
    }

    /**
     * 通过主键ID删除之后处理
     *
     * @param id 主键
     */
    public void afterDeleteById(String id) {
    }

    /**
     * 根据class获取表名
     *
     * @param clazz 参数0
     * @return java.lang.String
     */
    public String getTableName(Class<?> clazz) {
        // 获取表信息
        com.baomidou.mybatisplus.core.metadata.TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        if (tableInfo != null) {
            return tableInfo.getTableName();
        }

        // 如果没有找到表信息，返回类名的小写形式
        return StringUtils.underlineToCamel(clazz.getSimpleName()).toLowerCase();
    }

}
