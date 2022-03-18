package kr.co.strato.domain.pod.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(PodPersistentVolumeClaimPK.class)
@Table(name = "pod_persistent_volume_claim")
public class PodPersistentVolumeClaimEntity {
    
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pod_idx")
    private PodEntity pod;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "persistent_volume_claim_idx")
    private PersistentVolumeClaimEntity persistentVolumeClaim;
	
}
