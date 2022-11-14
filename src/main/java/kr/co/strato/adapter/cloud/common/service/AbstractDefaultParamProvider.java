package kr.co.strato.adapter.cloud.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractDefaultParamProvider {
	public static final String KEY_CLUSTER_NAME = "clusterName";
	public static final String KEY_CLUSTER_DESC = "clusterDesc";
	public static final String KEY_KUBERNETES_VERSION = "kubernetesVersion";
	public static final String KEY_REGION = "region";
	public static final String KEY_NODE_POOLS = "nodePools";
	public static final String KEY_NODE_COUNT = "nodeCount";
	public static final String KEY_VM_TYPE = "vmType";
	
	protected abstract Map<String, Object> getDefaultProvisioningParam();
	protected abstract Map<String, Object> getDefaultDeleteParam();
	protected abstract Map<String, Object> getDefaultScaleParam();
	protected abstract Map<String, Object> getDefaultModify();
	
	
	public Map<String, Object> genProvisioningParam(String clusterName, String clusterDesc, String kubeletVersion, String region, String vmType, Integer nodeCount) {
		Map<String, Object> param = new HashMap<>();
		param.putAll(getDefaultProvisioningParam());
		
		List<Map<String, Object>> nodePools = new ArrayList<>();
		Map<String, Object> nodePool = new HashMap<>();
		nodePool.putAll(((List<Map<String, Object>>)param.get(KEY_NODE_POOLS)).get(0));
		
		
		nodePools.add(nodePool);
		param.put(KEY_NODE_POOLS, nodePools);	
		
		
		if(clusterName != null) {
			param.put(KEY_CLUSTER_NAME, clusterName);
		}
		
		if(clusterDesc != null) {
			param.put(KEY_CLUSTER_DESC, clusterDesc);
		}
		
		if(kubeletVersion != null) {
			param.put(KEY_KUBERNETES_VERSION, kubeletVersion);
		}
		
		if(region != null) {
			param.put(KEY_REGION, region);
		}
		
		if(vmType != null) {
			nodePool.put(KEY_VM_TYPE, vmType);
		}
		
		if(nodeCount != null) {
			nodePool.put(KEY_NODE_COUNT, nodeCount);
		}
		
		return param;
	}
	
	public Map<String, Object> genDeleteParam(String clusterName, String region) {
		Map<String, Object> param = new HashMap<>();
		param.putAll(getDefaultDeleteParam());
		
		if(clusterName != null) {
			param.put(KEY_CLUSTER_NAME, clusterName);
		}
		
		if(region != null) {
			param.put(KEY_REGION, region);
		}
		
		return param;
	}
	
	public Map<String, Object> genScaleParam(String clusterName, String region, Integer nodeCount) {
		Map<String, Object> param = new HashMap<>();
		param.putAll(getDefaultScaleParam());
		
		if(clusterName != null) {
			param.put(KEY_CLUSTER_NAME, clusterName);
		}
		
		if(region != null) {
			param.put(KEY_REGION, region);
		}
		
		if(nodeCount != null) {
			param.put(KEY_NODE_COUNT, nodeCount);
		}
		
		return param;
	}
	
	public Map<String, Object> genModifyParam(String clusterName, String region, String vmType, Integer nodeCount) {
		Map<String, Object> param = new HashMap<>();
		param.putAll(getDefaultScaleParam());
		
		if(clusterName != null) {
			param.put(KEY_CLUSTER_NAME, clusterName);
		}
		
		if(region != null) {
			param.put(KEY_REGION, region);
		}
		
		if(vmType != null) {
			param.put(KEY_VM_TYPE, vmType);
		}
		
		if(nodeCount != null) {
			param.put(KEY_NODE_COUNT, nodeCount);
		}
		
		return param;
	}
	
	
	public Map<String, Object> newProvisioningParam() {
		Map<String, Object> param = new HashMap<>();
		param.putAll(getDefaultProvisioningParam());
		return param;
	}
	
	public Map<String, Object> newDeleteParam() {
		Map<String, Object> param = new HashMap<>();
		param.putAll(getDefaultDeleteParam());
		return param;
	}
	
	public Map<String, Object> newScaleParam() {
		Map<String, Object> param = new HashMap<>();
		param.putAll(getDefaultScaleParam());
		return param;
	}
	
	public Map<String, Object> newModify() {
		Map<String, Object> param = new HashMap<>();
		param.putAll(getDefaultModify());
		return param;
	}
	 
	public Map<String, Object> provisioning(Map<String, Object> param) {
		return set(getDefaultProvisioningParam(), param);
	}

	public Map<String, Object> delete(Map<String, Object> param) {
		return set(getDefaultDeleteParam(), param);
	}

	public Map<String, Object> scale(Map<String, Object> param) {
		return set(getDefaultScaleParam(), param);
	}

	public Map<String, Object> modify(Map<String, Object> param) {
		return set(getDefaultModify(), param);
	}

	public boolean isVaildProvisioningParam(Map<String, Object> param) {
		return isVaildParam(getDefaultProvisioningParam(), param);
	}

	public boolean isVaildDeleteParam(Map<String, Object> param) {
		return isVaildParam(getDefaultDeleteParam(), param);
	}

	public boolean isVaildScaleParam(Map<String, Object> param) {
		return isVaildParam(getDefaultScaleParam(), param);
	}

	public boolean isVaildModifyParam(Map<String, Object> param) {
		return isVaildParam(getDefaultModify(), param);
	}
	
	private Map<String, Object> set(Map<String, Object> defaultParam, Map<String, Object> targetParam) {
		Iterator<String> iter = defaultParam.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			
			Object value = targetParam.get(key);
			if(value == null) {
				//디폴트값 셋팅.
				targetParam.put(key, defaultParam.get(key));
			}
		}
		return targetParam;
	}
	
	/**
	 * 필수 파라메터 충족 여부 리턴.
	 * @param defaultParam
	 * @param targetParam
	 * @return
	 */
	private boolean isVaildParam(Map<String, Object> defaultParam, Map<String, Object> targetParam) {
		Iterator<String> iter = defaultParam.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			
			Object value = targetParam.get(key);
			if(value == null) {
				return false;
			}
		}
		return true;
	}
}
