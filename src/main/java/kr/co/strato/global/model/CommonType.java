package kr.co.strato.global.model;

public enum CommonType implements ResponseType{
    OK("10001", "정상처리 되었습니다.", null);

    private String code;
    private String message;
    private String detail;

    CommonType(String code, String message, String detail){
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
