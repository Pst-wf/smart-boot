package com.smart.tools.excel;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Excel超大数据读取，抽象Excel2007读取器，excel2007的底层数据结构是xml文件，采用SAX的事件驱动的方法解析
 * xml，需要继承DefaultHandler，在遇到文件内容时，事件会触发，这种做法可以大大降低 内存的耗费，特别使用于大数据量的文件。
 *
 * @author wf
 * @since 2014-9-2
 */
public abstract class ExcelReader extends DefaultHandler {

    /**
     * 共享字符串表
     */
    private SharedStringsTable sst;

    /**
     * 上一次的内容
     */
    private String lastContents;
    private boolean nextIsString;

    private int sheetIndex = -1;
    private final List<String> rowList = new ArrayList<>();

    /**
     * 当前行
     */
    private int curRow = 0;
    /**
     * 当前列
     */
    private int curCol = 0;
    /**
     * 日期标志
     */
    private boolean dateFlag;
    /**
     * 数字标志
     */
    private boolean numberFlag;
    /**
     * 是否是T元素
     */
    private boolean isT;

    /**
     * 遍历工作簿中所有的电子表格
     *
     * @param filename 文件名
     * @throws Exception 异常
     */
    public void process(String filename) throws Exception {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader(pkg);
        SharedStringsTable sst = r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);
        Iterator<InputStream> sheets = r.getSheetsData();
        while (sheets.hasNext()) {
            curRow = 0;
            sheetIndex++;
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
        }
    }

    /**
     * 只遍历一个电子表格，其中sheetId为要遍历的sheet索引，从1开始，1-3
     *
     * @param filename 文件名
     * @param sheetId  sheet编号
     * @throws Exception 异常
     */
    public void process(String filename, int sheetId) throws Exception {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader(pkg);
        SharedStringsTable sst = r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);
        // 根据 rId# 或 rSheet# 查找sheet
        InputStream sheet2 = r.getSheet("rId" + sheetId);
        sheetIndex++;
        InputSource sheetSource = new InputSource(sheet2);
        parser.parse(sheetSource);
        sheet2.close();
    }

    public XMLReader fetchSheetParser(SharedStringsTable sst)
            throws SAXException {
        XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        this.sst = sst;
        parser.setContentHandler(this);
        return parser;
    }

    @Override
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) {
        //单元格
        String c = "c";
        if (c.equals(name)) {
            // 如果下一个元素是 SST 的索引，则将nextIsString标记为true
            String cellType = attributes.getValue("t");
            nextIsString = "s".equals(cellType);
            // 日期格式
            String cellDateType = attributes.getValue("s");
            dateFlag = "1".equals(cellDateType);
            String cellNumberType = attributes.getValue("s");
            numberFlag = "2".equals(cellNumberType);

        }
        // 当元素为t时
        isT = "t".equals(name);
        // 置空
        lastContents = "";
    }

    @Override
    public void endElement(String uri, String localName, String name) {
        // 根据SST的索引值的到单元格的真正要存储的字符串
        // 这时characters()方法可能会被调用多次
        if (nextIsString) {
            try {
                int idx = Integer.parseInt(lastContents);
                lastContents = new XSSFRichTextString(sst.getEntryAt(idx))
                        .toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //单元格的值
        String v = "v";
        if (isT) {
            String value = lastContents.trim();
            rowList.add(curCol, value);
            curCol++;
            isT = false;
            // 如果单元格是字符串则v标签的值为该字符串在SST中的索引
            // 将单元格内容加入rowList中，在这之前先去掉字符串前后的空白符
        } else if (v.equals(name)) {
            String value = lastContents.trim();
            value = "".equals(value) ? " " : value;
            try {
                // 日期格式处理
                if (dateFlag) {
                    Date date = DateUtil.getJavaDate(Double.parseDouble(value));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    value = dateFormat.format(date);
                }
                // 数字类型处理
                if (numberFlag) {
                    BigDecimal bd = new BigDecimal(value);
                    value = bd.setScale(3, RoundingMode.UP).toString();
                }
            } catch (Exception e) {
                // 转换失败仍用读出来的值
                e.printStackTrace();
            }
            rowList.add(curCol, value);
            curCol++;
        } else {
            String row = "row";
            // 如果标签名称为 row ，这说明已经是行尾，调用 optRows() 方法
            if (row.equals(name)) {
                getRows(sheetIndex + 1, curRow, rowList);
                rowList.clear();
                curRow++;
                curCol = 0;
            }
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) {
        // 得到单元格内容的值
        lastContents += new String(ch, start, length);
    }

    /**
     * 获取行数据回调
     *
     * @param sheetIndex sheet索引
     * @param curRow     当前行
     * @param rowList    数据
     */
    public abstract void getRows(int sheetIndex, int curRow, List<String> rowList);
}
