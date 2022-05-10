package kr.co.strato.domain.persistentVolumeClaim.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.pod.model.PodPersistentVolumeClaimEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "persistent_volume_claim")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersistentVolumeClaimEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "persistent_volume_claim_idx")
	private Long id;
	
	@Column(name = "persistent_volume_claim_name")
	private String name;
	
	@Column(name = "persistent_volume_claim_uid")
	private String uid;
	
	@Column(name = "created_at")
	private String createdAt;
	
	//@Column(name = "status")
	//private String status;
	
	@Column(name = "storage_class")
	private String storageClass;
	
	@Column(name = "access_type")
	private String accessType;
	
	@Column(name = "storage_capacity")
	private String storageCapacity;
	
	@Column(name = "storage_request")
	private String storageRequest;
	
	@ManyToOne
    @JoinColumn(name = "namespace_idx")
    private NamespaceEntity namespace;
	
	@OneToMany(mappedBy = "persistentVolumeClaim")
    private List<PodPersistentVolumeClaimEntity> podPersistentVolumeClaims;
	
	private String yaml;
}
