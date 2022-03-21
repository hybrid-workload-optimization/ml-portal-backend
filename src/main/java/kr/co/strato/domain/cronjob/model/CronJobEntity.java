package kr.co.strato.domain.cronjob.model;

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

import kr.co.strato.domain.job.model.JobEntity;
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
@Table(name = "cron_job")
public class CronJobEntity {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cron_job_idx", unique = true)
	private Long cronJobIdx;
	
	@Column(name = "cron_job_name")
	private String cronJobName;
	
	@Column(name = "cron_job_uid")
	private String cronJobUid;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "schedule")
	private String schedule;
	
	@Column(name = "pause")
	private String pause;
	
	@Column(name = "last_schedule")
	private LocalDateTime lastSchedule;
	
	@Column(name = "concurrency_policy")
	private String concurrencyPolicy;
	
	@Column(name = "annotation")
	private String annotation;
	
	@Column(name = "label")
	private String label;
	
	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name = "job_idx")
	private JobEntity jobEntity;
	
	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name = "namespace_idx")
	private NamespaceEntity namespaceEntity;
}
