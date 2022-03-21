package kr.co.strato.domain.pod.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import kr.co.strato.domain.job.model.JobEntity;
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
@Table(name = "pod_job")
public class PodJobEntity {
	
	@Id
	@GeneratedValue
	@Column(name = "pod_job_idx")
	private Long podJobIdx;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_idx")
	private JobEntity job;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pod_idx")
	private PodEntity pod;

}
