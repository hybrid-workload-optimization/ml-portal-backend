package kr.co.strato.portal.cluster.v2.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodStatus;
import kr.co.strato.adapter.k8s.cluster.model.ClusterHealthAdapterDto;
import kr.co.strato.adapter.k8s.cluster.service.ClusterAdapterService;
import kr.co.strato.adapter.k8s.workload.service.WorkloadAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.model.ClusterEntity.ProvisioningStatus;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.v2.model.ClusterDto;
import kr.co.strato.portal.cluster.v2.model.ClusterOverviewDto;
import kr.co.strato.portal.cluster.v2.model.ClusterOverviewDto.PodList;
import kr.co.strato.portal.cluster.v2.model.ClusterOverviewDto.PodSummary;
import kr.co.strato.portal.cluster.v2.model.ClusterOverviewDto.WorkloadSummary;
import kr.co.strato.portal.cluster.v2.model.NamespaceDto;
import kr.co.strato.portal.cluster.v2.model.NodeDto;
import kr.co.strato.portal.cluster.v2.model.PersistentVolumeDto;
import kr.co.strato.portal.cluster.v2.runnable.GetNamespaceListRunnable;
import kr.co.strato.portal.cluster.v2.runnable.GetNodeListRunnable;
import kr.co.strato.portal.cluster.v2.runnable.GetPersistentVolumeRunnable;
import kr.co.strato.portal.cluster.v2.runnable.GetWorkloadListRunnable;
import kr.co.strato.portal.cluster.v2.runnable.WorkloadRunnableExecuter;
import kr.co.strato.portal.workload.v2.model.WorkloadDto;
import kr.co.strato.portal.workload.v2.service.WorkloadServiceV2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClusterServiceV2 {
	
	@Autowired
	private ClusterDomainService clusterDomainService;
	
	@Autowired
	private NodeService nodeService;
	
	@Autowired
	private WorkloadServiceV2 workloadService;
	
	@Autowired
	private WorkloadAdapterService workloadAdapterService;
	
	@Autowired
	private NamespaceService namespaceService;
	
	@Autowired
	private PersistentVolumeService pvService;
	
	@Autowired
	ClusterAdapterService clusterAdapterService;

	/**
	 * 클러스터 Overview 화면 데이터 조회.
	 * @param clusterIdx
	 * @return
	 */
	public ClusterOverviewDto.Overview getOverview(Long clusterIdx) {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		if(clusterEntity == null) {
			throw new PortalException("Not Found Cluster.");
		}
		return getOverview(clusterEntity);
	}
	
	public ClusterOverviewDto.Overview getOverview(ClusterEntity clusterEntity) {
		return getOverview(clusterEntity, false);
	}
	
	public ClusterOverviewDto.Overview getOverview(ClusterEntity clusterEntity, boolean attachHealty) {
		Long kubeConfigId = clusterEntity.getClusterId();
		
		GetWorkloadListRunnable workloadRunnable = new GetWorkloadListRunnable(workloadAdapterService, kubeConfigId);
		GetPersistentVolumeRunnable pvRunnable = new GetPersistentVolumeRunnable(pvService, kubeConfigId);
		
		WorkloadRunnableExecuter executer = new WorkloadRunnableExecuter();
		executer.addWorkloadRunnable(workloadRunnable);
		executer.addWorkloadRunnable(pvRunnable);
		executer.run();
		
		try {
			
			List<HasMetadata> list = (List<HasMetadata>)executer.getResult(workloadRunnable);
			List<PersistentVolumeDto.ListDto> pvList = (List<PersistentVolumeDto.ListDto>)executer.getResult(pvRunnable);
			
			
			
			List<WorkloadDto.List>  workloads = workloadService.getList(list);
			List<Pod> podList = list.stream()
					.filter(d -> d instanceof Pod)
					.map(d -> (Pod)d)
					.collect(Collectors.toList());
			
			GetNamespaceListRunnable namespaceRunnable = new GetNamespaceListRunnable(namespaceService, podList, kubeConfigId);
			GetNodeListRunnable nodeRunnable = new GetNodeListRunnable(nodeService, podList, kubeConfigId);
			
			executer = new WorkloadRunnableExecuter();
			executer.addWorkloadRunnable(namespaceRunnable);
			executer.addWorkloadRunnable(nodeRunnable);
			executer.run();
			
			
			List<NamespaceDto.ListDto> namespaceList = (List<NamespaceDto.ListDto>) executer.getResult(namespaceRunnable);
			List<NodeDto.ListDto> nodeList = (List<NodeDto.ListDto>) executer.getResult(nodeRunnable);
			
			
			
			ClusterOverviewDto.ClusterSummary clusterSummary = getClusterSummary(clusterEntity, nodeList, namespaceList, pvList, workloads, podList, attachHealty);			
			
			List<WorkloadDto.List> controlPlaneComponents = null;
			WorkloadSummary workloadSummary = null;
			if(workloads != null) {
				controlPlaneComponents = getControlPlaneComponents(workloads);			
				workloadSummary = getWorkloadSummary(workloads);	
			}
			
			PodSummary podSummary = null;
			if(podList != null) {
				podSummary = getPodSummary(podList);
			}
			
			ClusterOverviewDto.Overview overview = ClusterOverviewDto.Overview.builder()
					.clusterSummary(clusterSummary)
					.controlPlaneComponent(controlPlaneComponents)
					.workloadSummary(workloadSummary)
					.nodes(nodeList)
					.namespaces(namespaceList)
					.podSummary(podSummary)
					.build();
			
			return overview;
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}
	
	/**
	 * 클러스터 전체 요약 정보 조회
	 * @param cluster
	 * @param nodeList
	 * @param namespaceList
	 * @param pvList
	 * @param workloads
	 * @param podList
	 * @return
	 */
	private ClusterOverviewDto.ClusterSummary getClusterSummary(
			ClusterEntity cluster, 
			List<NodeDto.ListDto> nodeList,
			List<NamespaceDto.ListDto> namespaceList,
			List<PersistentVolumeDto.ListDto> pvList,
			List<WorkloadDto.List> workloads,
			List<Pod> podList,
			boolean attachHealty) {
		
		String name = cluster.getClusterName();
		String description = cluster.getDescription();
		String provider = cluster.getProvider();
		String region = cluster.getRegion();
		String version = cluster.getProviderVersion();
		String status = cluster.getProvisioningStatus();
		String createAt = null;
		String createBy = cluster.getCreateUserId();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
		try {
			Date date = formatter.parse(cluster.getCreatedAt());
			createAt = formatter.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}	
		
		Integer nodeCount = 0;		
		double cpuTotal = 0;
		double memoryTotal = 0;
		
		double cpuUsage = 0;
		double memoryUsage = 0;
		
		if(nodeList != null) {
			nodeCount = nodeList.size();
			for(NodeDto.ListDto n : nodeList) {
				cpuTotal += n.getUsageDto().getCpuCapacity();
				memoryTotal += n.getUsageDto().getMemoryCapacity();
				
				cpuUsage += n.getUsageDto().getCpuRequests();
				memoryUsage += n.getUsageDto().getMemoryRequests();
			}
		}
		
		
		double storageTotal = 0;
		double storageUsage = 0;
		
		if(pvList != null) {
			for(PersistentVolumeDto.ListDto dto : pvList) {
				storageTotal += dto.getSize();
				if(dto.getStatus().equals("Bound")) {
					storageUsage += dto.getSize();
				}
			}
		}
		
		ClusterDto.Status healthy = null;
		if(attachHealty) {
			healthy = getClusterStatus(cluster);
		}
		
		ClusterOverviewDto.ClusterSummary summary = ClusterOverviewDto.ClusterSummary.builder()
				.name(name)
				.description(description)
				.provider(provider)
				.region(region)
				.vision(version)
				.status(status)
				.createAt(createAt)
				.createBy(createBy)
				
				.vpcCidr(null)
				.serviceCidr(null)
				.podCidr(null)
				
				.cpuTotal(cpuTotal)
				.memoryTotal(memoryTotal)
				.cpuUsage(cpuUsage)
				.memoryUsage(memoryUsage)
				.storageTotal(storageTotal)
				.storageUsage(storageUsage)
				
				.countNode(nodeCount)
				.countNamespace(namespaceList.size())
				.countPV(pvList.size())
				.countWorkload(workloads.size())
				.countPod(podList.size())
				.healthy(healthy)
				.build();
		return summary;
	}
	
	/**
	 * Control Plane Component 정보 조회
	 * @param workloads
	 * @return
	 */
	private List<WorkloadDto.List> getControlPlaneComponents(List<WorkloadDto.List> workloads) {
		List<WorkloadDto.List> list = workloads.stream()
				.filter(w -> w.getNamespace().equals("kube-system"))
				.collect(Collectors.toList());
		return list;
	}
	
	/**
	 * 워크로드 요약 정보 조회
	 * @param workloads
	 * @return
	 */
	private WorkloadSummary getWorkloadSummary(List<WorkloadDto.List> workloads) {
		List<WorkloadDto.List> deployments = new ArrayList<>();
		List<WorkloadDto.List> statefulSets = new ArrayList<>();
		List<WorkloadDto.List> cronJobs = new ArrayList<>();
		List<WorkloadDto.List> jobs = new ArrayList<>();
		List<WorkloadDto.List> replicaSets = new ArrayList<>();
		List<WorkloadDto.List> daemonSets = new ArrayList<>();
		
		List<WorkloadDto.List> todayWorkload = new ArrayList<>();
		for(WorkloadDto.List l : workloads) {
			String createAt = l.getCreateAt();
			String lowerKind = l.getKind().toLowerCase();
			if(lowerKind.equals("deployment")) {
				deployments.add(l);
			} else if(lowerKind.equals("statefulset")) {
				statefulSets.add(l);
			} else if(lowerKind.equals("cronjob")) {
				cronJobs.add(l);
			} else if(lowerKind.equals("job")) {
				jobs.add(l);
			} else if(lowerKind.equals("replicaset")) {
				replicaSets.add(l);
			} else if(lowerKind.equals("daemonset")) {
				daemonSets.add(l);
			}
			
			if(DateUtil.isToday(createAt)) {
				todayWorkload.add(l);
			}
		}
		
		WorkloadSummary summary = WorkloadSummary.builder()
				.deployments(deployments)
				.statefulSets(statefulSets)
				.cronJobs(cronJobs)
				.jobs(jobs)
				.replicaSets(replicaSets)
				.daemonSets(daemonSets)
				.todayDeployedWorkload(todayWorkload)
				.build();
		return summary;
	}
	
	/**
	 * 파드별 요약 정보 조회
	 * @param podList
	 * @return
	 */
	private PodSummary getPodSummary(List<Pod> podList) {		
		Map<String, List<PodList>> podOperatingRate = new HashMap<>();
		Map<String, List<PodList>>  podDeployedByNode = new HashMap<>();
		List<PodList> restartWithin30minutesList = new ArrayList<>();
		for(Pod pod : podList) {
			PodStatus status = pod.getStatus();          
			
			Integer restart = 0;
			String podStatus = null;
			String nodeName = pod.getSpec().getNodeName();
			List<ContainerStatus> containerStatusList = status.getContainerStatuses();
			
			
			String uid = pod.getMetadata().getUid();
			String name = pod.getMetadata().getName();            			
			String namespace = pod.getMetadata().getNamespace();
			String kind = pod.getKind();
			String createAt = DateUtil.strToNewFormatter(pod.getMetadata().getCreationTimestamp());
			
			PodList l = PodList.builder()
					.uid(uid)
					.name(name)
					.namespace(namespace)
					.kind(kind)
					.restart(restart)
					.createAt(createAt)
					.build();
			
			//파드 상태 및 30분 이내 재시작 파드 구하기
            if (!containerStatusList.isEmpty()) {
            	ContainerStatus containerStatus = containerStatusList.get(0);
            	if(containerStatus.getState().getRunning() != null) {
            		podStatus = "Running";
            		
            		String startedAt = containerStatus.getState().getRunning().getStartedAt();
                	l.setStartedAt(startedAt);
                	
                	restart = containerStatus.getRestartCount();                	
                	if(restart > 0) {            		
                		if(DateUtil.isWithin30minutes(startedAt)) {            			
                			restartWithin30minutesList.add(l);
                		}
                	}
            	} else if(containerStatus.getState().getWaiting() != null) { 
            		String reason = containerStatus.getState().getWaiting().getReason();
            		podStatus = reason;
            	} else if(containerStatus.getState().getTerminated() != null) {
            		String reason = containerStatus.getState().getTerminated().getReason();
            		podStatus = reason;
            	}
            }
            l.setStatus(podStatus);
            
          //파드 가동률
			List<PodList> operatingList = podOperatingRate.get(podStatus);
			if(operatingList == null) {
				operatingList = new ArrayList<>();
				podOperatingRate.put(podStatus, operatingList);
			}
			operatingList.add(l);	
			
			//노드별 파드 배포현황
			List<PodList> deployedByNodeList = podDeployedByNode.get(nodeName);
			if(deployedByNodeList == null) {
				deployedByNodeList = new ArrayList<>();
				podDeployedByNode.put(nodeName, deployedByNodeList);
			}
			deployedByNodeList.add(l);
		}
		
		PodSummary summary = PodSummary.builder()
				.countTotal(podList.size())
				.podOperatingRate(podOperatingRate)
				.podDeployedByNode(podDeployedByNode)
				.podRestartList(restartWithin30minutesList)
				.build();
		return summary;
	}
	
	/**
	 * 클러스터 상태 정보 조회
	 * @param clusterEntity
	 * @return
	 */
	public ClusterDto.Status getClusterStatus(ClusterEntity clusterEntity) {
		ClusterDto.Status status = new ClusterDto.Status();
		if(clusterEntity != null) {
			Long kubeConfigId = clusterEntity.getClusterId();
			String pStatus = clusterEntity.getProvisioningStatus();
			
			ClusterHealthAdapterDto health = getClusterStatus(kubeConfigId, pStatus);
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
			} else if(pStatus.equals(ProvisioningStatus.SCALE.toString())) {
				
				health.setHealth("Scale in");
			} else if(pStatus.equals(ProvisioningStatus.PENDING.toString())) {
				//배포 실패
				health.setHealth("Waiting");
			} else {
				//배포 실패
				health.setHealth("Waiting");
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
	
}
