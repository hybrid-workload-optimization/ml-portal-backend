package kr.co.strato.adapter.cloud.vsphere.service;

import java.util.HashMap;
import java.util.Map;

import kr.co.strato.adapter.cloud.common.service.AbstractDefaultParamProvider;

public class VSphereDefaultParamProvider extends AbstractDefaultParamProvider {
	
	public static final String DEFAULT_CLUSTER_NAME= "cluster01";
	public static final String DEFAULT_KUBELET_VERSION= "v1.25.7";
	public static final int DEFAULT_NODE_COUNT= 1;
	
	private static final Map<String, Object> DEFAULT_PROVISION_PARAM = new HashMap<String, Object>() {{
		put(KEY_CLUSTER_NAME, 			DEFAULT_CLUSTER_NAME);
		put(KEY_KUBERNETES_VERSION,		DEFAULT_KUBELET_VERSION);
	}};
	
	
	private static final Map<String, Object> DEFAULT_DELETE_PARAM = new HashMap<String, Object>() {{
		put(KEY_CLUSTER_NAME, 			DEFAULT_CLUSTER_NAME);
	}};
	
	private static final Map<String, Object> DEFAULT_SCALE_PARAM = new HashMap<String, Object>() {{
		put(KEY_CLUSTER_NAME, 			DEFAULT_CLUSTER_NAME);
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
		return null;
	}

}
