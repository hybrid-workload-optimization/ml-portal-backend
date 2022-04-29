package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.ResourceErrorType;

public class DuplicateResourceNameException extends RuntimeException {

	private static final long serialVersionUID = 1202003L;
	
	
	ResourceErrorType errorType = ResourceErrorType.DUPLICATE_RESOURCE_NAME;
	
	public DuplicateResourceNameException(){
        super(ResourceErrorType.DUPLICATE_RESOURCE_NAME.getMessage());
    }

    public DuplicateResourceNameException(String detail){
        super(ResourceErrorType.DUPLICATE_RESOURCE_NAME.getMessage());
        errorType.setDetail(detail);
    }

    public DuplicateResourceNameException(Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(cause.getMessage());
    }

    public DuplicateResourceNameException(String detail, Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(detail);
    }

    public ResourceErrorType getErrorType(){
        return errorType;
    }
}
