package kr.co.strato.portal.cluster.v2.model;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StorageClassDto {


	@Getter
	@Setter
	@NoArgsConstructor
	public static class ListDto {
		private String uid;
		private String name;
		private String createdAt;
		private String provisioner;
		private String type;
		private Map<String, String> annotations;
    	private Map<String, String> labels;
	}

	
    @Getter
    @Setter
    @NoArgsConstructor
    public static class DetailDto extends ListDto {
    	private List<PersistentVolumeDto.ListDto> pvList;
    }
    
    @Getter
	@Setter
	@Builder
	public static class DeleteDto {
		private Long clusterIdx;
		private String name;
	}
}
