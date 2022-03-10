package kr.co.strato.portal.workload.model;

import java.time.LocalDateTime;
import java.util.HashMap;

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
        private String dayAgo;
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
        private String podUid;
        private String ip;
        private String ownerUid;
        private String qosClass;
//        private String image;
        
        private HashMap<String, Object> label;
        private LocalDateTime createdAt;
        
        private String node;
        private int restart;
        private String status;
        
        // TODO List로 바꾸기
        private HashMap<String, Object> condition;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class OwnerSearchParam{
        private Long clusterId;
        private String ownerUid;
    }
}
