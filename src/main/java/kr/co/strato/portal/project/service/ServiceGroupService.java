package kr.co.strato.portal.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.project.model.ProjectClusterEntity;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectClusterDomainService;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.global.error.exception.NotFoundProjectException;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.cluster.service.ClusterService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ServiceGroupService {
	
	@Autowired
	ProjectDomainService projectDomainService;
	
	@Autowired
	ProjectClusterDomainService projectClusterDomainService;
	
	@Autowired
	ClusterService clusterService;

	/**
	 * 서비스 그룹에 속한 클러스터 상세 정보 반환.
	 * @param uuid
	 * @return
	 */
	public List<ClusterDto.Detail> getGroupClusters(String uuid) {
		ProjectEntity entity = projectDomainService.getProjectByUuid(uuid);
		if(entity != null) {
			Long projectIdx = entity.getId();
			
			List<ClusterDto.Detail> result = new ArrayList<>();
			List<ProjectClusterEntity> list = projectClusterDomainService.getProjectClusters(projectIdx);
			for(ProjectClusterEntity e : list) {
				Long clusterIdx = e.getClusterIdx();				
				try {
					ClusterDto.Detail detail = clusterService.getCluster(clusterIdx);
					result.add(detail);
				} catch (Exception e1) {
					log.error("", e);
				}
			}
			return result;
			
		} else {
			throw new NotFoundProjectException();
		}
	}
}
