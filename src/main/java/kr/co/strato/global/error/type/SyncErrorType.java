package kr.co.strato.global.error.type;

import kr.co.strato.global.model.ResponseType;

public enum SyncErrorType implements ResponseType {
	SYNC_ROLE_ERROR("40001", "통합포탈 권한 동기화에 실패하였습니다.", null),
	SYNC_USER_ERROR("40002", "통합포탈 사용자 동기화에 실패하였습니다.", null),
	SYNC_GROUP_ERROR("40003", "통합포탈 그룹 동기화에 실패하였습니다.", null);

    private String code;
    private String message;
    private String detail;

    SyncErrorType(String code, String message, String detail){
        this.code = code;
        this.message = message;
        this.detail = detail;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getDetail() {
        return detail;
    }

    @Override
    public void setDetail(String detail) {
        this.detail = detail;
    }

}