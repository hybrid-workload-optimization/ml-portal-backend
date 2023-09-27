package kr.co.strato.portal.ml.v1.model;

import kr.co.strato.global.model.PageRequest;
import kr.co.strato.portal.cluster.v1.model.ClusterDto;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MLDto {
	
	/**
	 * ML 유니크 아이디
	 * ML 요청 시 KETI에서 채번하여 내려주는 값
	 * 삭제 또는 추천 자원으로 재 요청 시 최초 요청할때와 동일한 값으로 설정되어야 함.
	 */
	private String mlId;
	
	//ml 이름
	private String name;
	
	//ml 설명
	private String desc;
	
	//전처리, 검증, 학습, 추론, 서비스
	private String mlStepCode;
	
	//ML을 위한 리소스 정의
	private String yaml;	
	
	/**
	 * ML 시작 후 응답 받을 url
	 * 클러스터 생성 후 ML 시작 시 생성 시간이 5~10분 이상 걸릴 수 있으므로
	 * Async로 동작
	 */
	private String callbackUrl;
	
	//ML 상태(start, finish, error, running)
	private String status;
	
	
	@Getter
	@Setter
	public static class ApplyArg {
		private String mlId;
		private String userId;
		private String name;
		private String description;
		private String mlStepCode;
		private String yaml;
		private String callbackUrl;
		private String startCronSchedule;
		private String endCronSchedule;
	}
	
	@Getter
	@Setter
	public static class ListDto {
		private Long id;
		private String mlId;
		private String name;
		private String description;
		private String mlStepCode;
		private String status;
		private String createdAt;
		private String updatedAt;
		private String userId;
	}
	
	@Getter
	@Setter
	public static class ListDtoForPortal {
		private Long id;
		private String mlId;
		private String name;
		private String description;
		private String mlStep;
		private String mlStepCode;
		private String status;
		private String createdAt;
		private String updatedAt;
		private String userId;
		private Integer resourceCount;
	}
	
	@Getter
	@Setter
	public static class DetailForPortal {
		private Long id;
		private String mlId;
		private String userId;
		private String name;
		private String description;
		private String mlStep;
		private String mlStepCode;
		private String status;
		private String createdAt;
		private String updatedAt;
		private String callbackUrl;
		private java.util.List<MLResourceDto> resources;
		private ClusterDto.Detail[] clusters;
		private int jobCount;
		private int cronJobCount;
		private int deploymentCount;
		private int daemonSetCount;
		private int replicaSetCount;
		
		private int activeCount;
		private int succeededCount;			
		private int failedCount;
		
		private String promethusUrl;
		private String grafanaUrl;
		private String monitoringUrl;
	}
	
	@Getter
	@Setter
	public static class ListArg {
		private String userId;
		private String name;
		private PageRequest pageRequest;
	}
	
	@Getter
	@Setter
	public static class Detail {
		private Long id;
		private String mlId;
		private String userId;
		private String name;
		private String description;
		private String mlStepCode;
		private String status;
		private String createdAt;
		private String updatedAt;
		private String callbackUrl;
		private java.util.List<MLResourceDto> resources;
	}
	
	@Getter
	@Setter
	public static class DeleteArg {
		private String mlId;
		private boolean isDeleteCluster;
	}
	
}
