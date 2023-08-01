package kr.co.strato.portal.cluster.v2.runnable;

import java.util.List;

import io.fabric8.kubernetes.api.model.Pod;
import kr.co.strato.portal.cluster.v2.model.NamespaceDto;
import kr.co.strato.portal.cluster.v2.service.NamespaceService;

/**
 * 워크로드 리스트 조회
 * @author hclee
 *
 */
public class GetNamespaceListRunnable extends WorkloadRunnable {
	private NamespaceService namespaceService;
	private List<Pod> podList;
	private Long kubeConfigId;
	
	public GetNamespaceListRunnable(NamespaceService namespaceService, List<Pod> podList, Long kubeConfigId) {
		this.namespaceService = namespaceService;
		this.podList = podList;
		this.kubeConfigId = kubeConfigId;
	}


	@Override
	public void run() {
		List<NamespaceDto.ListDto> namespaceList = namespaceService.getList(kubeConfigId, podList);
		setResult(namespaceList);
	}

}
