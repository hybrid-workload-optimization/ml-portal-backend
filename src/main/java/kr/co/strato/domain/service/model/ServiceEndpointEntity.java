package kr.co.strato.domain.service.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "service_endpoint")
public class ServiceEndpointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_endpoint_idx")
    private Long id;

    private String host;

    private Integer port;

    private String name;

    private String ready;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_idx")
    private ServiceEntity service;

}
