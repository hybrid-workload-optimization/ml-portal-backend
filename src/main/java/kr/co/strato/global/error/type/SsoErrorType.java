package kr.co.strato.global.error.type;

import kr.co.strato.global.model.ResponseType;

public enum SsoErrorType implements ResponseType {
    SSO_CONNECTION_ERROR("20001", "SSO 서버 연결에 문제가 발생했습니다", null);


    private String code;
    private String message;
    private String detail;

    SsoErrorType(String code, String message, String detail){
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