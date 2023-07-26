package kr.co.strato.portal.workload.v2.model;

import java.util.HashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PodDto extends WorkloadCommonDto {
    private String ip;
    private String qosClass;
    private List<String> images;
    
    private String node;
    private int restart;
    private String status;
    private String ownerUid;        
    private String ownerKind;        
    private String ownerName;    
    
    private List<HashMap<String, Object>> condition;
    private List<PersistentVolumeClaimDto> pvcList;
}
