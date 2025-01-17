package com.smart.tools.excel;


import com.smart.common.utils.*;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.excel.annotation.ExcelField.Align;
import com.smart.model.excel.annotation.ExcelField.Type;
import com.smart.model.excel.annotation.ExcelFields;
import com.smart.model.excel.fieldtype.FieldType;
import com.smart.service.system.DictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * 导出Excel文件（导出“XLSX”格式，支持大数据量导出   @see org.apache.poi.ss.SpreadsheetVersion）
 *
 * @author wf
 * @version 2022-01-01 17:28:23
 */
@Slf4j
public class ExcelExport implements Closeable {


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
     * 样式列表
     */
    private Map<String, CellStyle> styles;

    /**
     * 当前行号
     */
    private int rowNo;

    /**
     * 注解列表（Object[]{ ExcelField, Field/Method }）
     */
    private List<Object[]> annotationList;
    /**
     * 排除字段
     */
    private List<String> excludeList;

    /**
     * 存储字段类型临时数据
     */
    private final Map<Class<? extends FieldType>, FieldType> fieldTypes = new HashMap<>();

    /**
     * 构造函数
     *
     * @param title 表格标题，传“空值”，表示无标题
     * @param cls   实体对象，通过annotation.ExportField获取标题
     */
    public ExcelExport(String title, Class<?> cls) {
        this(title, cls, null, Type.EXPORT);
    }

    /**
     * 构造函数
     *
     * @param title       表格标题，传“空值”，表示无标题
     * @param cls         实体对象，通过annotation.ExportField获取标题
     * @param excludeList 排除的字段
     */
    public ExcelExport(String title, Class<?> cls, List<String> excludeList) {
        this(title, cls, excludeList, Type.EXPORT);
    }

    /**
     * 构造函数
     *
     * @param title       表格标题，传“空值”，表示无标题
     * @param cls         实体对象，通过annotation.ExportField获取标题
     * @param excludeList 排除的字段
     * @param type        导出类型（1:导出数据；2：导出模板）
     * @param groups      导入分组
     */
    public ExcelExport(String title, Class<?> cls, List<String> excludeList, Type type, String... groups) {
        this(null, null, title, cls, excludeList, type, groups);
    }

    /**
     * 构造函数
     *
     * @param sheetName   指定Sheet名称
     * @param title       表格标题，传“空值”，表示无标题
     * @param cls         实体对象，通过annotation.ExportField获取标题
     * @param excludeList 排除的字段
     * @param type        导出类型（1:导出数据；2：导出模板）
     * @param groups      导入分组
     */
    public ExcelExport(String sheetName, String title, Class<?> cls, List<String> excludeList, Type type, String... groups) {
        this(null, sheetName, title, cls, excludeList, type, groups);
    }

    /**
     * 构造函数
     *
     * @param wb          指定现有工作簿对象
     * @param sheetName   指定Sheet名称
     * @param title       表格标题，传“空值”，表示无标题-+
     * @param cls         实体对象，通过annotation.ExportField获取标题
     * @param excludeList 排除的字段
     * @param type        导出类型（1:导出数据；2：导出模板）
     * @param groups      导入分组
     */
    public ExcelExport(Workbook wb, String sheetName, String title, Class<?> cls, List<String> excludeList, Type type, String... groups) {
        if (wb != null) {
            this.wb = wb;
        } else {
            this.wb = createWorkbook();
        }
        this.createSheet(sheetName, title, cls, excludeList, type, groups);
    }

    /**
     * 构造函数
     *
     * @param title      表格标题，传“空值”，表示无标题
     * @param headerList 表头数组
     */
    public ExcelExport(String title, List<String> headerList, List<Integer> headerWidthList) {
        this(null, null, title, headerList, headerWidthList);
    }

    /**
     * 构造函数
     *
     * @param sheetName  指定Sheet名称
     * @param title      表格标题，传“空值”，表示无标题
     * @param headerList 表头数组
     */
    public ExcelExport(String sheetName, String title, List<String> headerList, List<Integer> headerWidthList) {
        this(null, sheetName, title, headerList, headerWidthList);
    }

    /**
     * 构造函数
     *
     * @param wb                  指定现有工作簿对象
     * @param sheetName，指定Sheet名称
     * @param title               表格标题，传“空值”，表示无标题
     * @param headerList          表头列表
     */
    public ExcelExport(Workbook wb, String sheetName, String title, List<String> headerList, List<Integer> headerWidthList) {
        if (wb != null) {
            this.wb = wb;
        } else {
            this.wb = createWorkbook();
        }
        this.createSheet(sheetName, title, headerList, headerWidthList);
    }

    /**
     * 创建一个工作簿
     */
    public Workbook createWorkbook() {
        return new SXSSFWorkbook(500);
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
     * 创建工作表
     *
     * @param sheetName，指定Sheet名称
     * @param title               表格标题，传“空值”，表示无标题
     * @param cls                 实体对象，通过annotation.ExportField获取标题
     * @param excludeList         排除的字段
     * @param type                导出类型（1:导出数据；2：导出模板）
     * @param groups              导入分组
     */
    public void createSheet(String sheetName, String title, Class<?> cls, List<String> excludeList, Type type, String... groups) {
        this.annotationList = new ArrayList<>();
        this.excludeList = excludeList;
        // 从字段上获取
        Field[] fs = cls.getDeclaredFields();
        for (Field f : fs) {
            ExcelFields efs = f.getAnnotation(ExcelFields.class);
            if (efs != null && efs.value() != null) {
                for (ExcelField ef : efs.value()) {
                    addAnnotation(annotationList, ef, f, type, groups);
                }
            }
            ExcelField ef = f.getAnnotation(ExcelField.class);
            addAnnotation(annotationList, ef, f, type, groups);
        }
        // 从方法上获取
        Method[] ms = cls.getDeclaredMethods();
        for (Method m : ms) {
            ExcelFields efs = m.getAnnotation(ExcelFields.class);
            if (efs != null && efs.value() != null) {
                for (ExcelField ef : efs.value()) {
                    addAnnotation(annotationList, ef, m, type, groups);
                }
            }
            ExcelField ef = m.getAnnotation(ExcelField.class);
            addAnnotation(annotationList, ef, m, type, groups);
        }

        // 从类上获取
        ExcelFields annotation = cls.getAnnotation(ExcelFields.class);
        if (annotation != null && annotation.value() != null) {
            for (ExcelField excelField : annotation.value()) {
                addAnnotation(annotationList, excelField, cls, type, groups);
            }
        }

        // 字段排序
        annotationList.sort(Comparator.comparing(o -> ((ExcelField) o[0]).sort()));
        // Initialize
        List<String> headerList = new ArrayList<>();
        List<Integer> headerWidthList = new ArrayList<>();
        for (Object[] os : annotationList) {
            ExcelField ef = (ExcelField) os[0];
            String headerTitle = ef.title();
            // 如果是导出，则去掉注释
            if (type == Type.EXPORT) {
                String[] ss = StringUtil.split(headerTitle, "**", 2);
                if (ss.length == 2) {
                    headerTitle = ss[0];
                }
            }
            headerList.add(headerTitle);
            if (ef.words() != -1) {
                headerWidthList.add(ef.words() * 256);
            } else {
                headerWidthList.add(ef.width());
            }
        }
        // 创建工作表
        this.createSheet(sheetName, title, headerList, headerWidthList);
    }

    /**
     * 添加到 annotationList
     */
    private void addAnnotation(List<Object[]> annotationList, ExcelField ef, Object obj, Type type, String... groups) {
        if (ef != null) {
            boolean b = true;
            if (StringUtil.isNotBlank(ef.attrName())) {
                if (ListUtil.isNotEmpty(this.excludeList)) {
                    if (this.excludeList.contains(ef.attrName())) {
                        b = false;
                    }
                }
            }
            if (b) {
                if (ef.type() == Type.ALL || ef.type() == type) {
                    if (groups != null && groups.length > 0) {
                        boolean inGroup = false;
                        for (String g : groups) {
                            if (inGroup) {
                                break;
                            }
                            for (String efg : ef.groups()) {
                                if (StringUtil.equals(g, efg)) {
                                    inGroup = true;
                                    annotationList.add(new Object[]{ef, obj});
                                    break;
                                }
                            }
                        }
                    } else {
                        annotationList.add(new Object[]{ef, obj});
                    }
                }
            }
        }
    }

    /**
     * 创建工作表
     *
     * @param sheetName       指定Sheet名称
     * @param title           表格标题，传“空值”，表示无标题
     * @param headerList      表头字段设置
     * @param headerWidthList 表头字段宽度设置
     */
    public void createSheet(String sheetName, String title, List<String> headerList, List<Integer> headerWidthList) {
        this.sheet = wb.createSheet(StringUtil.defaultString(sheetName, StringUtil.defaultString(title, "Sheet1")));
        this.styles = createStyles(wb);
        this.rowNo = 0;
        // Create title
        if (StringUtil.isNotBlank(title)) {
            Row titleRow = sheet.createRow(rowNo++);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellStyle(styles.get("title"));
            titleCell.setCellValue(title);
            if (headerList.size() > 1) {
                sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(),
                        titleRow.getRowNum(), titleRow.getRowNum(), headerList.size() - 1));
            }
        }
        // Create header
        if (headerList == null) {
            throw new ExcelException("headerList not null!");
        }
        Row headerRow = sheet.createRow(rowNo++);
        headerRow.setHeightInPoints(16);
        for (int i = 0; i < headerList.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellStyle(styles.get("header"));
            String[] ss = StringUtil.split(headerList.get(i), "**", 2);
            if (ss.length == 2) {
                cell.setCellValue(ss[0]);
                Comment comment = this.sheet.createDrawingPatriarch().createCellComment(
                        new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
                comment.setRow(cell.getRowIndex());
                comment.setColumn(cell.getColumnIndex());
                comment.setString(new XSSFRichTextString(ss[1]));
                cell.setCellComment(comment);
            } else {
                cell.setCellValue(headerList.get(i));
            }
        }
        boolean isDefWidth = (headerWidthList != null && headerWidthList.size() == headerList.size());
        for (int i = 0; i < headerList.size(); i++) {
            int colWidth = -1;
            if (isDefWidth) {
                colWidth = headerWidthList.get(i);
            }
            if (colWidth == -1) {
                colWidth = sheet.getColumnWidth(i) * 2;
                colWidth = Math.max(colWidth, 3000);
            }
            if (colWidth == 0) {
                sheet.setColumnHidden(i, true);
            } else {
                sheet.setColumnWidth(i, colWidth);
            }
        }
        log.debug("Create sheet {} success.", sheetName);
    }

    /**
     * 创建表格样式
     *
     * @param wb 工作薄对象
     * @return 样式列表
     */
    private Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<>(0);

        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = wb.createFont();
        titleFont.setFontName("Arial");
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBold(true);
        style.setFont(titleFont);
        styles.put("title", style);

        style = wb.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10);
        style.setFont(dataFont);
        styles.put("data", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.LEFT);
        styles.put("data1", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.CENTER);
        styles.put("data2", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        styles.put("data3", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = wb.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        styles.put("header", style);

        return styles;
    }

    /**
     * 添加一行
     *
     * @return 行对象
     */
    public Row addRow() {
        return sheet.createRow(rowNo++);
    }

    /**
     * 添加一个单元格
     *
     * @param row    添加的行
     * @param column 添加列号
     * @param val    添加值
     * @return 单元格对象
     */
    public Cell addCell(Row row, int column, Object val) {
        return this.addCell(row, column, val, Align.AUTO, FieldType.class, null);
    }

    /**
     * 添加一个单元格
     *
     * @param row        添加的行
     * @param column     添加列号
     * @param val        添加值
     * @param align      对齐方式（1：靠左；2：居中；3：靠右）
     * @param dataFormat 数值格式（例如：0.00，yyyy-MM-dd）
     * @return 单元格对象
     */
    @SuppressWarnings("unchecked")
    public Cell addCell(Row row, int column, Object val, Align align, Class<? extends FieldType> fieldType, String dataFormat) {
        Cell cell = row.createCell(column);
        String defaultDataFormat = null;
        try {
            if (val == null) {
                cell.setCellValue(StringUtil.EMPTY);
            } else if (fieldType != FieldType.class) {
                FieldType ft = getFieldType(fieldType);
                cell.setCellValue(ft.setValue(val));
                defaultDataFormat = ft.getDataFormat();
            } else {
                if (val instanceof String) {
                    cell.setCellValue((String) val);
                } else if (val instanceof Integer) {
                    cell.setCellValue((Integer) val);
                    defaultDataFormat = "0";
                } else if (val instanceof Long) {
                    cell.setCellValue((Long) val);
                    defaultDataFormat = "0";
                } else if (val instanceof Double) {
                    cell.setCellValue((Double) val);
                    defaultDataFormat = "0.00";
                } else if (val instanceof Float) {
                    cell.setCellValue((Float) val);
                    defaultDataFormat = "0.00";
                } else if (val instanceof BigDecimal) {
                    cell.setCellValue(((BigDecimal) val).doubleValue());
                } else if (val instanceof Date) {
                    cell.setCellValue((Date) val);
                    defaultDataFormat = "yyyy-MM-dd HH:mm";
                } else {
                    // 如果没有指定 fieldType，切自行根据类型查找相应的转换类（com.smart.common.excel.fieldtype.值的类名+Type）
                    fieldType = (Class<? extends FieldType>) Class.forName(this.getClass().getName().replaceAll(this.getClass().getSimpleName(),
                            "fieldtype." + val.getClass().getSimpleName() + "Type"));
                    FieldType ft = getFieldType(fieldType);
                    cell.setCellValue(ft.setValue(val));
                    defaultDataFormat = ft.getDataFormat();
                }
            }
            CellStyle style = styles.get("data_column_" + column);
            if (style == null) {
                style = wb.createCellStyle();
                style.cloneStyleFrom(styles.get("data" + (align.value() >= 1 && align.value() <= 3 ? align.value() : "")));
                if (StringUtil.isNotBlank(dataFormat)) {
                    defaultDataFormat = dataFormat;
                }
                if (defaultDataFormat == null) {
                    defaultDataFormat = "@";
                }
                style.setDataFormat(wb.createDataFormat().getFormat(defaultDataFormat));
                styles.put("data_column_" + column, style);
            }
            cell.setCellStyle(style);
        } catch (Exception ex) {
            log.info("Set cell value [" + row.getRowNum() + "," + column + "] error: " + ex);
            cell.setCellValue(ObjectUtils.toString(val, () -> null));
        }
        return cell;
    }

    private FieldType getFieldType(Class<? extends FieldType> fieldType) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        FieldType ft = fieldTypes.get(fieldType);
        if (ft == null) {
            ft = fieldType.getDeclaredConstructor().newInstance();
            fieldTypes.put(fieldType, ft);
        }
        return ft;
    }

    /**
     * 添加数据（通过annotation.ExportField添加数据）
     *
     * @return list 数据列表
     */
    public <E> ExcelExport setDataList(List<E> list) {
        for (E e : list) {
            int column = 0;
            Row row = this.addRow();
            StringBuilder sb = new StringBuilder();
            for (Object[] os : annotationList) {
                ExcelField ef = (ExcelField) os[0];
                Object val = null;
                // Get entity value
                try {
                    if (StringUtil.isNotBlank(ef.attrName())) {
                        val = ReflectUtil.invokeGetter(e, ef.attrName());
                    } else {
                        if (os[1] instanceof Field) {
                            val = ReflectUtil.invokeGetter(e, ((Field) os[1]).getName());
                        } else if (os[1] instanceof Method) {
                            val = ReflectUtil.invokeMethod(e, ((Method) os[1]).getName(), new Class[]{}, new Object[]{});
                        }
                    }
                    // If is dict, get dict label
                    if (StringUtil.isNotBlank(ef.dictCode())) {
                        val = getDictService().getDictName(ef.dictCode(), val == null ? "" : val.toString(), "");
                    }
                } catch (Exception ex) {
                    log.info(ex.toString());
                    val = "";
                }
                this.addCell(row, column++, val, ef.align(), ef.fieldType(), ef.dataFormat());
                sb.append(val).append(", ");
            }
            log.debug("Write success: [" + row.getRowNum() + "] " + sb);
        }
        return this;
    }

    /**
     * 输出数据流
     *
     * @param os 输出数据流
     */
    public void write(OutputStream os) {
        try {
            wb.write(os);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 输出到客户端
     *
     * @param fileName 输出文件名
     */
    public ExcelExport write(HttpServletResponse response, String fileName) {
        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.addHeader("Content-Disposition", "attachment; filename=" + EncodeUtil.encodeUrl(fileName));
        try {
            write(response.getOutputStream());
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
        return this;
    }

    /**
     * 输出到文件
     *
     * @param name 输出文件名
     */
    public ExcelExport writeFile(String name) throws IOException {
        FileOutputStream os = new FileOutputStream(name);
        this.write(os);
        return this;
    }

    @Override
    public void close() {
        fieldTypes.clear();
        if (wb instanceof SXSSFWorkbook) {
            ((SXSSFWorkbook) wb).dispose();
        }
        try {
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
