package kr.co.strato.portal.ml.model;

public enum MLStepCode {
	PRETREATMENT("ml-step-100"),
	VERIFICATION("ml-step-200"),
	LEARNING("ml-step-300"),
	INFERENCE("ml-step-400"),
	SERVICE("ml-step-900"),;
	
	private String code;
	
	MLStepCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return this.code;
	}
}
