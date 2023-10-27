package kr.co.strato.portal.networking.v1.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import kr.co.strato.global.validation.annotation.K8sKind;
import kr.co.strato.global.validation.model.K8sKindType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class K8sServiceOnlyApiDto {

	@Getter
    @Setter    
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResListDto {
        private Long id;
        private String name;
        private String namespace;
        private HashMap<String, Object> label;
        private String type;
        private List<HashMap<String, Object>> internalEndpoints;
        private List<HashMap<String, Object>> externalEndpoints;
        private LocalDateTime age;
        private String clusterName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SearchParam{
        private Long clusterIdx;
        private String namespace;
        private String name;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    public static class DeleteParam{
        private Long clusterIdx;
        private String namespace;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReqCreateDto{
        private Long clusterIdx;
        @K8sKind(value = K8sKindType.Service)
        private String yaml;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReqUpdateDto{
        @K8sKind(value = K8sKindType.Service)
        private String yaml;
    }

    @Getter
    @Setter    
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResDetailDto {
        private Long id;
        private String serviceUid;
        private String serviceName;
        private String namespaceName;
        private HashMap<String, Object> label;
        private HashMap<String, Object> annotation;
        private HashMap<String, Object> selector;
        private LocalDateTime createdAt;
        private String clusterIp;
        private List<HashMap<String, Object>> internalEndpoints;
        private List<HashMap<String, Object>> externalEndpoints;
        private String type;
        private String sessionAffinity;
        private List<ResEndPointListDto> endpoints;
        private Long clusterId;
        private String clusterName;
        private Long clusterIdx;
        private Long projectIdx;
    }

    @Getter
    @Setter    
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResEndPointListDto{
        private Long id;
        private String host;
        private String endpointName;
        private Integer port;
        private String protocol;
        private String nodeName;
        private String ready;
    }
}
