package kr.co.strato.portal.alert.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.alert.model.AlertEntity;
import kr.co.strato.domain.alert.service.AlertDomainService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.work.model.WorkJobEntity;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.alert.model.AlertDto;
import kr.co.strato.portal.alert.model.AlertDtoMapper;
import kr.co.strato.portal.work.model.WorkJob.WorkJobStatus;
import kr.co.strato.portal.work.model.WorkJob.WorkJobType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AlertService {
	
	@Autowired
	private ClusterDomainService clusterDomainService;
	
	@Autowired
	private AlertDomainService alertDomainService;
	
	private static Map<String, List<Consumer<ServerSentEvent<AlertDto>>>> consumerMap;
	
	private static Runnable ssePingRunnable;
	
	public static int test_id=200;
	

	public void alert(String[] targetUserIds, String message) {
		
	}
	
	/**
	 * Cluster Job Alert 전달
	 * @param workJobEntity
	 */
	public void alertForClusterJob(WorkJobEntity workJobEntity) {
		WorkJobType workJobType = WorkJobType.valueOf(workJobEntity.getWorkJobType());
		WorkJobStatus workJobStatus	= WorkJobStatus.valueOf(workJobEntity.getWorkJobStatus());
		if(WorkJobStatus.WAITING == workJobStatus) {
			//WAITING 상태는 알람을 전송하지 않는다.
			//WAITING: 사용자가 작업 요청 내린 상태
			return;
		}
		
		String clusterName = workJobEntity.getWorkJobTarget();
		String createUserId = workJobEntity.getCreateUserId();
		
		//알람을 발송할 사용자 아이디
		List<String> userIds = new ArrayList<>();
		
		//클러스터 정보 가져오기
		Long clusterIdx = workJobEntity.getWorkJobReferenceIdx();
		ClusterEntity cluster = clusterDomainService.findByClusterName(clusterName);
		if(cluster != null) {
			
			//클러스터와 연결된 프로젝트의 유저 목록 가져오기
			List<UserEntity> list = clusterDomainService.getClusterUsers(clusterIdx);
			if(list != null && list.size() > 0) {
				userIds.addAll(list.stream().map(u -> u.getUserId()).collect(Collectors.toList()));
			}
		} else {
			log.error("Cluster가 존재하지 않습니다. ClusterName: {}", clusterName);
		}
		
		if(!userIds.contains(createUserId)) {
			userIds.add(createUserId);
		}
		
		for(String userId : userIds) {
			AlertEntity entity = AlertEntity.builder()
					.clusterIdx(clusterIdx)
					.clusterName(clusterName)
					.workJobType(workJobType.name())
					.workJobStatus(workJobStatus.name())
					.workJobIdx(workJobEntity.getWorkJobIdx())
					.createdAt(DateUtil.currentDateTime())
					.userId(userId)
					.confirmYn("N")
					.build();
			//DB 저장
			alertDomainService.register(entity);

			//Client 전송 SSE
			AlertDto dto = AlertDtoMapper.INSTANCE.toDto(entity);
			
			List<Consumer<ServerSentEvent<AlertDto>>> list = (List<Consumer<ServerSentEvent<AlertDto>>>)((ArrayList)getConsumerMap().get(userId)).clone();
			if(list != null) {
				for(Consumer<ServerSentEvent<AlertDto>> consumer : list) {
					if(consumer != null) {
						consumer.accept(ServerSentEvent.<AlertDto>builder().data(dto).build());
					}
				}
			}
		}		
	}
	
	/**
	 * Alert 목록 리턴.
	 * @param loginUser
	 * @return
	 */
	public List<AlertDto> getAlerts(String userId) {
		List<AlertEntity> list = alertDomainService.getList(userId);
		return list.stream().map(e -> AlertDtoMapper.INSTANCE.toDto(e)).collect(Collectors.toList());
	}
	
	/**
	 * 알람 삭제
	 * @param alertIdx
	 * @return
	 */
	public boolean deleteAlert(Long alertIdx) {
		 return alertDomainService.delete(alertIdx);
	}
	
	/**
	 * User 아이디에 할당된 알람 삭제.
	 * @param userId
	 */
	public void deleteByUserId(String userId) {
		alertDomainService.deleteByUserId(userId);
	}
	
	/**
	 * 알람 확인
	 * @param alertIdx
	 * @return
	 */
	public boolean confirm(Long alertIdx) {
		 return alertDomainService.confirm(alertIdx);
	}
	
	
	/**
	 * 작업 타이별 메시지 생성.
	 * @param workJobType
	 * @param clusterName
	 * @return
	 */
	private String genJobTypeMessage(WorkJobType workJobType, String clusterName) {
		String message = null;
		
		if(WorkJobType.CLUSTER_CREATE == workJobType) {
			message = String.format("%s 클러스터 생성", clusterName);
		} else if(WorkJobType.CLUSTER_SCALE == workJobType) {
			message = String.format("%s 클러스터 스케일 조정", clusterName);
		} else if(WorkJobType.CLUSTER_DELETE == workJobType) {
			message = String.format("%s 클러스터 삭제", clusterName);
		}
		return message;
	}
	
	/**
	 * 작업 상태별 메세지 생성.
	 * @param workJobStatus
	 * @param message
	 * @return
	 */
	private String genJobStatusMessage(WorkJobStatus workJobStatus) {
		String message = null;
		if(WorkJobStatus.WAITING == workJobStatus) {
			
		} else if(WorkJobStatus.STARTED == workJobStatus) {
			message = "시작!";
		} else if(WorkJobStatus.SUCCESS == workJobStatus) {
			message = "완료!";
		} else if(WorkJobStatus.FAIL == workJobStatus) {
			message = "실패!";
		}
		return message;
	}
	
	public static Map<String, List<Consumer<ServerSentEvent<AlertDto>>>> getConsumerMap() {
		if(consumerMap == null) {
			consumerMap = new ConcurrentHashMap<>();
		}
		return consumerMap;
	}
	
	public static void addConsumer(String userId, Consumer<ServerSentEvent<AlertDto>> consumer) {
		List<Consumer<ServerSentEvent<AlertDto>>> list = getConsumerMap().get(userId);
		if(list == null) {
			list = Collections.synchronizedList(new ArrayList<Consumer<ServerSentEvent<AlertDto>>>());
			getConsumerMap().put(userId, list);
		}
		
		if(!list.contains(consumer)) {
			list.add(consumer);
			
			//sse ping 실행.
			startSsePing();
		}
	}
	
	public static void removeConsumer(String userId, Consumer<ServerSentEvent<AlertDto>> consumer) {
		List<Consumer<ServerSentEvent<AlertDto>>> list = getConsumerMap().get(userId);
		if(list == null) {
			return;
		}
		
		if(list.contains(consumer)) {
			list.remove(consumer);
		}
		
		if(list.size() == 0) {
			getConsumerMap().remove(userId);
		}
	}
	
	/**
	 * SSE 연결 확인을 위해 1분에 한번씩 null 데이터를 발송.
	 */
	public static void startSsePing() {
		boolean isNull = ssePingRunnable == null;
		log.info("ssePingRunnable is null: {}", isNull);
		if(ssePingRunnable == null) {
			log.info("SSE-Ping Start");
			ssePingRunnable = new Runnable() {
				
				@Override
				public void run() {
					while(true) {
						Map<String, List<Consumer<ServerSentEvent<AlertDto>>>> map = getConsumerMap();
						if(map.size() == 0) {
							ssePingRunnable = null;
							log.info("SSE-Ping Stop - Consumer size: 0");
							break;
						}
						
						Set<String> keySet = map.keySet();
						for(String key : keySet) {
							List<Consumer<ServerSentEvent<AlertDto>>> list = getConsumerMap().get(key);
							if(list != null) {
								for(Consumer<ServerSentEvent<AlertDto>> consumer : list) {
									if(consumer != null) {
										consumer.accept(ServerSentEvent.<AlertDto>builder().data(new AlertDto()).build());
									}
								}
							}
							try {
								//1분에 한번씩 핑 테스트
								Thread.sleep(1000 * 60);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			};
			
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			executorService.submit(ssePingRunnable);
		}
	}
	
	public void sendTest() {
		test_id++;
		Long id = Long.valueOf(test_id);
		AlertDto dto = AlertDto.builder()
				.alertIdx(id)
				.clusterIdx(51L)
				.clusterName("cluster-cmp-prod")
				.createdAt("2022-04-15 16:13:38")
				.workJobStatus(WorkJobStatus.STARTED)
				.workJobType(WorkJobType.CLUSTER_CREATE)
				.confirmYn("N")
				.build();
		
		List<Consumer<ServerSentEvent<AlertDto>>> list = getConsumerMap().get("hclee@strato.co.kr");
		if(list != null) {
			for(Consumer<ServerSentEvent<AlertDto>> consumer : list) {
				if(consumer != null) {
					consumer.accept(ServerSentEvent.<AlertDto>builder().data(dto).build());
				}
			}
		}
	}
}
