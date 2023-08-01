package kr.co.strato.portal.cluster.v2.runnable;

import java.util.ArrayList;
import java.util.List;

import io.fabric8.kubernetes.api.model.Pod;
import kr.co.strato.portal.cluster.v2.model.NodeDto;
import kr.co.strato.portal.cluster.v2.service.NodeService;
import lombok.extern.slf4j.Slf4j;

/**
 * 워크로드 리스트 조회
 * @author hclee
 *
 */
@Slf4j
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
		List<NodeDto.ListDto> nodeList = new ArrayList<>();
		try {
			nodeList = nodeService.getList(kubeConfigId, podList);
		} catch (Exception e) {
			log.error("Node 리스트 조회 실패!");
			log.error("", e);
		}
		setResult(nodeList);
	}

}
