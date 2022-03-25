package kr.co.strato.portal.work.model;

public class WorkJob {

	public static enum WorkJobType {
		
		// Cluster 생성
		CLUSTER_CREATE,
		// Cluster 삭제
		CLUSTER_DELETE,
		// Cluster 스케일
		CLUSTER_SCALE;
		
	}
	
	public static enum WorkJobStatus {
		
		// 성공
		SUCCESS,
		// 실패
		FAIL,
		// 진행 중
		RUNNING,
		// 타임아웃(By REQUEST,RESPONSE-CALLBACK)
		TIMEOUT;
		
	}
	
	public static enum WorkJobData {
		
		HEADER,
		QUERY,
		PATH,
		BODY,
		
	}
	
	public static enum WorkJobState {
		
		// 시작
		STARTED,
		// 종료
		FINISHED
		
	}
}
