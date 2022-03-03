package kr.co.strato.portal.networking.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IngressDto {
	private Long id;
	private String name;
	private String uid;
	private String ingressClass;
	private LocalDateTime createdAt;
	private Long clusterIdx;
	private Long namespaceIdx;
	private Long ingressControllerIdx;
}
