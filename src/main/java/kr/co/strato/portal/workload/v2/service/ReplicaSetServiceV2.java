package kr.co.strato.portal.workload.v2.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import kr.co.strato.adapter.k8s.replicaset.service.ReplicaSetAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.portal.workload.v2.model.PodDto;
import kr.co.strato.portal.workload.v2.model.ReplicaSetDto;
import kr.co.strato.portal.workload.v2.model.WorkloadCommonDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReplicaSetServiceV2 extends WorkloadCommonV2 {
	
	@Autowired
	private ReplicaSetAdapterService replicaSetSetAdapterService;
	
	@Autowired
	private PodServiceV2 podService;

	@Override
	public WorkloadCommonDto toDto(ClusterEntity clusterEntity, HasMetadata data) throws Exception {
		return toDto(clusterEntity.getClusterId(), data);
	}
	
	public ReplicaSetDto toDto(Long kubeConfigId, HasMetadata data) {
		ReplicaSetDto dto = new ReplicaSetDto();
		setMetadataInfo(data, dto);		
		
		ReplicaSet r = (ReplicaSet) data;
		
		Integer runningPod = r.getStatus().getReadyReplicas();
		Integer desiredPod = r.getStatus().getReplicas();
		
		// TODO : 이미지는 여러개 존재할 수 있는 있으므로 추후 관련 내용을 보완해야 함 
        String image = r.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
        Map<String, String> selectors = r.getSpec().getSelector().getMatchLabels();
        
        List<PodDto> pods = null;
		try {
			pods = podService.getPodByOwnerUid(kubeConfigId, dto.getUid());
		} catch (Exception e) {
			log.error("", e);
		}
		
		dto.setRunningPod(runningPod);
		dto.setDesiredPod(desiredPod);
	    dto.setImage(image);
	    dto.setSelectors(selectors);
	    dto.setPods(pods);
		return dto;
	}
	
	/**
	 * ownerUid를 이용하여 ReplicaSet 리스트를 조회한다.
	 * @param kubeConfigId
	 * @param ownerUid
	 * @return
	 * @throws Exception
	 */
	public List<ReplicaSetDto> getJobByOwnerUid(Long kubeConfigId, String ownerUid) throws Exception  {
		List<ReplicaSet> list = replicaSetSetAdapterService.getListFromOwnerUid(kubeConfigId, ownerUid);
		if(list != null && list.size() > 0) {
			List<ReplicaSetDto> newList = list.stream().map(j -> toDto(kubeConfigId, j)).collect(Collectors.toList());
			return newList;
		}
		return null;
	}
}
