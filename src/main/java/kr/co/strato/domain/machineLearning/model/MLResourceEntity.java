package kr.co.strato.domain.machineLearning.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "machine_learning_resource")
public class MLResourceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ml_res_idx")
	private Long id;
	
	private String mlResName;
	
	private String kind;
	
	private Long resourceId;
	
	private String status;
	
	private Long clusterIdx;
	
	private String createdAt;
	
	private String updatedAt;
	
    private Long mlIdx;
	
	private String yaml;
}
