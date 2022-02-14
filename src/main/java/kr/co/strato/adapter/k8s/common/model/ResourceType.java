package kr.co.strato.adapter.k8s.common.model;

public enum ResourceType {
    node,
    namespace,
    persistentvolume,
    storageclass,
    deployment,
    statefulset,
    pod,
    cronjob,
    job,
    replicaset,
    daemonset,
    service,
    ingress,
    endpoints,
    pvc,
    configmap,
    secret;

    public String get(){
        return this.toString();
    }
}
