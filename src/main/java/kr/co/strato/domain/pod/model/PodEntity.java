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
import javax.persistence.Transient;

import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.Volume;
import kr.co.strato.domain.cluster.model.ClusterEntity;
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
    
    
    @ManyToOne(fetch = FetchType.LAZY)
   	@JoinColumn(name = "cluster_idx")
   	private ClusterEntity cluster;
    
    private String ip;
    
    private String qosClass;

    private int restart;

    @Lob
    private String annotation;

    @Lob
    private String label;
    
    @Lob
    private String image;
    
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
    
    private String ownerName;
    
    @OneToMany(mappedBy = "pod", cascade = CascadeType.REMOVE)
    private List<PodStatefulSetEntity> podStatefulSet;
    
    @OneToMany(mappedBy = "pod", cascade = CascadeType.REMOVE)
    private List<PodReplicaSetEntity> podReplicaSet;
    
    @OneToMany(mappedBy = "pod", cascade = CascadeType.REMOVE)
    private List<PodDaemonSetEntity> podDaemonSet;
    
    @OneToMany(mappedBy = "pod", cascade = CascadeType.REMOVE)
    private List<PodJobEntity> podJob;
    
    @OneToMany(mappedBy = "pod", cascade = CascadeType.REMOVE)
    private List<PodPersistentVolumeClaimEntity> podPersistentVolumeClaim;
    
    // cpu & memory limit 용량 관련
    @Transient
    private List<Quantity> cpuRequests;

    @Transient
    private List<Quantity> cpuLimits;

    @Transient
    private List<Quantity> memoryRequests;

    @Transient
    private List<Quantity> memoryLimits;
    
    @Transient
    private List<Volume> volumes;
}
