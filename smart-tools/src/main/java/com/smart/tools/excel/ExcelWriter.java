package com.smart.tools.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Excel超大数据写入，抽象excel2007读入器，先构建.xlsx一张模板，改写模板中的sheet.xml,
 * 使用这种方法 写入.xlsx文件，不需要太大的内存
 *
 * @author wf
 * @since 2014-9-2
 */
@Slf4j
public abstract class ExcelWriter {

    private SpreadsheetWriter sw;

    /**
     * 写入电子表格的主要流程
     *
     * @param fileName 文件名
     * @throws Exception 异常
     */
    public void process(String fileName) throws Exception {

        // 建立工作簿和电子表格对象
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("sheet1");

        // 持有电子表格数据的xml文件名 例如 /xl/worksheets/sheet1.xml
        String sheetRef = sheet.getPackagePart().getPartName().getName();

        // 保存模板
        FileOutputStream os = new FileOutputStream("template.xlsx");
        wb.write(os);
        os.close();

        // 生成xml文件
        File tmp = File.createTempFile("sheet", ".xml");
        Writer fw = new FileWriter(tmp);
        sw = new SpreadsheetWriter(fw);
        generate();
        fw.close();

        // 使用产生的数据替换模板
        File templateFile = new File("template.xlsx");
        FileOutputStream out = new FileOutputStream(fileName);
        substitute(templateFile, tmp, sheetRef.substring(1), out);
        out.close();
        // 删除文件之前调用一下垃圾回收器，否则无法删除模板文件
        System.gc();
        // 删除临时模板文件
        if (templateFile.isFile() && templateFile.exists()) {
            boolean delete = templateFile.delete();
            if (!delete) {
                log.error("删除临时模板文件失败");
            }
        }
    }

    /**
     * 类使用者应该使用此方法进行写操作
     *
     * @throws Exception 异常
     */
    public abstract void generate() throws Exception;

    public void beginSheet() throws IOException {
        sw.beginSheet();
    }

    public void insertRow(int rowNumber) throws IOException {
        sw.insertRow(rowNumber);
    }

    public void createCell(int columnIndex, String value) throws IOException {
        sw.createCell(columnIndex, value, -1);
    }

    public void createCell(int columnIndex, double value) throws IOException {
        sw.createCell(columnIndex, value, -1);
    }

    public void endRow() throws IOException {
        sw.endRow();
    }

    public void endSheet() throws IOException {
        sw.endSheet();
    }

    /**
     * @param zipFile the template file
     * @param tmpFile the XML file with the sheet data
     * @param entry   the name of the sheet entry to substitute, e.g. xl/worksheets/sheet1.xml
     * @param out     the stream to write the result to
     */
    private static void substitute(File zipFile, File tmpFile, String entry,
                                   OutputStream out) throws IOException {
        ZipFile zip = null;
        ZipOutputStream zos = null;
        InputStream is = null;
        try {
            zip = new ZipFile(zipFile);
            zos = new ZipOutputStream(out);
            @SuppressWarnings("unchecked")
            Enumeration<ZipEntry> en = (Enumeration<ZipEntry>) zip.entries();
            while (en.hasMoreElements()) {
                ZipEntry ze = en.nextElement();
                if (!ze.getName().equals(entry)) {
                    zos.putNextEntry(new ZipEntry(ze.getName()));
                    try (InputStream is2 = zip.getInputStream(ze)) {
                        copyStream(is2, zos);
                    }
                }
            }
            zos.putNextEntry(new ZipEntry(entry));
            is = Files.newInputStream(tmpFile.toPath());
            copyStream(is, zos);
        } finally {
            if (is != null) {
                is.close();
            }
            if (zos != null) {
                zos.close();
            }
            if (zip != null) {
                zip.close();
            }
        }
    }

    private static void copyStream(InputStream in, OutputStream out)
            throws IOException {
        byte[] chunk = new byte[1024];
        int count;
        while ((count = in.read(chunk)) >= 0) {
            out.write(chunk, 0, count);
        }
    }

    /**
     * 在写入器中写入电子表格
     */
    public static class SpreadsheetWriter {
        private final Writer _out;
        private int rowNumber_;
        private static final String LINE_SEPARATOR = System
                .getProperty("line.separator");

        public SpreadsheetWriter(Writer out) {
            _out = out;
        }

        public void beginSheet() throws IOException {
            _out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">");
            _out.write("<sheetData>" + LINE_SEPARATOR);
        }

        public void endSheet() throws IOException {
            _out.write("</sheetData>");
            _out.write("</worksheet>");
        }

        /**
         * 插入新行
         *
         * @param rowNumber 以0开始
         */
        public void insertRow(int rowNumber) throws IOException {
            _out.write("<row r=\"" + (rowNumber + 1) + "\">" + LINE_SEPARATOR);
            this.rowNumber_ = rowNumber;
        }

        /**
         * 插入行结束标志
         */
        public void endRow() throws IOException {
            _out.write("</row>" + LINE_SEPARATOR);
        }

        /**
         * 插入新列
         *
         * @param columnIndex 列索引
         * @param value       值
         * @param styleIndex  样式
         * @throws IOException 异常
         */
        public void createCell(int columnIndex, String value, int styleIndex)
                throws IOException {
            String ref = new CellReference(rowNumber_, columnIndex)
                    .formatAsString();
            _out.write("<c r=\"" + ref + "\" t=\"inlineStr\"");
            if (styleIndex != -1) {
                _out.write(" s=\"" + styleIndex + "\"");
            }
            _out.write(">");
            _out.write("<is><t>" + encoderXML(value) + "</t></is>");
            _out.write("</c>");
        }

        public void createCell(int columnIndex, String value)
                throws IOException {
            createCell(columnIndex, value, -1);
        }

        public void createCell(int columnIndex, double value, int styleIndex)
                throws IOException {
            String ref = new CellReference(rowNumber_, columnIndex)
                    .formatAsString();
            _out.write("<c r=\"" + ref + "\" t=\"n\"");
            if (styleIndex != -1) {
                _out.write(" s=\"" + styleIndex + "\"");
            }
            _out.write(">");
            _out.write("<v>" + value + "</v>");
            _out.write("</c>");
        }

        public void createCell(int columnIndex, double value)
                throws IOException {
            createCell(columnIndex, value, -1);
        }

        public void createCell(int columnIndex, Calendar value, int styleIndex)
                throws IOException {
            createCell(columnIndex, DateUtil.getExcelDate(value, false),
                    styleIndex);
        }
    }

    /**
     * XML Encode
     */
    private static final String[] XML_CODE = new String[256];

    static {
        // Special characters
        XML_CODE['\''] = "'";
        // double quote
        XML_CODE['\"'] = "\"";
        // ampersand
        XML_CODE['&'] = "&";
        // lower than
        XML_CODE['<'] = "<";
        // greater than
        XML_CODE['>'] = ">";
    }

    /**
     * <p>
     * Encode the given text into xml.
     * </p>
     *
     * @param string the text to encode
     * @return the encoded string
     */
    public static String encoderXML(String string) {
        if (string == null) {
            return "";
        }
        int n = string.length();
        char character;
        String xmlChar;
        StringBuilder builder = new StringBuilder();
        // loop over all the characters of the String.
        for (int i = 0; i < n; i++) {
            character = string.charAt(i);
            // the xml-code of these characters are added to a StringBuffer
            // one by one
            try {
                xmlChar = XML_CODE[character];
                if (xmlChar == null) {
                    builder.append(character);
                } else {
                    builder.append(XML_CODE[character]);
                }
            } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                builder.append(character);
            }
        }
        return builder.toString();
    }
}
