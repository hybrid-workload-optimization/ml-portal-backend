package kr.co.strato.domain.pod.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
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
@Table(name = "pod_stateful_set")
public class PodStatefulSetEntity {
	
	@Id
	@Column(name = "pod_stateful_set_idx")
	private Long podStatefulSetIdx;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stateful_set_idx")
	private StatefulSetEntity statefulSet;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pod_idx")
	private PodEntity pod;

}
