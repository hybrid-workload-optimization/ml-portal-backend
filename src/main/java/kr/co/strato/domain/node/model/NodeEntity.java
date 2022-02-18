package kr.co.strato.domain.node.model;

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
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "node")
public class NodeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "node_idx")
	private Long id;

	@Column(name = "node_name")
	private String name;

	@Column(name = "node_uid")
	private String uid;
	private String ip;
	private String status;
	
	@Column(name = "k8s_version")
	private String k8sVersion;
	
	@Column(name = "allocated_cpu")
	private float allocatedCpu;
	
	@Column(name = "allocated_memory")
	private float allocatedMemory;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "pod_cidr")
	private String podCidr;
	
	@Column(name = "os_image")
	private String osImage;
	
	@Column(name = "kernel_version")
	private String kernelVersion;
	private String architecture;
	
	@Column(name = "kubelet_version")
	private String kubeletVersion;
	
	@Column(name = "kubeproxy_version")
	private String kubeproxyVersion;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cluster_idx")
	//private Long clusterIdx;
	private ClusterEntity clusterIdx;
	
	@Lob
	private String annotation;
	@Lob
	private String label;
	@Lob
	@Column(name = "`condition`")
	private String condition;
	@Lob
	@Column(name = "`role`")
	private String role;
}
