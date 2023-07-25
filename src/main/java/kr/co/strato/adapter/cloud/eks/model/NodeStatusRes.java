package kr.co.strato.adapter.cloud.eks.model;

import lombok.Data;

@Data
public class NodeStatusRes {

	private String instanceId;
	private String status;

}
