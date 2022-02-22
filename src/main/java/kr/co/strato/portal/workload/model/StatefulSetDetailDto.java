package kr.co.strato.portal.workload.model;

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
        private String yaml;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResDetailDto{
        private String name;
        private String namespace;
        private String uid;
        private String annotation;
        private String createdAt;
        private String image;
        private HashMap<String, Object> label;
        private Integer runningReplicas;
        private Integer desiredReplicas;
    }
}
