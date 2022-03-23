package kr.co.strato.portal.work.model;

import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.portal.work.model.WorkJob.WorkJobStatus;
import kr.co.strato.portal.work.model.WorkJob.WorkJobType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkJobDto {

	private Long workJobIdx;
	
	private String workJobType;
	
	private String workJobTarget;
	
	private String workJobStatus;
	
	private String workJobMessage;
	
	private String workJobDataRequest;
	
	private String workJobDataResponse;
	
	private String workJobStartAt;
	
	private String workJobEndAt;
	
	private String workSyncYn;
	
	private String createUserId;
	
	private String createUserName;

	private Long workJobReferenceIdx;
	
	
	public Long getWorkJobIdx() {
		return workJobIdx;
	}

	public void setWorkJobIdx(Long workJobIdx) {
		this.workJobIdx = workJobIdx;
	}

	public String getWorkJobType() {
		return workJobType;
	}

	public void setWorkJobType(String workJobType) {
		this.workJobType = workJobType;
	}

	public String getWorkJobTarget() {
		return workJobTarget;
	}

	public void setWorkJobTarget(String workJobTarget) {
		this.workJobTarget = workJobTarget;
	}

	public String getWorkJobStatus() {
		return workJobStatus;
	}

	public void setWorkJobStatus(String workJobStatus) {
		this.workJobStatus = workJobStatus;
	}

	public String getWorkJobMessage() {
		return workJobMessage;
	}

	public void setWorkJobMessage(String workJobMessage) {
		this.workJobMessage = workJobMessage;
	}

	public String getWorkJobDataRequest() {
		return workJobDataRequest;
	}

	public void setWorkJobDataRequest(String workJobDataRequest) {
		this.workJobDataRequest = workJobDataRequest;
	}

	public String getWorkJobDataResponse() {
		return workJobDataResponse;
	}

	public void setWorkJobDataResponse(String workJobDataResponse) {
		this.workJobDataResponse = workJobDataResponse;
	}

	public String getWorkJobStartAt() {
		return workJobStartAt;
	}

	public void setWorkJobStartAt(String workJobStartAt) {
		this.workJobStartAt = workJobStartAt;
	}

	public String getWorkJobEndAt() {
		return workJobEndAt;
	}

	public void setWorkJobEndAt(String workJobEndAt) {
		this.workJobEndAt = workJobEndAt;
	}

	public String getWorkSyncYn() {
		return workSyncYn;
	}

	public void setWorkSyncYn(String workSyncYn) {
		this.workSyncYn = workSyncYn;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	
	public static Builder builder() {
		return new WorkJobDto().new Builder();
	}
	
	public Long getWorkJobReferenceIdx() {
		return workJobReferenceIdx;
	}

	public void setWorkJobReferenceIdx(Long workJobReferenceIdx) {
		this.workJobReferenceIdx = workJobReferenceIdx;
	}

	@Override
	public String toString() {
		return "WorkJobDto [workJobIdx=" + workJobIdx + ", workJobType=" + workJobType + ", workJobTarget="
				+ workJobTarget + ", workJobStatus=" + workJobStatus + ", workJobMessage=" + workJobMessage
				+ ", workJobDataRequest=" + workJobDataRequest + ", workJobDataResponse=" + workJobDataResponse
				+ ", workJobStartAt=" + workJobStartAt + ", workJobEndAt=" + workJobEndAt + ", workSyncYn=" + workSyncYn
				+ ", createUserId=" + createUserId + ", createUserName=" + createUserName + ", workJobReferenceIdx="
				+ workJobReferenceIdx + "]";
	}


	public class Builder {
		
		private Long workJobIdx;
		private WorkJobType workJobType;
		private String workJobTarget;
		private WorkJobStatus workJobStatus;
		private String workJobMessage;
		private String workJobDataRequest;
		private Map<String, Object> workJobRequest;
		private String workJobDataResponse;
		private Map<String, Object> workJobResponse;
		private String workJobStartAt;
		private String workJobEndAt;
		private String workSyncYn;
		private String createUserId;
		private String createUserName;
		private Long workJobReferenceIdx;
		
		public Builder workJobIdx(Long workJobIdx) {
			this.workJobIdx = workJobIdx;
			return this;
		}
		
		public Builder workJobType(WorkJobType workJobType) {
			this.workJobType = workJobType;
			return this;
		}
		
		public Builder workJobTarget(String workJobTarget) {
			this.workJobTarget = workJobTarget;
			return this;
		}
		
		public Builder workJobStatus(WorkJobStatus workJobStatus) {
			this.workJobStatus = workJobStatus;
			return this;
		}
		
		public Builder workJobMessage(String workJobMessage) {
			this.workJobMessage = workJobMessage;
			return this;
		}
		
		public Builder workJobDataRequest(String workJobDataRequest) {
			this.workJobDataRequest = workJobDataRequest;
			return this;
		}
		
		/**
		 * Map Struct 를 통한 변환 에러를 위하여 추가 선언
		 * 
		 * @param workJobRequest
		 * @return
		 */
		public Builder workJobRequest(Map<String, Object> workJobRequest) {
			this.workJobRequest = workJobRequest;
			return this;
		}
		
		public Builder workJobDataResponse(String workJobDataResponse) {
			this.workJobDataResponse = workJobDataResponse;
			return this;
		}
		
		/**
		 * Map Struct 를 통한 변환 에러를 위하여 추가 선언
		 * 
		 * @param workJobResponse
		 * @return
		 */
		public Builder workJobResponse(Map<String, Object> workJobResponse) {
			this.workJobResponse = workJobResponse;
			return this;
		}
		
		public Builder workJobStartAt(String workJobStartAt) {
			this.workJobStartAt = workJobStartAt;
			return this;
		}
		
		public Builder workJobEndAt(String workJobEndAt) {
			this.workJobEndAt = workJobEndAt;
			return this;
		}
		
		public Builder workSyncYn(String workSyncYn) {
			this.workSyncYn = workSyncYn;
			return this;
		}
		
		public Builder createUserId(String createUserId) {
			this.createUserId = createUserId;
			return this;
		}
		
		public Builder createUserName(String createUserName) {
			this.createUserName = createUserName;
			return this;
		}
		
		public Builder workJobReferenceIdx(Long workJobReferenceIdx) {
			this.workJobReferenceIdx = workJobReferenceIdx;
			return this;
		}
		
		public WorkJobDto build() {
			WorkJobDto dto = new WorkJobDto();
			dto.setWorkJobIdx(workJobIdx);
			dto.setWorkJobType(workJobType.name());
			dto.setWorkJobTarget(workJobTarget);
			dto.setWorkJobStatus(workJobStatus.name());
			dto.setWorkJobMessage(workJobMessage);
			try {
				if (!CollectionUtils.isEmpty(workJobRequest)) {
					dto.setWorkJobDataRequest(new ObjectMapper().writeValueAsString(workJobRequest));
				}
			} catch (JsonProcessingException e) {
				log.warn("[WorkHistoryDto] Can't convert work job data request");
			}
			try {
				if (!CollectionUtils.isEmpty(workJobResponse)) {
					dto.setWorkJobDataResponse(new ObjectMapper().writeValueAsString(workJobResponse));
				}
			} catch (JsonProcessingException e) {
				log.warn("[WorkHistoryDto] Can't convert work job data response");
			}
			dto.setWorkJobStartAt(workJobStartAt);
			dto.setWorkJobEndAt(workJobEndAt);
			dto.setWorkSyncYn(workSyncYn);
			dto.setCreateUserId(createUserId);
			dto.setCreateUserName(createUserName);
			dto.setWorkJobReferenceIdx(workJobReferenceIdx);
			
			return dto;
		}
		
	}
}
