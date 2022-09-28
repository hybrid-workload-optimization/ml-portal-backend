package kr.co.strato.domain.machineLearning.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import kr.co.strato.domain.project.model.ProjectEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "machine_learning_project_mapping")
@Getter
@Setter
@ToString
public class MLProjectMappingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ml_project_mapping_idx")
	private Long id;
	
	@OneToOne
    @JoinColumn(name = "ml_idx")
	private MLEntity ml;
	
	@OneToOne
    @JoinColumn(name = "project_idx")
	private ProjectEntity project;
}
