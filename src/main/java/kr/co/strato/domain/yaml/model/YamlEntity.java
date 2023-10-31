package kr.co.strato.domain.yaml.model;

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
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "yaml_history")
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class YamlEntity {

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long yamlIdx;
	private Long clusterIdx;
	private String kind;	
	private String name;
	private String namespace;
	private String yaml;
	private String createAt;
	private String createBy;
	
}
