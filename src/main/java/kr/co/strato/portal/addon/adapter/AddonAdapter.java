package kr.co.strato.portal.addon.adapter;

import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.HasMetadata;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.portal.addon.model.Addon;
import kr.co.strato.portal.addon.service.AddonService;

public interface AddonAdapter {
	
	/**
	 * k8s 리소스에 파라메터를 반영한다.
	 * @param resource
	 * @param parameters
	 * @return
	 */
	public List<HasMetadata> setParameter(List<HasMetadata> resource, Map<String, Object> parameters);
	
	/**
	 * Addon 실제 endpoints 반영. 
	 * @param addon
	 */
	public void setDetails(AddonService service, ClusterEntity cluster, Addon addon);
}
