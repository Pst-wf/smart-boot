package com.smart.job.core.biz.model;

import java.io.Serializable;

/**
 * Created by xuxueli on 2017-05-10 20:22:42
 */
public class RegistryParam implements Serializable {
    private static final long serialVersionUID = 42L;

    private String registryGroup;
    private String registryKey;
    private String registryValue;

    private String registryTitle;

    public RegistryParam() {
    }

    public RegistryParam(String registryGroup, String registryKey, String registryValue, String registryTitle) {
        this.registryGroup = registryGroup;
        this.registryKey = registryKey;
        this.registryValue = registryValue;
        this.registryTitle = registryTitle;
    }

    public String getRegistryGroup() {
        return registryGroup;
    }

    public void setRegistryGroup(String registryGroup) {
        this.registryGroup = registryGroup;
    }

    public String getRegistryKey() {
        return registryKey;
    }

    public void setRegistryKey(String registryKey) {
        this.registryKey = registryKey;
    }

    public String getRegistryValue() {
        return registryValue;
    }

    public void setRegistryValue(String registryValue) {
        this.registryValue = registryValue;
    }

    public String getRegistryTitle() {
        return registryTitle;
    }

    public void setRegistryTitle(String registryTitle) {
        this.registryTitle = registryTitle;
    }

    @Override
    public String toString() {
        return "RegistryParam{" +
                "registryGroup='" + registryGroup + '\'' +
                ", registryKey='" + registryKey + '\'' +
                ", registryValue='" + registryValue + '\'' +
                ", registryTitle='" + registryTitle + '\'' +
                '}';
    }
}
