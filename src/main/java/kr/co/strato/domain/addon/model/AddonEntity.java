package kr.co.strato.domain.addon.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(AddonIdPK.class)
@Table(name = "cluster_addon")
public class AddonEntity {	
	
	@Id
	@Column(name = "cluster_idx")
	private Long clusterIdx;
	
	@Id 
	@Column(name = "addon_id")
	private String addonId;
	
	@Column(name = "install_user_id")
	private String installUserId;
	
	@Column(name = "install_at")
	private LocalDateTime installAt;
	
	/**
	 * PK를 생성하여 리턴.
	 * @return
	 */
	public AddonIdPK getPK() {
		AddonIdPK pk = new AddonIdPK();
		pk.setAddonId(addonId);
		pk.setClusterIdx(clusterIdx);
		return pk;
	}
}
