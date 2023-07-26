package kr.co.strato.portal.workload.v2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import kr.co.strato.adapter.k8s.statefulset.service.StatefulSetAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.portal.workload.v2.model.StatefulSetDto;
import kr.co.strato.portal.workload.v2.model.WorkloadCommonDto;

@Service
public class StatefulSetServiceV2 extends WorkloadCommonV2 {
	
	@Autowired
	private StatefulSetAdapterService statefulSetAdapterService;

	@Override
	public WorkloadCommonDto toDto(ClusterEntity clusterEntity, HasMetadata data) throws Exception {
		return toDto(clusterEntity.getClusterId(), data);
	}
	
	public StatefulSetDto toDto(Long kubeConfigId, HasMetadata data) {
		StatefulSetDto dto = new StatefulSetDto();
		setMetadataInfo(data, dto);		
		
		StatefulSet s = (StatefulSet) data;
		
		String image = s.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
		Integer replicas = s.getStatus().getReplicas();
	    Integer readyReplicas = s.getStatus().getReadyReplicas();
		
	    dto.setImage(image);
	    dto.setReplicas(replicas);
	    dto.setReadyReplicas(readyReplicas);
		return dto;
	}
	
	/**
	 * ownerUid를 이용하여 StatefulSet 리스트를 조회한다.
	 * @param kubeConfigId
	 * @param ownerUid
	 * @return
	 * @throws Exception
	 */
	public List<StatefulSetDto> getJobByOwnerUid(Long kubeConfigId, String ownerUid) throws Exception  {
		List<StatefulSet> list = statefulSetAdapterService.getListFromOwnerUid(kubeConfigId, ownerUid);
		if(list != null && list.size() > 0) {
			List<StatefulSetDto> newList = list.stream().map(j -> toDto(kubeConfigId, j)).collect(Collectors.toList());
			return newList;
		}
		return null;
	}
}
