package kr.co.strato.domain.pod.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

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
    @Column(name = "pod_idx")
    private Long podIdx;

	@Id
    @Column(name = "persistent_volume_claim_idx")
    private Long persistentVolumeClaimIdx;

}
