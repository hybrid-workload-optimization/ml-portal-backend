package kr.co.strato.portal.workload.model;

import kr.co.strato.global.validation.annotation.K8sKind;
import kr.co.strato.global.validation.model.K8sKindType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
public class StatefulSetDetailDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReqUpdateDto{
        @K8sKind(value = K8sKindType.StatefulSet)
        private String yaml;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResDetailDto{
        private Long id;
        private String name;
        private String namespace;
        private String uid;
        private String createdAt;
        private String image;
        private HashMap<String, Object> annotation;
        private HashMap<String, Object> label;
        private Integer replicas;
        private Integer readyReplicas;
        private String clusterId;
        private String projectName;
        private String clusterName;
        private String projectIdx;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResPodListDto{
        private String name;
        private String namespace;
        private String node;
        private String status;
        private String createdAt;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResPodDiagramDto{
        private String name;
        private String controller;
        private String image;
        private String status;
        private String createdAt;
        private String lastUpdatedAt;
        private String ip;
        private String node;
    }
}
