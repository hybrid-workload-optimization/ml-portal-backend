package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.AuthErrorType;

public class AuthFailException extends RuntimeException{
    AuthErrorType errorType = AuthErrorType.FAIL_AUTH;

    public AuthFailException(){
        super(AuthErrorType.FAIL_AUTH.getMessage());
    }

    public AuthFailException(String detail){
        super(AuthErrorType.FAIL_AUTH.getMessage());
        errorType.setDetail(detail);
    }

    public AuthFailException(Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(cause.getMessage());
    }

    public AuthFailException(String detail, Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(detail);
    }

    public AuthErrorType getErrorType(){
        return errorType;
    }
}
