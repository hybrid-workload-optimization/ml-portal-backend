package kr.co.strato.portal.networking.model;

import kr.co.strato.global.validation.annotation.K8sKind;
import kr.co.strato.global.validation.model.K8sKindType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class K8sServiceDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResListDto{
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
        private Long projectIdx;
        private Long clusterIdx;
        private Long namespaceIdx;
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
    @NoArgsConstructor
    public static class ResDetailDto{
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
        private String yaml;
    }

    @Getter
    @Setter
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
