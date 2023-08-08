package kr.co.strato.portal.ml.v1.model;

public enum MLClusterType {
	JOB_CLUSTER("JOB"),
	SERVICE_CLUSTER("SERVICE");
	
	private String type;
	
	MLClusterType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
}
