package com.smart.job.core.biz.model;

import java.io.Serializable;

/**
 * @author xuxueli 2020-04-11 22:27
 */
public class IdleBeatParam implements Serializable {
    private static final long serialVersionUID = 42L;

    public IdleBeatParam() {
    }

    public IdleBeatParam(String jobId) {
        this.jobId = jobId;
    }

    private String jobId;


    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

}