package kr.co.strato.domain.menu.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "menu")
@Getter
@Setter
@ToString
public class MenuEntity {
	
	@Id
	@GeneratedValue
	private Long menuIdx;
	private String menuName;
	private String menuUrl;
	private Long parentMenuIdx;
	private Integer menuOrder;
	private Integer menuDepth;
	private Character useYn;
	
	
//	컬럼명	#	Data Type	Not Null	Auto Increment	Key	디폴트	Extra	Expression	Comment
//	menu_idx	1	bigint(20)	true	false	PRI	[NULL]		[NULL]	
//	menu_name	2	varchar(50)	false	false	[NULL]	NULL		[NULL]	
//	menu_url	3	varchar(200)	false	false	[NULL]	NULL		[NULL]	
//	parent_menu_idx	4	bigint(20)	false	false	[NULL]	NULL		[NULL]	
//	menu_order	5	int(11)	false	false	[NULL]	NULL		[NULL]	
//	menu_depth	6	int(11)	false	false	[NULL]	NULL		[NULL]	
//	use_yn	7	char(1)	false	false	[NULL]	'Y'		[NULL]	

}
