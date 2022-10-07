package kr.co.strato.adapter.cloud.common.proxy;

import java.util.Map;

public interface InterfaceProxy {

	/**
     * 클러스터 프로비저닝
     * @param param
     * @return
     */
    //public String provisioning(Map<String, Object> headers, Map<String, Object> param);    
    public String provisioning(Map<String, Object> param);
    
    /**
     * 클러스터 삭제
     * @param clusterName
     * @return
     */
    //public boolean delete(Map<String, Object> headers, Map<String, Object> param);
    public boolean delete(Map<String, Object> param);
    
    /**
     * 클러스터 스케일 조정
     * @param arg
     * @return
     */
    //public boolean scale(Map<String, Object> headers, Map<String, Object> param);
    public boolean scale(Map<String, Object> param);
    
    /**
     * node pool 변경
     * @param param
     * @return
     */
   // public boolean modify(Map<String, Object> headers, Map<String, Object> param);
    public boolean modify(Map<String, Object> param);
    
}
