package kr.co.strato.portal.cluster.v2.runnable;

import java.util.ArrayList;
import java.util.List;

import kr.co.strato.portal.cluster.v2.model.PersistentVolumeDto;
import kr.co.strato.portal.cluster.v2.service.PersistentVolumeService;
import lombok.extern.slf4j.Slf4j;

/**
 * 워크로드 리스트 조회
 * @author hclee
 *
 */
@Slf4j
public class GetPersistentVolumeRunnable extends WorkloadRunnable {
	private PersistentVolumeService pvService;
	private Long kubeConfigId;
	
	public GetPersistentVolumeRunnable(PersistentVolumeService pvService, Long kubeConfigId) {
		this.pvService = pvService;
		this.kubeConfigId = kubeConfigId;
	}


	@Override
	public void run() {
		List<PersistentVolumeDto.ListDto> pvList = new ArrayList<>();
		try {
			pvList = pvService.getList(kubeConfigId);
		} catch (Exception e) {
			log.error("PersistentVolume 리스트 조회 실패!");
			log.error("", e);
		}
		setResult(pvList);
	}

}
