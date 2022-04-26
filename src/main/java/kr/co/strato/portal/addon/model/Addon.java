package kr.co.strato.portal.addon.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.co.strato.domain.addon.model.AddonEntity;
import lombok.Data;

@Data
public class Addon{
	private String addonId;	
	private String addonType;
	private String name;
	private String version;	
	private String desc;	
	private List<Package> packages;
	private List<Parameter> parameters;	
	private String iconPath;
	
	@JsonIgnore
	private List<String> yamls;
	
	@JsonIgnore
	private String adapter;
	
	private RequiredSpec requiredSpec;
	
	private boolean installed;
	private String installUserId;
	private String installAt;
	
	/**
	 * Entity 정보 셋.
	 * @param addonEntity
	 */
	public void setAddonEntity(AddonEntity addonEntity) {
		if(addonEntity != null) {
			setInstalled(true);
			setInstallUserId(addonEntity.getInstallUserId());
			setInstallAt(addonEntity.getInstallAt());
		}
	}
	
}
