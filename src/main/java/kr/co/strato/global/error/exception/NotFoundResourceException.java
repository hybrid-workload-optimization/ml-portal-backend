package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.BasicErrorType;

/**
 * 리소스가 존재하지 않을 때 발생 시키는 Exception
 */
public class NotFoundResourceException extends RuntimeException{
    BasicErrorType errorType = BasicErrorType.NOT_FOUND_RESOURCE;

    public NotFoundResourceException(){
        super(BasicErrorType.NOT_FOUND_RESOURCE.getMessage());
    }

    public NotFoundResourceException(String detail){
        super(BasicErrorType.NOT_FOUND_RESOURCE.getMessage());
        errorType.setDetail(detail);
    }

    public NotFoundResourceException(Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(cause.getMessage());
    }

    public NotFoundResourceException(String detail, Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(detail);
    }

    public BasicErrorType getErrorType(){
        return errorType;
    }
}