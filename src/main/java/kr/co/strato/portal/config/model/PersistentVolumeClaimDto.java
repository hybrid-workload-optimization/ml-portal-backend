package kr.co.strato.portal.config.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.strato.domain.pod.model.PodPersistentVolumeClaimEntity;
import kr.co.strato.portal.workload.model.PodDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PersistentVolumeClaimDto {

	private Long projectIdx;
	private Long clusterIdx;
	private Long namespaceIdx;
	private Long persistentVolumeClaimIdx;
	private String yaml;
	
	@Getter
    @Setter
    @NoArgsConstructor
    public static class ResListDto{
        private Long id;
        private String accessType;
        private String namespace;
        private String name;
        private String status;
        private String storageClass;
        private String uid;
        private LocalDateTime createdAt;
    }
	
	@Getter
	@Setter
	public static class List {
		private Long id;
		private String name;
		private String namespace;
		private HashMap<String, Object> label;
		private String status;
		private String storageCapacity;
		private String storageRequest;
		private String accessType;
		private String storageClass;
		private String age;
	}
	
	@Getter
	@Setter
	public static class Detail {
		private Long id;
		private String name;
		private String namespace;
		private String uid;
		//private HashMap<String, Object> label;
		private String status;
		//private String storageCapacity;
		//private String storageRequest;
		private String accessType;
		private String storageClass;
		private String createdAt;
		private Long projectIdx;
	}
	
	@Getter
	@Setter
	public static class Search {
		private Long projectIdx;
		private Long clusterIdx;
		private Long namespaceIdx;
	}
}
