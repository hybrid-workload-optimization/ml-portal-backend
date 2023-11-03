package kr.co.strato.portal.cluster.v2.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.HostPathVolumeSource;
import io.fabric8.kubernetes.api.model.NFSVolumeSource;
import io.fabric8.kubernetes.api.model.ObjectReference;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.persistentVolume.service.PersistentVolumeAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.v2.model.PersistentVolumeDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PersistentVolumeService {
	
	@Autowired
	private ClusterDomainService clusterDomainService;
	
	@Autowired
	private PersistentVolumeAdapterService persistentVolumeAdapterService;
	
	
	public List<PersistentVolumeDto.ListDto> getList(Long clusterIdx) {
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		Long kubeConfigId = cluster.getClusterId();
		
		List<PersistentVolumeDto.ListDto> list = new ArrayList<>();
		List<PersistentVolume> pvList = persistentVolumeAdapterService.getPersistentVolumeList(kubeConfigId);
		for(PersistentVolume pv : pvList) {
			PersistentVolumeDto.ListDto dto = getPersistentVolumeDto(pv);
			list.add(dto);
		}
		return list;
	}
	
	public List<PersistentVolumeDto.ListDto> getList(ResourceListSearchInfo param) {
		List<PersistentVolumeDto.ListDto> list = new ArrayList<>();
		List<PersistentVolume> pvList = persistentVolumeAdapterService.getPersistentVolumeList(param);
		for(PersistentVolume pv : pvList) {
			PersistentVolumeDto.ListDto dto = getPersistentVolumeDto(pv);
			list.add(dto);
		}
		return list;
	}
	
	public PersistentVolumeDto.ListDto getDetail(Long clusterIdx, String name) {
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		Long kubeConfigId = cluster.getClusterId();
		PersistentVolume pv = persistentVolumeAdapterService.getPersistentVolume(kubeConfigId, name);
		if(pv != null) {
			return getPersistentVolumeDto(pv);
		}
		return null;
	}
	
	
	public List<PersistentVolumeDto.ListDto> getListForKubeConfigId(Long kubeConfigId) {
		List<PersistentVolumeDto.ListDto> list = new ArrayList<>();
		List<PersistentVolume> pvList = persistentVolumeAdapterService.getPersistentVolumeList(kubeConfigId);
		for(PersistentVolume pv : pvList) {
			PersistentVolumeDto.ListDto dto = getPersistentVolumeDto(pv);
			list.add(dto);
		}
		return list;
	}
	
	/**
	 * 삭제
	 * @param param
	 * @return
	 */
	public boolean delete(PersistentVolumeDto.DeleteDto param) {
		Long clusterIdx = param.getClusterIdx();
		String name = param.getName();
		
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);		
		boolean isDeleted = persistentVolumeAdapterService.deletePersistentVolume(cluster.getClusterId().intValue(), name);
		return isDeleted;
	}
	
	public PersistentVolumeDto.ListDto getPersistentVolumeDto(PersistentVolume pv) {
		String uid = pv.getMetadata().getUid();
		String name = pv.getMetadata().getName();
		
		PersistentVolumeStatus PVstatus = pv.getStatus();
		String status = PVstatus.getPhase();
		
		String createdAt = DateUtil.strToNewFormatter(pv.getMetadata().getCreationTimestamp());
		
		String accessModes = pv.getSpec().getAccessModes().get(0);

		ObjectReference claimRef = pv.getSpec().getClaimRef();
		String claim = "";
		if(claimRef != null) {
			claim = claimRef.getNamespace() + "/" + claimRef.getName();
		}
		
		
		String reclaimPolicy = pv.getSpec().getPersistentVolumeReclaimPolicy();
		String storageClassName = pv.getSpec().getStorageClassName();
		String type = "";
		HostPathVolumeSource hostPath = pv.getSpec().getHostPath();
		if(hostPath != null) {
			type = "Hostpath";
		}
		
		NFSVolumeSource nfs = pv.getSpec().getNfs();
		if(nfs != null) {
			type =  "NFS";
		}
		
		double size = 0F;
		Map<String, Quantity> capacity = pv.getSpec().getCapacity();
		Set<String> keys = capacity.keySet();
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			Quantity v = capacity.get(key);
			
			String storageAmount = v.getAmount().replaceAll("[^0-9]", "");
			String format = v.getFormat().replaceAll("[^a-zA-Z]", "");
			
			
			
			Float amount = Float.parseFloat(storageAmount.replaceAll("[^0-9]", ""));
    		if (format.equals("Mi")) {
    			size = amount * 1000000.0;
        	} else if (format.equals("Gi")) {
        		size = amount * 1000000000.0;
        	} else if (format.equals("M")) {
        		size = amount / 1000.0;
        	} else if (format.equals("G")) {
        		size = amount;
        	}
    		
		}
		pv.getSpec().getCapacity().size();
		
		Map<String, String> annotations = pv.getMetadata().getAnnotations();
		Map<String, String> labels = pv.getMetadata().getLabels();
		
		
		PersistentVolumeDto.ListDto dto = PersistentVolumeDto.ListDto.builder()
				.uid(uid)
				.name(name)
				.labels(labels)
				.annotations(annotations)
				.accessMode(accessModes)
				.claim(claim)
				.reclaimPolicy(reclaimPolicy)
				.status(status)
				.size(size)
				.type(type)
				.storageClass(storageClassName)
				.accessMode(accessModes)
				.createdAt(createdAt)
				.build();
		return dto;
	}

}
