package kr.co.strato.adapter.k8s.common.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YamlApplyParam {
    private Integer kubeConfigId;
    private String yaml;

}
