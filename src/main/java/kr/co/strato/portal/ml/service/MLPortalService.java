package kr.co.strato.portal.ml.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
import kr.co.strato.adapter.k8s.pod.service.PodAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.machineLearning.model.MLEntity;
import kr.co.strato.domain.machineLearning.model.MLResourceEntity;
import kr.co.strato.domain.machineLearning.service.MLDomainService;
import kr.co.strato.domain.machineLearning.service.MLResourceDomainService;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.cluster.service.ClusterService;
import kr.co.strato.portal.ml.model.MLDto;
import kr.co.strato.portal.ml.model.MLDto.ListArg;
import kr.co.strato.portal.ml.model.MLDtoMapper;
import kr.co.strato.portal.ml.model.MLResourceDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MLPortalService {
	
	@Autowired
	private MLDomainService mlDomainService;
	
	@Autowired
	private MLResourceDomainService mlResourceDomainService;
	
	@Autowired
	private ClusterService clusterService;
	
	@Autowired
	private ServiceFactory serviceFactory;
	
	@Autowired
	private PodAdapterService podAdapterService;
	
	@Autowired
	private MLClusterAPIAsyncService mlClusterService;
	
	/**
	 * ML 리스트 반환.
	 * @param param
	 * @return
	 */
	public Object getMlList(ListArg param) {
		String userId = param.getUserId();
		String name = param.getName();
		
		List<MLEntity> list = mlDomainService.getList(userId, name);
		
		List<MLDto.ListDtoForPortal> mlList = list.stream().map(c -> {
			return MLDtoMapper.INSTANCE.toListDtoForPortal(c);
		}).collect(Collectors.toList());
		
		for(MLDto.ListDtoForPortal l : mlList) {
			List<MLResourceEntity> resEntitys = mlResourceDomainService.getList(l.getId());
			l.setResourceCount(resEntitys.size());
		}
		
		PageRequest pageRequest = param.getPageRequest();
		if(pageRequest != null) {
			Page<MLDto.ListDtoForPortal> pages = new PageImpl<>(mlList, pageRequest.of(), mlList.size());
			return pages;
		} else {
			return mlList;
		}
	}
	
	/**
	 * ml 상세
	 * @param mlId
	 * @return
	 */
	public MLDto.DetailForPortal getMl(String mlId) {
		MLEntity entity = mlDomainService.get(mlId);
		ClusterDto.Detail cluster = null;
		try {
			cluster = clusterService.getCluster(entity.getClusterIdx());
		} catch (Exception e) {
			log.error("", e);
		}
		
		MLDto.DetailForPortal detail = MLDtoMapper.INSTANCE.toDetailDtoForPortal(entity);
		
		List<MLResourceEntity> resEntitys = mlResourceDomainService.getList(entity.getId());		
		List<MLResourceDto> resources = new ArrayList<>();
		
		int jobCount = 0;
		int cronJobCount = 0;
		int deploymentCount = 0;
		int daemonSetCount = 0;
		int replicaSetCount = 0;
		
		int active = 0;
		int succeeded = 0;			
		int failed = 0;
		
		for(MLResourceEntity resEntity : resEntitys) {			
			MLResourceDto resDto = MLDtoMapper.INSTANCE.toResDto(resEntity);
			resources.add(resDto);
			
			Long resId = resEntity.getResourceId();
			String kind = resEntity.getKind().toLowerCase();
			
			
			MLServiceInterface mlServiceInterface = getServiceInterface(kind);
			
			int totalPodCount = 0;
			int runningPodCount = 0;
			
			try {
				if(kind.equals("job")) {
					jobCount++;
					
					HasMetadata data = mlServiceInterface.getResource(resId);
					if(data != null) {
						Job job = (Job) data;
						JobStatus status = job.getStatus();
						
						int a = status.getActive() == null ? 0: status.getActive();
						int s = status.getSucceeded() == null ? 0: status.getSucceeded();
						int f = status.getFailed()== null ? 0: status.getFailed();
						
						active += a;
						succeeded += s;
						failed += f;
						
						totalPodCount += a + s + f;
						runningPodCount += a;
					}
					
				} else { 
					if(kind.equals("cronjob")) {
						cronJobCount++;
					} else if(kind.equals("deployment")) {
						deploymentCount++;
					} else if(kind.equals("daemonset")) {
						daemonSetCount++;
					} else if(kind.equals("replicaset")) {
						replicaSetCount++;
					}
					
					if(cluster != null && mlServiceInterface != null) {
						String uid = mlServiceInterface.getResourceUid(resId);
						
						List<Pod> pods = podAdapterService.getList(cluster.getClusterId(), null, uid, null, null);
						totalPodCount = pods.size();
						
						for(Pod p : pods) {
							String podStatus = p.getStatus().getPhase();
							if(podStatus.equals("Running")) {
								runningPodCount++;
							}
						}
					}
				}
			} catch (Exception e) {
				log.error("", e);
			}
			
			resDto.setTotalPodCount(totalPodCount);
			resDto.setRunningPodCount(runningPodCount);
			
		}
		
		detail.setJobCount(jobCount);
		detail.setCronJobCount(cronJobCount);
		detail.setDeploymentCount(deploymentCount);
		detail.setDaemonSetCount(daemonSetCount);
		detail.setReplicaSetCount(replicaSetCount);
		detail.setActiveCount(active);
		detail.setSucceededCount(succeeded);
		detail.setFailedCount(failed);
		
		Long clusterIdx = entity.getClusterIdx();
		
		
		ClusterDto.Detail clusterDetail = null;
		try {
			clusterDetail = clusterService.getCluster(clusterIdx);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String provisionStatus = clusterDetail.getProvisioningStatus();
		if(provisionStatus.equals(ClusterEntity.ProvisioningStatus.FINISHED.name())) {
			String prometheusUrl = mlClusterService.getPrometheusUrl(clusterIdx);
			String grafanaUrl = mlClusterService.getGrafanaUrl(clusterIdx);
			String clusterMonitoringUrl = String.format("%s/d/4b545447f/cluster-monitoring?orgId=1&refresh=30s&theme=light&kiosk=tvm", grafanaUrl);
			String gpuMonitoringUrl = String.format("%s/d/syl6zhqkz/gpu-nodes?orgId=1&refresh=30s&theme=light&kiosk=tvm", grafanaUrl);
			
			detail.setPromethusUrl(prometheusUrl);
			detail.setGrafanaUrl(grafanaUrl);
			detail.setMonitoringUrl(clusterMonitoringUrl);
			
		}

		
		detail.setClusters(new ClusterDto.Detail[] {clusterDetail});
		detail.setResources(resources);
		return detail;
	}
	
	@Transactional
	public boolean deleteResource(Long resId) {
		MLResourceEntity mlRes = mlResourceDomainService.get(resId);
		if(mlRes != null) {
			String kind = mlRes.getKind();
			Long id = mlRes.getResourceId();
			String yaml = mlRes.getYaml();		
			
			
			MLServiceInterface mlServiceInterface = getServiceInterface(kind);
			
			//실제 리소스 삭제.
			log.info("ML K8S 리소스 삭제. resId: {}", id);
			boolean isDelete = mlServiceInterface.delete(id, yaml);
			
			//리소스 삭제
			log.info("ML 리소스 삭제. ML ID: {}", resId);
			mlResourceDomainService.deleteById(resId);
			
			return true;
		}
		return false;
	}
	
	
	private MLServiceInterface getServiceInterface(String kind) {
		return serviceFactory.getMLServiceInterface(kind);
	}

}
