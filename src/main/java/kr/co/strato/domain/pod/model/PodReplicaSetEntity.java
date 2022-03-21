package kr.co.strato.domain.pod.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;
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
@Table(name = "pod_replica_set")
public class PodReplicaSetEntity {
	
	@Id
	@GeneratedValue
	@Column(name = "pod_replica_set_idx")
	private Long podReplicaSetIdx;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "replica_set_idx")
	private ReplicaSetEntity replicaSet;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pod_idx")
	private PodEntity pod;

}
