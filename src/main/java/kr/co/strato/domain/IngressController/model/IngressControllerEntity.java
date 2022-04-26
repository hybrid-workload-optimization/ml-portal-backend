package kr.co.strato.domain.IngressController.model;

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
@Table(name = "ingress_controller")
public class IngressControllerEntity {
	public static final String SERVICE_TYPE_NODE_PORT = "NodePort";
	public static final String SERVICE_TYPE_EXTERNAL_IPS = "ExternalIPs";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ingress_controller_idx")
	private Long id;

	@Column(name = "ingress_controller_name")
	private String name;
	
	@Column(name = "replicas")
	private Integer replicas;
	
	@Column(name = "service_type")
	private String serviceType;
	
	@Column(name = "external_ip")
	private String externalIp;
	
	@Column(name = "port")
	private String port;

	@Column(name = "ingress_class")
	private String ingressClass;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "default_yn")
	private String defaultYn;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cluster_idx")
	private ClusterEntity cluster;
	
}
