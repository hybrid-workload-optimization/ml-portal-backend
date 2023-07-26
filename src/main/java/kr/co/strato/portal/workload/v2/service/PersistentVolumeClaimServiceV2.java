package kr.co.strato.portal.workload.v2.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Quantity;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.portal.workload.v2.model.PersistentVolumeClaimDto;
import kr.co.strato.portal.workload.v2.model.WorkloadCommonDto;

@Service
public class PersistentVolumeClaimServiceV2 extends WorkloadCommonV2 {

	@Override
	public WorkloadCommonDto toDto(ClusterEntity clusterEntity, HasMetadata data) throws Exception {
		return toDto(data);
	}
	
	
	public PersistentVolumeClaimDto toDto(HasMetadata data) {
		PersistentVolumeClaimDto dto = new PersistentVolumeClaimDto();
		setMetadataInfo(data, dto);
		
		PersistentVolumeClaim p = (PersistentVolumeClaim)data;
		
		String stauts = p.getStatus().getPhase();
		 
		String accessType = null;
        List<String> accessList = p.getSpec().getAccessModes();
        if(accessList != null && accessList.size() > 0) {
        	for(int i = 0; i < accessList.size(); i++) {
        		if(i == 0) {
        			accessType = accessList.get(i);
        		} else {
        			accessType = accessType + ", " + accessList.get(i);
        		}
        	}
        }
        String storageClass = p.getSpec().getStorageClassName();
        
        String storageCapacity = null;
        Map<String, Quantity> capacity = p.getStatus().getCapacity();
        if(capacity != null) {
        	Quantity quantity = capacity.get("storage");
            if(quantity != null) {
            	String storageAmount = quantity.getAmount();
            	String formatAmount = quantity.getFormat();
            	storageCapacity = storageAmount + formatAmount;
            	storageCapacity.replaceAll("\"", "");
            }
        }
        
        
        String storageRequest = null;
        Map<String, Quantity> request = p.getSpec().getResources().getRequests();
        if(request != null) {
        	Quantity reqStorage = request.get("storage");
            if(reqStorage != null) {
            	String storageAmount = reqStorage.getAmount();
            	String formatAmount = reqStorage.getFormat();
            	storageRequest = storageAmount + formatAmount;
            	storageRequest = storageRequest.replaceAll("\"", "");
            }
        }
        
        dto.setStatus(stauts);
        dto.setAccessType(accessType);
        dto.setStorageClass(storageClass);
        dto.setCapacity(storageCapacity);
		return dto;
	}
}
