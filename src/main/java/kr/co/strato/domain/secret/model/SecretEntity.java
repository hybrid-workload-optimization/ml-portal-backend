package kr.co.strato.domain.secret.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "secret")
public class SecretEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "secret_idx")
	private Long id;
	
	@Column(name = "secret_name")
	private String name;
	
	@Column(name = "secret_uid")
	private String uid;
	
	@Column(name = "label")
	private String label;
	
	@Column(name = "data")
	private String data;
	
	@Column(name = "created_at")
	private String createdAt;
	
	@ManyToOne
    @JoinColumn(name = "namespace_idx")
    private NamespaceEntity namespace;
}
