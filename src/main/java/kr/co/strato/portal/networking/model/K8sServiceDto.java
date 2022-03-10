package kr.co.strato.portal.networking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;

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
        private String internalEndpoint;
        private String externalEndpoint;
        private String age;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SearchParam{
        private Long projectIdx;
        private Long clusterIdx;
        private Long namespaceIdx;
    }
}
