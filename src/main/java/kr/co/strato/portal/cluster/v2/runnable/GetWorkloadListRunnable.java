package kr.co.strato.portal.cluster.v2.runnable;

import java.util.ArrayList;
import java.util.List;

import io.fabric8.kubernetes.api.model.HasMetadata;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.workload.service.WorkloadAdapterService;
import lombok.extern.slf4j.Slf4j;

/**
 * 워크로드 리스트 조회
 * @author hclee
 *
 */
@Slf4j
public class GetWorkloadListRunnable extends WorkloadRunnable {
	private WorkloadAdapterService workloadAdapterService;
	private Long kubeConfigId;
	
	public GetWorkloadListRunnable(WorkloadAdapterService workloadAdapterService, Long kubeConfigId) {
		this.workloadAdapterService = workloadAdapterService;
		this.kubeConfigId = kubeConfigId;
	}


	@Override
	public void run() {
		ResourceListSearchInfo search = ResourceListSearchInfo.builder()
				.kubeConfigId(kubeConfigId)
				.build();		
		List<HasMetadata> list = new ArrayList<>();
		try {
			list = workloadAdapterService.getList(search);
		} catch (Exception e) {
			log.error("Workload 리스트 조회 실패!");
			log.error("", e);
		}
		setResult(list);
	}

}
