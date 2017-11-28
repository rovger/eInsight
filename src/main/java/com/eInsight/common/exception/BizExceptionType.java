package com.eInsight.common.exception;

public enum BizExceptionType {
    UNSPECIFIED(0, "Unspecified"),
    TEMPLATE_FILE_NOT_FOUND(100001, "func Configuration::getTemplate failed, due to Freemarker template file not found"),
    TEMPLATE_PROCESSING_ERROR(100002, "func Template::process failed, an exception occurs during template processing"),
    TEMPLATE_PROCESSING_IO_ERROR(100003, "func Template::process failed, an I/O exception occurs during writing to the writer");

    private int value;
    private String description;

    BizExceptionType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.description;
    }
}
