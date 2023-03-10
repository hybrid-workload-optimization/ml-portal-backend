package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.SyncErrorType;

public class SyncUserFailException extends RuntimeException {

	SyncErrorType errorType = SyncErrorType.SYNC_USER_ERROR;

	public SyncUserFailException() {
        super(SyncErrorType.SYNC_USER_ERROR.getMessage());
    }
	
}
