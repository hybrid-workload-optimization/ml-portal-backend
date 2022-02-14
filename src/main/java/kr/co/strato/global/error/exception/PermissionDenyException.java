package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.AuthErrorType;

public class PermissionDenyException extends RuntimeException{
    AuthErrorType errorType = AuthErrorType.PERMISSION_DENY;

    public PermissionDenyException(){
        super(AuthErrorType.PERMISSION_DENY.getMessage());
    }

    public PermissionDenyException(String detail){
        super(AuthErrorType.PERMISSION_DENY.getMessage());
        errorType.setDetail(detail);
    }

    public PermissionDenyException(Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(cause.getMessage());
    }

    public PermissionDenyException(String detail, Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(detail);
    }

    public AuthErrorType getErrorType(){
        return errorType;
    }
}
