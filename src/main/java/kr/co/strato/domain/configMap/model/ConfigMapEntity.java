package kr.co.strato.domain.configMap.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import kr.co.strato.domain.pod.model.PodPersistentVolumeClaimEntity;
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
@Table(name = "config_map")
public class ConfigMapEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "config_map_idx")
	private Long id;
	
	@Column(name = "config_map_name")
	private String name;
	
	@Column(name = "config_map_uid")
	private String uid;
	
	@Column(name = "data_type")
	private String dataType;
	
	@Column(name = "data")
	private String data;
	
	@Column(name = "created_at")
	private String createdAt;
	
	@ManyToOne
    @JoinColumn(name = "namespace_idx")
    private NamespaceEntity namespace;
}
