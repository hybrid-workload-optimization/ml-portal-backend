package kr.co.strato.portal.cluster.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import kr.co.strato.adapter.k8s.cluster.model.ClusterHealthAdapterDto;
import kr.co.strato.adapter.k8s.cluster.model.ClusterInfoAdapterDto;
import kr.co.strato.adapter.k8s.cluster.service.ClusterAdapterService;
import kr.co.strato.adapter.k8s.node.service.NodeAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.model.ClusterEntity.ProvisioningStatus;
import kr.co.strato.domain.cluster.model.ClusterEntity.ProvisioningType;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.kubeconfig.model.KubeconfigEntity;
import kr.co.strato.domain.kubeconfig.service.KubeconfigDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.node.service.NodeDomainService;
import kr.co.strato.domain.persistentVolume.model.PersistentVolumeEntity;
import kr.co.strato.domain.persistentVolume.service.PersistentVolumeDomainService;
import kr.co.strato.domain.persistentVolumeClaim.service.PersistentVolumeClaimDomainService;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.pod.service.PodDomainService;
import kr.co.strato.domain.project.model.ProjectClusterEntity;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectClusterDomainService;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.domain.setting.model.SettingEntity;
import kr.co.strato.domain.setting.service.SettingDomainService;
import kr.co.strato.domain.storageClass.model.StorageClassEntity;
import kr.co.strato.domain.storageClass.service.StorageClassDomainService;
import kr.co.strato.domain.work.model.WorkJobEntity;
import kr.co.strato.domain.work.service.WorkJobDomainService;
import kr.co.strato.global.error.exception.BadRequestException;
import kr.co.strato.global.error.exception.DuplicateResourceNameException;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.cluster.model.ClusterDto.Node;
import kr.co.strato.portal.cluster.model.ClusterDto.Summary;
import kr.co.strato.portal.cluster.model.ClusterDtoMapper;
import kr.co.strato.portal.cluster.model.ClusterNodeDto;
import kr.co.strato.portal.cluster.model.PublicClusterDto;
import kr.co.strato.portal.ml.service.MLClusterAPIAsyncService;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.service.UserService;
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
	
	@Autowired
	PublicClusterService publicClusterService;
	
	@Autowired
	ProjectDomainService projectDomainService;
	
	@Autowired
	PersistentVolumeDomainService pvDomainService;
	
	@Autowired
	StorageClassDomainService storageClassDomainService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	MLClusterAPIAsyncService mlClusterService;
	
	@Autowired
	KubeconfigDomainService kubeconfigDomainService;
	
	
	@Value("${portal.backend.service.url}")
	String portalBackendServiceUrl;
	
	@Value("${server.port}")
	Integer portalBackendServicePort;
	
	@Autowired
	ProjectClusterDomainService projectClusterDomainService;
	
	// callbackUrl - work job
	String portalBackendServiceCallbackUrl  = "/api/v1/work-job/callback";

			
	/**
	 * Cluster 목록 조회
	 * 
	 * @param pageable
	 * @return
	 * @throws Exception
	 */
	public Page<ClusterDto.List> getClusterList(UserDto loginUser, Pageable pageable) throws Exception {
		Page<ClusterEntity> clusterPage = clusterDomainService.getList(loginUser, pageable);
		
		List<ClusterDto.List> clusterList = clusterPage.getContent().stream()
				.map(c -> ClusterDtoMapper.INSTANCE.toList(c))
				.collect(Collectors.toList());
		
		
		//Health 정보 설정.
		for(ClusterDto.List item : clusterList) {
			Long kubeConfigId = item.getClusterId();
			String pStatus = item.getProvisioningStatus();
			
			log.info("Get cluster Health. clusterIdx: {}", item.getClusterIdx());
			ClusterHealthAdapterDto health = getClusterStatus(kubeConfigId, pStatus);
			
			item.setStatus(health.getHealth());
			item.setProblem((ArrayList<String>)health.getProblem());
		}
		return new PageImpl<>(clusterList, pageable, clusterPage.getTotalElements());
	}
	
	
	public ClusterDto.Status getClusterStatus(Long clusterIdx) {
		ClusterDto.Status status = new ClusterDto.Status();
		ClusterEntity clusterEntity = clusterDomainService.getNullable(clusterIdx);
		if(clusterEntity != null) {
			Long kubeConfigId = clusterEntity.getClusterId();
			String pStatus = clusterEntity.getProvisioningStatus();
			
			ClusterHealthAdapterDto health = getClusterStatus(kubeConfigId, pStatus);
			
			status.setNodeCount(clusterEntity.getNodeCount());
			status.setClusterIdx(clusterIdx);
			status.setStatus(health.getHealth());
			status.setProblem(status.getProblem());
		} else {
			status.setStatus("deleted");
		}
		return status;
	}
	
	public ClusterHealthAdapterDto getClusterStatus(Long kubeConfigId, String pStatus) {
		ClusterHealthAdapterDto health = new ClusterHealthAdapterDto();
		if(pStatus != null) {
			if(pStatus.equals(ProvisioningStatus.FINISHED.toString())) {
				if(kubeConfigId != null) {
					try {
						health = clusterAdapterService.getClusterHealthInfo(kubeConfigId);
					} catch(Exception e) {
						// Health 정보를 가져올 수 없는 경우.
						health = new ClusterHealthAdapterDto();
						health.setHealth("Unhealthy");
						health.addProbleam("Could not get cluster information.");
					}
				}
				
			} else if(pStatus.equals(ProvisioningStatus.READY.toString())) {
				//배포 준비
				health.setHealth("Waiting");
			} else if( pStatus.equals(ProvisioningStatus.STARTED.toString())) {
				//배포중
				health.setHealth("Deploying");
			} else if(pStatus.equals(ProvisioningStatus.DELETING.toString())) {
				//클러스터 삭제 중
				health.setHealth("Deleting");
			} else if(pStatus.equals(ProvisioningStatus.SCALE_OUT.toString())) {
				//클러스터 삭제 중
				health.setHealth("Scale out");
			} else if(pStatus.equals(ProvisioningStatus.SCALE_IN.toString())) {
				//클러스터 삭제 중
				health.setHealth("Scale in");
			} else if(pStatus.equals(ProvisioningStatus.FAILED.toString())) {
				//배포 실패
				health.setHealth("Fail");
				health.addProbleam("Cluster deployment failed.");
			}
		} else {
			health.setHealth("Error");
			health.addProbleam("Cluster deployment information does not exist.");
		}
		
		
		if(health.getHealth() == null) {
			health.setHealth("Error");
			health.addProbleam("Unknown Error.");
		}
		return health;
	}
	
	/**
	 * Cluster 등록
	 * 
	 * @param clusterDto
	 * @return
	 * @throws Exception
	 */
	public Long createCluster(ClusterDto.Form clusterDto, UserDto loginUser) throws Exception {
		ProvisioningType provisioningType = ClusterEntity.ProvisioningType.valueOf(clusterDto.getProvisioningType());
		
		Long projectIdx = clusterDto.getProjectIdx();
		Long clusterIdx = null;
		if (provisioningType == ProvisioningType.KUBECONFIG) {
			clusterIdx = createK8sCluster(clusterDto, loginUser);
		} else if (provisioningType == ProvisioningType.KUBESPRAY) {
			clusterIdx = createKubesprayCluster(clusterDto, loginUser);
		} else {
			// aks, eks.. - not supported
		}
		
		//서비스그룹(프로젝트)에 클러스터 추가
		if(projectIdx != null && clusterIdx != null) {
			ProjectClusterEntity projectClusterEntity = ProjectClusterEntity.builder()
    				.projectIdx(projectIdx)
    				.clusterIdx(clusterIdx)
    				.addedAt(DateUtil.currentDateTime())
    				.build();
            projectClusterDomainService.createProjectCluster(projectClusterEntity);
		}
		
		return clusterIdx;
	}
	
	/**
	 * (K8s) Cluster 등록
	 * 
	 * @param clusterDto
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	private Long createK8sCluster(ClusterDto.Form clusterDto, UserDto loginUser) throws Exception {		
		KubeconfigEntity entity = KubeconfigEntity.builder()
				.configContents(clusterDto.getKubeConfig())
				.provider(clusterDto.getProvider())
				.regDate(DateUtil.currentDateTime())
				.modDate(DateUtil.currentDateTime())
				.build();
		entity = kubeconfigDomainService.save(entity);
		
		Long clusterId = entity.getKubeConfigId();
		String clusterHealth = null;
		String kubeleteVersion = null;
		String clusterProblemString = null;
		
		try {
			
			// TODO : 이건 목록/상세 정보 조회 시 실시간으로 가져와야 할 듯함.. 
			// k8s - get cluster's information(health + version)
			ClusterInfoAdapterDto clusterInfo = clusterAdapterService.getClusterInfo(clusterId);
			clusterHealth = clusterInfo.getClusterHealth().getHealth();
			List<String> clusterProblem	= clusterInfo.getClusterHealth().getProblem();
			// for test
			//List<String> clusterProblem	= Arrays.asList("problem1", "problem12", "problem3");
			
			ObjectMapper mapper = new ObjectMapper();
			clusterProblemString = mapper.writeValueAsString(clusterProblem);
			kubeleteVersion = clusterInfo.getKubeletVersion();
		} catch (Exception e) {
			log.error("", e);
		}
		
		
		// db - insert cluster
		ClusterEntity clusterEntity = ClusterDtoMapper.INSTANCE.toEntity(clusterDto);
		clusterEntity.setClusterId(clusterId);
		clusterEntity.setStatus(clusterHealth);
		clusterEntity.setProblem(clusterProblemString);
		clusterEntity.setProviderVersion(kubeleteVersion);
		clusterEntity.setCreatedAt(DateUtil.currentDateTime());
		clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.FINISHED.name());
		clusterEntity.setCreateUserId(loginUser.getUserId());
		clusterEntity.setCreateUserName(loginUser.getUserName());

		
		clusterDomainService.register(clusterEntity);
		
		// sync k8s cluster
		log.info("Cluster Synchronization started.");
		clusterSyncService.syncCluster(clusterId, clusterEntity.getClusterIdx());
		
		return clusterEntity.getClusterIdx();
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
	private Long createKubesprayCluster(ClusterDto.Form clusterDto, UserDto loginUser) throws Exception {
		String loginUserId = loginUser.getUserId();
		
		boolean masterCheck = masterCheck(clusterDto.getNodes());
		if(!masterCheck) {
			//마스터가 홀수로 지정되지 않은 경우 에러 발생.
			log.error("마스터 노드가 짝수로 설정 되었습니다.");
			
			throw new BadRequestException();
		}
		
		boolean isDup = duplicateCheck(clusterDto.getNodes());
		if(isDup) {
			log.error("중복되는 노드 정보가 존재합니다.");
			
			//리소스 중복 에러 발생.
			throw new DuplicateResourceNameException();
		}
		
		// db - get kubespray version
		String kubesprayVersion = getKubesprayVersionFromSetting();
		log.info("[createKubesprayCluster] kubespray version : {}", kubesprayVersion);
		
		// db - insert cluster
		ClusterEntity clusterEntity = ClusterDtoMapper.INSTANCE.toEntity(clusterDto);
		clusterEntity.setCreatedAt(DateUtil.currentDateTime());
		clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.READY.name());
		clusterEntity.setCreateUserId(loginUserId);
		clusterEntity.setCreateUserName(loginUser.getUserName());
		
		clusterDomainService.register(clusterEntity);
		log.info("[createKubesprayCluster] register cluster : {}", clusterEntity.toString());
		
		
		// db - create work job
		WorkJobDto workJobDto = WorkJobDto.builder().
				workJobTarget(clusterDto.getClusterName())
				.workJobType(WorkJobType.CLUSTER_CREATE)
				.workJobStatus(WorkJobStatus.WAITING)
				.workJobStartAt(DateUtil.currentDateTime())
				.workSyncYn("N")
				.createUserId(loginUserId)
				.createUserName(loginUser.getUserName())
				.build();
		
		Long workJobIdx = workJobService.registerWorkJob(workJobDto);
		log.info("[createKubesprayCluster] work job idx : {}", workJobIdx);
				
		
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
	public Long updateCluster(Long clusterIdx, ClusterDto.Form clusterDto, UserDto loginUser) throws Exception {
		// db - get cluster
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		
		/*
		ProvisioningType provisioningType = ClusterEntity.ProvisioningType.valueOf(clusterEntity.getProvisioningType());
		if (provisioningType == ProvisioningType.KUBECONFIG) {
			return updateK8sCluster(clusterEntity, clusterDto, loginUser);
		} else if (provisioningType == ProvisioningType.KUBESPRAY) {
			
		}
		*/
		//업데이트는 모두 Kubespray로 통일.
		return updateKubesprayCluster(clusterEntity, clusterDto, loginUser);
	}

	private Long updateK8sCluster(ClusterEntity clusterEntity, ClusterDto.Form clusterDto, UserDto loginUser) throws Exception {
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
		clusterEntity.setUpdateUserId(loginUser.getUserId());
		clusterEntity.setCreateUserName(loginUser.getUserName());
		
		clusterDomainService.update(clusterEntity);
		
		return null;
	}
	
	@Transactional(rollbackFor = Exception.class)
	private Long updateKubesprayCluster(ClusterEntity clusterEntity, ClusterDto.Form clusterDto, UserDto loginUser) throws Exception {
		// db - create work job
		WorkJobDto workJobDto = WorkJobDto.builder().
				workJobTarget(clusterDto.getClusterName())
				.workJobType(WorkJobType.CLUSTER_SCALE)
				.workJobStatus(WorkJobStatus.WAITING)
				.workJobStartAt(DateUtil.currentDateTime())
				.createUserId(loginUser.getUserId())
				.createUserName(loginUser.getUserName())
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
		
		
		
		
		int changeSize = clusterDto.getNodes().size();
		int originalSize = clusterDto.getOriginalNodes().size();
		
		if(changeSize != originalSize) {
			//스케일 조정이 필요한 경우.
			
			boolean masterCheck = masterCheck(clusterDto.getNodes());
			if(!masterCheck) {
				//마스터가 홀수로 지정되지 않은 경우 에러 발생.
				log.error("마스터 노드가 짝수로 설정 되었습니다.");
				
				throw new BadRequestException();
			}
			
			//중복되는 노드 정보가 존재하는지 채크
			boolean isDup = duplicateCheck(clusterDto.getNodes());
			if(isDup) {
				log.error("중복되는 노드 정보가 존재합니다.");
				
				//리소스 중복 에러 발생.
				throw new DuplicateResourceNameException();
			}
			
			boolean isScaleOut = false;
			
			if(changeSize > originalSize) {
				//Scale out
				clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.SCALE_OUT.name());
				isScaleOut = true;
			} else if(changeSize < originalSize) {
				//Scale in
				clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.SCALE_IN.name());
				
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
			
		}
		
		
		clusterDomainService.update(clusterEntity);
		log.info("[updateKubesprayCluster] update cluster : {}", clusterEntity.toString());
		
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
		return getCluster(detail, clusterEntity);
	}
	
	public ClusterDto.Detail getClusterForMonitoring(Long clusterIdx) throws Exception {
		// cluster
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		
		// convert from entity to dto
		ClusterDto.DetailForMonitoring detail = ClusterDtoMapper.INSTANCE.toDetailForMonitoring(clusterEntity);
		return getCluster(detail, clusterEntity);
	}	
	
	public ClusterDto.Detail getClusterForDevOps(Long clusterIdx) throws Exception {
		// cluster
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		
		// convert from entity to dto
		ClusterDto.DetailForDevOps detail = ClusterDtoMapper.INSTANCE.toDetailForDevOps(clusterEntity);
		return getCluster(detail, clusterEntity);
	}	
	
	
	public ClusterDto.Detail getCluster(ClusterDto.Detail detail, ClusterEntity clusterEntity) throws Exception {
		Long clusterIdx = clusterEntity.getClusterIdx();
		
		// 1. add info by provisioning type
		Long workJobIdx = null;
		
		String provisionType = clusterEntity.getProvisioningType();
		if(provisionType != null) {
			ProvisioningType provisioningType = ClusterEntity.ProvisioningType.valueOf(provisionType);
			if (provisioningType == ProvisioningType.KUBECONFIG) {
				try {
					addKubesprayClusterInfo(clusterEntity, detail);
					addK8sClusterInfo(clusterEntity, detail);
				} catch (Exception e) {
					log.error("", e);
				}
				
			} else if (provisioningType == ProvisioningType.KUBESPRAY) {
				addKubesprayClusterInfo(clusterEntity, detail);
				
				// get workJobIdx
				WorkJobEntity workJobEntity = workJobDomainService.getWorkJobByWorkJobTypeAndReferenceIdx(WorkJobType.CLUSTER_CREATE.name(), clusterIdx);
				log.debug("[getCluster] workJobEntity = {}", workJobEntity);
				
				if (workJobEntity != null) {
					workJobIdx = workJobEntity.getWorkJobIdx();
				}
			}
		}
		
		
		// 2. add info by workJobIdx
		detail.setWorkJobIdx(workJobIdx);
		
		ClusterHealthAdapterDto health = getClusterStatus(clusterEntity.getClusterId(), clusterEntity.getProvisioningStatus());
		
		detail.setStatus(health.getHealth());
		detail.setProblem((ArrayList<String>)health.getProblem());
		
		ProjectEntity projectEntity =  projectDomainService.getProjectDetailByClusterId(clusterIdx);
		if(projectEntity != null) {
			//프로젝트에 소속된 클러스터는 권한 관리를 위해 프로젝트 아이디를 넣어 보냄.
			detail.setProjectIdx(projectEntity.getId());
		}
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
				
		
		
		PageRequest pageRequest = new PageRequest();
		pageRequest.setSize(Integer.MAX_VALUE);
		
		// node
		List<NodeEntity> nodes = nodeDomainService.getNodeList(pageRequest.of(), clusterIdx, null).getContent();
		
		// namespace
		List<NamespaceEntity> namespaces = namespaceDomainService.getNamespaceList(pageRequest.of(), clusterIdx, null).getContent();
		
		// pod
		List<PodEntity> pods = podDomainService.getPods(pageRequest.of(), null, clusterIdx, null, null).getContent();
		
		//pv
		List<PersistentVolumeEntity> pvs = pvDomainService.getPersistentVolumeList(pageRequest.of(), clusterIdx, null).getContent();
		
		List<StorageClassEntity> storageClasses = storageClassDomainService.getStorageClassList(pageRequest.of(), clusterIdx, null).getContent();
		
		
		// TODO : pvc list
		// not implemented
		
		log.debug("[Cluster Summary] nodes/namespaces/pods size = {}/{}/{}", nodes.size(), namespaces.size(), pods.size());
		
		List<NodeEntity> masterNodes = null;
		List<NodeEntity> workerNodes = null;
		
		int availableMasterCount = 0;
		int availableWorkerCount = 0;
		
		String provider = clusterEntity.getProvider();
		if(provider.toLowerCase().equals("kubernetes")
				||provider.toLowerCase().equals("vmware")) {
			masterNodes = nodes.stream().filter(n -> n.getRole().contains("master")).collect(Collectors.toList());
			workerNodes = nodes.stream().filter(n -> n.getRole().contains("worker") || "[]".equals(n.getRole())).collect(Collectors.toList());
		} else {
			masterNodes = new ArrayList<>();
			workerNodes = nodes;		
		}
		
		
		// k8s - get node
		List<io.fabric8.kubernetes.api.model.Node> k8sNodes = null;
		try {
			k8sNodes = nodeAdapterService.getNodeList(clusterEntity.getClusterId());
		} catch (Exception e) {
			log.error("", e);
		}
		
		if(k8sNodes != null && k8sNodes.size() > 0) {
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
		}
		
		log.debug("[Cluster Summary] masterNodes/workerNodes size = {}/{}", masterNodes.size(), workerNodes.size());
		log.debug("[Cluster Summary] availableMasterCount/availableWorkerCount = {}/{}", availableMasterCount, availableWorkerCount);
		
		int masterCount = masterNodes.size();
		int workerCount = workerNodes.size();
		
		
		Long kubeConfigId = clusterEntity.getClusterId();
		String pStatus = clusterEntity.getProvisioningStatus();
		
		log.info("Get cluster Health. clusterIdx: {}", clusterEntity.getClusterIdx());
		ClusterHealthAdapterDto health = getClusterStatus(kubeConfigId, pStatus);
		
		
		// Master/Worker 가동률
		float availableMasterPercent = masterCount > 0 ? (availableMasterCount * 100 / masterCount) : 0;
		
		//Public 클라우드의 쿠버네티스는 마스터 노드가 조회되지 않음
		//정상 가동중이라면 마스터 가동률 100프로로 설정
		if(!clusterEntity.getProvider().equals("Kubernetes") && health.getHealth().equals("Healthy")) {
			availableMasterPercent = 100;
		}		
		float availableWorkerPercent = workerCount > 0 ? (availableWorkerCount * 100 / workerCount) : 0;
		
		
		
		Summary summary = new ClusterDto.Summary();
		summary.setMasterCount(masterCount);
		summary.setWorkerCount(workerCount);
		summary.setAvailableMasterPercent(availableMasterPercent);
		summary.setAvailableWorkerPercent(availableWorkerPercent);
		summary.setNamespaceCount(namespaces.size());
		summary.setPodCount(pods.size());
		summary.setPvcCount(0); // TODO : pvc count
		summary.setHealthInfo(health);
		summary.setPvCount(pvs.size());
		summary.setStorageClassCount(storageClasses.size());
		
		return summary;
	}
	
	/**
	 * Cluster 삭제
	 * 
	 * @param clusterIdx
	 * @throws Exception
	 */
	public Long deleteCluster(Long clusterIdx, UserDto loginUser) throws Exception {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		
		
		ProvisioningType provisioningType = ClusterEntity.ProvisioningType.valueOf(clusterEntity.getProvisioningType());
		if (provisioningType == ProvisioningType.KUBESPRAY) {
			return deleteKubesprayCluster(clusterEntity, loginUser);
		} else if (provisioningType == ProvisioningType.KUBECONFIG) {
			return deleteK8sCluster(clusterEntity);
		} else {
			//public Cloud에 있는 cluster 삭제			
			PublicClusterDto.Delete deleteParam = PublicClusterDto.Delete.builder()
					.clusterIdx(clusterIdx)
					.build();
			publicClusterService.deleteCluster(deleteParam, loginUser);
			
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
		try {
			boolean isDeleted = clusterAdapterService.deleteCluster(clusterEntity.getClusterId());
		} catch (Exception e) {
			log.error("", e);		
		}		
		
		//클러스터 삭제
		clusterDomainService.delete(clusterEntity);
		
		return null;
	}
	
	/**
	 * 
	 * (Kubespray) Cluster 삭제
	 * 
	 * @param clusterEntity
	 * @throws Exception
	 */
	private Long deleteKubesprayCluster(ClusterEntity clusterEntity, UserDto loginUser) throws Exception {
		// db - create work job
		WorkJobDto workJobDto = WorkJobDto.builder().
				workJobTarget(clusterEntity.getClusterName())
				.workJobType(WorkJobType.CLUSTER_DELETE)
				.workJobStatus(WorkJobStatus.WAITING)
				.workJobStartAt(DateUtil.currentDateTime())
				.createUserId("hclee@strato.co.kr")
				.createUserName("hclee")
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
		
		try {
			boolean isDeleted = clusterCloudService.removeCluster(clusterCloudDto);
			System.out.println(isDeleted);
			/*
			if (!isDeleted) {
				throw new PortalException("Cluster deletion failed");
			}
			*/
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		//삭제 중 상태로 업데이트
		clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.DELETING.name());
		clusterDomainService.update(clusterEntity);
		
		//클러스터 삭제
		//clusterDomainService.delete(clusterEntity);
		
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
	 * 노드 정보 중복 채크
	 * @param nodes
	 * @return
	 */
	private boolean duplicateCheck(ArrayList<Node> nodes) {
		Map<String, List<Node>> nameMap = new HashMap<>();
		Map<String, List<Node>> ipMap = new HashMap<>();
		
		for(Node n : nodes) {
			String name = n.getName();
			String ip = n.getIp();
			
			List<Node> nameList = nameMap.get(name);
			if(nameList == null) {
				nameList = new ArrayList<>();
				nameList.add(n);
				
				nameMap.put(name, nameList);
			} else {
				//이름 중복
				return true;
			}
			
			List<Node> ipList = ipMap.get(ip);
			if(ipList == null) {
				ipList = new ArrayList<>();
				ipList.add(n);
				
				ipMap.put(ip, ipList);
			} else {
				//ip 중복
				return true;
			}
		}
		return false;
	}
	
	private boolean masterCheck(ArrayList<Node> nodes) {
		int masterCount = 0;
		for(Node n : nodes) {
			if(n.getNodeTypes().contains("master")) {
				masterCount++;
			}
		}		
		return masterCount%2 == 1;
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
	
	public void deleteClusterDB(Long clusterIdx) {
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		clusterDomainService.delete(cluster);
	}

}
