package kr.co.strato.domain.statefulset.model;


import com.vladmihalcea.hibernate.type.json.JsonStringType;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

}
