package kr.co.strato.adapter.k8s.ingressController.model;

import java.util.List;

public class IngressController {
	private String name;	
	private List<String> address;	
	private List<ServicePort> ports;
	private String serviceType;
	private String creationTimestamp;
	private boolean isDefault;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getAddress() {
		return address;
	}
	public void setAddress(List<String> address) {
		this.address = address;
	}
	public List<ServicePort> getPorts() {
		return ports;
	}
	public void setPorts(List<ServicePort> ports) {
		this.ports = ports;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getCreationTimestamp() {
		return creationTimestamp;
	}
	public void setCreationTimestamp(String creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
}
