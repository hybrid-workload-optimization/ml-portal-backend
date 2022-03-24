package kr.co.strato.portal.dashboard.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.Pod;
import kr.co.strato.adapter.k8s.node.service.NodeAdapterService;
import kr.co.strato.adapter.k8s.pod.service.PodAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.portal.cluster.model.ClusterNodeDto;
import kr.co.strato.portal.cluster.model.ClusterNodeDtoMapper;
import kr.co.strato.portal.cluster.service.ClusterNodeService;
import kr.co.strato.portal.common.service.SelectService;
import kr.co.strato.portal.dashboard.model.DashboardSystemAdminDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DashboardService {
	
	@Autowired
    private SelectService selectService;
	
	@Autowired
    private ClusterDomainService clusterDomainService;
	
	@Autowired
	private ProjectDomainService projectDomainService;
	
	@Autowired
	private NodeAdapterService nodeAdapterService;
	
	@Autowired
	private ClusterNodeService nodeService;
	
	@Autowired
    private PodAdapterService podAdapterService;
	
	
	/**
	 * System admin용 Dashboard 데이터 반환.
	 * @param projectIdx
	 * @param clusterIdx
	 * @return
	 */
	public DashboardSystemAdminDto getDashboardInfoForSystemAdmin(Long projectIdx, Long clusterIdx) {
		int projectCount = 0;
		int clusterCount = 0;
		int nodeCount = 0;
		
		int masterCount = 0;
		int workerCount = 0;		
		int masterReadyCount = 0;
		int workerReadyCount = 0;		
		int restartWithinTenMinutes = 0;	
		
		int readyCount = 0;
		int totalUtilization = 0;
		int masterUtilization = 0;
		int workerUtilization = 0;
		
		String nodeUtilizationState = null;
		
		List<NodeEntity> nodeEntitys = new ArrayList<>();
		if(projectIdx == null) {
			projectCount = selectService.getSelectProjects().size();		
		} else {			
			if(projectDomainService.getProjectById(projectIdx).orElse(null) != null) {
				projectCount = 1;	
			}
		}
		
		List<ClusterNodeDto.ResListDto> list = new ArrayList<>();
		List<ClusterEntity> clusters = getKubeConfigIds(projectIdx, clusterIdx);
		clusterCount = clusters.size();
		
		if(clusterCount > 0) {
			long currentTime = new Date().getTime();
			for(ClusterEntity cluster: clusters) {
				Long kubeConfigId = cluster.getClusterId();
				String clusterName = cluster.getClusterName();
				try {
					List<Node> nodes = nodeAdapterService.getNodeList(kubeConfigId);
					for(Node node : nodes) {
						NodeEntity entity = nodeService.toEntity(node, cluster.getClusterIdx());
						nodeEntitys.add(entity);
						
						List<Pod> k8sPods = podAdapterService.getList(kubeConfigId, entity.getName(), null, null, null);
						long runningSize = k8sPods.stream().filter(p -> p.getStatus().getPhase().equals("Running")).count();
						int podCount = k8sPods.size();
						
						String podStatus = String.format("%d/%d", runningSize, podCount);
			        	
			        	
			        	ClusterNodeDto.ResListDto nodeListDto = ClusterNodeDtoMapper.INSTANCE.toResListDto(entity);
			        	nodeListDto.setClusterName(clusterName);
			        	nodeListDto.setPodStatus(podStatus);
			        	list.add(nodeListDto);
			        	
						
						boolean isReady = Boolean.parseBoolean(entity.getStatus());				
						String role = entity.getRole();
						if(role.toLowerCase().contains("master")) {
							masterCount++;
							if(isReady) {
								masterReadyCount++;
							}
						} else {
							workerCount++;
							if(isReady) {
								workerReadyCount++;
							}
						}
						
						if(withinTenMinutes(node, currentTime)) {
							//10분 이내 재시작 노드
							restartWithinTenMinutes++;
						}
					}			
					nodeCount+=nodes.size();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
				
			}
			
			readyCount = masterReadyCount + workerReadyCount;
			
			totalUtilization = Long.valueOf(Math.round((double) readyCount / (double) nodeCount * 100)).intValue();
			masterUtilization = Long.valueOf(Math.round((double) masterReadyCount / (double) masterCount * 100)).intValue();
			workerUtilization = Long.valueOf(Math.round((double) workerReadyCount / (double) workerCount * 100)).intValue();
			
			//90 이상 Good
			//90 미만 70 이상 Warning
			//70 미만 Bad
			if(totalUtilization >= 90) {
				nodeUtilizationState = "Good";
			} else if(totalUtilization > 70) {
				nodeUtilizationState = "Warning";
			} else {
				nodeUtilizationState = "Bad";
			}
		}
		
		return DashboardSystemAdminDto.builder()
				.projectCount(projectCount)
				.clusterCount(clusterCount)
				.nodeCount(nodeCount)
				.restartWithinTenMinutes(restartWithinTenMinutes)
				.masterCount(masterCount)
				.workerCount(workerCount)
				.masterReadyCount(masterReadyCount)
				.workerReadyCount(workerReadyCount)
				.totalUtilization(totalUtilization)
				.masterUtilization(masterUtilization)
				.workerUtilization(workerUtilization)
				.nodeUtilizationState(nodeUtilizationState)
				.nodeList(list)
				.build();
	}
	
	/**
	 * 조건에 맞는 KubeConfigId를 반환한다.
	 * @param projectIdx
	 * @param clusterIdx
	 * @return
	 */
	private List<ClusterEntity> getKubeConfigIds(Long projectIdx, Long clusterIdx) {
		List<ClusterEntity> clusters = null;
		if(clusterIdx == null) {
	        if(projectIdx != null && projectIdx > 0L){
	            clusters = clusterDomainService.getListByProjectIdx(projectIdx);
	        } else{
	            clusters = clusterDomainService.getListAll();
	        }
		} else {
			clusters = new ArrayList<>();
			ClusterEntity entry = clusterDomainService.get(clusterIdx);
			if(entry != null) {
				clusters.add(entry);
			}
		}
		return clusters;
	}
	
	/**
	 * 10분 이내 재시작 유무 리턴.
	 * @param node
	 * @param currentTime
	 * @return
	 * @throws ParseException
	 */
	private boolean withinTenMinutes (Node node, long currentTime) throws ParseException {
		NodeCondition nodeCondition = node.getStatus().getConditions().stream()
				.filter(c -> c.getType().equals("Ready"))
				.findFirst().get();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));   
		
		Date lastTransitionTime = formatter.parse(nodeCondition.getLastTransitionTime());
		formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		
		
		long tTime = lastTransitionTime.getTime();	
		long now = currentTime - 60 * 10000;
		boolean isReady = Boolean.parseBoolean(nodeCondition.getStatus());
		
		
		if(tTime > now && isReady) {
			//10분 이내 재시작
			return true;
		}
		return false;
	}
	
}
