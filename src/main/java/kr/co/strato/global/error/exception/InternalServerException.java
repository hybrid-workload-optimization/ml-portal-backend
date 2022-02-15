package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.BasicErrorType;

public class InternalServerException extends RuntimeException{
    BasicErrorType errorType = BasicErrorType.SERVER_ERROR;

    public InternalServerException(){
        super(BasicErrorType.SERVER_ERROR.getMessage());
    }

    public InternalServerException(String detail){
        super(BasicErrorType.SERVER_ERROR.getMessage());
        errorType.setDetail(detail);
    }

    public InternalServerException(Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(cause.getMessage());
    }

    public InternalServerException(String detail, Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(detail);
    }

    public BasicErrorType getErrorType(){
        return errorType;
    }
}