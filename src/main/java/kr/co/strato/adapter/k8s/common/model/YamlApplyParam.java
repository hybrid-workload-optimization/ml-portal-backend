package kr.co.strato.adapter.k8s.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YamlApplyParam {
    private Long kubeConfigId;
    private String yaml;

}
