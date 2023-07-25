package kr.co.strato.portal.workload.service;

import java.util.ArrayList;
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
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.workload.service.WorkloadAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.portal.workload.model.WorkloadDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkloadService {
	
	@Autowired
	private ClusterDomainService clusterDomainService;

	@Autowired
	private WorkloadAdapterService workloadAdapterService;
	
	public List<WorkloadDto.List> getList(WorkloadDto.SearchParam param) {
		Long clusterIdx = param.getClusterIdx();
		ClusterEntity entity = clusterDomainService.get(clusterIdx);
		if(entity == null) {
			log.error("Workload 리스트 가져오기 실패!");
			log.error("존재하지 않는 클러스터 입니다. clusterIdx: {}", clusterIdx);
			return null;
		}
		
		List<WorkloadDto.List> result = new ArrayList<>();
		try {			
			Long kubeConfigId = entity.getClusterId();
			ResourceListSearchInfo search = ResourceListSearchInfo.builder()
					.kubeConfigId(kubeConfigId)
					.kinds(param.getKinds())
					.namespace(param.getNamespace())
					.build();
			
			
			List<HasMetadata> list = workloadAdapterService.getList(search);
			
			//uid를 키로 맵으로 변환
			Map<String, WorkloadItem> map = new HashMap<>();
			for(HasMetadata data : list) {
				String uid = data.getMetadata().getUid();
				WorkloadItem item = new WorkloadItem(data);
				map.put(uid, item);
			}
			
			//부모 자식 구조 생성 및 자식 아이템 리스트에서 제거
			for(HasMetadata data : list) {
				List<OwnerReference> ownerRef = data.getMetadata().getOwnerReferences();
				if(ownerRef != null && ownerRef.size() > 0) {
					String uid = data.getMetadata().getUid();
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
					map.remove(uid);
				}
			}
			
			Iterator<String> iter = map.keySet().iterator();
			while(iter.hasNext()) {
				WorkloadItem item = map.get(iter.next());
				HasMetadata data = item.getData();
				
				List<Pod> pods = getPods(item);
				
				String uid = data.getMetadata().getUid();
				String name = data.getMetadata().getName();
				String namespace = data.getMetadata().getNamespace();
				String kind = data.getKind();				
				Map<String, String> labels = data.getMetadata().getLabels();
				Integer podCountTotal = pods.size();
				Integer podCountReady = getReadyPodCount(data, pods);
				String health = getHealth(data, pods, podCountReady);
				String createAt = data.getMetadata().getCreationTimestamp();
				
				WorkloadDto.List listItem = WorkloadDto.List.builder()
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
			Collections.sort(result, new Comparator<WorkloadDto.List>() {

				@Override
				public int compare(WorkloadDto.List o1, WorkloadDto.List o2) {
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
	
	/**
	 * 준비된 파드수를 조회한다.
	 * @param data
	 * @param pods
	 * @return
	 */
	private Integer getReadyPodCount(HasMetadata data, List<Pod> pods) {
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
	private String getHealth(HasMetadata data, List<Pod> pods, Integer readyPodCount) {
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
	
	@Data
	public static class WorkloadItem {
        private HasMetadata data;
        private List<WorkloadItem> children;
        
        public WorkloadItem(HasMetadata data) {
        	this.children = new ArrayList<>();
        	this.data = data;
        }
        
        public void addChild(WorkloadItem child) {
        	if(!this.children.contains(child)) {
        		this.children.add(child);
        	}
        }
        
        public void removeChild(WorkloadItem child) {
        	this.children.remove(child);
        }
    }
} 
