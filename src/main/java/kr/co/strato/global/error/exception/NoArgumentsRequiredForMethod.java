package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.BasicErrorType;

public class NoArgumentsRequiredForMethod extends RuntimeException{
    BasicErrorType errorType = BasicErrorType.SERVER_ERROR;

    public NoArgumentsRequiredForMethod(){
        super(BasicErrorType.SERVER_ERROR.getMessage());
    }

    public NoArgumentsRequiredForMethod(String detail){
        super(BasicErrorType.SERVER_ERROR.getMessage());
        errorType.setDetail(detail);
    }

    public NoArgumentsRequiredForMethod(Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(cause.getMessage());
    }

    public NoArgumentsRequiredForMethod(String detail, Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(detail);
    }

    public BasicErrorType getErrorType(){
        return errorType;
    }
}