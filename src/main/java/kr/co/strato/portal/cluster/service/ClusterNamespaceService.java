package kr.co.strato.portal.cluster.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.Namespace;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.namespace.service.NamespaceAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.common.service.NonNamespaceDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.model.ClusterNamespaceDto;
import kr.co.strato.portal.cluster.model.ClusterNamespaceDtoMapper;
import kr.co.strato.portal.common.service.NonNamespaceService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClusterNamespaceService extends NonNamespaceService {

	@Autowired
	private NamespaceAdapterService namespaceAdapterService;
	@Autowired
	private NamespaceDomainService 	namespaceDomainService;
	@Autowired
	private ClusterDomainService clusterDomainService;
	
	
	public Page<ClusterNamespaceDto.ResListDto> getClusterNamespaceList(Pageable pageable, ClusterNamespaceDto.SearchParam searchParam) {
		Page<NamespaceEntity> namespacePage = namespaceDomainService.getNamespaceList(pageable, searchParam.getClusterIdx(), searchParam.getName());
		List<ClusterNamespaceDto.ResListDto> namespaceList = namespacePage.getContent().stream().map(c -> ClusterNamespaceDtoMapper.INSTANCE.toResListDto(c)).collect(Collectors.toList());
		
		Page<ClusterNamespaceDto.ResListDto> page = new PageImpl<>(namespaceList, pageable, namespacePage.getTotalElements());
		return page;
	}

	@Transactional(rollbackFor = Exception.class)
	public List<Namespace> getClusterNamespaceListSet(Long kubeConfigId) {
		List<Namespace> namespaceList = namespaceAdapterService.getNamespaceList(kubeConfigId);
		
		synClusterNamespaceSave(namespaceList,kubeConfigId);
		return namespaceList;
	}


    @Transactional(rollbackFor = Exception.class)
    public boolean deleteClusterNamespace(Long id){
    	NamespaceEntity n = namespaceDomainService.getDetail(id.longValue());
        Long clusterId = n.getCluster().getClusterId();
        String resourceName = n.getName();

        boolean isDeleted = namespaceAdapterService.deleteNamespace(clusterId.intValue(), resourceName);
        if(isDeleted){
            return namespaceDomainService.delete(id.longValue());
        }else{
            throw new InternalServerException("k8s Namespace 삭제 실패");
        }
    }

    public ClusterNamespaceDto.ResDetailDto getClusterNamespaceDetail(Long id){
    	NamespaceEntity nodeEntity = namespaceDomainService.getDetail(id); 

    	ClusterNamespaceDto.ResDetailDto clusterNodeDto = ClusterNamespaceDtoMapper.INSTANCE.toResDetailDto(nodeEntity);
        return clusterNodeDto;
    }
    
	
   public String getClusterNamespaceYaml(Long kubeConfigId,String name){
    	String yaml = namespaceAdapterService.getNamespaceYaml(kubeConfigId,name); 
    	
    	yaml = Base64Util.encode(yaml);
        return yaml;
    }
   
	public List<Long> registerClusterNamespace(ClusterNamespaceDto.ReqCreateDto yamlApplyParam) {
		Long clusterIdx = yamlApplyParam.getClusterIdx();
		
		//이름 중복채크
		duplicateCheckResourceCreation(clusterIdx, yamlApplyParam.getYaml());
		
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		
		
		String yamlDecode = Base64Util.decode(yamlApplyParam.getYaml());
		
		List<Namespace> clusterNamespaces = namespaceAdapterService.registerNamespace(clusterEntity.getClusterId(), yamlDecode);
		List<Long> ids = new ArrayList<>();
		for (Namespace n : clusterNamespaces) {
			try {
				NamespaceEntity namespace = toEntity(n, clusterIdx);
				// save
				Long id = namespaceDomainService.register(namespace);
				ids.add(id);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new InternalServerException("parsing error");
			}
		}

		return ids;
	}
	
	
	public List<Long> updateClusterNamespace(Long namespaceId, YamlApplyParam yamlApplyParam){
        String yaml = Base64Util.decode(yamlApplyParam.getYaml());
        ClusterEntity cluster = namespaceDomainService.getCluster(namespaceId);
        Long clusterId = cluster.getClusterId();

        List<Namespace> namespaces = namespaceAdapterService.registerNamespace(clusterId, yaml);

        List<Long> ids = namespaces.stream().map( n -> {
            try {
            	NamespaceEntity updatePersistentVolume = toEntity(n,clusterId);

                Long id = namespaceDomainService.update(updatePersistentVolume, namespaceId);

                return id;
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("json 파싱 에러");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("PersistentVolume update error");
            }
        }).collect(Collectors.toList());

        return ids;

    }
	
	
	public List<Long> synClusterNamespaceSave(List<Namespace> clusterNamespaces, Long clusterIdx) {
		List<Long> ids = new ArrayList<>();
		
		for (Namespace n : clusterNamespaces) {
			try {
				NamespaceEntity namespace = toEntity(n, clusterIdx);
				// save
				Long id = namespaceDomainService.register(namespace);
				ids.add(id);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new InternalServerException("parsing error");
			}
		}

		return ids;
	}
	
	 private NamespaceEntity toEntity(Namespace	n, Long clusterIdx) throws JsonProcessingException {
	        ObjectMapper mapper = new ObjectMapper();
	     // k8s Object -> Entity
	       
			// k8s Object -> Entity
			String name = n.getMetadata().getName();
			String uid = n.getMetadata().getUid();
		// List<NamespaceCondition> conditions = n.getStatus().getConditions();
		//	boolean status = conditions.stream().filter(condition -> condition.getType().equals("Ready"))
		//			.map(condition -> condition.getStatus().equals("True")).findFirst().orElse(false);
			String status = n.getStatus().getPhase();
			String createdAt = n.getMetadata().getCreationTimestamp();
			String annotations = mapper.writeValueAsString(n.getMetadata().getAnnotations());
			String label = mapper.writeValueAsString(n.getMetadata().getLabels());

			ClusterEntity clusterEntity = new ClusterEntity();
			clusterEntity.setClusterIdx(clusterIdx);

			NamespaceEntity namespace = NamespaceEntity.builder().name(name).uid(uid).status(String.valueOf(status))
					.createdAt(DateUtil.strToLocalDateTime(createdAt))
					.cluster(clusterEntity)
					.annotation(annotations).label(label)
					.build();

	        return namespace;
	    }

	@Override
	protected NonNamespaceDomainService getDomainService() {
		return namespaceDomainService;
	}
}
