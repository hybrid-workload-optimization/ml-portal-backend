package kr.co.strato.adapter.k8s.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class YamlApplyParam {
    private Long kubeConfigId;
    private String yaml;
    private String namespace;

}
