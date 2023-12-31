package kr.co.strato.domain.ingress.model;

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

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
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
@Table(name = "ingress")
public class IngressEntity {
	public static final String INGRESS_NAME_NGINX = "nginx";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ingress_idx")
	private Long id;

	@Column(name = "ingress_name")
	private String name;

	@Column(name = "ingress_uid")
	private String uid;
	
	@Column(name = "ingress_class")
	private String ingressClass;
	
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@ManyToOne
	@JoinColumn(name = "namespace_idx")
	private NamespaceEntity namespace;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cluster_idx")
	private ClusterEntity cluster;
	
	private String yaml;

}
