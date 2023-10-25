package kr.co.strato.adapter.cloud.sks.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VSphereVMAction {
	private List<String> vmNames;
	private String action;
}
