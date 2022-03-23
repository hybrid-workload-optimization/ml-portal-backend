package kr.co.strato.domain.setting.model;



import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "setting")
public class SettingEntity {
	
	// Type
	public static final String TYPE_GENERAL	= "GENERAL"; 
	public static final String TYPE_TOOLS	= "TOOLS";
	
	// Key
	public static final String KEY_GENERAL_HOME_DIRECTORY	= "HOME_DIRECTORY";
	public static final String KEY_TOOLS_KUBESPRAY			= "KUBESPRAY";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "setting_idx")
	private Long settingIdx;
	
	@Column(name = "setting_type")
	private String settingType;
	
	@Column(name = "setting_key")
	private String settingKey;
	
	@Column(name = "setting_value")
	private String settingValue;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "updated_at")
	private Date updatedAt;
}
