package kr.co.strato.global.error.type;

import kr.co.strato.global.model.ResponseType;

public enum ResourceErrorType implements ResponseType {
	
	DUPLICATE_RESOURCE_NAME("30001", "중복된 리소스 이름입니다.", null),
	DUPLICATE_INGRESS_PATH("30002", "중복된 Ingress Path입니다.", null);

    private String code;
    private String message;
    private String detail;

    ResourceErrorType(String code, String message, String detail) {
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
