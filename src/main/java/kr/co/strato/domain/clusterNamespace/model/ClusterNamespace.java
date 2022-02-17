package kr.co.strato.domain.clusterNamespace.model;

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
@Table(name = "namespace")
public class ClusterNamespace {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "namespace_idx")
	private Long id;

	@Column(name = "namespace_name")
	private String name;

	@Column(name = "namespace_uid")
	private String uid;
	
	private String status;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
//	@ManyToOne(fetch = FetchType.LAZY)
//  @JoinColumn(name = "cluster_idx")
//	private Long clusterIdx;
	@Column(name = "cluster_idx")
	private Long clusterIdx;
	
	@Lob
	private String annotation;
	@Lob
	private String label;
	@Lob
	private String condition;
	
}
