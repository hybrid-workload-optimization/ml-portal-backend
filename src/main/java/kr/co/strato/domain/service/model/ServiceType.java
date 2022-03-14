package kr.co.strato.domain.service.model;

import java.util.Locale;

public enum ServiceType {
    ClusterIP, LoadBalancer, NodePort;

    public String get(){
        return this.toString();
    }

    public static ServiceType get(String type){
        if("CLUSTERIP".equals(type.toUpperCase())){
            return ServiceType.ClusterIP;
        }else if("LOADBALANCER".equals(type.toUpperCase())){
            return ServiceType.LoadBalancer;
        }else if("NODEPORT".equals(type.toUpperCase())){
            return ServiceType.NodePort;
        }
        return null;
    }
}
