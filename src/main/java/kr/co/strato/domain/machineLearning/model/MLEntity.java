package kr.co.strato.domain.machineLearning.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "machine_learning")
@Getter
@Setter
@ToString
public class MLEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ml_idx")
	private Long id;
	private String mlId;
	private String userId;
	
	@Column(name = "ml_name")
	private String name;
	private String description;
	private String mlStepCode;
	private String yaml;
	private String status;
	private Long clusterIdx;
	private String createdAt;	
	private String updatedAt;
	private String callbackUrl;
}
