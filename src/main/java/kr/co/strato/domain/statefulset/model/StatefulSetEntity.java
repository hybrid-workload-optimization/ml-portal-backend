package kr.co.strato.domain.statefulset.model;


import java.time.LocalDateTime;
import java.util.List;

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
import kr.co.strato.domain.pod.model.PodStatefulSetEntity;
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
@Table(name = "stateful_set")
public class StatefulSetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stateful_set_idx")
    private Long id;

    private String statefulSetUid;

    private String statefulSetName;

    private LocalDateTime createdAt;

    private String image;

    @Lob
    private String annotation;

    @Lob
    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "namespace_idx")
    private NamespaceEntity namespace;

    @OneToMany(mappedBy = "statefulSet")
    private List<PodStatefulSetEntity> podStatefulSets;
    
    private String yaml;
    
}
