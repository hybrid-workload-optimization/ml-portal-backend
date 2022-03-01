package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.ProjectErrorType;

public class DeleteProjectFailException extends RuntimeException {

	ProjectErrorType errorType = ProjectErrorType.FAIL_PROJECT_DELETE;
	
	public DeleteProjectFailException() {
        super(ProjectErrorType.FAIL_PROJECT_DELETE.getMessage());
    }
}
