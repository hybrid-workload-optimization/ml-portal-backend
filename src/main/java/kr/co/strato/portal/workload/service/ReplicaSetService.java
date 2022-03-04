package kr.co.strato.portal.workload.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import kr.co.strato.adapter.k8s.replicaset.service.ReplicaSetAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;
import kr.co.strato.domain.replicaset.service.ReplicaSetDomainService;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.workload.model.ReplicaSetDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReplicaSetService {

	@Autowired
	ReplicaSetAdapterService replicaSetAdapterService;
	
	@Autowired
	ReplicaSetDomainService replicaSetDomainService;
	
	@Autowired
	ClusterDomainService clusterDomainService;
	
	@Autowired
	NamespaceDomainService namespaceDomainService;
	
	/**
	 * Replica Set 등록
	 * 
	 * @param replicaSetDto
	 * @return
	 * @throws Exception
	 */
	public List<Long> registerReplicaSet(ReplicaSetDto replicaSetDto) throws Exception {
		// get clusterId(kubeConfigId)
		ClusterEntity cluster = clusterDomainService.get(replicaSetDto.getClusterIdx());
		
		// k8s - post replica set
		List<ReplicaSet> replicaSetList = replicaSetAdapterService.registerReplicaSet(cluster.getClusterId(), replicaSetDto.getYaml());
		
		// db - save replica set
		List<Long> result = replicaSetList.stream()
				.map(r -> {
					ReplicaSetEntity replicaSetEntity = null;
					try {
						replicaSetEntity = toReplicaSetEntity(cluster, r);
					} catch (PortalException e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getErrorType().getDetail());
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getMessage());
					}
					return replicaSetDomainService.register(replicaSetEntity);
				})
				.collect(Collectors.toList());
		
		return result;
	}
	
	/**
	 * Replica Set 삭제
	 * 
	 * @param replicaSetIdx
	 * @throws Exception
	 */
	public void deleteReplicaSet(Long replicaSetIdx) throws Exception {
		ReplicaSetEntity replicaSetEntity = replicaSetDomainService.get(replicaSetIdx);
		Long clusterId			= replicaSetEntity.getNamespace().getClusterIdx().getClusterId();
		String namespaceName	= replicaSetEntity.getNamespace().getName();
        String replicaSetName	= replicaSetEntity.getReplicaSetName();
        
		boolean isDeleted = replicaSetAdapterService.deleteReplicaSet(clusterId, namespaceName, replicaSetName);
		if (!isDeleted) {
			throw new PortalException("ReplicaSet deletion failed");
		}
		
		replicaSetDomainService.delete(replicaSetEntity);
	}
	
	/**
	 * K8S Replica Set 정보를 Replica Set Entity 형태로 변환
	 * 
	 * @param clusterEntity
	 * @param r
	 * @return
	 * @throws Exception
	 */
	private ReplicaSetEntity toReplicaSetEntity(ClusterEntity clusterEntity, ReplicaSet r) throws PortalException, Exception {
		ObjectMapper mapper = new ObjectMapper();

        String name			= r.getMetadata().getName();
        String namespace	= r.getMetadata().getNamespace();
        String uid			= r.getMetadata().getUid();
        // TODO : 이미지는 여러개 존재할 수 있는 있으므로 추후 관련 내용을 보완해야 함 
        String image		= r.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
        String selector		= mapper.writeValueAsString(r.getSpec().getSelector().getMatchLabels());
        String label		= mapper.writeValueAsString(r.getMetadata().getLabels());
        String annotations	= mapper.writeValueAsString(r.getMetadata().getAnnotations());
        String createAt		= r.getMetadata().getCreationTimestamp();
        
        // find namespaceIdx
        List<NamespaceEntity> namespaceList = namespaceDomainService.findByNameAndClusterIdx(namespace, clusterEntity);
        if (CollectionUtils.isEmpty(namespaceList)) {
        	throw new PortalException("Can't find namespace : " + namespace);
        }
        
        ReplicaSetEntity result = ReplicaSetEntity.builder()
                .replicaSetName(name)
                .replicaSetUid(uid)
                .image(image)
                .selector(selector)
                .label(label)
                .annotation(annotations)
                .createdAt(DateUtil.convertDateTime(createAt))
                .namespace(namespaceList.get(0))
                .build();

        return result;
	}
	
}
