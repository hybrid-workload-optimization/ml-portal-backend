package kr.co.strato.global.model;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class JwtTokenModel {

	@Data
	public static class header {
		private String alg;
		private String typ;
		private String kid;
	}
	
	@Data
	public static class payload {
		private String exp;
		private String iat;
		private String jti;
		private String iss;
		private List<String> aud;
		private String sub;
		private String typ;
		private String azp;
		private String session_state;
		private String acr;
		@SerializedName("allowed-origins")
		private String[] allowedOrigins;
		@SerializedName("realm_access")
		private Map<String, List<String>> realmAccess;
		@SerializedName("resource_access")
		private Map<String, Map<String, List<String>>> resourceAccess;
		private String scope;
		private String sid;
		@SerializedName("email_verified")
		private Boolean emailVerified;
		@SerializedName("preferred_username")
		private String preferredUsername;
		private String email;
		private String accessToken;
		
		/**
		 * 클라이언트 롤 반환.
		 * @param clientId
		 * @return
		 */
		public List<String> getClientRoles(String clientId) {
			Map<String, List<String>> res = getResourceAccess().get(clientId);
			if(res != null) {
				return res.get("roles");
			}
			return null;
		}
		
		/**
		 * 클라이언트 Audience 검사.
		 * @param clientId
		 * @return
		 */
		public boolean hasAudience(String clientId) {
			return getAud().contains(clientId);
		}
	}
	
}
