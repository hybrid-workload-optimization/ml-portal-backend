package kr.co.strato.portal.networking.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class IngressDto {
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class ReqCreateDto{
	    //TODO validation체크
	    private Long clusterIdx;
	    private String yaml;
	}


	@Getter
	@Setter
	@NoArgsConstructor
	public static class ResListDto{
		private Long id;
		private String name;
		private String uid;
		private String ingressClass;
		private LocalDateTime createdAt;
		private Long namespaceIdx;
		private Long ingressControllerIdx;
	}

	
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResDetailDto{
    	private Long id;
    	private String name;
    	private String uid;
    	private String ingressClass;
    	private LocalDateTime createdAt;
    	private Long namespaceIdx;
    	private Long ingressControllerIdx;
    }
	
	@Getter
	@Setter
	@NoArgsConstructor
	@ToString
	public static class SearchParam{
	    private Long namespaceIdx;
	    private String name;
	}
}
