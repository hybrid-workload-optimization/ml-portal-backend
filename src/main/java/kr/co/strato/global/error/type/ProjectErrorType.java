package kr.co.strato.global.error.type;

import kr.co.strato.global.model.ResponseType;

public enum ProjectErrorType implements ResponseType {
	
	FAIL_PROJECT_CREATE("12001", "프로젝트를 생성하지 못했습니다.", null),
    FAIL_PROJECT_UPDATE("12002", "프로젝트를 수정하지 못했습니다.", null),
    FAIL_PROJECT_DELETE("12003", "프로젝트를 삭제하지 못했습니다.", null),
	ALEADY_USE_CLUSTER("12004", "Cluster를 사용하고 있는 Poject가 있습니다.", null),
	NOT_FOUND_PROJECT("12005", "Project가 존재하지 않습니다.", null);

    private String code;
    private String message;
    private String detail;

    ProjectErrorType(String code, String message, String detail) {
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
