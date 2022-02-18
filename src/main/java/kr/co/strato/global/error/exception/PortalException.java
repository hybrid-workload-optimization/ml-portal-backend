package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.BasicErrorType;

public class PortalException extends RuntimeException {

	BasicErrorType errorType = BasicErrorType.SERVER_ERROR;
	
	public PortalException(){
        super(BasicErrorType.SERVER_ERROR.getMessage());
    }

    public PortalException(String detail){
        super(BasicErrorType.SERVER_ERROR.getMessage());
        errorType.setDetail(detail);
    }

    public PortalException(Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(cause.getMessage());
    }

    public PortalException(String detail, Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(detail);
    }

    public BasicErrorType getErrorType(){
        return errorType;
    }
	
}
