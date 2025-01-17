package com.smart.tools.excel;

/**
 * Excel Exception
 *
 * @author wf
 * @version 2022-01-01 17:28:23
 */
public class ExcelException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExcelException() {
        super();
    }

    public ExcelException(String message) {
        super(message);
    }

    public ExcelException(Throwable cause) {
        super(cause);
    }

    public ExcelException(String message, Throwable cause) {
        super(message, cause);
    }
}
