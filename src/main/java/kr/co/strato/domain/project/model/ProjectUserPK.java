package kr.co.strato.domain.project.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProjectUserPK implements Serializable {

	private String userId;
	private Long projectIdx;
}
