package kr.co.strato.domain.user.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import kr.co.strato.domain.menu.model.MenuEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_role_menu")
@Getter
@Setter
@ToString
public class UserRoleMenuEntity {

	@Id
	@GeneratedValue
	@Column(name = "user_role_menu_idx")
	private Long id;
//	private Long menuIdx;
//	private Long userRoleIdx;
	@Column(name = "create_user_id")
	private String createUserId;
	
	@Column(name = "create_user_name")
	private String createUserName;
	
	@Column(name = "created_at")
	private Date created_at;
	
	@Column(name = "viewable_yn")
	private String viewableYn;
	
	@Column(name = "writable_yn")
	private String writableYn;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_role_idx")
	private UserRoleEntity userRole;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_idx")
	private MenuEntity menu;
}
