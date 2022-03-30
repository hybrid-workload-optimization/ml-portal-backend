package kr.co.strato.domain.project.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@IdClass(ProjectClusterPK.class)
@Table(name="project_cluster")
public class ProjectClusterEntity {

	@Id
	@Column(name="cluster_idx")
	private Long clusterIdx;
	
	@Id
	@Column(name="project_idx")
	private Long projectIdx;
	
	@Column(name="added_at")
	private String addedAt;
}
