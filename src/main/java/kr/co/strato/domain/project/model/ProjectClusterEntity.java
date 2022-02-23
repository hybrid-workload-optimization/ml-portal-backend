package kr.co.strato.domain.project.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@Table(name="project_cluster")
public class ProjectClusterEntity {

	@Id
	@Column(name="cluster_idx")
	private Long clusterIdx;
	
	@Column(name="project_idx")
	private Long projectIdx;
}
