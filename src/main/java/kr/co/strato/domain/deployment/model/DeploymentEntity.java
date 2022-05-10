package kr.co.strato.domain.deployment.model;

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
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "deployment")
public class DeploymentEntity {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deployment_idx", unique = true)
	private Long deploymentIdx;
	
	@Column(name = "deployment_name")
	private String deploymentName;
	
	@Column(name = "deployment_uid")
	private String deploymentUid;
	
	@Column(name = "image")
	private String image;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "strategy")
	private String strategy;
	
	@Column(name = "selector")
	private String selector;
	
	@Column(name = "max_surge")
	private Float maxSurge;
	
	@Column(name = "max_unavailable")
	private Float maxUnavailable;
	
	@Column(name = "annotation")
	private String annotation;
	
	@Column(name = "label")
	private String label;
	
	@Column(name = "pod_updated")
	private Integer podUpdated;
	
	@Column(name = "pod_replicas")
	private Integer podReplicas;
	
	@Column(name = "pod_ready")
	private Integer podReady;
	
	@Column(name = "`condition`")
	private String condition;
	
	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name = "namespace_idx")
	private NamespaceEntity namespaceEntity;
	
	@Column(name = "yaml")
	private String yaml;

}
