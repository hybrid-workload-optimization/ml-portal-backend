package kr.co.strato.portal.cluster.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.Node;
import kr.co.strato.adapter.k8s.cluster.model.ClusterAdapterDto;
import kr.co.strato.adapter.k8s.cluster.model.ClusterInfoAdapterDto;
import kr.co.strato.adapter.k8s.cluster.service.ClusterAdapterService;
import kr.co.strato.adapter.k8s.node.service.NodeAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.cluster.model.ClusterDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClusterService {

	@Autowired
	ClusterDomainService clusterDomainService;
	
	@Autowired
	ClusterAdapterService clusterAdapterService;
	
	@Autowired
	NodeAdapterService nodeAdapterService;
	
	@Autowired
	ClusterNodeService clusterNodeService;
	
	/**
	 * Cluster 목록 조회
	 * 
	 * @param pageable
	 * @return
	 * @throws Exception
	 */
	public Page<ClusterDto> getClusterList(Pageable pageable) throws Exception {
		Page<ClusterEntity> clusterPage = clusterDomainService.getList(pageable);
		List<ClusterDto> clusterList = clusterPage.getContent().stream().map(c -> ClusterDtoMapper.INSTANCE.toDto(c)).collect(Collectors.toList());
		
		return new PageImpl<>(clusterList, pageable, clusterPage.getTotalElements());
	}
	
	/**
	 * Cluster 등록
	 * 
	 * @param clusterDto
	 * @return
	 * @throws Exception
	 */
	public Long registerCluster(ClusterDto clusterDto) throws Exception {
		// k8s - post cluster
		ClusterAdapterDto clusterAdapterDto = ClusterAdapterDto.builder()
				.provider(clusterDto.getProvider())
				.configContents(Base64.getEncoder().encodeToString(clusterDto.getKubeConfig().getBytes()))
				.build();
		
		String strClusterId = clusterAdapterService.registerCluster(clusterAdapterDto);
		if (StringUtils.isEmpty(strClusterId)) {
			throw new PortalException("Cluster registration failed");
		}
		
		// kubeCofingId = clusterId
		Long clusterId = Long.valueOf(strClusterId);
		
		// k8s - get cluster's information(health + version)
		ClusterInfoAdapterDto clusterInfo = clusterAdapterService.getClusterInfo(clusterId);
		String clusterHealth		= clusterInfo.getClusterHealth().getHealth();
		List<String> clusterProblem	= clusterInfo.getClusterHealth().getProblem();
		// for test
		//List<String> clusterProblem	= Arrays.asList("problem1", "problem12", "problem3");
		
		Map<String, Object> clusterProblemMap = new HashMap<>();
		clusterProblemMap.put(ClusterEntity.DATA_KEY_PROBLEM, clusterProblem);
		
		ObjectMapper mapper = new ObjectMapper();
		String clusterProblemString = mapper.writeValueAsString(clusterProblemMap);
		
		// k8s - get cluster's nodes
		List<Node> nodeList = nodeAdapterService.getNodeList(clusterId);
		
		// db - insert cluster
		ClusterEntity clusterEntity = ClusterDtoMapper.INSTANCE.toEntity(clusterDto);
		clusterEntity.setClusterId(clusterId);
		clusterEntity.setStatus(clusterHealth);
		clusterEntity.setProblem(clusterProblemString);
		clusterEntity.setProviderVersion(clusterInfo.getKubeletVersion());
		clusterEntity.setCreatedAt(DateUtil.currentDateTime());
		
		clusterDomainService.register(clusterEntity);
		
		// db - insert cluster's nodes
		clusterNodeService.synClusterNodeSave(nodeList, clusterEntity.getClusterIdx());
		
		return clusterEntity.getClusterIdx();
	}

	/**
	 * Cluster 수정
	 * 
	 * @param clusterIdx
	 * @param clusterDto
	 * @return
	 * @throws Exception
	 */
	public Long updateCluster(Long clusterIdx, ClusterDto clusterDto) throws Exception {
		ClusterAdapterDto clusterAdapterDto = ClusterAdapterDto.builder()
				.provider(clusterDto.getProvider())
				.configContents(Base64.getEncoder().encodeToString(clusterDto.getKubeConfig().getBytes()))
				.kubeConfigId(clusterDto.getClusterId())
				.build();
		
		boolean isUpdated = clusterAdapterService.updateCluster(clusterAdapterDto);
		if (!isUpdated) {
			throw new PortalException("Cluster modification failed");
		}
		
		ClusterEntity clusterEntity = ClusterDtoMapper.INSTANCE.toEntity(clusterDto);
		clusterEntity.setClusterIdx(clusterIdx);
		
		clusterDomainService.update(clusterEntity);
		
		return clusterEntity.getClusterIdx();
	}

	/**
	 * Cluster 상세 조회
	 * 
	 * @param clusterIdx
	 * @return
	 * @throws Exception
	 */
	public ClusterDto getCluster(Long clusterIdx) throws Exception {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		
		return ClusterDtoMapper.INSTANCE.toDto(clusterEntity);
	}

	/**
	 * Cluster 삭제
	 * 
	 * @param clusterIdx
	 * @throws Exception
	 */
	public void deleteCluster(Long clusterIdx) throws Exception {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		
		boolean isDeleted = clusterAdapterService.deleteCluster(clusterEntity.getClusterId());
		if (!isDeleted) {
			throw new PortalException("Cluster deletion failed");
		}
		
		clusterDomainService.delete(clusterEntity);
	}

	/**
	 * Cluster 중복 확인(By CusterName)
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public boolean isClusterDuplication(String name) throws Exception {
		return clusterDomainService.isClusterDuplication(name);
	}

	/**
	 * Cluster 연결 테스트
	 * 
	 * @param configContents
	 * @return
	 * @throws Exception
	 */
	public boolean isClusterConnection(String configContents) throws Exception {
		return clusterAdapterService.isClusterConnection(Base64.getEncoder().encodeToString(configContents.getBytes()));
	}

}
