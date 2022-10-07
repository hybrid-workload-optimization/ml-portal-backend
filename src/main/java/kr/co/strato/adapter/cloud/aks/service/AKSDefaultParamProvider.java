package kr.co.strato.adapter.cloud.aks.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.strato.adapter.cloud.common.service.AbstractDefaultParamProvider;

public class AKSDefaultParamProvider extends AbstractDefaultParamProvider {
	
	public static final String DEFAULT_CLUSTER_NAME= "cluster01";
	public static final String DEFAULT_KUBELET_VERSION= "1.23.12";
	public static final String DEFAULT_REGION= "koreacentral";
	public static final String DEFAULT_VM_TYPE= "Standard_DS2_v2";
	public static final int DEFAULT_NODE_COUNT= 1;
	
	private static final Map<String, Object> NODE_POOL_1 = new HashMap<String, Object>() {{
		put(KEY_VM_TYPE, 			DEFAULT_VM_TYPE);
		put(KEY_NODE_COUNT, 		DEFAULT_NODE_COUNT);
	}};
	
	private static final List<Map<String, Object>> DEFAULT_NODE_POOLS = new ArrayList<>(Arrays.asList(NODE_POOL_1));
	
	private static final Map<String, Object> DEFAULT_PROVISION_PARAM = new HashMap<String, Object>() {{
		put(KEY_CLUSTER_NAME, 			DEFAULT_CLUSTER_NAME);
		put(KEY_KUBERNETES_VERSION,		DEFAULT_KUBELET_VERSION);
		put(KEY_REGION,					DEFAULT_REGION);
		put(KEY_NODE_POOLS, 			DEFAULT_NODE_POOLS);
	}};
	
	
	private static final Map<String, Object> DEFAULT_DELETE_PARAM = new HashMap<String, Object>() {{
		put(KEY_CLUSTER_NAME, 			DEFAULT_CLUSTER_NAME);
	}};
	
	private static final Map<String, Object> DEFAULT_SCALE_PARAM = new HashMap<String, Object>() {{
		put(KEY_CLUSTER_NAME, 			DEFAULT_CLUSTER_NAME);
		put(KEY_NODE_COUNT, 			DEFAULT_NODE_COUNT);
	}};
		
	private static final Map<String, Object> DEFAULT_MODIFT_PARAM = new HashMap<String, Object>() {{
		put(KEY_CLUSTER_NAME, 			DEFAULT_CLUSTER_NAME);
		put(KEY_VM_TYPE, 				DEFAULT_VM_TYPE);
		put(KEY_NODE_COUNT, 			DEFAULT_NODE_COUNT);
	}};

	@Override
	protected Map<String, Object> getDefaultProvisioningParam() {
		return DEFAULT_PROVISION_PARAM;
	}

	@Override
	protected Map<String, Object> getDefaultDeleteParam() {
		return DEFAULT_DELETE_PARAM;
	}

	@Override
	protected Map<String, Object> getDefaultScaleParam() {
		return DEFAULT_SCALE_PARAM;
	}

	@Override
	protected Map<String, Object> getDefaultModify() {
		return DEFAULT_MODIFT_PARAM;
	}

}
