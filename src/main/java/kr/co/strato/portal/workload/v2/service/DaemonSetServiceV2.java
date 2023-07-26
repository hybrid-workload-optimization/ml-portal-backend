package kr.co.strato.portal.workload.v2.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.DaemonSetStatus;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.portal.workload.v2.model.DaemonSetDto;
import kr.co.strato.portal.workload.v2.model.PodDto;
import kr.co.strato.portal.workload.v2.model.WorkloadCommonDto;

@Service
public class DaemonSetServiceV2 extends WorkloadCommonV2 {
	
	@Autowired
	private PodServiceV2 podService;

	@Override
	public WorkloadCommonDto toDto(ClusterEntity clusterEntity, HasMetadata data) throws Exception {
		DaemonSetDto dto = new DaemonSetDto();
		setMetadataInfo(data, dto);		
		
		DaemonSet d = (DaemonSet) data;
		DaemonSetStatus status = d.getStatus();
		
		// TODO : 이미지는 여러개 존재할 수 있는 있으므로 추후 관련 내용을 보완해야 함 
		String image = d.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
        Map<String, String> selector = d.getSpec().getSelector().getMatchLabels();
        
        List<PodDto> pods = podService.getPodByOwnerUid(clusterEntity.getClusterId(), dto.getUid());
        Integer readyReplicas = status.getNumberReady();		
		Integer desiredScheduled = status.getDesiredNumberScheduled();
        
	    
        dto.setImage(image);
	    dto.setSelector(selector);
	    dto.setRunningPod(readyReplicas);
	    dto.setDesiredPod(desiredScheduled);
	    dto.setPods(pods);
	    
		return dto;
	}
}
