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
import io.fabric8.kubernetes.api.model.NamespaceCondition;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.namespace.service.NamespaceAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.model.ClusterNamespaceDto;
import kr.co.strato.portal.cluster.model.ClusterNamespaceDtoMapper;

@Service
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
	public List<Namespace> getClusterNamespaceListSet(Integer clusterId) {
		List<Namespace> namespaceList = namespaceAdapterService.getNamespaceList(clusterId);
		
		synClusterNamespaceSave(namespaceList,clusterId);
		return namespaceList;
	}

	public List<Long> synClusterNamespaceSave(List<Namespace> clusterNamespaces, Integer kubeConfigId) {
		List<Long> ids = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		for (Namespace n : clusterNamespaces) {
			try {
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
				clusterEntity.setClusterIdx(Integer.toUnsignedLong(kubeConfigId));

				NamespaceEntity namespace = NamespaceEntity.builder().name(name).uid(uid).status(String.valueOf(status))
						.createdAt(DateUtil.strToLocalDateTime(createdAt))
						.clusterIdx(clusterEntity)
						.annotation(annotations).label(label)
						.build();

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
		ObjectMapper mapper = new ObjectMapper();
		for (Namespace n : clusterNamespaces) {
			try {
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
				clusterEntity.setClusterIdx(Integer.toUnsignedLong(kubeConfigId));

				NamespaceEntity namespace = NamespaceEntity.builder().name(name).uid(uid).status(String.valueOf(status))
						.createdAt(DateUtil.strToLocalDateTime(createdAt))
						.clusterIdx(clusterEntity)
						.annotation(annotations).label(label)
						.build();

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

}
