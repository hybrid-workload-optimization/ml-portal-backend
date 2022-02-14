package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.BasicErrorType;

public class BadRequestException extends RuntimeException{
    BasicErrorType errorType = BasicErrorType.BAD_REQUEST;

    public BadRequestException(){
        super(BasicErrorType.BAD_REQUEST.getMessage());
    }

    public BadRequestException(String detail){
        super(BasicErrorType.BAD_REQUEST.getMessage());
        errorType.setDetail(detail);
    }

    public BadRequestException(Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(cause.getMessage());
    }

    public BadRequestException(String detail, Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(detail);
    }

    public BasicErrorType getErrorType(){
        return errorType;
    }
}