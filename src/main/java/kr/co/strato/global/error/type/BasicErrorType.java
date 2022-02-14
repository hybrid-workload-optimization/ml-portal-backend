package kr.co.strato.global.error.type;

import kr.co.strato.global.model.ResponseType;

public enum BasicErrorType implements ResponseType {
    SERVER_ERROR("10002", "서버에 문제가 발생했습니다.", null),
    BAD_REQUEST_ARGS("10003", "요청 인자가 잘못 되었습니다.", null),
    NOT_FOUND_RESOURCE("10004", "해당 리소스가 존재하지 않습니다.", null),
    ALREADY_EXIST_RESOURCE("10005", "이미 존재하는 리소스입니다.", null),
    BAD_REQUEST("10006", "잘못된 요청입니다.", null);


    private String code;
    private String message;
    private String detail;

    BasicErrorType(String code, String message, String detail){
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