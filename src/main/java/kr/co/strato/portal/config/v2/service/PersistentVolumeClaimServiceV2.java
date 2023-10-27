package kr.co.strato.portal.config.v2.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Quantity;
import kr.co.strato.adapter.k8s.persistentVolumeClaim.service.PersistentVolumeClaimAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.portal.config.v2.model.ConfigCommonDto;
import kr.co.strato.portal.config.v2.model.PersistentVolumeClaimDto;
import kr.co.strato.portal.workload.v2.model.WorkloadCommonDto;
import kr.co.strato.portal.workload.v2.service.WorkloadCommonV2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PersistentVolumeClaimServiceV2 extends WorkloadCommonV2 {

	@Autowired
	ClusterDomainService clusterDomainService;
	
	@Autowired
	PersistentVolumeClaimAdapterService persistentVolumeClaimAdapterService;
	
	
	/**
	 * 리스트 조회
	 * @param clusterIdx
	 * @return
	 * @throws Exception
	 */
	public List<PersistentVolumeClaimDto> getList(Long clusterIdx) throws Exception {
		ClusterEntity entity = clusterDomainService.get(clusterIdx);
		Long kubeConfigId = entity.getClusterId();
				
		List<PersistentVolumeClaim> list = persistentVolumeClaimAdapterService.getList(kubeConfigId);		
		List<PersistentVolumeClaimDto> result = new ArrayList<>();
		if(list != null) {
			for(PersistentVolumeClaim c : list) {
				PersistentVolumeClaimDto dto = (PersistentVolumeClaimDto)toDto(entity, c);
				result.add(dto);
			}
		}
		return result;
	}
	
	
	/**
	 * Persistent Volume Claim 상세 조회
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public PersistentVolumeClaimDto getDetail(ConfigCommonDto.Search search) throws Exception {
		ClusterEntity entity = clusterDomainService.get(search.getClusterIdx());
		Long kubeConfigId = entity.getClusterId();
				
		PersistentVolumeClaim pvc = persistentVolumeClaimAdapterService.get(kubeConfigId, search.getNamespace(), search.getName());
		PersistentVolumeClaimDto dto = (PersistentVolumeClaimDto)toDto(entity, pvc);
		return dto;
	}
	
	
	/**
	 * Persistent Volume Claim Yaml 조회
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public String getYaml(ConfigCommonDto.Search search) throws Exception {
		ClusterEntity entity = clusterDomainService.get(search.getClusterIdx());
		Long kubeConfigId = entity.getClusterId();
				
		String yaml = persistentVolumeClaimAdapterService.getYaml(kubeConfigId, search.getNamespace(), search.getName());
		return Base64.getEncoder().encodeToString(yaml.getBytes());
	}
	
	
	/**
	 * Persistent Volume Claim 삭제
	 * @param search
	 * @throws Exception
	 */
	public boolean delete(ConfigCommonDto.Search search) throws Exception {
		ClusterEntity entity = clusterDomainService.get(search.getClusterIdx());
		Long kubeConfigId = entity.getClusterId();
				
		boolean isOk = persistentVolumeClaimAdapterService.delete(kubeConfigId, search.getNamespace(), search.getName());
		return isOk;
	}

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
        dto.setStorageCapacity(storageCapacity);
		return dto;
	}

}
