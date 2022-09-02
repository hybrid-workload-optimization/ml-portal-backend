package kr.co.strato.adapter.ml.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ForecastDto {

	@Getter
	@Setter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ReqForecastDto {
		private String model;
		private String category;
		private List<PodSpecDto> podSpec;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class ResForecastDto {
		private String model;
		private Integer count;
		private List<NodeSpecDto> items;
	}
}
