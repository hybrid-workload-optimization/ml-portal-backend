package kr.co.strato.global.error.exception;

import kr.co.strato.global.error.type.SyncErrorType;

public class SyncGroupFailException extends RuntimeException {

	SyncErrorType errorType = SyncErrorType.SYNC_GROUP_ERROR;

	public SyncGroupFailException() {
        super(SyncErrorType.SYNC_GROUP_ERROR.getMessage());
    }
	
}
