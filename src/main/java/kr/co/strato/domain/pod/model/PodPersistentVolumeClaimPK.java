package kr.co.strato.domain.pod.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class PodPersistentVolumeClaimPK implements Serializable {

	private Long podIdx;
	private Long persistentVolumeClaimIdx;
}

