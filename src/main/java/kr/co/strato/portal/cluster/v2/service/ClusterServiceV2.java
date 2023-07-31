package kr.co.strato.portal.cluster.v2.service;

import java.util.ArrayList;
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
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.workload.service.WorkloadAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.v2.model.ClusterOverviewDto;
import kr.co.strato.portal.cluster.v2.model.ClusterOverviewDto.PodList;
import kr.co.strato.portal.cluster.v2.model.ClusterOverviewDto.PodSummary;
import kr.co.strato.portal.cluster.v2.model.ClusterOverviewDto.WorkloadSummary;
import kr.co.strato.portal.cluster.v2.model.NamespaceDto;
import kr.co.strato.portal.cluster.v2.model.NodeDto;
import kr.co.strato.portal.cluster.v2.model.PersistentVolumeDto;
import kr.co.strato.portal.workload.v1.model.WorkloadDto;
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

	/**
	 * 클러스터 Overview 화면 데이터 조회.
	 * @param clusterIdx
	 * @return
	 */
	public Object getOverview(Long clusterIdx) {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		if(clusterEntity == null) {
			throw new PortalException("Not Found Cluster.");
		}
		
		Long kubeConfigId = clusterEntity.getClusterId();
		ResourceListSearchInfo search = ResourceListSearchInfo.builder()
				.kubeConfigId(kubeConfigId)
				.build();
		
		try {
			List<HasMetadata> list = workloadAdapterService.getList(search);
			List<WorkloadDto.List> workloads = workloadService.getList(list);
			
			
			List<Pod> podList = list.stream()
					.filter(d -> d instanceof Pod)
					.map(d -> (Pod)d)
					.collect(Collectors.toList());
			
			List<NamespaceDto.ListDto> namespaceList = namespaceService.getList(kubeConfigId, podList);
			List<NodeDto.ListDto> nodeList = nodeService.getList(kubeConfigId, podList);
			List<PersistentVolumeDto.ListDto> pvList = pvService.getList(kubeConfigId);
			
			ClusterOverviewDto.ClusterSummary clusterSummary = getClusterSummary(clusterEntity, nodeList, namespaceList, pvList, workloads, podList);			
			List<WorkloadDto.List> getControlPlaneComponents = getControlPlaneComponents(workloads);			
			WorkloadSummary workloadSummary = getWorkloadSummary(workloads);			
			PodSummary podSummary = getPodSummary(podList);
			
			ClusterOverviewDto.Overview overview = ClusterOverviewDto.Overview.builder()
					.clusterSummary(clusterSummary)
					.controlPlaneComponent(getControlPlaneComponents)
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
	
	
	private ClusterOverviewDto.ClusterSummary getClusterSummary(
			ClusterEntity cluster, 
			List<NodeDto.ListDto> nodeList,
			List<NamespaceDto.ListDto> namespaceList,
			List<PersistentVolumeDto.ListDto> pvList,
			List<WorkloadDto.List> workloads,
			List<Pod> podList) {
		
		String name = cluster.getClusterName();
		String provider = cluster.getProvider();
		String region = cluster.getRegion();
		String version = cluster.getProviderVersion();
		String status = cluster.getProvisioningStatus();
		String createAt = cluster.getCreatedAt();
		String createBy = cluster.getCreateUserId();
		
		Integer nodeCount = nodeList.size();
		
		double cpuTotal = 0;
		double memoryTotal = 0;
		
		double cpuUsage = 0;
		double memoryUsage = 0;
		
		for(NodeDto.ListDto n : nodeList) {
			cpuTotal += n.getUsageDto().getCpuCapacity();
			memoryTotal += n.getUsageDto().getMemoryCapacity();
			
			cpuUsage += n.getUsageDto().getCpuRequests();
			memoryUsage += n.getUsageDto().getMemoryRequests();
		}
		
		double storageTotal = 0;
		double storageUsage = 0;
		
		for(PersistentVolumeDto.ListDto dto : pvList) {
			storageTotal += dto.getSize();
			if(dto.getStatus().equals("Bound")) {
				storageUsage += dto.getSize();
			}
		}
		
		ClusterOverviewDto.ClusterSummary summary = ClusterOverviewDto.ClusterSummary.builder()
				.name(name)
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
	
}
