package kr.co.strato.portal.cluster.service;

import java.util.ArrayList;
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

import io.fabric8.kubernetes.api.model.NodeCondition;
import kr.co.strato.adapter.cloud.cluster.model.ClusterCloudDto;
import kr.co.strato.adapter.cloud.cluster.service.ClusterCloudService;
import kr.co.strato.adapter.k8s.cluster.model.ClusterAdapterDto;
import kr.co.strato.adapter.k8s.cluster.model.ClusterInfoAdapterDto;
import kr.co.strato.adapter.k8s.cluster.service.ClusterAdapterService;
import kr.co.strato.adapter.k8s.node.service.NodeAdapterService;
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
import kr.co.strato.domain.work.model.WorkJobEntity;
import kr.co.strato.domain.work.service.WorkJobDomainService;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.cluster.model.ClusterDto.Node;
import kr.co.strato.portal.cluster.model.ClusterDto.Summary;
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
	ClusterNodeService clusterNodeService;
	
	@Autowired
	WorkJobService workJobService;
	
	@Autowired
	WorkJobDomainService workJobDomainService;
	
	@Autowired
	ClusterCloudService clusterCloudService;
	
	@Autowired
	SettingDomainService settingDomainService;
	
	@Autowired
	ClusterSyncService clusterSyncService;
	
	@Autowired
	NodeAdapterService nodeAdapterService;
	
	@Value("${portal.backend.service.url}")
	String portalBackendServiceUrl;
	
	@Value("${server.port}")
	Integer portalBackendServicePort;
	
	// callbackUrl - work job
	String portalBackendServiceCallbackUrl  = "/api/v1/work-job/callback";

			
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
			return createK8sCluster(clusterDto);
		} else if (provisioningType == ProvisioningType.KUBESPRAY) {
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
		
		// TODO : 이건 목록/상세 정보 조회 시 실시간으로 가져와야 할 듯함.. 
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
		clusterSyncService.syncCluster(clusterId, clusterEntity.getClusterIdx());
		
		return null;
	}
	
	/**
	 * Kubespray 버전 정보(Setting)
	 * TODO : 여기서 정보를 얻는게 맞을지 고민 필요
	 * 
	 * @return
	 * @throws Exception
	 */
	private String getKubesprayVersionFromSetting() throws Exception {
		SettingEntity settingParam = new SettingEntity();
		settingParam.setSettingType(SettingEntity.TYPE_TOOLS);
		settingParam.setSettingKey(SettingEntity.KEY_TOOLS_KUBESPRAY);
		
		SettingEntity settingEntity = settingDomainService.getSetting(settingParam);
		if (settingEntity != null) {
			return settingEntity.getSettingValue();
		}
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
		String kubesprayVersion = getKubesprayVersionFromSetting();
		log.info("[createKubesprayCluster] kubespray version : {}", kubesprayVersion);
		
		// db - insert cluster
		ClusterEntity clusterEntity = ClusterDtoMapper.INSTANCE.toEntity(clusterDto);
		clusterEntity.setCreatedAt(DateUtil.currentDateTime());
		clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.READY.name());
		
		clusterDomainService.register(clusterEntity);
		log.info("[createKubesprayCluster] register cluster : {}", clusterEntity.toString());
		
		// db - insert cluster's node
		/*
		for (ClusterDto.Node node : clusterDto.getNodes()) {
			NodeEntity nodeEntity = new NodeEntity();
			nodeEntity.setName(node.getName());
			nodeEntity.setIp(node.getIp());
			nodeEntity.setRole(new ObjectMapper().writeValueAsString(node.getNodeTypes()));
			nodeEntity.setCluster(clusterEntity);
			
			nodeDomainService.register(nodeEntity);
		}
		*/
		
		// kubespray - create cluster
		ClusterCloudDto clusterCloudDto = ClusterDtoMapper.INSTANCE.toClusterCloudDto(clusterDto);
		clusterCloudDto.setKubesprayVersion(kubesprayVersion);
		clusterCloudDto.setCallbackUrl(portalBackendServiceUrl + ":" + portalBackendServicePort + portalBackendServiceCallbackUrl);
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
		
		ProvisioningType provisioningType = ClusterEntity.ProvisioningType.valueOf(clusterEntity.getProvisioningType());
		if (provisioningType == ProvisioningType.KUBECONFIG) {
			return updateK8sCluster(clusterEntity, clusterDto);
		} else if (provisioningType == ProvisioningType.KUBESPRAY) {
			return updateKubesprayCluster(clusterEntity, clusterDto);
		}
		
		return null;
	}

	private Long updateK8sCluster(ClusterEntity clusterEntity, ClusterDto.Form clusterDto) throws Exception {
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
		//clusterEntity.setClusterName(clusterDto.getClusterName());
		//clusterEntity.setKubeConfig(clusterDto.getKubeConfig());
		clusterEntity.setDescription(clusterDto.getDescription());
		// TODO : update_user information
		
		clusterDomainService.update(clusterEntity);
		
		return null;
	}
	
	@Transactional(rollbackFor = Exception.class)
	private Long updateKubesprayCluster(ClusterEntity clusterEntity, ClusterDto.Form clusterDto) throws Exception {
		// db - create work job
		WorkJobDto workJobDto = WorkJobDto.builder().
				workJobTarget(clusterDto.getClusterName())
				.workJobType(WorkJobType.CLUSTER_SCALE)
				.workJobStatus(WorkJobStatus.RUNNING)
				.workJobStartAt(DateUtil.currentDateTime())
				.workSyncYn("N")
				.build();
		
		Long workJobIdx = workJobService.registerWorkJob(workJobDto);
		log.info("[updateKubesprayCluster] work job idx : {}", workJobIdx);
		
		// db - get kubespray version
		String kubesprayVersion = getKubesprayVersionFromSetting();
		log.info("[updateKubesprayCluster] kubespray version : {}", kubesprayVersion);
		
		// db - update cluster
		clusterEntity.setDescription(clusterDto.getDescription());
		// TODO : update_user information
		
		clusterDomainService.update(clusterEntity);
		log.info("[updateKubesprayCluster] update cluster : {}", clusterEntity.toString());
		
		// kubespray - scale-in/out cluster
		boolean isScaleOut = false;
		if (clusterDto.getNodes().size() >= clusterDto.getOriginalNodes().size()) { // scale-out
			isScaleOut = true;
		} else { // scale-in
			// copy from original nodes
			ArrayList<ClusterDto.Node> copyNodes = new ArrayList<>();
			ArrayList<ClusterDto.Node> originalNodes = clusterDto.getOriginalNodes();
			originalNodes.forEach(o -> {
				ClusterDto.Node n = new ClusterDto.Node();
				n.setName(o.getName());
				n.setIp(o.getIp());
				
				ArrayList<String> roles = new ArrayList<>();
				for (String role : o.getNodeTypes()) {
					roles.add(role);
				}
				n.setNodeTypes(roles);
				
				copyNodes.add(n);
			});
			
			// find to deleted nodes 
			for (int i = 0; i < originalNodes.size(); i++) {
				for (ClusterDto.Node node : clusterDto.getNodes()) {
					// find same node : based on node's ip
					if (originalNodes.get(i).getIp().equals(node.getIp())) {
						originalNodes.remove(i);
					}
				}
			}
			
			ArrayList<String> deletedNodes = new ArrayList<>();
			for (ClusterDto.Node node : originalNodes) {
				deletedNodes.add(node.getName());
			}
			
			// set node
			clusterDto.setRemoveNodes(deletedNodes);
			clusterDto.setNodes(copyNodes);
		}
		
		ClusterCloudDto clusterCloudDto = ClusterDtoMapper.INSTANCE.toClusterCloudDto(clusterDto);
		clusterCloudDto.setKubesprayVersion(kubesprayVersion);
		clusterCloudDto.setCallbackUrl(portalBackendServiceUrl + ":" + portalBackendServicePort + portalBackendServiceCallbackUrl);
		clusterCloudDto.setWorkJobIdx(workJobIdx);
		{
			// db - update work job
			Map<String, Object> workJobDataRequest	= new HashMap<>();
			workJobDataRequest.put(WorkJobData.BODY.name(), clusterCloudDto);
			
			String workJobRequest = new ObjectMapper().writeValueAsString(workJobDataRequest);
			log.info("[updateKubesprayCluster] work job request : {}", workJobRequest);
			
			workJobDto.setWorkJobIdx(workJobIdx);
			workJobDto.setWorkJobDataRequest(workJobRequest);
			workJobDto.setWorkJobReferenceIdx(clusterEntity.getClusterIdx());
			
			workJobService.updateWorkJob(workJobDto);
		}
		
		boolean isUpdated = isScaleOut ? clusterCloudService.scaleOutCluster(clusterCloudDto) : clusterCloudService.scaleInCluster(clusterCloudDto);
		if (!isUpdated) {
			throw new PortalException("Cluster scale failed");
		}
		
		return workJobIdx;
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
		
		// convert from entity to dto
		ClusterDto.Detail detail = ClusterDtoMapper.INSTANCE.toDetail(clusterEntity);
		
		// 1. add info by provisioning type
		Long workJobIdx = null;
		
		ProvisioningType provisioningType = ClusterEntity.ProvisioningType.valueOf(clusterEntity.getProvisioningType());
		if (provisioningType == ProvisioningType.KUBECONFIG) {
			addK8sClusterInfo(clusterEntity, detail);
		} else if (provisioningType == ProvisioningType.KUBESPRAY) {
			addKubesprayClusterInfo(clusterEntity, detail);
			
			// get workJobIdx
			WorkJobEntity workJobEntity = workJobDomainService.getWorkJobByWorkJobTypeAndReferenceIdx(WorkJobType.CLUSTER_CREATE.name(), clusterIdx);
			log.debug("[getCluster] workJobEntity = {}", workJobEntity);
			
			if (workJobEntity != null) {
				workJobIdx = workJobEntity.getWorkJobIdx();
			}
		}
		
		// 2. add info by workJobIdx
		detail.setWorkJobIdx(workJobIdx);
		
		return detail;
	}
	
	private void addK8sClusterInfo(ClusterEntity clusterEntity, ClusterDto.Detail detail) throws Exception {
		// original code
		ClusterAdapterDto cluster = clusterAdapterService.getCluster(clusterEntity.getClusterId());
		log.debug("[addK8sClusterInfo] kube config : {}", cluster.getConfigContents());
		
		detail.setKubeConfig(cluster.getConfigContents());
		// temporary code
		//detail.setKubeConfig(clusterEntity.getKubeConfig());
	}
	
	@SuppressWarnings("unchecked")
	private void addKubesprayClusterInfo(ClusterEntity clusterEntity, ClusterDto.Detail detail) throws Exception {
		ArrayList<ClusterDto.Node> results = new ArrayList<>();
		
		ObjectMapper mapper = new ObjectMapper();
		
		for (NodeEntity n : clusterEntity.getNodes()) {
			Node node = new ClusterDto.Node();
			node.setName(n.getName());
			node.setIp(n.getIp());
			node.setNodeTypes(mapper.readValue(n.getRole(), ArrayList.class));
			
			results.add(node);
		}
		
		detail.setNodes(results);
	}
	

	/**
	 * Cluster 상세 조회(요약)
	 * 
	 * @param clusterIdx
	 * @return
	 * @throws Exception
	 */
	public ClusterDto.Summary getClusterSummary(Long clusterIdx) throws Exception {
		// db - get cluster
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
				
		// k8s - get node
		List<io.fabric8.kubernetes.api.model.Node> k8sNodes = nodeAdapterService.getNodeList(clusterEntity.getClusterId());
		
		PageRequest pageRequest = new PageRequest();
		pageRequest.setSize(Integer.MAX_VALUE);
		
		// node
		List<NodeEntity> nodes = nodeDomainService.getNodeList(pageRequest.of(), clusterIdx, null).getContent();
		
		// namespace
		List<NamespaceEntity> namespaces = namespaceDomainService.getNamespaceList(pageRequest.of(), clusterIdx, null).getContent();
		
		// pod
		List<PodEntity> pods = podDomainService.getPods(pageRequest.of(), null, clusterIdx, null, null).getContent();
		
		// TODO : pvc list
		// not implemented
		
		log.debug("[Cluster Summary] nodes/namespaces/pods size = {}/{}/{}", nodes.size(), namespaces.size(), pods.size());
		
		List<NodeEntity> masterNodes = nodes.stream().filter(n -> n.getRole().contains("master")).collect(Collectors.toList());
		List<NodeEntity> workerNodes = nodes.stream().filter(n -> n.getRole().contains("worker") || "[]".equals(n.getRole())).collect(Collectors.toList());
		
		int availableMasterCount = 0;
		for (NodeEntity n : masterNodes) {
			for (io.fabric8.kubernetes.api.model.Node k8sNode : k8sNodes) {
				String uid = k8sNode.getMetadata().getUid();
				List<NodeCondition> conditions = k8sNode.getStatus().getConditions();
				boolean status = conditions.stream().filter(condition -> condition.getType().equals("Ready"))
						.map(condition -> condition.getStatus().equals("True")).findFirst().orElse(false);
				
				if (n.getUid().equals(uid) && status) {
					availableMasterCount++;
				}
			}
		}
		
		int availableWorkerCount = 0;
		for (NodeEntity n : workerNodes) {
			for (io.fabric8.kubernetes.api.model.Node k8sNode : k8sNodes) {
				String uid = k8sNode.getMetadata().getUid();
				List<NodeCondition> conditions = k8sNode.getStatus().getConditions();
				boolean status = conditions.stream().filter(condition -> condition.getType().equals("Ready"))
						.map(condition -> condition.getStatus().equals("True")).findFirst().orElse(false);
				
				if (n.getUid().equals(uid) && status) {
					availableWorkerCount++;
				}
			}
		}
		
		log.debug("[Cluster Summary] masterNodes/workerNodes size = {}/{}", masterNodes.size(), workerNodes.size());
		log.debug("[Cluster Summary] availableMasterCount/availableWorkerCount = {}/{}", availableMasterCount, availableWorkerCount);
		
		// Master/Worker 수량
		int masterCount = masterNodes.size();
		int workerCount = workerNodes.size();
		
		// Master/Worker 가동률
		float availableMasterPercent = masterCount > 0 ? (availableMasterCount * 100 / masterCount) : 0;
		float availableWorkerPercent = workerCount > 0 ? (availableWorkerCount * 100 / workerCount) : 0;
		
		Summary summary = new ClusterDto.Summary();
		summary.setMasterCount(masterCount);
		summary.setWorkerCount(workerCount);
		summary.setAvailableMasterPercent(availableMasterPercent);
		summary.setAvailableWorkerPercent(availableWorkerPercent);
		summary.setNamespaceCount(namespaces.size());
		summary.setPodCount(pods.size());
		summary.setPvcCount(0); // TODO : pvc count
		
		return summary;
	}
	
	/**
	 * Cluster 삭제
	 * 
	 * @param clusterIdx
	 * @throws Exception
	 */
	public Long deleteCluster(Long clusterIdx) throws Exception {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		
		ProvisioningType provisioningType = ClusterEntity.ProvisioningType.valueOf(clusterEntity.getProvisioningType());
		if (provisioningType == ProvisioningType.KUBECONFIG) {
			return deleteK8sCluster(clusterEntity);
		} else if (provisioningType == ProvisioningType.KUBESPRAY) {
			return deleteKubesprayCluster(clusterEntity);
		}
		
		return null;
	}
	
	/**
	 * (K8s) Cluster 삭제
	 * 
	 * @param clusterEntity
	 * @throws Exception
	 */
	private Long deleteK8sCluster(ClusterEntity clusterEntity) throws Exception {
		boolean isDeleted = clusterAdapterService.deleteCluster(clusterEntity.getClusterId());
		if (!isDeleted) {
			throw new PortalException("Cluster deletion failed");
		}
		
		clusterDomainService.delete(clusterEntity);
		
		return null;
	}
	
	/**
	 * (Kubespray) Cluster 삭제
	 * 
	 * @param clusterEntity
	 * @throws Exception
	 */
	private Long deleteKubesprayCluster(ClusterEntity clusterEntity) throws Exception {
		// db - create work job
		WorkJobDto workJobDto = WorkJobDto.builder().
				workJobTarget(clusterEntity.getClusterName())
				.workJobType(WorkJobType.CLUSTER_DELETE)
				.workJobStatus(WorkJobStatus.RUNNING)
				.workJobStartAt(DateUtil.currentDateTime())
				.workSyncYn("N")
				.build();
		
		Long workJobIdx = workJobService.registerWorkJob(workJobDto);
		log.info("[deleteKubesprayCluster] work job idx : {}", workJobIdx);
		
		// db - get kubespray version
		String kubesprayVersion = getKubesprayVersionFromSetting();
		log.info("[deleteKubesprayCluster] kubespray version : {}", kubesprayVersion);
		
		// kubespray - create cluster
		ClusterCloudDto clusterCloudDto = ClusterDtoMapper.INSTANCE.toClusterCloudDto(clusterEntity);
		clusterCloudDto.setKubesprayVersion(kubesprayVersion);
		clusterCloudDto.setCallbackUrl(portalBackendServiceUrl + ":" + portalBackendServicePort + portalBackendServiceCallbackUrl);
		clusterCloudDto.setWorkJobIdx(workJobIdx);
		{
			// db - update work job
			Map<String, Object> workJobDataRequest	= new HashMap<>();
			workJobDataRequest.put(WorkJobData.BODY.name(), clusterCloudDto);
			
			String workJobRequest = new ObjectMapper().writeValueAsString(workJobDataRequest);
			log.info("[deleteKubesprayCluster] work job request : {}", workJobRequest);
			
			workJobDto.setWorkJobIdx(workJobIdx);
			workJobDto.setWorkJobDataRequest(workJobRequest);
			workJobDto.setWorkJobReferenceIdx(clusterEntity.getClusterIdx());
			
			workJobService.updateWorkJob(workJobDto);
		}
		
		boolean isDeleted = clusterCloudService.removeCluster(clusterCloudDto);
		if (!isDeleted) {
			throw new PortalException("Cluster deletion failed");
		}
		
		return workJobIdx;
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
