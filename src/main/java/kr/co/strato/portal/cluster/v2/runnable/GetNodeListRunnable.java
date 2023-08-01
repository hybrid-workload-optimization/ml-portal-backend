package kr.co.strato.portal.cluster.v2.runnable;

import java.util.List;

import io.fabric8.kubernetes.api.model.Pod;
import kr.co.strato.portal.cluster.v2.model.NodeDto;
import kr.co.strato.portal.cluster.v2.service.NodeService;

/**
 * 워크로드 리스트 조회
 * @author hclee
 *
 */
public class GetNodeListRunnable extends WorkloadRunnable {
	private NodeService nodeService;
	private List<Pod> podList;
	private Long kubeConfigId;
	
	public GetNodeListRunnable(NodeService nodeService, List<Pod> podList, Long kubeConfigId) {
		this.nodeService = nodeService;
		this.podList = podList;
		this.kubeConfigId = kubeConfigId;
	}


	@Override
	public void run() {
		List<NodeDto.ListDto> nodeList = nodeService.getList(kubeConfigId, podList);
		setResult(nodeList);
	}

}
