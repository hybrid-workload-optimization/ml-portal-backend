package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.ProjectErrorType;

public class AleadyUserClusterException extends RuntimeException {

	ProjectErrorType errorType = ProjectErrorType.ALEADY_USE_CLUSTER;
	
	public AleadyUserClusterException() {
        super(ProjectErrorType.ALEADY_USE_CLUSTER.getMessage());
    }
}