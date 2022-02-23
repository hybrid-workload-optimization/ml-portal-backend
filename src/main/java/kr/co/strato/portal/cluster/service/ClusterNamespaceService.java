package kr.co.strato.portal.cluster.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.HostPathVolumeSource;
import io.fabric8.kubernetes.api.model.NFSVolumeSource;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceCondition;
import io.fabric8.kubernetes.api.model.ObjectReference;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.namespace.service.NamespaceAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.persistentVolume.model.PersistentVolumeEntity;
import kr.co.strato.domain.storageClass.model.StorageClassEntity;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.model.ClusterNamespaceDto;
import kr.co.strato.portal.cluster.model.ClusterNamespaceDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClusterNamespaceService {

	@Autowired
	private NamespaceAdapterService namespaceAdapterService;
	@Autowired
	private NamespaceDomainService 	namespaceDomainService;
	
	
	public Page<ClusterNamespaceDto> getClusterNamespaceList(String name,Pageable pageable) {
		Page<NamespaceEntity> namespacePage = namespaceDomainService.findByName(name,pageable);
		List<ClusterNamespaceDto> namespaceList = namespacePage.getContent().stream().map(c -> ClusterNamespaceDtoMapper.INSTANCE.toDto(c)).collect(Collectors.toList());
		
		Page<ClusterNamespaceDto> page = new PageImpl<>(namespaceList, pageable, namespacePage.getTotalElements());
		return page;
	}

	@Transactional(rollbackFor = Exception.class)
	public List<Namespace> getClusterNamespaceListSet(Integer kubeConfigId) {
		List<Namespace> namespaceList = namespaceAdapterService.getNamespaceList(kubeConfigId);
		
		synClusterNamespaceSave(namespaceList,kubeConfigId);
		return namespaceList;
	}


	
	public void deleteClusterNamespace(Integer kubeConfigId, NamespaceEntity namespaceEntity) throws Exception {
		namespaceDomainService.delete(namespaceEntity);
		namespaceAdapterService.deleteNode(kubeConfigId, namespaceEntity.getName());
	}
	
    public ClusterNamespaceDto getClusterNamespaceDetail(Long id){
    	NamespaceEntity nodeEntity = namespaceDomainService.getDetail(id); 

    	ClusterNamespaceDto clusterNodeDto = ClusterNamespaceDtoMapper.INSTANCE.toDto(nodeEntity);
        return clusterNodeDto;
    }
    
	
   public String getClusterNamespaceYaml(Integer kubeConfigId,String name){
    	String namespaceYaml = namespaceAdapterService.getNamespaceYaml(kubeConfigId,name); 
        return namespaceYaml;
    }
   
	public List<Long> registerClusterNamespace(YamlApplyParam yamlApplyParam, Integer kubeConfigId) {
		List<Namespace> clusterNamespaces = namespaceAdapterService.registerNamespace(yamlApplyParam.getKubeConfigId(),yamlApplyParam.getYaml());
		List<Long> ids = new ArrayList<>();
		for (Namespace n : clusterNamespaces) {
			try {
				NamespaceEntity namespace = toEntity(n,kubeConfigId);
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
        Integer clusterId = Long.valueOf(cluster.getClusterId()).intValue();

        List<Namespace> namespaces = namespaceAdapterService.registerNamespace(clusterId.intValue(), yaml);

        List<Long> ids = namespaces.stream().map( n -> {
            try {
            	NamespaceEntity updatePersistentVolume = toEntity(n,clusterId);

                Long id = namespaceDomainService.update(updatePersistentVolume, namespaceId, clusterId.longValue());

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
	
	
	public List<Long> synClusterNamespaceSave(List<Namespace> clusterNamespaces, Integer kubeConfigId) {
		List<Long> ids = new ArrayList<>();
		
		for (Namespace n : clusterNamespaces) {
			try {
				NamespaceEntity namespace = toEntity(n,kubeConfigId);
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
	
	 private NamespaceEntity toEntity(Namespace	n,Integer clusterId) throws JsonProcessingException {
	        ObjectMapper mapper = new ObjectMapper();
	     // k8s Object -> Entity
	        List<NamespaceCondition> conditions = n.getStatus().getConditions();
			// k8s Object -> Entity
			String name = n.getMetadata().getName();
			String uid = n.getMetadata().getUid();

			boolean status = conditions.stream().filter(condition -> condition.getType().equals("Ready"))
					.map(condition -> condition.getStatus().equals("True")).findFirst().orElse(false);

			String createdAt = n.getMetadata().getCreationTimestamp();
			String annotations = mapper.writeValueAsString(n.getMetadata().getAnnotations());
			String label = mapper.writeValueAsString(n.getMetadata().getLabels());

			ClusterEntity clusterEntity = new ClusterEntity();
			clusterEntity.setClusterIdx(Integer.toUnsignedLong(clusterId));

			NamespaceEntity namespace = NamespaceEntity.builder().name(name).uid(uid).status(String.valueOf(status))
					.createdAt(DateUtil.strToLocalDateTime(createdAt))
					.clusterIdx(clusterEntity)
					.annotation(annotations).label(label)
					.build();

	        return namespace;
	    }
}
