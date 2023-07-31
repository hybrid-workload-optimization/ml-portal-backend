package kr.co.strato.adapter.k8s.common.model;

public enum ResourceType {
    cluster,
	node,
    namespace,
    persistentVolume,
    storageClass,
    deployment,
    statefulSet,
    pod,
    cronJob,
    job,
    replicaSet,
    daemonSet,
    service,
    ingress,
    endpoints,
    pvc,
    configMap,
    secret,
    ingressClass,
    resourceQuota;

    public String get(){
        return this.toString();
    }
}
