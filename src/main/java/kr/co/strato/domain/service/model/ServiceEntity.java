package kr.co.strato.domain.service.model;

import kr.co.strato.domain.ingress.model.IngressEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="service")
public class ServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_idx")
    private Long id;

    private String serviceUid;

    private String serviceName;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ServiceType type;

    private String clusterIp;

    private String sessionAffinity;

    @Lob
    private String internalEndpoint;

    @Lob
    private String externalEndpoint;

    @Lob
    private String selector;

    @Lob
    private String annotation;

    @Lob
    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name =  "namespace_idx")
    private NamespaceEntity namespace;
    
    private String yaml;



}
