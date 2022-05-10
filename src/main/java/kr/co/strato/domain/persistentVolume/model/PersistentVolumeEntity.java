package kr.co.strato.domain.persistentVolume.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.storageClass.model.StorageClassEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "persistent_volume")
public class PersistentVolumeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "persistent_volume_idx")
	private Long id;

	@Column(name = "persistent_volume_name")
	private String name;

	@Column(name = "persistent_volume_uid")
	private String uid;
	private String status;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "access_mode")
	private String accessMode;
	private String claim;
	private String reclaim;
	
	@Column(name = "reclaim_policy")
	private String reclaimPolicy;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "storage_class_idx")
	private StorageClassEntity storageClass;
	
	
	private String type;
	private String path;
	
	@Column(name = "resource_name")
	private String resourceName;
	
	private int size;

	@Lob
	private String annotation;
	@Lob
	private String label;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cluster_idx")
	private ClusterEntity cluster;
	
	@Column(name = "yaml")
	private String yaml;
	
}
