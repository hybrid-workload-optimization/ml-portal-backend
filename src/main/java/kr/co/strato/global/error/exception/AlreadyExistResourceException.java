package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.BasicErrorType;

/**
 * 이미 존재하는 리소스가 있을 때 발생시키는 Exception
 */
public class AlreadyExistResourceException extends RuntimeException{
    BasicErrorType errorType = BasicErrorType.ALREADY_EXIST_RESOURCE;

    public AlreadyExistResourceException(){
        super(BasicErrorType.ALREADY_EXIST_RESOURCE.getMessage());
    }

    public AlreadyExistResourceException(String detail){
        super(BasicErrorType.ALREADY_EXIST_RESOURCE.getMessage());
        errorType.setDetail(detail);
    }

    public AlreadyExistResourceException(Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(cause.getMessage());
    }

    public AlreadyExistResourceException(String detail, Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(detail);
    }

    public BasicErrorType getErrorType(){
        return errorType;
    }
}
