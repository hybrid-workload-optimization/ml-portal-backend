package kr.co.strato.domain.job.model;

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

import kr.co.strato.domain.namespace.model.NamespaceEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "job")
public class JobEntity {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_idx", unique = true)
	private Long jobIdx;
	
	@Column(name = "job_name")
	private String jobName;
	
	@Column(name = "job_uid")
	private String jobUid;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "image")
	private String image;
	
	@Column(name = "parallel_execution")
	private String parallelExecution;
	
	@Column(name = "completion_mode")
	private String completionMode;
	
	@Column(name = "annotation")
	private String annotation;
	
	@Column(name = "label")
	private String label;
	
	@Column(name = "status")
	private String status;
	
	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name = "namespace_idx")
	private NamespaceEntity namespaceEntity;
}
