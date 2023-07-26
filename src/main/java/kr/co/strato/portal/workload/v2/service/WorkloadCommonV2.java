package kr.co.strato.portal.workload.v2.service;

import java.util.Map;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.workload.v2.model.WorkloadCommonDto;

public abstract class WorkloadCommonV2 {
	
	/**
	 * 공통 정보 맵핑
	 * @param data
	 * @param dto
	 */
	protected void setMetadataInfo(HasMetadata data, WorkloadCommonDto dto) {
		ObjectMeta metadata = data.getMetadata();
		
		String uid = metadata.getUid();
        String name = metadata.getName();
        String namespace = metadata.getNamespace();
        String kind = data.getKind();
        String createAt = DateUtil.convertDateTime(metadata.getCreationTimestamp());       
        Map<String, String> annotations = metadata.getAnnotations();
        Map<String, String> labels = metadata.getLabels();
        
        
        dto.setUid(uid);
        dto.setName(name);
        dto.setNamespace(namespace);
        dto.setKind(kind);
        dto.setCreatedAt(createAt);
        dto.setAnnotations(annotations);
        dto.setLabels(labels);
	}

	/**
	 * DTO로 변환
	 * @param data
	 * @return
	 */
	public abstract  WorkloadCommonDto toDto(ClusterEntity clusterEntity, HasMetadata data) throws Exception;
	
	
}
