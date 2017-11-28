package com.eInsight.common.exception;

public class BizRuntimeException extends RuntimeException {
    private BizExceptionType type;

    public BizRuntimeException() {
        super(BizExceptionType.UNSPECIFIED.getDescription());
        this.type = BizExceptionType.UNSPECIFIED;
    }

    public BizRuntimeException(BizExceptionType type) {
        super(type.getDescription());
        this.type = type;
    }

    public BizRuntimeException(BizExceptionType type, Throwable cause) {
        super(type.getDescription(), cause);
        this.type = type;
    }

    public BizExceptionType getType() {
        return this.type;
    }
}
