package com.smart.job.core.biz.model;

import java.io.Serializable;

/**
 * @author xuxueli 2020-04-11 22:27
 */
public class LogParam implements Serializable {
    private static final long serialVersionUID = 42L;

    public LogParam() {
    }

    public LogParam(long logDateTim, String logId, int fromLineNum) {
        this.logDateTim = logDateTim;
        this.logId = logId;
        this.fromLineNum = fromLineNum;
    }

    private long logDateTim;
    private String logId;
    private int fromLineNum;

    public long getLogDateTim() {
        return logDateTim;
    }

    public void setLogDateTim(long logDateTim) {
        this.logDateTim = logDateTim;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public int getFromLineNum() {
        return fromLineNum;
    }

    public void setFromLineNum(int fromLineNum) {
        this.fromLineNum = fromLineNum;
    }

}