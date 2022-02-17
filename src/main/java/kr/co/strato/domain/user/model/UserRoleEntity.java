package kr.co.strato.domain.user.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
