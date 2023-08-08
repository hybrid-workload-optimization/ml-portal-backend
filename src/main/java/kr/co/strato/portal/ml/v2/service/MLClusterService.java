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
import kr.co.strato.portal.ml.v2.model.MLClusterDto.Cluster;
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

	public List<Cluster> getClusterList() {
		List<Cluster> result = new ArrayList<>();
		List<ClusterEntity> clusterList = clusterDomainService.getListAll();
		for(ClusterEntity entity : clusterList) {
			Long kubeConfigId = entity.getClusterId();
			Long clusterIdx = entity.getClusterIdx();
			String name = entity.getClusterName();
			String description = entity.getDescription();
			String provider = entity.getProvider();
			String region = entity.getRegion();
			String version = entity.getProviderVersion();
			String status = entity.getProvisioningStatus();
			String createAt = entity.getCreatedAt();
			
			
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
				
				Cluster cluster = Cluster.builder()
						.clusterIdx(clusterIdx)
						.name(name)
						.description(description)
						.provider(provider)
						.region(region)
						.vision(version)
						.status(status)
						.createAt(createAt)
						.prometheusUrl(prometheusUrl)
						.nodes(nodeList)
						.build();
				result.add(cluster);
			} catch (Exception e) {
				log.error("", e);
			}
		}
		return result;
	}
}
