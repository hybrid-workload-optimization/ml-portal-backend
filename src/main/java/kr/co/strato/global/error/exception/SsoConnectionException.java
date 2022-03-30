package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.BasicErrorType;
import kr.co.strato.global.error.type.SsoErrorType;

public class SsoConnectionException extends RuntimeException {

	SsoErrorType errorType = SsoErrorType.SSO_CONNECTION_ERROR;
	
	public SsoConnectionException(){
        super(BasicErrorType.SERVER_ERROR.getMessage());
    }

    public SsoConnectionException(String detail){
        super(BasicErrorType.SERVER_ERROR.getMessage());
        errorType.setDetail(detail);
    }

    public SsoConnectionException(Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(cause.getMessage());
    }

    public SsoConnectionException(String detail, Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(detail);
    }

    public SsoErrorType getErrorType(){
        return errorType;
    }
	
}
