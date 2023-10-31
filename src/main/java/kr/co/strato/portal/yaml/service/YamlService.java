package kr.co.strato.portal.yaml.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import io.fabric8.kubernetes.api.model.HasMetadata;
import kr.co.strato.adapter.k8s.common.model.ApplyResult;
import kr.co.strato.adapter.k8s.workload.service.WorkloadAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.yaml.model.YamlEntity;
import kr.co.strato.domain.yaml.service.YamlDomainService;
import kr.co.strato.global.auth.JwtToken;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.model.UserDto.UserRole;
import kr.co.strato.portal.workload.v2.model.WorkloadItem;
import kr.co.strato.portal.yaml.model.YamlDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class YamlService {
	
	@Autowired
	private ClusterDomainService clusterDomainService;
	
	@Autowired
	private WorkloadAdapterService workloadAdapterService;
	
	@Autowired
	private YamlDomainService yamlDomainService;
	
	@Value("${auth.client.client-id}")
	private String clientId;
	
	/**
	 * yaml 히스토리 내역 조회
	 * @param search
	 * @return
	 */
	public List<YamlDto> getYaml(YamlDto.Search search) {
		Long clusterIdx = search.getClusterIdx();
		String kind = search.getKind();
		String name = search.getName();
		String namespace = search.getNamespace();
		
		List<YamlEntity> list = yamlDomainService.get(clusterIdx, kind, name, namespace);
		
		if(list == null || list.size() < 1) { 
			//저장 내역이 없는 경우 Kubernetes I/F로 부터 받아온다.
			list = new ArrayList<>();
			
			ClusterEntity entity = clusterDomainService.get(clusterIdx);
			Long kubeConfigId = entity.getClusterId();
			String yaml = workloadAdapterService.resourceYaml(kubeConfigId, kind, namespace, name);
			
			if(yaml != null && yaml.length() > 0) {
				YamlEntity newEntity = YamlEntity.builder()
						.clusterIdx(clusterIdx)
						.kind(kind)
						.name(name)
						.namespace(namespace)
						.yaml(yaml)
						.createAt(DateUtil.currentDateTime())
						.createBy(getLoginUser().getUserId())
						.build();
				list.add(newEntity);
			}
		}
		
		List<YamlDto> result = new ArrayList<>();
		for(YamlEntity entity : list) {
			String encodedYaml = Base64.getEncoder().encodeToString(entity.getYaml().getBytes());
			
			YamlDto dto = YamlDto.builder()
					.kind(entity.getKind())
					.name(entity.getName())
					.namespace(entity.getNamespace())
					.yaml(encodedYaml)
					.createAt(entity.getCreateAt())
					.createBy(entity.getCreateBy())
					.build();
			result.add(dto);
		}
		return result;
	}

	
	public boolean deleteYaml(Long yamlIdx) {
		yamlDomainService.delete(yamlIdx);
		return true;
	}
	
	/**
	 * Workload 리소스 생성.
	 * @param param
	 * @return
	 */
	public YamlDto.ApplyResultDto apply(YamlDto.ApplyDto param) {
		Long clusterIdx = param.getClusterIdx();
		ClusterEntity entity = clusterDomainService.get(clusterIdx);
		
		Long kubeConfigId = entity.getClusterId();
		String yamlStr = Base64Util.decode(param.getYaml());
		
		save(clusterIdx, yamlStr);
		
		ApplyResult.Result result = workloadAdapterService.apply(kubeConfigId, yamlStr);
		
		List<YamlDto.ApplyResourceDto> list = null;
		if(result.isSuccess()) {
			list = getList(result.getResources());
		}
		
		YamlDto.ApplyResultDto resultDto = YamlDto.ApplyResultDto.builder()
				.success(result.isSuccess())
				.errorMessage(result.getErrorMessage())
				.resources(list)
				.build();
		return resultDto;
	}
	
	/**
	 * yaml 히스토리 저장
	 * @param yamlStr
	 */
	private void save(Long clusterIdx, String yamlStr) {
		Yaml yaml = new Yaml();
		for(Object object : yaml.loadAll(yamlStr)) {
			if(object instanceof Map) {
				Map map = (Map) object;
				
				String kind = null;
				if(map.get("kind") != null) {
					kind = (String)map.get("kind");
				}
				
				String name = null;
				String namespace = null;
				if(map.get("metadata") != null) {
					if(((Map)map.get("metadata")).get("name") != null) {
						name = (String)((Map)map.get("metadata")).get("name");
					}
					
					if(((Map)map.get("metadata")).get("namespace") != null) {
						namespace = (String)((Map)map.get("metadata")).get("namespace");
					} else {
						namespace = "default";
					}					
				}
				
				String output = new Yaml().dump(map);
				String loginUserId = null;
				UserDto loginUser = getLoginUser();
				if(loginUser != null) {
					loginUserId = loginUser.getUserId();
				}
				
				
				YamlEntity entity = YamlEntity.builder()
						.clusterIdx(clusterIdx)
						.kind(kind)
						.name(name)
						.namespace(namespace)
						.yaml(output)
						.createAt(DateUtil.currentDateTime())
						.createBy(loginUserId)
						.build();
				
				yamlDomainService.save(entity);
				
			} else {
				log.error("yaml 파일이 잘못되었습니다.");
			}
		}
	}
	
	public List<YamlDto.ApplyResourceDto> getList(List<HasMetadata> list) {
		List<YamlDto.ApplyResourceDto> result = new ArrayList<>();
		try {
			//uid를 키로 맵으로 변환
			Map<String, WorkloadItem> map = new HashMap<>();
			Map<String, WorkloadItem> mapList = new HashMap<>();
			for(HasMetadata data : list) {
				String uid = data.getMetadata().getUid();
				WorkloadItem item = new WorkloadItem(data);
				map.put(uid, item);
				mapList.put(uid, item);
			}
			
			Iterator<String> iter = mapList.keySet().iterator();
			while(iter.hasNext()) {
				WorkloadItem item = mapList.get(iter.next());
				HasMetadata data = item.getData();
				
				String uid = data.getMetadata().getUid();
				String name = data.getMetadata().getName();
				
				String namespace = data.getMetadata().getNamespace();
				String kind = data.getKind();				
				Map<String, String> labels = data.getMetadata().getLabels();
				String createAt = DateUtil.strToNewFormatter(data.getMetadata().getCreationTimestamp());
				
				YamlDto.ApplyResourceDto listItem = YamlDto.ApplyResourceDto.builder()
						.uid(uid)
						.name(name)
						.namespace(namespace)
						.kind(kind)
						.labels(labels)
						.createAt(createAt)
						.build();
				
				result.add(listItem);
			}
			
			//생성일자 기준 오름차순 정렬
			Collections.sort(result, new Comparator<YamlDto.ApplyResourceDto>() {

				@Override
				public int compare(YamlDto.ApplyResourceDto o1, YamlDto.ApplyResourceDto o2) {
					String createAto1 = o1.getCreateAt();
					String createAto2 = o2.getCreateAt();
					return createAto2.compareTo(createAto1);
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public UserDto getLoginUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();		
		if(auth.getPrincipal() instanceof JwtToken) {
			JwtToken principal = (JwtToken)auth.getPrincipal();
			
	        UserDto user = new UserDto();
	        user.setUserId(principal.getPayload().getPreferredUsername());
	        user.setEmail(principal.getPayload().getEmail());
	        
	        List<String> roles = principal.getClientRoles(clientId);
	        if(roles != null && roles.size() > 0) {
        		String role = roles.iterator().next();
        		
        		UserRole userRole = UserRole.builder().userRoleCode(role).build();
        		user.setUserRole(userRole);
        	}       
	        return user;
		}
		return null;
	}
}
