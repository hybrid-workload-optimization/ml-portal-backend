package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.ResourceErrorType;

public class DuplicateIngressPathException extends RuntimeException {

	private static final long serialVersionUID = 1207005L;
	
	
	ResourceErrorType errorType = ResourceErrorType.DUPLICATE_INGRESS_PATH;
	
	public DuplicateIngressPathException(){
        super(ResourceErrorType.DUPLICATE_INGRESS_PATH.getMessage());
    }

    public DuplicateIngressPathException(String detail){
        super(ResourceErrorType.DUPLICATE_INGRESS_PATH.getMessage());
        errorType.setDetail(detail);
    }

    public DuplicateIngressPathException(Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(cause.getMessage());
    }

    public DuplicateIngressPathException(String detail, Throwable cause){
        super(cause.getMessage(), cause);
        errorType.setDetail(detail);
    }

    public ResourceErrorType getErrorType(){
        return errorType;
    }
}
