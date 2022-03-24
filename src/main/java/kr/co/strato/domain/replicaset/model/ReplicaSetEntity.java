package kr.co.strato.domain.replicaset.model;

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

import kr.co.strato.domain.deployment.model.DeploymentEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
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
@Table(name = "replica_set")
public class ReplicaSetEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "replica_set_idx")
    private Long replicaSetIdx;

    @Column(name = "replica_set_uid")
    private String replicaSetUid;

    @Column(name = "replica_set_name")
    private String replicaSetName;

    @Column(name = "created_at")
    private String createdAt;

    @Column
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
    

    @ManyToOne
    @JoinColumn(name = "deployment_idx")
    private DeploymentEntity deployment;

    

    
    @OneToMany(mappedBy = "replicaSet")
    private List<PodReplicaSetEntity> podReplicaSets;
}