package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.ProjectErrorType;

public class UpdateProjectFailException extends RuntimeException {

	ProjectErrorType errorType = ProjectErrorType.FAIL_PROJECT_UPDATE;
	
	public UpdateProjectFailException() {
        super(ProjectErrorType.FAIL_PROJECT_UPDATE.getMessage());
    }
}
