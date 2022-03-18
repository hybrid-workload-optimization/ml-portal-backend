package kr.co.strato.domain.persistentVolumeClaim.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import kr.co.strato.domain.namespace.model.NamespaceEntity;
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
	private LocalDateTime createdAt;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "storage_class")
	private String storageClass;
	
	@Column(name = "access_type")
	private String accessType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "namespace_idx")
	private NamespaceEntity namespace;
	
}
