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

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.storageClass.service.StorageClassAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.storageClass.model.StorageClassEntity;
import kr.co.strato.domain.storageClass.service.StorageClassDomainService;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.model.ClusterStorageClassDto;
import kr.co.strato.portal.cluster.model.ClusterStorageClassDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClusterStorageClassService {

	@Autowired
	private StorageClassAdapterService storageClassAdapterService;
	@Autowired
	private StorageClassDomainService storageClassDomainService;
	
	
	public Page<ClusterStorageClassDto> getClusterStorageClassList(String name,Pageable pageable) {
		Page<StorageClassEntity> storageClassPage = storageClassDomainService.findByName(name,pageable);
		List<ClusterStorageClassDto> storageClassList = storageClassPage.getContent().stream().map(c -> ClusterStorageClassDtoMapper.INSTANCE.toDto(c)).collect(Collectors.toList());
		
		Page<ClusterStorageClassDto> page = new PageImpl<>(storageClassList, pageable, storageClassPage.getTotalElements());
		return page;
	}

	@Transactional(rollbackFor = Exception.class)
	public List<StorageClass> getClusterStorageClassListSet(Integer clusterId) {
		List<StorageClass> storageClassList = storageClassAdapterService.getStorageClassList(clusterId);
		
		synClusterStorageClassSave(storageClassList,clusterId);
		return storageClassList;
	}

	public List<Long> synClusterStorageClassSave(List<StorageClass> storageClassList, Integer clusterId) {
		List<Long> ids = new ArrayList<>();
		for (StorageClass sc : storageClassList) {
			try {
				StorageClassEntity clusterStorageClass = toEntity(sc,clusterId);

				// save
				Long id = storageClassDomainService.register(clusterStorageClass);
				ids.add(id);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new InternalServerException("parsing error");
			}
		}

		return ids;
	}
	
	
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteClusterStorageClass(Long id){
    	StorageClassEntity sc = storageClassDomainService.getDetail(id.longValue());
        Long clusterId = sc.getClusterIdx().getClusterId();
        String storageClassName = sc.getName();

        boolean isDeleted = storageClassAdapterService.deleteStorageClass(clusterId.intValue(), storageClassName);
        if(isDeleted){
            return storageClassDomainService.delete(id.longValue());
        }else{
            throw new InternalServerException("k8s StorageClass 삭제 실패");
        }
    }

	
    public ClusterStorageClassDto getClusterStorageClassDetail(Long id){
    	StorageClassEntity storageClassEntity = storageClassDomainService.getDetail(id); 

    	ClusterStorageClassDto clusterStorageClassDto = ClusterStorageClassDtoMapper.INSTANCE.toDto(storageClassEntity);
        return clusterStorageClassDto;
    }
	
	
    public String getClusterStorageClassYaml(Integer kubeConfigId,String name){
     	String namespaceYaml = storageClassAdapterService.getStorageClassYaml(kubeConfigId,name); 
         return namespaceYaml;
     }
    
	
	public List<Long> registerClusterStorageClass(YamlApplyParam yamlApplyParam, Integer clusterId) {
		String yamlDecode = Base64Util.decode(yamlApplyParam.getYaml());
		
		List<StorageClass> storageClassList = storageClassAdapterService.registerStorageClass(yamlApplyParam.getKubeConfigId(), yamlDecode);
		List<Long> ids = new ArrayList<>();

		for (StorageClass sc : storageClassList) {
			try {
				// k8s Object -> Entity
				StorageClassEntity clusterStorageClass = toEntity(sc,clusterId);

				// save
				Long id = storageClassDomainService.register(clusterStorageClass);
				ids.add(id);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new InternalServerException("parsing error");
			}
		}

		return ids;
	}
	
	public List<Long> updateClusterStorageClass(Long storageClassId, YamlApplyParam yamlApplyParam){
        String yaml = Base64Util.decode(yamlApplyParam.getYaml());
        ClusterEntity cluster = storageClassDomainService.getCluster(storageClassId);
        Integer clusterId = Long.valueOf(cluster.getClusterId()).intValue();

        List<StorageClass> storageClass = storageClassAdapterService.registerStorageClass(clusterId.intValue(), yaml);

        List<Long> ids = storageClass.stream().map( sc -> {
            try {
            	StorageClassEntity updatestorageClass = toEntity(sc,clusterId);

                Long id = storageClassDomainService.update(updatestorageClass, storageClassId, clusterId.longValue());

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
	
	
	 private StorageClassEntity toEntity(StorageClass sc,Integer clusterId) throws JsonProcessingException {
	        ObjectMapper mapper = new ObjectMapper();
	    	// k8s Object -> Entity
			String name = sc.getMetadata().getName();
			String uid = sc.getMetadata().getUid();
			String createdAt = sc.getMetadata().getCreationTimestamp();
			String provider = sc.getProvisioner();
			String type = sc.getParameters().get("type");
			
			String annotations = mapper.writeValueAsString(sc.getMetadata().getAnnotations());
			String label = mapper.writeValueAsString(sc.getMetadata().getLabels());
			
			ClusterEntity clusterEntity = new ClusterEntity();
			clusterEntity.setClusterIdx(Integer.toUnsignedLong(clusterId));
			
			StorageClassEntity clusterStorageClass = StorageClassEntity.builder().name(name).uid(uid)
					.createdAt(DateUtil.strToLocalDateTime(createdAt))
					.provider(provider).type(type)
					.clusterIdx(clusterEntity)
					.annotation(annotations).label(label)
					.build();

	        return clusterStorageClass;
	    }
		
	
}
