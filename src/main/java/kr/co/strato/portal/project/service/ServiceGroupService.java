package kr.co.strato.portal.project.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.k8s.cluster.model.ClusterAdapterDto;
import kr.co.strato.adapter.k8s.cluster.service.ClusterAdapterService;
import kr.co.strato.adapter.sso.model.dto.CSPAccountDTO;
import kr.co.strato.adapter.sso.service.CSPAccountAdapterService;
import kr.co.strato.domain.project.model.ProjectClusterEntity;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectClusterDomainService;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.global.error.exception.NotFoundProjectException;
import kr.co.strato.portal.cluster.v1.model.ArgoCDInfo;
import kr.co.strato.portal.cluster.v1.model.ClusterDto;
import kr.co.strato.portal.cluster.v1.service.ClusterService;
import kr.co.strato.portal.ml.v1.service.MLClusterAPIAsyncService;
import kr.co.strato.portal.project.model.CSPAccountDto;
import kr.co.strato.portal.project.model.mapper.CSPAccountDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ServiceGroupService {
	
	@Autowired
	private ProjectDomainService projectDomainService;
	
	@Autowired
	private ProjectClusterDomainService projectClusterDomainService;
	
	@Autowired
	private ClusterService clusterService;
	
	@Autowired
	private MLClusterAPIAsyncService mlClusterService;
	
	@Autowired
	private ClusterAdapterService clusterAdapterService;
	
	@Autowired
	private CSPAccountAdapterService cspAccountAdapterService;

	/**
	 * 서비스 그룹에 속한 클러스터 상세 정보 반환.
	 * @param uuid
	 * @return
	 */
	public List<ClusterDto.Detail> getGroupClusters(String uuid, String requestType) {
		ProjectEntity entity = projectDomainService.getProjectByUuid(uuid);
		if(entity != null) {
			Long projectIdx = entity.getId();		
			
			List<ClusterDto.Detail> result = new ArrayList<>();
			List<ProjectClusterEntity> list = projectClusterDomainService.getProjectClusters(projectIdx);
			for(ProjectClusterEntity e : list) {
				Long clusterIdx = e.getClusterIdx();				
				try {
					ClusterDto.Detail detail = null;
					if(requestType != null && requestType.toLowerCase().equals("devops")) {
						//DevOps
						detail = clusterService.getClusterForDevOps(clusterIdx);
					} else {
						//Monitoring
						detail = clusterService.getClusterForMonitoring(clusterIdx);
					}
					
					detail.setProvisioningLog(null);
					result.add(detail);
					
					if(detail instanceof ClusterDto.DetailForDevOps) {
						ArgoCDInfo info = mlClusterService.getArgoCDInfo(clusterIdx);
						
						ClusterDto.DetailForDevOps m = (ClusterDto.DetailForDevOps)detail;
						m.setArgocd(info);
						
						//kubconfig
						Long kubeConfigId = detail.getClusterId();
						ClusterAdapterDto clusterAdapterDto = null;
						try {
							clusterAdapterDto = clusterAdapterService.getCluster(kubeConfigId);
							String kubeConfig = clusterAdapterDto.getConfigContents();
							if(kubeConfig != null) {
								byte[] byteArr = Base64.getEncoder().encode(kubeConfig.getBytes());
								String encoded = new String(byteArr);
								m.setKubeConfig(encoded);
							}
						} catch (Exception e2) {
							log.error("", e2);
						}
					} else if(detail instanceof ClusterDto.DetailForMonitoring) {
						String prometheusUrl = mlClusterService.getPrometheusUrl(clusterIdx);
						String grafanaUrl = mlClusterService.getGrafanaUrl(clusterIdx);
						
						ClusterDto.DetailForMonitoring m = (ClusterDto.DetailForMonitoring)detail;
						m.setPrometheusUrl(prometheusUrl);
						m.setGrafanaUrl(grafanaUrl);
					} 
					
				} catch (Exception e1) {
					log.error("", e);
				}
			}
			return result;
			
		} else {
			throw new NotFoundProjectException();
		}
	}
	
	public List<CSPAccountDto> getGroupCspAccount(Long projectIdx, String provider) {
		ProjectEntity entity = projectDomainService.getProject(projectIdx);
		if(entity != null) {
			String uuid = entity.getUuid();
			String csp = null;
			if(provider != null) {
				csp = getClusterCSP(provider);
			}
			List<CSPAccountDTO> list = cspAccountAdapterService.getAccounts(uuid, csp);
			List<CSPAccountDto> result = CSPAccountDtoMapper.INSTANCE.dtoList(list);
			return result;
		}
		return null;
	}
	
	
	public String getClusterCSP(String provider) {
    	String type = null;
    	String lowerProvider = provider.toLowerCase();
    	if(lowerProvider.equals("azure")) {
			type = "AZURE";
		} else if(lowerProvider.equals("gcp")) {
			type = "GCP";
		} else if(lowerProvider.equals("aws")) {
			type = "AWS";
		} else if(lowerProvider.equals("naver")) {
			type = "NAVER";
		} else if(lowerProvider.equals("nhn")) {
			type = "NHN";
		} else if(lowerProvider.equals("alibaba")) {
			type = "ALIBABA";
		}
    	return type;
    }
			
}
