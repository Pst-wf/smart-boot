package com.smart.job.core.enums;

import lombok.Getter;

/**
 * @author xxl
 * @since 2017/5/9.
 */
@Getter
public enum ExecutorBlockStrategyEnum {
    /**
     * Serial execution
     */
    SERIAL_EXECUTION("Serial execution"),

    /**
     * Discard Later
     */
    DISCARD_LATER("Discard Later"),

    /**
     * Discard Later
     */
    COVER_EARLY("Cover Early");

    private String title;
    ExecutorBlockStrategyEnum(String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static ExecutorBlockStrategyEnum match(String name, ExecutorBlockStrategyEnum defaultItem) {
        if (name != null) {
            for (ExecutorBlockStrategyEnum item:ExecutorBlockStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }
}
