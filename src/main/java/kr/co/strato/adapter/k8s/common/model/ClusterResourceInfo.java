package kr.co.strato.adapter.k8s.common.model;

import io.swagger.annotations.ApiModelProperty;

/**
 * 클러스터 리소스 정보
 */
public class ClusterResourceInfo {
    @ApiModelProperty(value="Cluster ID", example="4", required=true)
    private Integer clusterId;

    @ApiModelProperty(value="리소스 이름", example="name01", required=true)
    private String name;

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}