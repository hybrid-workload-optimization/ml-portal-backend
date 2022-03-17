package kr.co.strato.portal.cluster.service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.storage.StorageClass;
import kr.co.strato.adapter.k8s.cluster.model.ClusterAdapterDto;
import kr.co.strato.adapter.k8s.cluster.model.ClusterInfoAdapterDto;
import kr.co.strato.adapter.k8s.cluster.service.ClusterAdapterService;
import kr.co.strato.adapter.k8s.namespace.service.NamespaceAdapterService;
import kr.co.strato.adapter.k8s.node.service.NodeAdapterService;
import kr.co.strato.adapter.k8s.persistentVolume.service.PersistentVolumeAdapterService;
import kr.co.strato.adapter.k8s.storageClass.service.StorageClassAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.persistentVolumeClaim.service.PersistentVolumeClaimDomainService;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.pod.service.PodDomainService;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.cluster.model.ClusterDtoMapper;
import kr.co.strato.portal.cluster.model.ClusterNodeDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClusterService {

	@Autowired
	ClusterDomainService clusterDomainService;
	
	@Autowired
	NamespaceDomainService namespaceDomainService;
	
	@Autowired
	PodDomainService podDomainService;
	
	@Autowired
	PersistentVolumeClaimDomainService persistentVolumeClaimDomainService;
	
	@Autowired
	ClusterAdapterService clusterAdapterService;
	
	@Autowired
	NodeAdapterService nodeAdapterService;
	
	@Autowired
	NamespaceAdapterService namespaceAdapterService;
	
	@Autowired
	PersistentVolumeAdapterService persistentVolumeAdapterService;
	
	@Autowired
	StorageClassAdapterService storageClassAdapterService;
	
	@Autowired
	ClusterNodeService clusterNodeService;

	@Autowired
	ClusterNamespaceService clusterNamespaceService;
	
	@Autowired
	ClusterPersistentVolumeService clusterPersistentVolumeService;
	
	@Autowired
	ClusterStorageClassService clusterStorageClassService;
	
	
	/**
	 * Cluster 목록 조회
	 * 
	 * @param pageable
	 * @return
	 * @throws Exception
	 */
	public Page<ClusterDto.List> getClusterList(Pageable pageable) throws Exception {
		Page<ClusterEntity> clusterPage = clusterDomainService.getList(pageable);
		
		List<ClusterDto.List> clusterList = clusterPage.getContent().stream()
				.map(c -> ClusterDtoMapper.INSTANCE.toList(c))
				.collect(Collectors.toList());
		
		return new PageImpl<>(clusterList, pageable, clusterPage.getTotalElements());
	}
	
	/**
	 * Cluster 등록
	 * 
	 * @param clusterDto
	 * @return
	 * @throws Exception
	 */
	public Long createCluster(ClusterDto.Form clusterDto) throws Exception {
		if (!StringUtils.isEmpty(clusterDto.getKubeConfig())) {
			// k8s를 통한 cluster 등록
			return createK8sCluster(clusterDto);
		} else {
			// kubespray를 통한 cluster 생성 및 등록
			return createKubesprayCluster(clusterDto);
		}
	}
	
	/**
	 * (K8s) Cluster 등록
	 * 
	 * @param clusterDto
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	private Long createK8sCluster(ClusterDto.Form clusterDto) throws Exception {
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
		
		ObjectMapper mapper = new ObjectMapper();
		String clusterProblemString = mapper.writeValueAsString(clusterProblem);
		
		// db - insert cluster
		ClusterEntity clusterEntity = ClusterDtoMapper.INSTANCE.toEntity(clusterDto);
		clusterEntity.setClusterId(clusterId);
		clusterEntity.setStatus(clusterHealth);
		clusterEntity.setProblem(clusterProblemString);
		clusterEntity.setProviderVersion(clusterInfo.getKubeletVersion());
		clusterEntity.setCreatedAt(DateUtil.currentDateTime());
		
		clusterDomainService.register(clusterEntity);
		
		// sync k8s cluster
		log.info("Cluster Synchronization started.");
		syncCluster(clusterId, clusterEntity.getClusterIdx());
		
		return clusterEntity.getClusterIdx();
	}
	
	/**
	 * (Kubespray) Cluster 등록
	 * TODO : 구현 필요
	 * 
	 * @param clusterDto
	 * @return
	 * @throws Exception
	 */
	private Long createKubesprayCluster(ClusterDto.Form clusterDto) throws Exception {
		return null;
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void syncCluster(Long clusterId, Long clusterIdx) throws Exception {
		// k8s - get namespace
		List<Namespace> namespaces = namespaceAdapterService.getNamespaceList(clusterId);
				
		// k8s - get node
		List<Node> nodes = nodeAdapterService.getNodeList(clusterId);
		
		// k8s - get pv
		List<PersistentVolume> persistentVolumes = persistentVolumeAdapterService.getPersistentVolumeList(clusterId);
		
		// k8s - get storage class
		List<StorageClass> storageClasses = storageClassAdapterService.getStorageClassList(clusterId);
		
		// db - insert namespace
		clusterNamespaceService.synClusterNamespaceSave(namespaces, clusterIdx);
				
		// db - insert node
		clusterNodeService.synClusterNodeSave(nodes, clusterIdx);
		
		// db - insert pv
		clusterPersistentVolumeService.synClusterPersistentVolumeSave(persistentVolumes, clusterIdx);
		
		// db - storage class
		clusterStorageClassService.synClusterStorageClassSave(storageClasses, clusterIdx);
	}
	
	/**
	 * Cluster 수정
	 * 
	 * @param clusterIdx
	 * @param clusterDto
	 * @return
	 * @throws Exception
	 */
	public Long updateCluster(Long clusterIdx, ClusterDto.Form clusterDto) throws Exception {
		// db - get cluster
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		
		// k8s - update cluster
		ClusterAdapterDto clusterAdapterDto = ClusterAdapterDto.builder()
				.provider(clusterDto.getProvider())
				.configContents(Base64.getEncoder().encodeToString(clusterDto.getKubeConfig().getBytes()))
				.kubeConfigId(clusterEntity.getClusterId())
				.build();
		
		boolean isUpdated = clusterAdapterService.updateCluster(clusterAdapterDto);
		if (!isUpdated) {
			throw new PortalException("Cluster modification failed");
		}
		
		// db - update cluster
		clusterEntity.setClusterName(clusterDto.getClusterName());
		clusterEntity.setKubeConfig(clusterDto.getKubeConfig());
		clusterEntity.setDescription(clusterDto.getDescription());
		
		clusterDomainService.update(clusterEntity);
		
		return clusterIdx;
	}

	/**
	 * Cluster 상세 조회
	 * 
	 * @param clusterIdx
	 * @return
	 * @throws Exception
	 */
	public ClusterDto.Detail getCluster(Long clusterIdx) throws Exception {
		// cluster
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		
		ClusterDto.Detail detail = ClusterDtoMapper.INSTANCE.toDetail(clusterEntity);
		
		PageRequest pageRequest = new PageRequest();
		
		// node
		List<NodeEntity> nodes = clusterEntity.getNodes();
		
		// namespace
		List<NamespaceEntity> namespaces = namespaceDomainService.getNamespaceList(pageRequest.of(), clusterIdx, null).getContent();
		
		// pod
		List<PodEntity> pods = podDomainService.getPods(pageRequest.of(), null, clusterIdx, null, null).getContent();
		
		// TODO : pvc list
		
		log.debug("[getCluster] nodes/namespaces/pods size = {}/{}/{}", nodes.size(), namespaces.size(), pods.size());
		
		List<NodeEntity> masterNodes = nodes.stream().filter(n -> n.getRole().contains("master")).collect(Collectors.toList());
		List<NodeEntity> workerNodes = nodes.stream().filter(n -> n.getRole().contains("worker")).collect(Collectors.toList());
		
		List<NodeEntity> availableMasterNodes = masterNodes.stream().filter(n -> n.getStatus().equals("true")).collect(Collectors.toList());
		List<NodeEntity> availableworkerNodes = workerNodes.stream().filter(n -> n.getStatus().equals("true")).collect(Collectors.toList());
		
		log.debug("[getCluster] masterNodes/workerNodes size = {}/{}", masterNodes.size(), workerNodes.size());
		log.debug("[getCluster] availableMasterNodes/availableworkerNodes size = {}/{}", availableMasterNodes.size(), availableworkerNodes.size());
		
		// Master/Worker 수량
		int masterCount = masterNodes.size();
		int workerCount = workerNodes.size();
		
		// Master/Worker 가동률
		float availableMasterPercent = masterCount > 0 ? (availableMasterNodes.size() * 100 / masterCount) : 0;
		float availableWorkerPercent = workerCount > 0 ? (availableworkerNodes.size() * 100 / workerCount) : 0;
		
		detail.setMasterCount(masterCount);
		detail.setWorkerCount(workerCount);
		detail.setAvailableMasterPercent(availableMasterPercent);
		detail.setAvailableWorkerPercent(availableWorkerPercent);
		detail.setNamespaceCount(namespaces.size());
		detail.setPodCount(pods.size());
		detail.setPvcCount(0); // TODO : pvc count
		
		return detail;
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

	public Page<ClusterNodeDto.ResListDto> getClusterNodeList(Long clusterIdx, Pageable pageable) {
		return clusterNodeService.getClusterNodeList(clusterIdx, pageable);
	}

	public Page<ClusterNodeDto.ResListDto> getClusterDeploymentList(Long clusterIdx, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	public Page<ClusterNodeDto.ResListDto> getClusterStatefulSetList(Long clusterIdx, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	public Page<ClusterNodeDto.ResListDto> getClusterPodList(Long clusterIdx, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	public Page<ClusterNodeDto.ResListDto> getClusterCronJobList(Long clusterIdx, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	public Page<ClusterNodeDto.ResListDto> getClusterJobList(Long clusterIdx, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	public Page<ClusterNodeDto.ResListDto> getClusterReplicaSetList(Long clusterIdx, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	public Page<ClusterNodeDto.ResListDto> getClusterDaemonSetList(Long clusterIdx, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	public Page<ClusterNodeDto.ResListDto> getClusterServiceList(Long clusterIdx, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	public Page<ClusterNodeDto.ResListDto> getClusterIngressList(Long clusterIdx, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}
}
