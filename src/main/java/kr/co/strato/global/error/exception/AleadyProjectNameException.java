package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.ProjectErrorType;

public class AleadyProjectNameException extends RuntimeException {

	ProjectErrorType errorType = ProjectErrorType.ALEADY_PROJECT_NAME;
	
	public AleadyProjectNameException() {
        super(ProjectErrorType.ALEADY_PROJECT_NAME.getMessage());
    }
}
