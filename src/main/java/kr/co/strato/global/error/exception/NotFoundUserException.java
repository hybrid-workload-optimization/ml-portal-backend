package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.AuthErrorType;

public class NotFoundUserException extends RuntimeException{
    AuthErrorType errorType = AuthErrorType.NOT_FOUND_USER;

    public NotFoundUserException(){
        super(AuthErrorType.NOT_FOUND_USER.getMessage());
    }

    public NotFoundUserException(String detail){
        super(AuthErrorType.NOT_FOUND_USER.getMessage());
        errorType.setDetail(detail);
    }

    public NotFoundUserException(Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(cause.getMessage());
    }

    public NotFoundUserException(String detail, Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(detail);
    }

    public AuthErrorType getErrorType(){
        return errorType;
    }
}
