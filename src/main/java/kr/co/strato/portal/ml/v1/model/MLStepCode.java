package kr.co.strato.portal.ml.v1.model;

public enum MLStepCode {	
	Pretreatment("ml-step-100"),
	Verification("ml-step-200"),
	Learning("ml-step-300"),
	Inference("ml-step-400"),
	Service("ml-step-900"),;
	
	private String code;
	
	MLStepCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public static MLStepCode getByCode(String code) {
		if(code.equals(Pretreatment.getCode())) {
			return Pretreatment;
		} else if(code.equals(Verification.getCode())) {
			return Verification;
		} else if(code.equals(Learning.getCode())) {
			return Learning;
		} else if(code.equals(Inference.getCode())) {
			return Inference;
		} else if(code.equals(Service.getCode())) {
			return Service;
		}
		return null;
	}
}
