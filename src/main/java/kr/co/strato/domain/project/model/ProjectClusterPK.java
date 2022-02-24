package kr.co.strato.domain.project.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProjectClusterPK implements Serializable {

	private Long clusterIdx;
	private Long projectIdx;
}