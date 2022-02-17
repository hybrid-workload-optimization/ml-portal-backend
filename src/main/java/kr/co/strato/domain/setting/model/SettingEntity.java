package kr.co.strato.domain.setting.model;

import java.sql.Date;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SettingEntity {
	private Long settingIdx;
	private String settingType;
	private String settingKey;
	private String settingValue;
	private String description;
	private Date updatedAt;
}
