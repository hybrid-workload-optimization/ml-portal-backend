package kr.co.strato.domain.storageClass.model;

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
@Table(name = "storage_class")
public class StorageClassEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "storage_class_idx")
	private Long id;

	@Column(name = "storage_class_name")
	private String name;

	@Column(name = "storage_class_uid")
	private String uid;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	private String provider;
	
	private String type;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cluster_idx")
	private ClusterEntity clusterIdx;
	
	@Lob
	private String annotation;
	@Lob
	private String label;

	
}
