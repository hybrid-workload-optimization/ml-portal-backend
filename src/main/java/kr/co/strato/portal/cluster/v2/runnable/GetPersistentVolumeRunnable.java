package kr.co.strato.portal.cluster.v2.runnable;

import java.util.List;

import kr.co.strato.portal.cluster.v2.model.PersistentVolumeDto;
import kr.co.strato.portal.cluster.v2.service.PersistentVolumeService;

/**
 * 워크로드 리스트 조회
 * @author hclee
 *
 */
public class GetPersistentVolumeRunnable extends WorkloadRunnable {
	private PersistentVolumeService pvService;
	private Long kubeConfigId;
	
	public GetPersistentVolumeRunnable(PersistentVolumeService pvService, Long kubeConfigId) {
		this.pvService = pvService;
		this.kubeConfigId = kubeConfigId;
	}


	@Override
	public void run() {
		List<PersistentVolumeDto.ListDto> pvList = pvService.getList(kubeConfigId);
		setResult(pvList);
	}

}
