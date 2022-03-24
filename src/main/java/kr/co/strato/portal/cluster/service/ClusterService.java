package kr.co.strato.portal.cluster.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import kr.co.strato.adapter.cloud.cluster.model.ClusterCloudDto;
import kr.co.strato.adapter.cloud.cluster.service.ClusterCloudService;
import kr.co.strato.adapter.k8s.cluster.model.ClusterAdapterDto;
import kr.co.strato.adapter.k8s.cluster.model.ClusterInfoAdapterDto;
import kr.co.strato.adapter.k8s.cluster.service.ClusterAdapterService;
import kr.co.strato.adapter.k8s.namespace.service.NamespaceAdapterService;
import kr.co.strato.adapter.k8s.node.service.NodeAdapterService;
import kr.co.strato.adapter.k8s.persistentVolume.service.PersistentVolumeAdapterService;
import kr.co.strato.adapter.k8s.storageClass.service.StorageClassAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.model.ClusterEntity.ProvisioningType;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.node.service.NodeDomainService;
import kr.co.strato.domain.persistentVolumeClaim.service.PersistentVolumeClaimDomainService;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.pod.service.PodDomainService;
import kr.co.strato.domain.setting.model.SettingEntity;
import kr.co.strato.domain.setting.service.SettingDomainService;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.cluster.model.ClusterDtoMapper;
import kr.co.strato.portal.cluster.model.ClusterNodeDto;
import kr.co.strato.portal.work.model.WorkJob.WorkJobData;
import kr.co.strato.portal.work.model.WorkJob.WorkJobStatus;
import kr.co.strato.portal.work.model.WorkJob.WorkJobType;
import kr.co.strato.portal.work.model.WorkJobDto;
import kr.co.strato.portal.work.service.WorkJobService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClusterService {

	@Autowired
	ClusterDomainService clusterDomainService;
	
	@Autowired
	NodeDomainService nodeDomainService;
	
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
	
	@Autowired
	WorkJobService workJobService;
	
	@Autowired
	ClusterCloudService clusterCloudService;
	
	@Autowired
	SettingDomainService settingDomainService;
	
	@Value("${portal.backend.service.url}")
	String portalBackendServiceUrl;
	
	@Value("${server.port}")
	Integer portalBackendServicePort;
	
	
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
		ProvisioningType provisioningType = ClusterEntity.ProvisioningType.valueOf(clusterDto.getProvisioningType());
		
		if (provisioningType == ProvisioningType.KUBECONFIG) {
			// k8s를 통한 cluster 등록
			return createK8sCluster(clusterDto);
		} else if (provisioningType == ProvisioningType.KUBESPRAY) {
			// kubespray를 통한 cluster 생성 및 등록
			return createKubesprayCluster(clusterDto);
		} else {
			// aks, eks.. - not supported
			return null;
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
		clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.FINISHED.name());
		
		clusterDomainService.register(clusterEntity);
		
		// sync k8s cluster
		log.info("Cluster Synchronization started.");
		syncCluster(clusterId, clusterEntity.getClusterIdx());
		
		return null;
	}
	
	/**
	 * (Kubespray) Cluster 등록
	 * 
	 * @param clusterDto
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	private Long createKubesprayCluster(ClusterDto.Form clusterDto) throws Exception {
		// callbackUrl
		String callbackUrl  = "/api/v1/work-job/callback";
		
		// db - create work job
		WorkJobDto workJobDto = WorkJobDto.builder().
				workJobTarget(clusterDto.getClusterName())
				.workJobType(WorkJobType.CLUSTER_CREATE)
				.workJobStatus(WorkJobStatus.RUNNING)
				.workJobStartAt(DateUtil.currentDateTime())
				.workSyncYn("N")
				.build();
		
		Long workJobIdx = workJobService.registerWorkJob(workJobDto);
		log.info("[createKubesprayCluster] work job idx : {}", workJobIdx);
		
		// db - get kubespray version
		SettingEntity settingParam = new SettingEntity();
		settingParam.setSettingType(SettingEntity.TYPE_TOOLS);
		settingParam.setSettingKey(SettingEntity.KEY_TOOLS_KUBESPRAY);
		
		SettingEntity settingEntity = settingDomainService.getSetting(settingParam);
		
		String kubesprayVersion = settingEntity.getSettingValue();
		log.info("[createKubesprayCluster] kubespray version : {}", kubesprayVersion);
		
		// db - insert cluster
		ClusterEntity clusterEntity = ClusterDtoMapper.INSTANCE.toEntity(clusterDto);
		clusterEntity.setCreatedAt(DateUtil.currentDateTime());
		clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.READY.name());
		
		clusterDomainService.register(clusterEntity);
		log.info("[createKubesprayCluster] register cluster : {}", clusterEntity.toString());
		
		// db - insert cluster's node
		for (ClusterDto.Node node : clusterDto.getNodes()) {
			NodeEntity nodeEntity = new NodeEntity();
			nodeEntity.setName(node.getName());
			nodeEntity.setIp(node.getIp());
			nodeEntity.setRole(new ObjectMapper().writeValueAsString(node.getNodeTypes()));
			nodeEntity.setCluster(clusterEntity);
			
			nodeDomainService.register(nodeEntity);
		}
		
		// kubespray - create cluster
		ClusterCloudDto clusterCloudDto = ClusterDtoMapper.INSTANCE.toClusterCloudDto(clusterDto);
		clusterCloudDto.setUserName("root");
		clusterCloudDto.setKubesprayVersion(kubesprayVersion);
		clusterCloudDto.setCallbackUrl(portalBackendServiceUrl + ":" + portalBackendServicePort + callbackUrl);
		clusterCloudDto.setWorkJobIdx(workJobIdx);
		{
			// db - update work job
			Map<String, Object> workJobDataRequest	= new HashMap<>();
			workJobDataRequest.put(WorkJobData.BODY.name(), clusterCloudDto);
			
			String workJobRequest = new ObjectMapper().writeValueAsString(workJobDataRequest);
			log.info("[createKubesprayCluster] work job request : {}", workJobRequest);
			
			workJobDto.setWorkJobIdx(workJobIdx);
			workJobDto.setWorkJobDataRequest(workJobRequest);
			workJobDto.setWorkJobReferenceIdx(clusterEntity.getClusterIdx());
			
			workJobService.updateWorkJob(workJobDto);
		}
		
		boolean isCreated = clusterCloudService.createCluster(clusterCloudDto);
		if (!isCreated) {
			throw new PortalException("Cluster creation failed");
		}
		
		return workJobIdx;
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
		
		List<NodeEntity> availableMasterNodes = masterNodes.stream().filter(n -> ("true").equals(n.getStatus())).collect(Collectors.toList());
		List<NodeEntity> availableworkerNodes = workerNodes.stream().filter(n -> ("true").equals(n.getStatus())).collect(Collectors.toList());
		
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
