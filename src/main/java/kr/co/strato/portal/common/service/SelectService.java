package kr.co.strato.portal.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.Namespace;
import kr.co.strato.adapter.k8s.namespace.service.NamespaceAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.service.UserRoleDomainService;
import kr.co.strato.portal.common.model.SelectDto;
import kr.co.strato.portal.common.model.SelectDtoMapper;
import kr.co.strato.portal.setting.model.UserDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SelectService {
    @Autowired
    private ProjectDomainService projectDomainService;

    @Autowired
    private ClusterDomainService clusterDomainService;

    @Autowired
    private NamespaceDomainService namespaceDomainService;
    
    @Autowired
    private NamespaceAdapterService namespaceAdapterService;
    
    @Autowired
    private UserRoleDomainService userRoleDomainService;
    
    public List<SelectDto> getSelectProjects(UserDto loginUser){
        List<ProjectEntity> projects = projectDomainService.getUserProjects(loginUser);
        List<SelectDto> selectProjects =  projects.stream().map( e -> SelectDtoMapper.INSTANCE.toDto(e)).collect(Collectors.toList());

        return selectProjects;
    }

    public List<SelectDto> getSelectClusters(UserDto loginUser, Long projectIdx){
        List<ClusterEntity> clusters = new ArrayList<>();
        if(projectIdx != null && projectIdx > 0L){
            clusters = clusterDomainService.getListByProjectIdx(projectIdx);
        }else{
            clusters = clusterDomainService.getListAll();
        }
        List<SelectDto> selectClusters = clusters.stream().map( e  -> SelectDtoMapper.INSTANCE.toDto(e)).collect(Collectors.toList());
        return selectClusters;
    }

    public List<SelectDto> getSelectNamespaces(Long clusterIdx){
        List<NamespaceEntity> namespaces = namespaceDomainService.findByClusterIdx(clusterIdx);
        List<SelectDto> selectNamespaces = namespaces.stream().map(e -> SelectDtoMapper.INSTANCE.toDto(e)).collect(Collectors.toList());
        return selectNamespaces;
    }
    
    
    public List<SelectDto> getSelectNamespacesAPIOnly(Long clusterIdx) {
    	ClusterEntity cluster = clusterDomainService.get(clusterIdx);
    	Long kubeConfigId = cluster.getClusterId();
    	List<Namespace> list = namespaceAdapterService.getNamespaceList(kubeConfigId);
    	
    	log.info("Select namespace: clusterIdx, {}, kubeConfigId: {}", clusterIdx, kubeConfigId);
    	
        List<SelectDto> selectNamespaces = list.stream().map(e -> SelectDtoMapper.INSTANCE.toDto(e)).collect(Collectors.toList());
        
        for(SelectDto select : selectNamespaces) {
        	log.info(select.getText());
        }
        return selectNamespaces;
    }
    

	public List<SelectDto> getUserRoles() {
		List<UserRoleEntity> roles = userRoleDomainService.getUseUserRole();
		List<SelectDto> selectRoles = roles.stream().map(e -> SelectDtoMapper.INSTANCE.toDto(e)).collect(Collectors.toList());
		return selectRoles;
	}

}
