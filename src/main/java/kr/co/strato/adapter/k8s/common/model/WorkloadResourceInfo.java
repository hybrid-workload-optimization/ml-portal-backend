package kr.co.strato.adapter.k8s.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkloadResourceInfo {
    @ApiModelProperty(value="kubeConfigId", example="4", required=true)
    private Integer kubeConfigId;
    @ApiModelProperty(value="리소스 이름", example="name01", required=true)
    private String name;
    @ApiModelProperty(value="namespace 이름", example="namespace01", required=false)
    private String namespace;
}
