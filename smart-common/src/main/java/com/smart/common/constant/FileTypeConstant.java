package com.smart.common.constant;

/**
 * 文件类型常量
 *
 * @author wf
 * @version 1.0.0
 * @since 2023/1/5
 */
public class FileTypeConstant {
    public final static String XLS = "xls";
    public final static String XLSX = "xlsx";
    /**
     * 正在编辑文档
     */
    public final static int CALLBACK_STATUS_1 = 1;
    /**
     * 文档已准备好保存
     */
    public final static int CALLBACK_STATUS_2 = 2;
    /**
     * 发生文档保存错误
     */
    public final static int CALLBACK_STATUS_3 = 3;
    /**
     * 文档已关闭，没有任何更改
     */
    public final static int CALLBACK_STATUS_4 = 4;

    public final static int CALLBACK_STATUS_5 = 5;
    /**
     * 正在编辑文档，但保存了当前文档状态
     */
    public final static int CALLBACK_STATUS_6 = 6;
    /**
     * 强制保存文档时发生错误
     */
    public final static int CALLBACK_STATUS_7 = 7;

}
