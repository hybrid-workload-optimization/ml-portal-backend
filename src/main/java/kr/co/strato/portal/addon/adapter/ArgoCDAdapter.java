package kr.co.strato.portal.addon.adapter;

import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.HasMetadata;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.portal.addon.model.Addon;
import kr.co.strato.portal.addon.service.AddonService;

public class ArgoCDAdapter implements AddonAdapter {

	@Override
	public List<HasMetadata> setParameter(List<HasMetadata> resource, Map<String, Object> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDetails(AddonService service, ClusterEntity cluster, Addon addon) {
				
	}

}
