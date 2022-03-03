package kr.co.strato.domain.user.model;

import java.util.ArrayList;
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

import lombok.Getter;
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
	private Long id;
	
	@Column(name = "user_role_name")
	private String userRoleName;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "userRole")
	private List<UserRoleMenuEntity> userRoleMenus = new ArrayList<UserRoleMenuEntity>();
	
	public void addToUserRoleMenu(UserRoleMenuEntity userRoleMenuEntity) {
		userRoleMenuEntity.setUserRole(this);
		this.userRoleMenus.add(userRoleMenuEntity);
	}
}
