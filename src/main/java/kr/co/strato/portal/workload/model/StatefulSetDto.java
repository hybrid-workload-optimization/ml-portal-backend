package kr.co.strato.portal.workload.model;

import java.time.LocalDateTime;
import java.util.HashMap;

import kr.co.strato.global.validation.annotation.K8sKind;
import kr.co.strato.global.validation.model.K8sKindType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class StatefulSetDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReqCreateDto{
        //TODO validation체크
        private Long clusterIdx;

        @K8sKind(value = K8sKindType.StatefulSet)
        private String yaml;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResListDto{
        private Long id;
        private String name;
        private String namespace;
        private int podCnt;
        private int podTotalCnt;
        private String image;
        private LocalDateTime dayAgo;
        private HashMap<String, Object> label;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class SearchParam{
        private Long projectId;
        private Long clusterId;
        private Long namespaceId;
    }
}
