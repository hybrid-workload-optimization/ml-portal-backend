package kr.co.strato.portal.ml.v2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.workload.service.WorkloadAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.portal.cluster.v2.model.NodeDto;
import kr.co.strato.portal.cluster.v2.service.NodeService;
import kr.co.strato.portal.ml.v1.service.MLClusterAPIAsyncService;
import kr.co.strato.portal.ml.v2.model.MLClusterDto.ClusterDetail;
import kr.co.strato.portal.ml.v2.model.MLClusterDto.ClusterList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MLClusterService {
	
	@Autowired
	private ClusterDomainService clusterDomainService;
	
	@Autowired
	private WorkloadAdapterService workloadAdapterService;
	
	@Autowired
	private NodeService nodeService;
	
	@Autowired
	private MLClusterAPIAsyncService mlClusterAsyncService;

	/**
	 * 머신러닝을 위한 클러스터 리스트 조회
	 * 머신러닝 클러스터는 임시로 생성된 projectIdx: 538번에 소속 되어있다.
	 * @return
	 */
	public List<ClusterList> getClusterList() {
		Long mlProjectIdx = 538L;
		List<ClusterList> result = new ArrayList<>();
		List<ClusterEntity> clusterList = clusterDomainService.getListByProjectIdx(mlProjectIdx);
		for(ClusterEntity entity : clusterList) {
			ClusterList cluster = new ClusterList();
			setClusterDto(entity, cluster);
			result.add(cluster);
		}
		return result;
	}
	
	/**
	 * 클러스터 상세 정보 조회
	 * @param clusterIdx
	 * @return
	 */
	public ClusterDetail getClusterDetail(Long clusterIdx) {		
		ClusterEntity entity = clusterDomainService.get(clusterIdx);
		if(entity != null) {
			ClusterDetail cluster = new ClusterDetail();
			setClusterDto(entity, cluster);
			
			Long kubeConfigId = entity.getClusterId();
			ResourceListSearchInfo search = ResourceListSearchInfo.builder()
					.kubeConfigId(kubeConfigId)
					.build();		
			try {
				List<HasMetadata> list = workloadAdapterService.getList(search);
				
				List<Pod> podList = list.stream()
						.filter(d -> d instanceof Pod)
						.map(d -> (Pod)d)
						.collect(Collectors.toList());
				
				List<NodeDto.ListDto> nodeList = nodeService.getList(kubeConfigId, podList);
				String prometheusUrl = mlClusterAsyncService.getPrometheusUrl(entity);
				
				cluster.setPrometheusUrl(prometheusUrl);
				cluster.setNodes(nodeList);
				return cluster;
			} catch (Exception e) {
				log.error("", e);
			}
		}		
		return null;
	}
	
	public void setClusterDto(ClusterEntity entity, ClusterList dto) {
		Long clusterIdx = entity.getClusterIdx();
		String name = entity.getClusterName();
		String description = entity.getDescription();
		String provider = entity.getProvider();
		String region = entity.getRegion();
		String version = entity.getProviderVersion();
		String status = entity.getProvisioningStatus();
		String createAt = entity.getCreatedAt();
		
		dto.setClusterIdx(clusterIdx);
		dto.setName(name);
		dto.setDescription(description);
		dto.setProvider(provider);
		dto.setRegion(region);
		dto.setVision(version);
		dto.setStatus(status);
		dto.setCreateAt(createAt);
	}
}
