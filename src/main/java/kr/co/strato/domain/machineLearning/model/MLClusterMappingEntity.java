package kr.co.strato.domain.machineLearning.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "machine_learning_cluster_mapping")
@Getter
@Setter
@ToString
public class MLClusterMappingEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ml_cluster_mapping_idx")
	private Long id;
	
	@OneToOne
	@NotFound(action=NotFoundAction.IGNORE)
    @JoinColumn(name = "ml_cluster_idx")
	private MLClusterEntity mlCluster;
	
	@OneToOne
    @JoinColumn(name = "ml_idx")
	private MLEntity ml;
	
	private String createdAt;	
	private String updatedAt;
}
