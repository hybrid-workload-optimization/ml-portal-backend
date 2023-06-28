package kr.co.strato.global.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class JwtToken {
	private String accessTokenStr;
	private Header header;
	private Payload payload;
	
	/**
	 * 클라이언트 롤 반환.
	 * @param clientId
	 * @return
	 */
	public List<String> getClientRoles(String clientId) {
		if(payload != null) {
			List<String> role = payload.getResourceAccess().get(clientId);
			return role;
		}
		return null;
	}
	
	public void setClientRole(String clientId, List<String> roles) {
		if(payload != null) {
			List<String> res = payload.getResourceAccess().get(clientId);
			if(res == null) {
				res.addAll(roles);
			}
		}
	}
	
	/**
	 * 접근 가능한 클라이언트 리스
	 * @return
	 */
	public List<String> getAuthorizedClients() {
		if(payload != null) {
			Set<String> keys = payload.getResourceAccess().keySet();
			return new ArrayList<>(keys);
		}
		return null;
	}
	
	/**
	 * 클라이언트 Audience 검사.
	 * @param clientId
	 * @return
	 */
	public boolean hasAudience(String clientId) {
		if(payload != null &&  payload.getAud() != null) {
			Object aud =  payload.getAud();
			if(aud instanceof String) {
				return ((String)aud).equals(clientId);
			} else if(aud instanceof List) {
				return ((List)aud).contains(clientId);
			}
		}
		return false;
	}

	@Data
	public static class Header {
		private String alg;
		private String typ;
		private String kid;
	}
	
	@Data
	public static class Payload {
		private String exp;
		private String iat;
		private String jti;
		private String iss;
		private Object aud;
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
		private Map<String, List<String>> resourceAccess;
		@SerializedName("group_access")
		private Map<String, Map<String, String>> groupAccess;
		private String[] scope;
		private String sid;
		@SerializedName("email_verified")
		private Boolean emailVerified;
		@SerializedName("preferred_username")
		private String preferredUsername;
		private String email;
	}	
}
