package kr.co.strato.domain.ingress.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "ingress_rule")
public class IngressRuleEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ingress_rule_idx")
	private Long id;

	private String host;

	private String protocol;
	
	private String path;
	
	@Column(name = "path_type")
	private String pathType;
	
	private String service;
	
	private String endpoint;
	
	private int port;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ingress_idx")
	private IngressEntity ingress;
	
}
