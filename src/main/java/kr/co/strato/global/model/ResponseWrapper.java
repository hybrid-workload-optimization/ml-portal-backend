package kr.co.strato.global.model;

import java.io.Serializable;

public class ResponseWrapper<T> implements Serializable {
    private String code;
    private String message;
    private String detail;
    private T result;

    public ResponseWrapper(){
        CommonType type = CommonType.OK;
        this.code = type.getCode();
        this.message = type.getMessage();
        this.detail = type.getDetail();
    }

    public ResponseWrapper(T result){
        CommonType type = CommonType.OK;
        this.code = type.getCode();
        this.message = type.getMessage();
        this.detail = type.getDetail();
        this.result = result;
    }

    public ResponseWrapper(T result, ResponseType type){
        this.code = type.getCode();
        this.message = type.getMessage();
        this.detail = type.getDetail();
        this.result = result;
    }

    public ResponseWrapper(ResponseType type){
        this.code = type.getCode();
        this.message = type.getMessage();
        this.detail = type.getDetail();
    }

    public String getCode() {
        return code;
    }

    public ResponseWrapper<T> setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResponseWrapper<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getDetail() {
        return detail;
    }

    public ResponseWrapper<T> setDetail(String detail) {
        this.detail = detail;
        return this;
    }

    public T getResult() {
        return result;
    }

    public ResponseWrapper<T> setResult(T result) {
        this.result = result;
        return this;
    }
}
