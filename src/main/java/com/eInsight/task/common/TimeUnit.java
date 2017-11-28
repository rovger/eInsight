package com.eInsight.task.common;

public enum TimeUnit {
    MINUTELY("MINUTE"),
    HOURLY("HOUR"),
    DAILY("DAY"),
    WEEKLY("WEEK"),
    MONTHLY("MONTH");

    private String type;

    TimeUnit(String type) {
        this.type = type;
    }

    public String getName() {
        return this.type;
    }
}
