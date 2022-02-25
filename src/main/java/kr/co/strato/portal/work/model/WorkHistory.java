package kr.co.strato.portal.work.model;

public class WorkHistory {

	public static enum WorkMenu1 {
		
		PROJECT			("Project"),
		CLUSTER			("Cluster"),
		WORKLOAD		("Workload"),
		NETWORKING		("Networking"),
		CONFIG			("Config"),
		SETTING			("Setting");

		private String menuName;
		
		private WorkMenu1(String menuName) {
			this.menuName = menuName;
		}

		public String getMenuName() {
			return menuName;
		}
		
	}
	
	public static enum WorkMenu2 {
		
		NONE(""),
		
		// Workload
		DEPLOYMENT				("Deployment"),
		STATEFUL_SET			("Stateful Set"),
		POD						("Pod"),
		CRON_JOB				("Cron Job"),
		JOB						("Job"),
		REPLICA_SET				("Replica Set"),
		DAEMON_SET				("Daemon Set"),
		
		// Networking
		SERVICE					("Service"),
		INGRESS					("Ingress"),
		INGRESS_CONTROLLER		("Ingress Controller"),
		
		// Config
		PERSISTENT_VOLUME_CLAIM	("Persistent Volume Claim"),
		CONFIG_MAP				("Config Map"),
		SECRET					("Secret"),
		
		// Setting
		GENERAL					("General"),
		USER					("User"),
		AUTHORITY				("Authority"),
		CODE_MANAGEMENT			("Code Management"),
		TOOL					("Tool");
		
		private String menuName;
		
		private WorkMenu2(String menuName) {
			this.menuName = menuName;
		}

		public String getMenuName() {
			return menuName;
		}
		
	}

	public static enum WorkMenu3 {
		
		// 3차 메뉴가 존재한다면 선언하십시오. 
		NONE("");
		
		private String menuName;
		
		private WorkMenu3(String menuName) {
			this.menuName = menuName;
		}

		public String getMenuName() {
			return menuName;
		}
		
	}

	public static enum WorkAction {
		
		// 공통 Action
		LIST	("목록 조회"),
		DETAIL	("상세 조회"),
		INSERT	("등록"),
		UPDATE	("수정"),
		DELETE	("삭제");
		
		// 공통 Action으로 정리되지 않은 Action을 메뉴별로 정리하여 선언하십시오. 
		
		// Project 메뉴에서 발생되는 Action
			// ex) DETAIL_POPUP_PROJECT ("프로젝트 팝업 조회")
		
		// Cluser 메뉴에서 발생되는 Action
		
		// Workload 메뉴에서 발생되는 Action
		
		// Config 메뉴에서 발생되는 Action
		
		// Setting 메뉴에서 발생되는 Action
		
		private String actionName;
		
		private WorkAction(String actionName) {
			this.actionName = actionName;
		}

		public String getActionName() {
			return actionName;
		}
	}
	
	public static enum WorkResult {
		
		SUCCESS		("성공"),
		FAIL		("실패");
		
		private String resultName;
		
		private WorkResult(String resultName) {
			this.resultName = resultName;
		}
		
		public String getResultName() {
			return resultName;
		}
		
	}
	
}
