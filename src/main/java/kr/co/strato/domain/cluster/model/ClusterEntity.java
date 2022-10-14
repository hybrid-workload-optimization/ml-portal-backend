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

/**
 * @see : 2022.04.07
 * Relation이 있는 Entity Class에는 ToString 하지 말것!!
 * 각 Entity Class에서 ToString을 하고 출력할때 런타임 오류 발생됨.
 *
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cluster")
public class ClusterEntity {
	
	public static enum ProvisioningType {
		KUBECONFIG,
		KUBESPRAY,
		AKS,
		GKE,
		EKS,
		NAVER
	}
	
	public static enum ProvisioningStatus {
		PENDING,
		READY,
		STARTED,
		FINISHED,
		DELETING,
		FAILED,
		SCALE,
		SCALE_IN,
		SCALE_OUT,
		MODIFY
	}
	
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
	
	@Column(name = "provisioning_type")
	private String provisioningType;
	
	@Column(name = "provisioning_status")
	private String provisioningStatus;
	
	@Lob
	@Column(name = "provisioning_log")
	private String provisioningLog;
	
	@Column(name = "provisioning_user")
	private String provisioningUser;
	
	@Column(name = "vm_type")
	private String vmType;
	
	@Column(name = "node_count")
	private Integer nodeCount;
	
	@Column(name = "use_type")
	private String useType;
	
	@Column(name = "region")
	private String region;
	
	@Column(name = "network_location")
	private String networkLocation;
	
}
