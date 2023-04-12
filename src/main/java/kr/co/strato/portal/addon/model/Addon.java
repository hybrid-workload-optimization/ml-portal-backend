package kr.co.strato.portal.addon.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.co.strato.domain.addon.model.AddonEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	private Icon icon;
	
	@JsonIgnore
	private List<String> yamls;
	
	@JsonIgnore
	private String adapter;
	
	private RequiredSpec requiredSpec;
	
	private boolean installed;
	private String installUserId;
	private String installAt;
	
	@Getter
	@Setter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Icon {
		private String format;
		private String data;
	}
	
	public void setIconData(String format, String data) {
		Icon i = Icon.builder()
				.format(format)
				.data(data)
				.build();
		icon = i;
	}
	
	/**
	 * Entity 정보 셋.
	 * @param addonEntity
	 */
	public void setAddonEntity(AddonEntity addonEntity) {
		if(addonEntity != null) {
			setInstalled(true);
			setInstallUserId(addonEntity.getInstallUserId());
			setInstallAt(addonEntity.getInstallAt());
		} else {
			setInstalled(false);
		}
	}
	
}
