package kr.co.strato.portal.workload.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.strato.domain.pod.model.PodPersistentVolumeClaimEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PodDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReqCreateDto{
        //TODO validation체크
        private Long clusterId;
        private String yaml;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResListDto{
        private Long id;
        private String name;
        private String namespace;
        private String node;
        private String status;
        private int restart;
        private float cpu;
        private float memory;
        private LocalDateTime createdAt;
        private HashMap<String, Object> label;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SearchParam{
        private Long projectId;
        private Long clusterId;
        private Long namespaceId;
        private Long nodeId;
        private String ownerUid;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResDetailDto{
    	private Long id;
        private String name;
        private String namespace;
        private String uid;
        private String ip;
        private String qosClass;
//        private String image;
        
        private HashMap<String, Object> annotation;
        private HashMap<String, Object> label;
        private LocalDateTime createdAt;
        
        private String node;
        private int restart;
        private String status;
        private String kind;
        
        private List<HashMap<String, Object>> condition;
        private List<HashMap<String, Object>> persistentVolumeClaims;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResOwnerDto{
    	private String type;
    	private String name;
    	private String image;
    	private String pod;
    	private LocalDateTime createdAt;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class OwnerSearchParam{
        private Long clusterId;
        private String nodeName;
        private String namespace;
        private String storageClass;
        private Map<String, String> selector;
        private String ownerName;
        private String ownerUid;
    }
}
