package kr.co.strato.domain.daemonset.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.pod.model.PodDaemonSetEntity;
import kr.co.strato.domain.pod.model.PodReplicaSetEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "daemon_set")
public class DaemonSetEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daemon_set_idx")
    private Long daemonSetIdx;
	
	@Column(name = "daemon_set_uid")
	private String daemonSetUid;
	
	@Column(name = "daemon_set_name")
	private String daemonSetName;
	
	@Column(name = "created_at")
    private String createdAt;
	
	private String image;
	
	@Lob
	private String selector;
	
	@Lob
	private String annotation;
	
	@Lob
	private String label;
	
	@ManyToOne
    @JoinColumn(name = "namespace_idx")
    private NamespaceEntity namespace;
	
	@OneToMany(mappedBy = "daemonSet")
    private List<PodDaemonSetEntity> podDaemonSets;
}
