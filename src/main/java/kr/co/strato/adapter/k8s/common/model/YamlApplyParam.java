package kr.co.strato.adapter.k8s.common.model;

public class YamlApplyParam {
    private Integer clusterId;
    private String yaml;

    public Integer getClusterId() {
        return clusterId;
    }
    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }
    public String getYaml() {
        return yaml;
    }
    public void setYaml(String yaml) {
        this.yaml = yaml;
    }
}
