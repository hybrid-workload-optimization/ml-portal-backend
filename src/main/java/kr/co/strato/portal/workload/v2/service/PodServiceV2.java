package kr.co.strato.portal.workload.v2.service;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodStatus;
import kr.co.strato.adapter.k8s.persistentVolumeClaim.service.PersistentVolumeClaimAdapterService;
import kr.co.strato.adapter.k8s.pod.service.PodAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.portal.workload.v2.model.PersistentVolumeClaimDto;
import kr.co.strato.portal.workload.v2.model.PodDto;
import kr.co.strato.portal.workload.v2.model.WorkloadCommonDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PodServiceV2 extends WorkloadCommonV2 {
	
	@Autowired
	private PersistentVolumeClaimAdapterService persistentVolumeClaimAdapterService;
	
	@Autowired
	private PersistentVolumeClaimServiceV2 pvcService;
	
	@Autowired
    private PodAdapterService podAdapterService;

	@Override
	public WorkloadCommonDto toDto(ClusterEntity clusterEntity, HasMetadata data) throws Exception {
		return toDto(clusterEntity.getClusterId(), data);
	}
	
	public PodDto toDto(Long kubeconfigId, HasMetadata data) {
		PodDto dto = new PodDto();
		
		try {
			setMetadataInfo(data, dto);		
			
			ObjectMapper mapper = new ObjectMapper();
			Pod pod = (Pod) data;
			
			PodSpec spec = pod.getSpec();
	        PodStatus status = pod.getStatus();
	        
	        List<ContainerStatus> containerList = status.getContainerStatuses();        
	        List<OwnerReference> ownerList = pod.getMetadata().getOwnerReferences(); 
			
	        String namespace = pod.getMetadata().getNamespace();
			String nodeName = spec.getNodeName();
	        String ip = status.getPodIP();
	        String statusStr = status.getPhase();
	        String qosClass = status.getQosClass();
	        
	        String ownerUid = null;
	        String ownerKind = null;
	        String ownerName = null;
	        Integer restart = 0;
	        ContainerStatus containerStatus = null;
	        if (!containerList.isEmpty()) {
	        	containerStatus = containerList.get(0);
	        	restart = containerStatus.getRestartCount();
	        }
	        if (!ownerList.isEmpty()) {
	        	OwnerReference owner = pod.getMetadata().getOwnerReferences().get(0);
				if(owner != null) {
					ownerName = owner.getName();
					ownerKind = owner.getKind();
					ownerUid = owner.getUid();
				}
	        }
	        
	        List<String> images = spec.getContainers().stream().map(container -> container.getImage()).distinct().collect(Collectors.toList());
			
	        String conditions = mapper.writeValueAsString(status.getConditions());
	        List<HashMap<String, Object>> conditionslist = mapper.readValue(conditions, List.class);
	        
	        
	        
	        List<String> pvcNames = pod.getSpec().getVolumes().stream()
					.map(v -> v.getPersistentVolumeClaim())
					.filter(p -> p != null)
					.map(p -> p.getClaimName())
					.collect(toList());
			
			List<PersistentVolumeClaimDto> pvcList = new ArrayList<>();
			for(String pvcName : pvcNames) {
				try {
					PersistentVolumeClaim pvc = persistentVolumeClaimAdapterService.get(kubeconfigId, namespace, pvcName);
					
					if(pvc != null) {
						PersistentVolumeClaimDto pvcDto = pvcService.toDto(pvc);
						
						if (pvcDto != null) {
							pvcList.add(pvcDto);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	        
	        dto.setNode(nodeName);
	        dto.setIp(ip);
	        dto.setStatus(statusStr);
	        dto.setQosClass(qosClass);
	        dto.setRestart(restart);
	        dto.setImages(images);
	        dto.setOwnerUid(ownerUid);
	        dto.setOwnerKind(ownerKind);
	        dto.setOwnerName(ownerName);
	        dto.setCondition(conditionslist);
	        dto.setPvcList(pvcList);
		} catch (Exception e) {
			log.error("", e);
		}
		return dto;
	}
	
	/**
	 * OwnerUid로 파드 검색
	 * @param kubeConfigId
	 * @param ownerUid
	 * @return
	 * @throws Exception
	 */
	public List<PodDto> getPodByOwnerUid(Long kubeConfigId, String ownerUid) throws Exception  {
		List<Pod> pods = podAdapterService.getList(kubeConfigId, null, ownerUid, null, null);
		if(pods != null && pods.size() > 0) {
			List<PodDto> list = pods.stream().map(p -> toDto(kubeConfigId, p)).toList();
			return list;
		}
		return null;
	}
	
}
