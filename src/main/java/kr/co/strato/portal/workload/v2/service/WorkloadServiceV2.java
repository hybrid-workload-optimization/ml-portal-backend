package kr.co.strato.portal.workload.v2.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodCondition;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import kr.co.strato.adapter.k8s.common.model.ApplyResult;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.workload.service.WorkloadAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.workload.v2.model.WorkloadCommonDto;
import kr.co.strato.portal.workload.v2.model.WorkloadDto;
import kr.co.strato.portal.workload.v2.model.WorkloadDto.ApplyResultDto;
import kr.co.strato.portal.workload.v2.model.WorkloadDto.ResourceParam;
import kr.co.strato.portal.workload.v2.model.WorkloadItem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkloadServiceV2 {
	
	@Autowired
	private ClusterDomainService clusterDomainService;

	@Autowired
	private WorkloadAdapterService workloadAdapterService;
	
	@Autowired
	private DeploymentServiceV2 deploymentService;
	
	@Autowired
	private StatefulSetServiceV2 statefulSetService;
	
	@Autowired
	private PodServiceV2 podService;
	
	@Autowired
	private CronJobServiceV2 cronJobService;
	
	@Autowired
	private JobServiceV2 jobService;
	
	@Autowired
	private DaemonSetServiceV2 daemonSetService;
	
	@Autowired
	private ReplicaSetServiceV2 replicaSetService;
	
	/**
	 * 워크로드 리스트 조회
	 * @param param
	 * @return
	 */
	public List<WorkloadDto.ListDto> getList(WorkloadDto.SearchParam param) {
		Long clusterIdx = param.getClusterIdx();
		ClusterEntity entity = clusterDomainService.get(clusterIdx);
		if(entity == null) {
			log.error("Workload 리스트 가져오기 실패!");
			log.error("존재하지 않는 클러스터 입니다. clusterIdx: {}", clusterIdx);
			return null;
		}
		
		Long kubeConfigId = entity.getClusterId();
		ResourceListSearchInfo search = ResourceListSearchInfo.builder()
				.kubeConfigId(kubeConfigId)
				.kinds(param.getKinds())
				.name(param.getName())
				.namespace(param.getNamespace())
				.build();
		
		
		List<HasMetadata> list = null;
		try {
			list = workloadAdapterService.getList(search);
		} catch (Exception e) {
			log.error("", e);
			throw new PortalException("리소스 조회 실패!");
		}
		return getList(list, param.getName());
	}
	
	public List<WorkloadDto.ListDto> getList(List<HasMetadata> list) {
		return getList(list, null);
	}
	
	public List<WorkloadDto.ListDto> getList(List<HasMetadata> list, String keyword) {
		List<WorkloadDto.ListDto> result = new ArrayList<>();
		try {
			//uid를 키로 맵으로 변환
			Map<String, WorkloadItem> map = new HashMap<>();
			Map<String, WorkloadItem> mapList = new HashMap<>();
			for(HasMetadata data : list) {
				String uid = data.getMetadata().getUid();
				WorkloadItem item = new WorkloadItem(data);
				map.put(uid, item);
				mapList.put(uid, item);
			}
			
			//부모 자식 구조 생성 및 자식 아이템 리스트에서 제거
			for(HasMetadata data : list) {
				List<OwnerReference> ownerRef = data.getMetadata().getOwnerReferences();
				if(ownerRef != null && ownerRef.size() > 0) {
					String uid = data.getMetadata().getUid();
					String name = data.getMetadata().getName();
					String kind = data.getKind();
					
					for(OwnerReference r : ownerRef) {
						String ownerRefUid = r.getUid();
						
						//자식 리소스로 추가
						WorkloadItem parentItem = map.get(ownerRefUid);
						WorkloadItem childItem = map.get(uid);
						
						if(parentItem != null && childItem != null) {
							parentItem.addChild(childItem);
						} 
					}
					
					//리스트에서 제거
					mapList.remove(uid);
				}
			}
			
			Iterator<String> iter = mapList.keySet().iterator();
			while(iter.hasNext()) {
				WorkloadItem item = mapList.get(iter.next());
				HasMetadata data = item.getData();
				
				List<Pod> pods = getPods(item);
				
				String uid = data.getMetadata().getUid();
				String name = data.getMetadata().getName();
				
				
				if(keyword != null && keyword.length() > 0) {
					//키워드 검색인 경우 이름으로 필터링
					if(!name.contains(keyword)) {
						continue;
					}
				}
				
				String namespace = data.getMetadata().getNamespace();
				String kind = data.getKind();				
				Map<String, String> labels = data.getMetadata().getLabels();
				Integer podCountTotal = pods.size();
				Integer podCountReady = getReadyPodCount(data, pods);
				String health = getHealth(data, pods, podCountReady);
				String createAt = DateUtil.strToNewFormatter(data.getMetadata().getCreationTimestamp());
				
				WorkloadDto.ListDto listItem = WorkloadDto.ListDto.builder()
						.uid(uid)
						.name(name)
						.namespace(namespace)
						.kind(kind)
						.labels(labels)
						.podCountTotal(podCountTotal)
						.podCountReady(podCountReady)
						.health(health)
						.createAt(createAt)
						.build();
				
				result.add(listItem);
			}
			
			//생성일자 기준 오름차순 정렬
			Collections.sort(result, new Comparator<WorkloadDto.ListDto>() {

				@Override
				public int compare(WorkloadDto.ListDto o1, WorkloadDto.ListDto o2) {
					String createAto1 = o1.getCreateAt();
					String createAto2 = o2.getCreateAt();
					return createAto2.compareTo(createAto1);
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public WorkloadCommonDto getDetail(WorkloadDto.DetailParam param) {
		Long clusterIdx = param.getClusterIdx();
		ClusterEntity entity = clusterDomainService.get(clusterIdx);
		
		Long kubeConfigId = entity.getClusterId();
		String kind = param.getKind();
		String namespace = param.getNamespace();
		String name = param.getName();
		
		HasMetadata data = workloadAdapterService.getDetail(kubeConfigId, kind, namespace, name);
		
		WorkloadCommonDto dto = null;
		try {
			if(data != null) {
				dto = toDto(entity, data);
			} else {
				log.error("데이터가 존재하지 않아 상세 정보를 조회할 수 없습니다.");
				log.error("kubeConfigId: {}, kind: {}, namespace: {}, name: {}", kubeConfigId, kind, namespace, name);
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return dto;
	}
	
	/**
	 * 프론트에 내려줄 dto로 변환.
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private WorkloadCommonDto toDto(ClusterEntity clusterEntity, HasMetadata data) throws Exception {
		WorkloadCommonV2 workloadService = null;
		if(data instanceof Deployment) {
			workloadService = deploymentService;
		} else if(data instanceof StatefulSet) {
			workloadService = statefulSetService;
		} else if(data instanceof Pod) {
			workloadService = podService;
		} else if(data instanceof CronJob) {
			workloadService = cronJobService;
		} else if(data instanceof Job) {
			workloadService = jobService;
		} else if(data instanceof DaemonSet) {
			workloadService = daemonSetService;
		} else if(data instanceof ReplicaSet) {
			workloadService = replicaSetService;
		}
		
		WorkloadCommonDto dto = null;
		if(workloadService != null) {
			dto = workloadService.toDto(clusterEntity, data);
		} else {
			log.error("DTO 변환 실패! 지원하지 않는 서비스 타입입니다. kind: {}", data.getKind());
		}
		return dto;
	}
	
	/**
	 * 준비된 파드수를 조회한다.
	 * @param data
	 * @param pods
	 * @return
	 */
	public Integer getReadyPodCount(HasMetadata data, List<Pod> pods) {
		//워크로드 리소스의 파드가 0이면 건강 상태를 알지못함 으로 처리
		Integer readyCount = 0;
		if(pods.size() > 0) {
			for(Pod pod: pods) {
				PodStatus podStatus = pod.getStatus();
				
				Optional<PodCondition> readyOptinal =  
						podStatus.getConditions().stream().filter(c -> c.getType().equals("Ready")).findFirst();
				
				//컨테이너가 준비된 상태가 아니면 건강하지 않음으로 처리 한다.
				if(readyOptinal.isPresent()) {
					String status = readyOptinal.get().getStatus();
					if(status.toLowerCase().equals("true")) {
						readyCount ++;
						continue;
					}
				}
			}
		}
		return readyCount;
	}
	
	/**
	 * 워크로드 리소스의 건강 상태를 조회한다.
	 * Job, CronJob등 파드의 종료가 있는 리소스들은 별도의 채크 로직을 만들어야 함.
	 * @param data
	 * @param pods
	 * @param readyPodCount
	 * @return
	 */
	public String getHealth(HasMetadata data, List<Pod> pods, Integer readyPodCount) {
		//워크로드 리소스의 파드가 0이면 건강 상태를 알지못함 으로 처리
		String health = "Unknown";
		if(pods.size() > 0) {
			health = pods.size() == readyPodCount? "Healthy" : "Unhealthy";
		}
		return health;
	}
	
	
	/**
	 * Workload 리소스의 파드를 구해 리턴
	 * @param item
	 * @return
	 */
	private List<Pod> getPods(WorkloadItem item) {
		List<Pod> list = new ArrayList<>();	
		List<WorkloadItem> children = item.getChildren();
		for(WorkloadItem child: children) {
			if(child.getData() instanceof Pod) {
				list.add((Pod) child.getData());
			}
			
			if(child.getChildren().size() > 0) {
				List<Pod> childPods = getPods(child);
				list.addAll(childPods);
			}
		}
		return list;
	}
	
	/**
	 * Workload 리소스 생성.
	 * @param param
	 * @return
	 */
	public ApplyResultDto apply(WorkloadDto.ApplyDto param) {
		Long clusterIdx = param.getClusterIdx();
		ClusterEntity entity = clusterDomainService.get(clusterIdx);
		
		Long kubeConfigId = entity.getClusterId();
		String yamlStr = Base64Util.decode(param.getYaml());
		
		ApplyResult.Result result = workloadAdapterService.apply(kubeConfigId, yamlStr);
		
		List<WorkloadDto.ListDto> list = null;
		if(result.isSuccess()) {
			list = getList(result.getResources());
		}
		
		ApplyResultDto resultDto = ApplyResultDto.builder()
				.success(result.isSuccess())
				.errorMessage(result.getErrorMessage())
				.resources(list)
				.build();
		return resultDto;
	}
	
	/**
	 * 리소스 삭제
	 * @return
	 */
	public boolean delete(ResourceParam p) {
		ClusterEntity entity = clusterDomainService.get(p.getClusterIdx());
		Long kubeConfigId = entity.getClusterId();
		boolean isOk = workloadAdapterService.delete(kubeConfigId, p.getKind(), p.getNamespace(), p.getName());
		if(isOk) {
			//TODO: 리소스 삭제 완료까지 대기 처리
			
		}
		return isOk;
	}
	
	/**
	 * 리소스 yaml 조회
	 * @param p
	 * @return
	 */
	public String getResourceYaml(ResourceParam p) {
		ClusterEntity entity = clusterDomainService.get(p.getClusterIdx());
		Long kubeConfigId = entity.getClusterId();
		String yaml = workloadAdapterService.resourceYaml(kubeConfigId, p.getKind(), p.getNamespace(), p.getName());
		return Base64.getEncoder().encodeToString(yaml.getBytes());
	}
} 
