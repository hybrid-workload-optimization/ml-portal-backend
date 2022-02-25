package kr.co.strato.portal.workload.model;

import kr.co.strato.global.model.PageRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
public class StatefulSetDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReqCreateDto{
        //TODO validation체크
        private Integer clusterId;
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
