package kr.co.strato.domain.machineLearning.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "machine_learning_cluster")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MLClusterEntity {
	
	public static enum ClusterStatus {
		PENDING,
		PROVISIONING,
		PROVISIONING_STARTED,
		PROVISIONING_FINISHED,
		PROVISIONING_FAIL,
		MODIFY_START,
		MODIFY_FINISHED,
		MODIFY_FAIL,		
		DELETE_START,
		DELETE_FAIL,
		SCALE_START,
		SCALE_FAIL,
		STARTED,
		FINISHED,
		DELETING,
		DELETED,
		FAILED,
		SCALE_IN,
		SCALE_OUT
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ml_cluster_idx")
	private Long id;
	
	@OneToOne
    @JoinColumn(name = "cluster_idx")
	private ClusterEntity cluster;
	
	private String clusterType;
	private String createdAt;	
	private String updatedAt;
	private String status;
}
