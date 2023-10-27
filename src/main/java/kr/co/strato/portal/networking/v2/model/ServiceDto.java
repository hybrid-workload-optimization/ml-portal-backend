package kr.co.strato.portal.networking.v2.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.strato.portal.workload.v2.model.WorkloadCommonDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ServiceDto extends WorkloadCommonDto {	
	private Map<String, String> selector;
    private String clusterIp;
    private List<HashMap<String, Object>> internalEndpoints;
    private List<HashMap<String, Object>> externalEndpoints;
    private String type;
    private String sessionAffinity;
    private List<EndpointListDto> endpoints;


    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EndpointListDto {
        private Long id;
        private String host;
        private String endpointName;
        private Integer port;
        private String protocol;
        private String nodeName;
        private String ready;
    }
}
