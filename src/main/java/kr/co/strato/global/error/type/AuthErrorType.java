package kr.co.strato.global.error.type;

import kr.co.strato.global.model.ResponseType;

public enum AuthErrorType implements ResponseType {
    FAIL_AUTH("11001", "인증에 실패하였습니다.", null),
    NOT_FOUND_USER("11002", "사용자 정보가 존재하지 않습니다.", null),
    PERMISSION_DENY("11003", "권한이 없습니다.", null);

    private String code;
    private String message;
    private String detail;

    AuthErrorType(String code, String message, String detail){
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
