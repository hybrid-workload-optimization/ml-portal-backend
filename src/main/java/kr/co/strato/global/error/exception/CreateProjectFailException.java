package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.ProjectErrorType;

public class CreateProjectFailException extends RuntimeException {

	ProjectErrorType errorType = ProjectErrorType.FAIL_PROJECT_CREATE;
	
	public CreateProjectFailException() {
        super(ProjectErrorType.FAIL_PROJECT_CREATE.getMessage());
    }
}
