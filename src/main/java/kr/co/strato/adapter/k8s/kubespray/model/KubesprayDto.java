package kr.co.strato.adapter.k8s.kubespray.model;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@ToString
@Getter
public class KubesprayDto {
	public String version;
	public HashMap<String, String> data;
}
