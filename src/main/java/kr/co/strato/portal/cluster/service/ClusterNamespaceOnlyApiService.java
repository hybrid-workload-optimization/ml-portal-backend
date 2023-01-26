package kr.co.strato.portal.cluster.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.fabric8.kubernetes.api.model.Namespace;
import kr.co.strato.adapter.k8s.namespace.service.NamespaceAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.portal.cluster.model.ClusterNamespaceDto;
import kr.co.strato.portal.cluster.model.ClusterNamespaceDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClusterNamespaceOnlyApiService {

	@Autowired
	private NamespaceAdapterService namespaceAdapterService;
	
	@Autowired
	private ClusterDomainService clusterDomainService;
	
	
	public Page<ClusterNamespaceDto.ResListDto> getClusterNamespaceList(Pageable pageable, ClusterNamespaceDto.SearchParam searchParam) {
		ClusterEntity cluster = clusterDomainService.get(searchParam.getClusterIdx());
		Long KubeConfigId = cluster.getClusterId();
		String keyword = searchParam.getName();
		
		List<Namespace> list = namespaceAdapterService.getNamespaceList(KubeConfigId);
		
		Stream<Namespace> stream = list.stream();
		if(keyword != null) {
			stream = stream.filter(n -> n.getMetadata().getName().contains(keyword));
		}
		List<ClusterNamespaceDto.ResListDto> namespaceList = stream
				.map(c -> (ClusterNamespaceDto.ResListDto)ClusterNamespaceDtoMapper.INSTANCE.toDto(c, false))
				.collect(Collectors.toList());
		
		namespaceList.stream().forEach(n -> n.setClusterIdx(searchParam.getClusterIdx()));		
		Page<ClusterNamespaceDto.ResListDto> page = new PageImpl<>(namespaceList, pageable, namespaceList.size());
		return page;
	}


    @Transactional(rollbackFor = Exception.class)
    public boolean deleteClusterNamespace(Long clusterIdx, String name){
    	ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		Long KubeConfigId = cluster.getClusterId();
    	return namespaceAdapterService.deleteNamespace(KubeConfigId.intValue(), name);
    }

    
    public ClusterNamespaceDto.ResDetailDto getClusterNamespaceDetail(Long clusterIdx, String name) {
    	ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		Long KubeConfigId = cluster.getClusterId();
    	Namespace namespace = namespaceAdapterService.getNamespace(KubeConfigId, name);
    	ClusterNamespaceDto.ResDetailDto clusterNodeDto = (ClusterNamespaceDto.ResDetailDto) 
    			ClusterNamespaceDtoMapper.INSTANCE.toDto(namespace, true);
    	clusterNodeDto.setClusterIdx(clusterIdx);    	
        return clusterNodeDto;
    }
    
	
	public String getClusterNamespaceYaml(Long clusterIdx, String name) {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		String yaml = namespaceAdapterService.getNamespaceYaml(clusterEntity.getClusterId(), name);
		yaml = Base64Util.encode(yaml);
		return yaml;
	}

	public void registerClusterNamespace(ClusterNamespaceDto.ReqCreateDto yamlApplyParam) {
		ClusterEntity clusterEntity = clusterDomainService.get(yamlApplyParam.getClusterIdx());
		Long kubeConfigId = clusterEntity.getClusterId();
		String yamlDecode = Base64Util.decode(yamlApplyParam.getYaml());
		List<Namespace> clusterNamespaces = namespaceAdapterService.registerNamespace(kubeConfigId, yamlDecode);
	}
	
	
	public void updateClusterNamespace(ClusterNamespaceDto.ReqCreateDto yamlApplyParam){
		ClusterEntity clusterEntity = clusterDomainService.get(yamlApplyParam.getClusterIdx());
        Long kubeConfigId = clusterEntity.getClusterId();

        String yaml = Base64Util.decode(yamlApplyParam.getYaml());
        List<Namespace> namespaces = namespaceAdapterService.registerNamespace(kubeConfigId, yaml);
    }
	
}
