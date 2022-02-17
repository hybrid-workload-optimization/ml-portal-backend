package kr.co.strato.domain.user.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import kr.co.strato.domain.menu.model.MenuEntity;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_role")
@Getter
@Setter
@ToString
public class UserRoleEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_idx")
	private Long userRoleIdx;
	
	@Column(name = "user_role_name")
	private String userRoleName;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_role_idx")
	private List<UserRoleMenuEntity> userRoleMenus;
}
