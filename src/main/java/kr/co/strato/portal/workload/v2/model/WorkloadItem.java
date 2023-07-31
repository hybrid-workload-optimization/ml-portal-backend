package kr.co.strato.portal.workload.v2.model;

import java.util.ArrayList;
import java.util.List;

import io.fabric8.kubernetes.api.model.HasMetadata;
import lombok.Data;

@Data
public class WorkloadItem {
	private HasMetadata data;
    private List<WorkloadItem> children;
    
    public WorkloadItem(HasMetadata data) {
    	this.children = new ArrayList<>();
    	this.data = data;
    }
    
    public void addChild(WorkloadItem child) {
    	if(!this.children.contains(child)) {
    		this.children.add(child);
    	}
    }
    
    public void removeChild(WorkloadItem child) {
    	this.children.remove(child);
    }
}
