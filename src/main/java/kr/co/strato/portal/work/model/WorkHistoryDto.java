package kr.co.strato.portal.work.model;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.portal.work.model.WorkHistory.WorkAction;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu1;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu2;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu3;
import kr.co.strato.portal.work.model.WorkHistory.WorkResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkHistoryDto {
	
	private String workMenu1;
	
	private String workMenu2;
	
	private String workMenu3;
	
	private String workAction;
	
	private String target;
	
	private String metadata;
	
	private String result;
	
	private String message;
	
	private String createUserId;
	
	private String createUserName;
	
	private String createdAt;
	
	private Long workJobIdx;
	
	public String getWorkMenu1() {
		return workMenu1;
	}

	public void setWorkMenu1(String workMenu1) {
		this.workMenu1 = workMenu1;
	}

	public String getWorkMenu2() {
		return workMenu2;
	}

	public void setWorkMenu2(String workMenu2) {
		this.workMenu2 = workMenu2;
	}

	public String getWorkMenu3() {
		return workMenu3;
	}

	public void setWorkMenu3(String workMenu3) {
		this.workMenu3 = workMenu3;
	}

	public String getWorkAction() {
		return workAction;
	}

	public void setWorkAction(String workAction) {
		this.workAction = workAction;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public static Builder builder() {
		return new WorkHistoryDto().new Builder();
	}
	
	public Long getWorkJobIdx() {
		return workJobIdx;
	}

	public void setWorkJobIdx(Long workJobIdx) {
		this.workJobIdx = workJobIdx;
	}

	@Override
	public String toString() {
		return "WorkHistoryDto [workMenu1=" + workMenu1 + ", workMenu2=" + workMenu2 + ", workMenu3=" + workMenu3
				+ ", workAction=" + workAction + ", target=" + target + ", metadata=" + metadata + ", result=" + result
				+ ", message=" + message + ", createUserId=" + createUserId + ", createUserName=" + createUserName
				+ ", createdAt=" + createdAt + ", workJobIdx=" + workJobIdx + "]";
	}

	public class Builder {
		
		private WorkMenu1 workMenu1;
		private WorkMenu2 workMenu2;
		private WorkMenu3 workMenu3;
		private WorkAction workAction;
		private String target;
		private String metadata;
		private Map<String, Object> meta;
		private WorkResult result;
		private String message;
		private String createUserId;
		private String createUserName;
		private String createdAt;
		private Long workJobIdx;
		
		public Builder workMenu1(WorkMenu1 workMenu1) {
			this.workMenu1 = workMenu1;
			return this;
		}
		
		public Builder workMenu2(WorkMenu2 workMenu2) {
			this.workMenu2 = workMenu2;
			return this;
		}
		
		public Builder workMenu3(WorkMenu3 workMenu3) {
			this.workMenu3 = workMenu3;
			return this;
		}
		
		public Builder workAction(WorkAction workAction) {
			this.workAction = workAction;
			return this;
		}
		
		public Builder target(String target) {
			this.target = target;
			return this;
		}
		
		/**
		 * Map Struct에서 매개변수의 형이 다른 metadata를 사용하면 컴파일 오류가 나서 metadata를 위한 추가 메서드를 선언함
		 * 
		 * (왜 빌더를 통해서 entity -> dto 로 변환을 해 주는지 모르겠음, 버그인가?)
		 * 
		 * @param meta
		 * @return
		 */
		public Builder meta(Map<String, Object> meta) {
			this.meta = meta;
			return this;
		}
		
		public Builder metadata(String metadata) {
			this.metadata = metadata;
			return this;
		}
		
		public Builder result(WorkResult result) {
			this.result = result;
			return this;
		}
		
		public Builder message(String message) {
			this.message = message;
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
		
		public Builder createdAt(String createdAt) {
			this.createdAt = createdAt;
			return this;
		}
		
		public Builder workJobIdx(Long workJobIdx) {
			this.workJobIdx = workJobIdx;
			return this;
		}
		
		public WorkHistoryDto build() {
			WorkHistoryDto dto = new WorkHistoryDto();
			dto.setWorkMenu1(workMenu1.getMenuName());
			dto.setWorkMenu2(workMenu2.getMenuName());
			dto.setWorkMenu3(workMenu3.getMenuName());
			dto.setWorkAction(workAction.getActionName());
			dto.setTarget(target);
			try {
				if (!meta.isEmpty()) {
					dto.setMetadata(new ObjectMapper().writeValueAsString(meta));
				}
			} catch (JsonProcessingException e) {
				log.warn("[WorkHistoryDto] Can't convert metadata");
			}
			dto.setResult(result.getResultName());
			dto.setMessage(message);
			dto.setCreateUserId(createUserId);
			dto.setCreateUserName(createUserName);
			dto.setCreatedAt(createdAt);
			dto.setWorkJobIdx(workJobIdx);
			
			return dto;
		}
		
	}
	
}
