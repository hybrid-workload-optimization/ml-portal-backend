package kr.co.strato.adapter.k8s.ingressController.model;

public class ServicePort {
	private String protocol;	
	private Integer port;
	
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
}
