package com.smart.tools.excel;


import com.smart.common.callback.MethodCallback;
import com.smart.common.utils.DateUtil;
import com.smart.common.utils.*;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.excel.annotation.ExcelField.Type;
import com.smart.model.excel.annotation.ExcelFields;
import com.smart.model.excel.fieldtype.FieldType;
import com.smart.service.system.DictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;

import static com.smart.common.constant.FileTypeConstant.XLS;
import static com.smart.common.constant.FileTypeConstant.XLSX;


/**
 * 导入Excel文件（支持“XLS”和“XLSX”格式）
 *
 * @author wf
 * @version 2020-3-5
 */
@Slf4j
public class ExcelImport implements Closeable {


    private static DictService dictService;

    private static DictService getDictService() {
        if (dictService == null) {
            dictService = SpringUtil.getBean(DictService.class);
        }
        return dictService;
    }

    /**
     * 工作薄对象
     */
    private final Workbook wb;

    /**
     * 工作表对象
     */
    private Sheet sheet;

    /**
     * 标题行数
     */
    private int headerNum;

    /**
     * 存储字段类型临时数据
     */
    private final Map<Class<? extends FieldType>, FieldType> fieldTypes = new HashMap<>();

    /**
     * 构造函数
     *
     * @param file 导入文件对象，读取第一个工作表
     * @throws IOException 异常
     */
    public ExcelImport(File file) throws IOException {
        this(file, 0, 0);
    }

    /**
     * 构造函数
     *
     * @param file      导入文件对象，读取第一个工作表
     * @param headerNum 标题行数，数据行号=标题行数+1
     * @throws IOException 异常
     */
    public ExcelImport(File file, int headerNum)
            throws IOException {
        this(file, headerNum, 0);
    }

    /**
     * 构造函数
     *
     * @param file             导入文件对象
     * @param headerNum        标题行数，数据行号=标题行数+1
     * @param sheetIndexOrName 工作表编号或名称，从0开始
     * @throws IOException 异常
     */
    public ExcelImport(File file, int headerNum, Object sheetIndexOrName)
            throws IOException {
        this(file.getName(), Files.newInputStream(file.toPath()), headerNum, sheetIndexOrName);
    }

    /**
     * 构造函数
     *
     * @param multipartFile    导入文件对象
     * @param headerNum        标题行数，数据行号=标题行数+1
     * @param sheetIndexOrName 工作表编号或名称，从0开始
     * @throws IOException 异常
     */
    public ExcelImport(MultipartFile multipartFile, int headerNum, Object sheetIndexOrName)
            throws IOException {
        this(multipartFile.getOriginalFilename(), multipartFile.getInputStream(), headerNum, sheetIndexOrName);
    }

    /**
     * 构造函数
     *
     * @param fileName         文件名
     * @param is               输入流
     * @param headerNum        标题行数，数据行号=标题行数+1
     * @param sheetIndexOrName 工作表编号或名称
     * @throws IOException 异常
     */
    public ExcelImport(String fileName, InputStream is, int headerNum, Object sheetIndexOrName)
            throws IOException {
        if (StringUtil.isBlank(fileName)) {
            throw new ExcelException("导入文档为空!");
        } else if (fileName.toLowerCase().endsWith(XLS)) {
            this.wb = new HSSFWorkbook(is);
        } else if (fileName.toLowerCase().endsWith(XLSX)) {
            this.wb = new XSSFWorkbook(is);
        } else {
            throw new ExcelException("文档格式不正确!");
        }
        this.setSheet(sheetIndexOrName, headerNum);
        log.debug("Initialize success.");
    }

    /**
     * 添加到 annotationList
     */
    private void addAnnotation(List<Object[]> annotationList, ExcelField ef, Object fOrM, String... groups) {
        if (ef != null) {
            if (ef.type() == Type.ALL || ef.type() == Type.IMPORT) {
                if (groups != null && groups.length > 0) {
                    boolean inGroup = false;
                    for (String g : groups) {
                        if (inGroup) {
                            break;
                        }
                        for (String efg : ef.groups()) {
                            if (StringUtil.equals(g, efg)) {
                                inGroup = true;
                                annotationList.add(new Object[]{ef, fOrM});
                                break;
                            }
                        }
                    }
                } else {
                    annotationList.add(new Object[]{ef, fOrM});
                }
            }
        }
    }

    /**
     * 获取当前工作薄
     *
     * @author wf
     */
    public Workbook getWorkbook() {
        return wb;
    }

    /**
     * 设置当前工作表和标题行数
     *
     * @author wf
     */
    public void setSheet(Object sheetIndexOrName, int headerNum) {
        if (sheetIndexOrName instanceof Integer || sheetIndexOrName instanceof Long) {
            this.sheet = this.wb.getSheetAt(ObjectUtil.toInteger(sheetIndexOrName));
        } else {
            this.sheet = this.wb.getSheet(ObjectUtil.toString(sheetIndexOrName));
        }
        if (this.sheet == null) {
            throw new ExcelException("没有找到‘" + sheetIndexOrName + "’工作表!");
        }
        this.headerNum = headerNum;
    }

    /**
     * 获取行对象
     *
     * @param rowNumber 行号
     * @return 返回Row对象，如果空行返回null
     */
    public Row getRow(int rowNumber) {
        Row row = this.sheet.getRow(rowNumber);
        if (row == null) {
            return null;
        }
        // 验证是否是空行，如果空行返回null
        short cellNum = 0;
        short emptyNum = 0;
        Iterator<Cell> it = row.cellIterator();
        while (it.hasNext()) {
            cellNum++;
            Cell cell = it.next();
            if (StringUtil.isBlank(cell.toString())) {
                emptyNum++;
            }
        }
        if (cellNum == emptyNum) {
            return null;
        }
        return row;
    }

    /**
     * 获取数据行号
     *
     * @return int
     */
    public int getDataRowNum() {
        return headerNum;
    }

    /**
     * 获取最后一个数据行号
     *
     * @return int
     */
    public int getLastDataRowNum() {
        //return this.sheet.getLastRowNum() + headerNum;
        return this.sheet.getLastRowNum() + 1;
    }

    /**
     * 获取最后一个列号
     *
     * @return int
     */
    public int getLastCellNum() {
        Row row = this.getRow(headerNum);
        return row == null ? 0 : row.getLastCellNum();
    }

    /**
     * 获取单元格值
     *
     * @param row    获取的行
     * @param column 获取单元格列号
     * @return 单元格值
     */
    public Object getCellValue(Row row, int column) {
        if (row == null) {
            return null;
        }
        Object val = "";
        try {
            Cell cell = row.getCell(column);
            if (cell != null) {
                if (cell.getCellType() == CellType.NUMERIC) {
                    val = cell.getNumericCellValue();
                    if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                        // POI Excel 日期格式转换
                        val = org.apache.poi.ss.usermodel.DateUtil.getJavaDate((Double) val);
                    } else {
                        if ((Double) val % 1 > 0) {
                            val = new DecimalFormat("0.00").format(val);
                        } else {
                            val = new DecimalFormat("0").format(val);
                        }
                    }
                } else if (cell.getCellType() == CellType.STRING) {
                    val = cell.getStringCellValue();
                } else if (cell.getCellType() == CellType.FORMULA) {
                    try {
                        val = cell.getStringCellValue();
                    } catch (Exception e) {
                        FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
                                .getCreationHelper().createFormulaEvaluator();
                        evaluator.evaluateFormulaCell(cell);
                        CellValue cellValue = evaluator.evaluate(cell);
                        switch (cellValue.getCellType()) {
                            case NUMERIC:
                                val = cellValue.getNumberValue();
                                break;
                            case STRING:
                                val = cellValue.getStringValue();
                                break;
                            case BOOLEAN:
                                val = cellValue.getBooleanValue();
                                break;
                            case ERROR:
                                val = ErrorEval.getText(cellValue.getErrorValue());
                                break;
                            default:
                                val = cell.getCellFormula();
                        }
                    }
                } else if (cell.getCellType() == CellType.BOOLEAN) {
                    val = cell.getBooleanCellValue();
                } else if (cell.getCellType() == CellType.ERROR) {
                    val = cell.getErrorCellValue();
                }
            }
        } catch (Exception e) {
            return val;
        }
        return val;
    }

    /**
     * 获取导入数据列表
     *
     * @param cls    导入对象类型
     * @param groups 导入分组
     */
    public <E> List<E> getDataList(Class<E> cls, String... groups) throws Exception {
        return getDataList(cls, false, groups);
    }

    /**
     * 获取导入数据列表
     *
     * @param cls              导入对象类型
     * @param isThrowException 遇见错误是否抛出异常
     * @param groups           导入分组
     */
    public <E> List<E> getDataList(Class<E> cls, final boolean isThrowException, String... groups) throws Exception {
        return getDataList(cls, params -> {
            if (isThrowException) {
                Exception ex = (Exception) params[0];
                int rowNum = (int) params[1];
                int columnNum = (int) params[2];
                throw new ExcelException("获取表格数据 [" + rowNum + "," + columnNum + "]", ex);
            }
            return null;
        }, groups);
    }

    /**
     * 获取导入数据列表
     *
     * @param cls               导入对象类型
     * @param exceptionCallback 遇见错误是否抛出异常
     * @param groups            导入分组
     */
    @SuppressWarnings("unchecked")
    public <E> List<E> getDataList(Class<E> cls, MethodCallback exceptionCallback, String... groups) throws Exception {
        List<Object[]> annotationList = new ArrayList<>();
        // 类注解获取
        ExcelFields cEfs = cls.getAnnotation(ExcelFields.class);
        if (cEfs != null && cEfs.value() != null) {
            for (ExcelField ef : cEfs.value()) {
                addAnnotation(annotationList, ef, cls, groups);
            }
        }
        // 字段注解获取
        Field[] fs = cls.getDeclaredFields();
        for (Field f : fs) {
            ExcelFields efs = f.getAnnotation(ExcelFields.class);
            if (efs != null && efs.value() != null) {
                for (ExcelField ef : efs.value()) {
                    addAnnotation(annotationList, ef, f, groups);
                }
            }
            ExcelField ef = f.getAnnotation(ExcelField.class);
            addAnnotation(annotationList, ef, f, groups);
        }
        // 方法注解获取
        Method[] ms = cls.getDeclaredMethods();
        for (Method m : ms) {
            ExcelFields efs = m.getAnnotation(ExcelFields.class);
            if (efs != null && efs.value() != null) {
                for (ExcelField ef : efs.value()) {
                    addAnnotation(annotationList, ef, m, groups);
                }
            }
            ExcelField ef = m.getAnnotation(ExcelField.class);
            addAnnotation(annotationList, ef, m, groups);
        }
        // 排序
        annotationList.sort(Comparator.comparing(o -> ((ExcelField) o[0]).sort()));
        // 获取Excel数据
        List<E> dataList = new ArrayList<>();
        for (int i = this.getDataRowNum(); i < this.getLastDataRowNum(); i++) {
            E e = cls.getDeclaredConstructor().newInstance();
            Row row = this.getRow(i);
            if (row == null) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < annotationList.size(); j++) {
                Object[] os = annotationList.get(j);
                ExcelField ef = (ExcelField) os[0];
                int column = (ef.column() != -1) ? ef.column() : j;
                Object val = this.getCellValue(row, column);
                if (val != null) {
                    // 如果是字典则获取字典值
                    if (StringUtil.isNotBlank(ef.dictCode())) {
                        try {
                            val = getDictService().getDictValue(ef.dictCode(), val.toString(), "");
                        } catch (Exception ex) {
                            log.error("表格数据转换对应字典字典值[ {} ]失败 [{},{}] error: {}", ef.dictCode(), i, column, ex.getMessage());
                            val = null;
                        }
                    }
                    // 转换格式
                    Class<?> valType = Class.class;
                    if (os[1] instanceof Field) {
                        valType = ((Field) os[1]).getType();
                    } else if (os[1] instanceof Method) {
                        Method method = ((Method) os[1]);
                        if ("get".equals(method.getName().substring(0, 3))) {
                            valType = method.getReturnType();
                        } else if ("set".equals(method.getName().substring(0, 3))) {
                            valType = ((Method) os[1]).getParameterTypes()[0];
                        }
                    }
                    try {
                        if (StringUtil.isNotBlank(ef.attrName())) {
                            if (ef.fieldType() != FieldType.class) {
                                FieldType ft = getFieldType(ef.fieldType());
                                val = ft.getValue(ObjectUtil.toString(val));
                            }
                        } else {
                            if (val != null) {
                                if (valType == String.class) {
                                    String s = String.valueOf(val.toString());
                                    if (StringUtil.endsWith(s, ".0")) {
                                        val = StringUtil.substringBefore(s, ".0");
                                    } else {
                                        val = String.valueOf(val.toString());
                                    }
                                } else if (valType == Integer.class) {
                                    val = Double.valueOf(val.toString()).intValue();
                                } else if (valType == Long.class) {
                                    val = Double.valueOf(val.toString()).longValue();
                                } else if (valType == Double.class) {
                                    val = Double.valueOf(val.toString());
                                } else if (valType == Float.class) {
                                    val = Float.valueOf(val.toString());
                                } else if (valType == BigDecimal.class) {
                                    val = new BigDecimal(val.toString());
                                } else if (valType == Date.class) {
                                    if (val instanceof String) {
                                        val = DateUtil.parseDate(val);
                                    } else if (val instanceof Double) {
                                        // POI Excel 日期格式转换
                                        val = org.apache.poi.ss.usermodel.DateUtil.getJavaDate((Double) val);
                                    }
                                } else {
                                    if (ef.fieldType() != FieldType.class) {
                                        FieldType ft = getFieldType(ef.fieldType());
                                        val = ft.getValue(ObjectUtil.toString(val));
                                    } else {
                                        // 如果没有指定 fieldType，请自行根据类型查找相应的转换类（com.smart.model.excel.fieldtype.值的类名+Type）
                                        Class<? extends FieldType> fieldType = (Class<? extends FieldType>) Class.forName(this.getClass().getName().replaceAll(this.getClass().getSimpleName(),
                                                "fieldtype." + valType.getSimpleName() + "Type"));
                                        FieldType ft = getFieldType(fieldType);
                                        val = ft.getValue(ObjectUtil.toString(val));
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                        log.info("Get cell value [" + i + "," + column + "] error: " + ex);
                        val = null;
                        // 参数：Exception ex, int rowNum, int columnNum
                        exceptionCallback.execute(ex, i, column);
                    }
                    // 导入的数据进行 xss 过滤
                    if (val instanceof String) {
                        val = EncodeUtil.xssFilter(val.toString());
                    }
                    // set entity value
                    if (StringUtil.isNotBlank(ef.attrName())) {
                        ReflectUtil.invokeSetter(e, ef.attrName(), val);
                    } else {
                        if (os[1] instanceof Field) {
                            ReflectUtil.invokeSetter(e, ((Field) os[1]).getName(), val);
                        } else if (os[1] instanceof Method) {
                            String methodName = ((Method) os[1]).getName();
                            if ("get".equals(methodName.substring(0, 3))) {
                                methodName = "set" + StringUtil.substringAfter(methodName, "get");
                            }
                            ReflectUtil.invokeMethod(e, methodName, new Class[]{valType}, new Object[]{val});
                        }
                    }
                }
                sb.append(val).append(", ");
            }
            dataList.add(e);
            log.debug("Read success: [" + i + "] " + sb);
        }
        return dataList;
    }

    private FieldType getFieldType(Class<? extends FieldType> fieldType) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        FieldType ft = fieldTypes.get(fieldType);
        if (ft == null) {
            ft = fieldType.getDeclaredConstructor().newInstance();
            fieldTypes.put(fieldType, ft);
        }
        return ft;
    }

    @Override
    public void close() {
        fieldTypes.clear();
        try {
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
