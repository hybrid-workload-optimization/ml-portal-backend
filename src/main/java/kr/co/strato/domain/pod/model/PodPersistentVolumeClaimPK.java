package kr.co.strato.domain.pod.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class PodPersistentVolumeClaimPK implements Serializable {

	private Long pod;
	private Long persistentVolumeClaim;
	
	public PodPersistentVolumeClaimPK() {}
	public PodPersistentVolumeClaimPK(Long pod, Long persistentVolumeClaim) {
		this.pod = pod;
		this.persistentVolumeClaim = persistentVolumeClaim;
	}
}

