package kr.co.strato.adapter.k8s.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 클러스터 리소스 정보
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClusterResourceInfo {
    @ApiModelProperty(value="kubeConfigId", example="4", required=true)
    private Integer kubeConfigId;

    @ApiModelProperty(value="리소스 이름", example="name01", required=true)
    private String name;

}