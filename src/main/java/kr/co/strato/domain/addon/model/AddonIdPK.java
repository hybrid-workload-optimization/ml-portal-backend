package kr.co.strato.domain.addon.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class AddonIdPK implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long clusterIdx;
	private String addonId;

}
