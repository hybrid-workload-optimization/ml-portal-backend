package kr.co.strato.domain.pod.model;


import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.node.model.NodeEntity;
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
@Table(name = "pod")
public class PodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pod_idx", unique = true)
    private Long id;

    private String podUid;

    private String podName;
    
    private String status;

    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "node_idx")
	private NodeEntity node;
    
    private String ip;
    
    private String qosClass;

    private int restart;

    @Lob
    private String annotation;

    @Lob
    private String label;
    
    private float cpu;
    
    private float memory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "namespace_idx")
    private NamespaceEntity namespace;
    
    @Lob
    @Column(name = "`condition`")
	private String condition;
    
    private String ownerUid;
    
    private String kind;
    
    @OneToMany(mappedBy = "pod", cascade = CascadeType.REMOVE)
    private List<PodStatefulSetEntity> podStatefulSet;
    
    @OneToMany(mappedBy = "pod", cascade = CascadeType.REMOVE)
    private List<PodReplicaSetEntity> podReplicaSet;
    
//    @OneToMany(mappedBy = "pod", cascade = CascadeType.REMOVE)
//    private List<PodDaemonetEntity> podDaemonSet;
    
    @OneToMany(mappedBy = "pod", cascade = CascadeType.REMOVE)
    private List<PodJobEntity> podJob;
    
    @OneToMany(mappedBy = "pod", cascade = CascadeType.REMOVE)
    private List<PodPersistentVolumeClaimEntity> podPersistentVolumeClaim;
    
}
