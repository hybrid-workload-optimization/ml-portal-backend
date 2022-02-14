package kr.co.strato.adapter.k8s.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * 리소스 리스트 검색 정보
 */
public class ResourceListSearchInfo {

    @ApiModelProperty(value="Cluster ID", example="4", required=true)
    private Integer clusterId;
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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public String getNamespace() {
        return namespace;
    }
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNodeName() {
        return nodeName;
    }
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    public String getOwnerResourceType() {
        return ownerResourceType;
    }

    public void setOwnerResourceType(String ownerResourceType) {
        this.ownerResourceType = ownerResourceType;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public Map<String, String> getSelector() {
        return selector;
    }

    public void setSelector(Map<String, String> selector) {
        this.selector = selector;
    }
}
