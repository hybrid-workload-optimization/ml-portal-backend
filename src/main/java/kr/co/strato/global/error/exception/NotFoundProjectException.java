package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.ProjectErrorType;

public class NotFoundProjectException extends RuntimeException {

	ProjectErrorType errorType = ProjectErrorType.NOT_FOUND_PROJECT;
	
	public NotFoundProjectException() {
        super(ProjectErrorType.NOT_FOUND_PROJECT.getMessage());
    }
}
