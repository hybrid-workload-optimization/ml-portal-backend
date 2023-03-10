package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.SyncErrorType;

public class SyncRoleFailException extends RuntimeException {

	SyncErrorType errorType = SyncErrorType.SYNC_ROLE_ERROR;

	public SyncRoleFailException() {
        super(SyncErrorType.SYNC_ROLE_ERROR.getMessage());
    }
	
}