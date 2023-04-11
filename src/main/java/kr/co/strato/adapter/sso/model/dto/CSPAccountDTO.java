package kr.co.strato.adapter.sso.model.dto;

import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CSPAccountDTO {
	private String uuid;
	private String csp;
	private Map<String, String> accountData;
	private String description;
	private String note;

	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class CreateAccount {
		private String accountGroupUuid;
		private String csp;
		private Map<String, String> accountData;
		private String description;
		private String note;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class UpdateAccount extends CreateAccount {
		private String uuid;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class DeleteAccount {
		private String uuid;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class SearchAccount {
		private String accountGroupUuid;
		private String companyUuid;
		private String serviceGroupUuid;
		private String csp;
		private Boolean notMappedToGroup;
	}
}
