package kr.co.strato.domain.setting.model;

import java.sql.Date;

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
