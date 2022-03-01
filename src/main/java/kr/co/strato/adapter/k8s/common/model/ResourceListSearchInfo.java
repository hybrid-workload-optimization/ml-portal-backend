package kr.co.strato.adapter.k8s.common.model;

import java.util.Map;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 리소스 리스트 검색 정보
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceListSearchInfo {

    @ApiModelProperty(value="kubeConfigId", example="4", required=true)
    private Long kubeConfigId;
    @ApiModelProperty(value="Namespace", example="default", required=false)
    private String namespace;
    //PersistentVolume
    @ApiModelProperty(value="Storage class", example="storage class1", required=false)
    private String storageClass;
    //Pod
    @ApiModelProperty(value="Node name", example="node1", required=false)
    private String nodeName;
    @ApiModelProperty(value="ownerResourceType", example="DaemonSet", required=false)
    private String ownerResourceType;
    @ApiModelProperty(value="ownerName", example="fluentd-elasticsearch", required=false)
    private String ownerName;
    @ApiModelProperty(value="ownerUid", example="84e5b3b2-7f74-4052-ba5b-cb0af917aecf", required=false)
    private String ownerUid;
    //Service
    @ApiModelProperty(value="selector", example="app: sss", required=false)
    private Map<String, String> selector;
    private String name;



}
