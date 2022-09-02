package kr.co.strato.adapter.ml.proxy;

import java.util.Map;

import kr.co.strato.adapter.ml.model.CloudParamDto;

public interface InterfaceProxy {

	/**
     * 클러스터 프로비저닝
     * @param param
     * @return
     */
    public String provisioning(Map<String, Object> param);
    
    /**
     * 클러스터 삭제
     * @param clusterName
     * @return
     */
    public boolean delete(String clusterName);
    
    /**
     * 클러스터 스케일 조정
     * @param arg
     * @return
     */
    public boolean scale(CloudParamDto.ScaleArg arg);
    
}
