package kr.co.strato.portal.dashboard.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.Pod;
import kr.co.strato.adapter.k8s.node.service.NodeAdapterService;
import kr.co.strato.adapter.k8s.pod.model.PodMapper;
import kr.co.strato.adapter.k8s.pod.service.PodAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.portal.cluster.v1.model.ClusterNodeDto;
import kr.co.strato.portal.cluster.v1.model.ClusterNodeDtoMapper;
import kr.co.strato.portal.cluster.v1.model.ClusterNodeDto.ResListDetailDto;
import kr.co.strato.portal.cluster.v1.service.ClusterNodeService;
import kr.co.strato.portal.common.service.SelectService;
import kr.co.strato.portal.dashboard.model.SystemAdminNodeStateDto;
import kr.co.strato.portal.setting.model.UserDto;
import lombok.Data;
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
	public SystemAdminNodeStateDto getNodeState(UserDto loginUser, Long projectIdx, Long clusterIdx) {
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
		
		if(projectIdx == null) {
			projectCount = selectService.getSelectProjects(loginUser).size();		
		} else {			
			if(projectDomainService.getProjectById(projectIdx).orElse(null) != null) {
				projectCount = 1;	
			}
		}
		
		List<ClusterEntity> clusters = getKubeConfigIds(loginUser, projectIdx, clusterIdx);
		clusterCount = clusters.size();
		
		if(clusterCount > 0) {
			long currentTime = new Date().getTime();
			List<GetNodeInfoRunnable> runnables = new ArrayList<>();
			for(ClusterEntity cluster: clusters) {
				
				Long kubeConfigId = cluster.getClusterId();
				if(kubeConfigId != null) {
					GetNodeInfoRunnable getNodeinfoRunnable = new GetNodeInfoRunnable(cluster, currentTime, false);
					runnables.add(getNodeinfoRunnable);
					Executors.newSingleThreadExecutor().submit(getNodeinfoRunnable);
				}
			}
			
			//작업이 완료될때 까지 대기.
			wait(runnables);
			
			for(GetNodeInfoRunnable r : runnables) {
				nodeCount += r.getNodeCount();
				masterCount += r.getMasterCount();
				workerCount += r.getWorkerCount();		
				masterReadyCount += r.getMasterReadyCount();
				workerReadyCount += r.getWorkerReadyCount();		
				restartWithinTenMinutes += r.getRestartWithinTenMinutes();
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
		
		return SystemAdminNodeStateDto.builder()
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
				.build();
	}
	
	/**
	 * 노드 리스트 반환.
	 * @param projectIdx
	 * @param clusterIdx
	 * @return
	 */
	public List<ResListDetailDto> getNodeList(UserDto loginUser, Long projectIdx, Long clusterIdx) {
		List<ResListDetailDto> list = new ArrayList<>();
		List<ClusterEntity> clusters = getKubeConfigIds(loginUser, projectIdx, clusterIdx);
		
		if(clusters.size() > 0) {
			long currentTime = new Date().getTime();
			List<GetNodeInfoRunnable> runnables = new ArrayList<>();
			for(ClusterEntity cluster: clusters) {
				Long kubeConfigId = cluster.getClusterId();
				if(kubeConfigId != null) {
					GetNodeInfoRunnable getNodeinfoRunnable = new GetNodeInfoRunnable(cluster, currentTime, true);
					runnables.add(getNodeinfoRunnable);
					Executors.newSingleThreadExecutor().submit(getNodeinfoRunnable);
				}				
			}
			
			//작업이 완료될때 까지 대기.
			wait(runnables);
			
			for(GetNodeInfoRunnable r : runnables) {
				list.addAll(r.getResListDtos());
			}
		}		
		
		Collections.sort(list, new Comparator<ResListDetailDto>() {
		    @Override
		    public int compare(ResListDetailDto b1,ResListDetailDto b2) {
		    	int dff = Long.valueOf(b2.getClusterIdx() - b1.getClusterIdx()).intValue();
		    	if(dff == 0) {
		    		b1.getName().compareTo(b2.getName());
		    	}
		    	return dff;
		    }
		});
		
		return list;
	}
	
	/**
	 * 파라메타 정보에 맞는 노드 반환.
	 * @param clusterIdx
	 * @param nodeName
	 * @return
	 */
	public Long getNodeId(Long clusterIdx, String nodeName) {
		NodeEntity entity = nodeService.getNodeByName(clusterIdx, nodeName);
		if(entity != null) {
			return entity.getId();
		}
		return -1L;
	}
	

	@Data
	class GetNodeInfoRunnable implements Runnable {
		private ClusterEntity cluster;
		private boolean isFinish;
		private Long currentTime;
		
		private int nodeCount = 0;
		private int masterCount = 0;
		private int workerCount = 0;		
		private int masterReadyCount = 0;
		private int workerReadyCount = 0;		
		private int restartWithinTenMinutes = 0;
		private boolean listCollect;
		
		private List<ClusterNodeDto.ResListDetailDto> resListDtos;

		public GetNodeInfoRunnable(ClusterEntity cluster, Long currentTime, boolean listCollect) {
			this.cluster = cluster;
			this.isFinish = false;
			this.currentTime = currentTime;
			this.listCollect = listCollect;
			this.resListDtos = new ArrayList<>();
		}

		@Override
		public void run() {
			try {
				Long clusterIdx = cluster.getClusterIdx();
				Long kubeConfigId = cluster.getClusterId();
				String clusterName = cluster.getClusterName();
				
				List<NodeEntity> nodeEntity = nodeService.getNodeList(clusterIdx);
				Map<String, NodeEntity> nodeMap = nodeEntity.stream()
					      .collect(Collectors.toMap(
					    		  NodeEntity::getName,
					    		  Function.identity()));
				
				ProjectEntity projectEntry = projectDomainService.getProjectDetailByClusterId(clusterIdx);
				
				List<Node> nodes = nodeAdapterService.getNodeList(kubeConfigId);
				nodeCount = nodes.size();
				for(Node node : nodes) {				
		        	boolean isReady = node.getStatus().getConditions().stream()
		        			.filter(condition -> condition.getType().equals("Ready"))
		    				.map(condition -> condition.getStatus().equals("True")).findFirst().orElse(false);
		        	
		        	List<String> roles = new ArrayList<>();
		    		node.getMetadata().getLabels().keySet().stream().filter(l -> l.contains("node-role"))
		    				.map(l -> l.split("/")[1]).iterator().forEachRemaining(roles::add);
		    		
					if(roles.contains("master")) {
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
					
					
					if(listCollect) {
						String name = node.getMetadata().getName();
						NodeEntity entity = nodeMap.get(name);
						if(entity != null) {
							ClusterEntity clusterEntity = new ClusterEntity();
							clusterEntity.setClusterIdx(clusterIdx);
							entity.setCluster(clusterEntity);
						} else {
							entity = nodeService.toEntity(node, clusterIdx);
						}
						
						List<Pod> pods = podAdapterService.getList(kubeConfigId, name, null, null, null);
						
						long runningSize = pods.stream().filter(p -> p.getStatus().getPhase().equals("Running")).count();
						int podCount = pods.size();
						
						String podStatus = String.format("%d/%d", runningSize, podCount);
			        	
			        	
			        	ClusterNodeDto.ResListDetailDto nodeListDto = ClusterNodeDtoMapper.INSTANCE.toResListDetailDto(entity);
			        	nodeListDto.setPodStatus(podStatus);
			        	nodeListDto.setClusterIdx(clusterIdx);
			        	nodeListDto.setClusterName(clusterName);
			        	nodeListDto.setUid(entity.getUid());
			        	
			        	if(projectEntry != null) {
			        		nodeListDto.setProjectIdx(projectEntry.getId());
			        		nodeListDto.setProjectName(projectEntry.getProjectName());
			        		
			        	}
			        	
			        	this.resListDtos.add(nodeListDto);
			        	
			        	
			        	List<PodEntity> podEntrys =  pods.stream().map( s -> {
			        		PodEntity pod = PodMapper.INSTANCE.toEntity(s);
			        		return pod;
			        	}).collect(Collectors.toList());
			        	
			        	ClusterNodeDto.ResDetailChartDto chartNode = new ClusterNodeDto.ResDetailChartDto();
			        	nodeService.setUsage(node, podEntrys, chartNode);
			        	nodeListDto.setDetailChart(chartNode);
					}
					
				}
				
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				this.isFinish = true;
			}
		}
		
	}
	
	
	
	/**
	 * 조건에 맞는 KubeConfigId를 반환한다.
	 * @param projectIdx
	 * @param clusterIdx
	 * @return
	 */
	private List<ClusterEntity> getKubeConfigIds(UserDto loginUser, Long projectIdx, Long clusterIdx) {
		List<ClusterEntity> clusters = null;
		if(clusterIdx == null) {
	        if(projectIdx != null && projectIdx > 0L){
	            clusters = clusterDomainService.getListByProjectIdx(projectIdx);
	        } else{
	            clusters = clusterDomainService.getListByLoginUser(loginUser);
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
	
	/**
	 * 작업이 완료 될 때 까지 대기.
	 * @param runnables
	 */
	private void wait(List<GetNodeInfoRunnable> runnables) {
		while(true) {
			int complateCount = 0;			
			for(GetNodeInfoRunnable r : runnables) {
				if(r.isFinish()) {
					complateCount++;
				}
			}
			
			if(runnables.size() == complateCount) {
				break;
			}
			try {Thread.sleep(100);} catch (InterruptedException e) {}
		}
	}
}
