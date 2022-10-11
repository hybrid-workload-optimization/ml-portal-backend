package kr.co.strato.adapter.cloud.gke.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.strato.adapter.cloud.common.service.AbstractDefaultParamProvider;

public class GKSDefaultParamProvider extends AbstractDefaultParamProvider {

	public static final String KEY_NETWORK = "network";
	public static final String KEY_SUBNETWORK = "subnetwork";
	
	public static final String DEFAULT_CLUSTER_NAME= "cluster01";
	public static final String DEFAULT_KUBELET_VERSION= "1.22.12-gke.300";
	public static final String DEFAULT_REGION= "asia-northeast3-a";
	public static final String DEFAULT_VM_TYPE= "e2-medium";
	public static final String DEFAULT_NETWORK= "strato-vpc-1";
	public static final String DEFAULT_SUBNETWORK= "strato-ne3-subnet-2";
	public static final int DEFAULT_NODE_COUNT= 1;
	
	private static final Map<String, Object> NODE_POOL_1 = new HashMap<String, Object>() {{
		put(KEY_VM_TYPE, 				DEFAULT_VM_TYPE);
		put(KEY_NODE_COUNT, 			DEFAULT_NODE_COUNT);
	}};
	
	private static final List<Map<String, Object>> DEFAULT_NODE_POOLS = new ArrayList<>(Arrays.asList(NODE_POOL_1));
	
	private static final Map<String, Object> DEFAULT_PROVISION_PARAM = new HashMap<String, Object>() {{
		put(KEY_CLUSTER_NAME, 			DEFAULT_CLUSTER_NAME);
		put(KEY_KUBERNETES_VERSION, 	DEFAULT_KUBELET_VERSION);
		put(KEY_REGION, 				DEFAULT_REGION);
		put(KEY_NODE_POOLS, 			DEFAULT_NODE_POOLS);
		put(KEY_NETWORK, 				DEFAULT_NETWORK);
		put(KEY_SUBNETWORK, 			DEFAULT_SUBNETWORK);
	}};
	
	
	private static final Map<String, Object> DEFAULT_DELETE_PARAM = new HashMap<String, Object>() {{
		put(KEY_CLUSTER_NAME, 			DEFAULT_CLUSTER_NAME);
		put(KEY_REGION, 				DEFAULT_REGION);
	}};
	
	private static final Map<String, Object> DEFAULT_SCALE_PARAM = new HashMap<String, Object>() {{
		put(KEY_CLUSTER_NAME, 			DEFAULT_CLUSTER_NAME);
		put(KEY_NODE_COUNT, 			DEFAULT_NODE_COUNT);
		put(KEY_REGION, 				DEFAULT_REGION);
	}};
		
	private static final Map<String, Object> DEFAULT_MODIFT_PARAM = new HashMap<String, Object>() {{
		put(KEY_CLUSTER_NAME, 			DEFAULT_CLUSTER_NAME);
		put(KEY_VM_TYPE, 				DEFAULT_VM_TYPE);
		put(KEY_NODE_COUNT, 			DEFAULT_NODE_COUNT);
		put(KEY_REGION, 				DEFAULT_REGION);
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
