package kr.co.strato.portal.workload.model;

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
        private Long podId;
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
        private String cpu;
        private String memory;
        private String dayAgo;
        private HashMap<String, Object> label;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SearchParam{
        private Long projectId;
        private Long clsuterId;
        private Long namespaceId;
    }
}
