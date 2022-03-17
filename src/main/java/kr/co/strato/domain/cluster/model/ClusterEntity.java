package kr.co.strato.domain.cluster.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import kr.co.strato.domain.node.model.NodeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "cluster")
public class ClusterEntity {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cluster_idx", unique = true)
    private Long clusterIdx;
	
	@Column(name = "cluster_name")
	private String clusterName;
	
	@Column
	private String provider;
	
	@Column(name = "provider_version")
	private String providerVersion;
	
	@Column(name = "kube_config", columnDefinition = "text")
	private String kubeConfig;
	
	@Column(length = 2000)
	private String description;
	
	@Column
	private String status;
	
	@Column(name = "create_user_id")
	private String createUserId;
	
	@Column(name = "create_user_name")
	private String createUserName;
	
	@Column(name = "created_at")
	private String createdAt;
	
	@Column(name = "update_user_id")
	private String updateUserId;
	
	@Column(name = "update_user_name")
	private String updateUserName;
	
	@Column(name = "updated_at")
	private String updatedAt;
	
	@Column(name = "cluster_id")
	private Long clusterId;
	
	@Lob
	private String problem;
	
	@Builder.Default
	@OneToMany(mappedBy = "cluster")
	private List<NodeEntity> nodes = new ArrayList<>();
	
}
