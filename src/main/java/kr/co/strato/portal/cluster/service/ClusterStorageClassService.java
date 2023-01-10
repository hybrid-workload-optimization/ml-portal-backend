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
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.common.service.NonNamespaceDomainService;
import kr.co.strato.domain.persistentVolume.model.PersistentVolumeEntity;
import kr.co.strato.domain.persistentVolume.service.PersistentVolumeDomainService;
import kr.co.strato.domain.storageClass.model.StorageClassEntity;
import kr.co.strato.domain.storageClass.service.StorageClassDomainService;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.model.ClusterStorageClassDto;
import kr.co.strato.portal.cluster.model.ClusterStorageClassDtoMapper;
import kr.co.strato.portal.common.service.NonNamespaceService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClusterStorageClassService extends NonNamespaceService {

	@Autowired
	private StorageClassAdapterService storageClassAdapterService;
	@Autowired
	private StorageClassDomainService storageClassDomainService;
	
	@Autowired
	private PersistentVolumeDomainService persistentVolumeDomainService;
	@Autowired
	private ClusterDomainService clusterDomainService;
	
	
	public Page<ClusterStorageClassDto.ResListDto> getClusterStorageClassList(Pageable pageable, ClusterStorageClassDto.SearchParam searchParam) {
		Page<StorageClassEntity> storageClassPage = storageClassDomainService.getStorageClassList(pageable, searchParam.getClusterIdx(), searchParam.getName());
		List<ClusterStorageClassDto.ResListDto> storageClassList = storageClassPage.getContent().stream().map(c -> ClusterStorageClassDtoMapper.INSTANCE.toResListDto(c)).collect(Collectors.toList());
		
		Page<ClusterStorageClassDto.ResListDto> page = new PageImpl<>(storageClassList, pageable, storageClassPage.getTotalElements());
		return page;
	}

	@Transactional(rollbackFor = Exception.class)
	public List<StorageClass> getClusterStorageClassListSet(Long clusterId) {
		List<StorageClass> storageClassList = storageClassAdapterService.getStorageClassList(clusterId);
		
		synClusterStorageClassSave(storageClassList,clusterId);
		return storageClassList;
	}

	public List<Long> synClusterStorageClassSave(List<StorageClass> storageClassList, Long clusterId) {
		return synClusterStorageClassSave(storageClassList, clusterId, null);
	}
	
	public List<Long> synClusterStorageClassSave(List<StorageClass> storageClassList, Long clusterId, String yaml) {
		List<Long> ids = new ArrayList<>();
		for (StorageClass sc : storageClassList) {
			try {
				StorageClassEntity clusterStorageClass = toEntity(sc,clusterId);
				clusterStorageClass.setYaml(yaml);
				
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
        Long clusterId = sc.getCluster().getClusterId();
        String storageClassName = sc.getName();

        boolean isDeleted = storageClassAdapterService.deleteStorageClass(clusterId.intValue(), storageClassName);
        if(isDeleted){
            return storageClassDomainService.delete(id.longValue());
        }else{
            throw new InternalServerException("k8s StorageClass 삭제 실패");
        }
    }

	
    public ClusterStorageClassDto.ResDetailDto getClusterStorageClassDetail(Long id){
    	StorageClassEntity storageClassEntity = storageClassDomainService.getDetail(id); 
    	List<PersistentVolumeEntity> pvList = persistentVolumeDomainService.findByStorageClassIdx(id);

    	ClusterStorageClassDto.ResDetailDto clusterStorageClassDto = ClusterStorageClassDtoMapper.INSTANCE.toResDetailDto(storageClassEntity);
        List<ClusterStorageClassDto.PvList> pvListDto = pvList.stream().map(c -> ClusterStorageClassDtoMapper.INSTANCE.toPvListDto(c)).collect(Collectors.toList());
        clusterStorageClassDto.setPvList(pvListDto);
    	
    	return clusterStorageClassDto;
    }
	
	
    public String getClusterStorageClassYaml(Long kubeConfigId,String name){
     	String yaml = storageClassAdapterService.getStorageClassYaml(kubeConfigId,name); 
     	yaml = Base64Util.encode(yaml);
         return yaml;
     }
    
    
    public String getYaml(Long id){
    	StorageClassEntity eEntity = storageClassDomainService.getDetail(id);
        String yaml = eEntity.getYaml();
        
        if(yaml == null) {
        	 String name = eEntity.getName();
             Long clusterId = eEntity.getCluster().getClusterId();

             yaml = storageClassAdapterService.getStorageClassYaml(clusterId, name);
        }
        yaml = Base64Util.encode(yaml);
        return yaml;
    }
    
	
	public List<Long> registerClusterStorageClass(ClusterStorageClassDto.ReqCreateDto yamlApplyParam) {
		Long clusterIdx = yamlApplyParam.getClusterIdx();
		
		//이름 중복채크
		duplicateCheckResourceCreation(clusterIdx, yamlApplyParam.getYaml());
		
		
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		
		String yamlDecode = Base64Util.decode(yamlApplyParam.getYaml());
		
		List<StorageClass> storageClassList = storageClassAdapterService.registerStorageClass(clusterEntity.getClusterId(), yamlDecode);
		List<Long> ids = new ArrayList<>();

		for (StorageClass sc : storageClassList) {
			try {
				// k8s Object -> Entity
				StorageClassEntity clusterStorageClass = toEntity(sc, clusterIdx);
				clusterStorageClass.setYaml(yamlDecode);

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
        Long clusterId = cluster.getClusterId();

        List<StorageClass> storageClass = storageClassAdapterService.registerStorageClass(clusterId, yaml);

        List<Long> ids = storageClass.stream().map( sc -> {
            try {
            	StorageClassEntity updatestorageClass = toEntity(sc,clusterId);
            	updatestorageClass.setYaml(yaml);

                Long id = storageClassDomainService.update(updatestorageClass, storageClassId);

                return id;
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("json 파싱 에러");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("StorageClass update error");
            }
        }).collect(Collectors.toList());
        return ids;
    }
	
	
	 private StorageClassEntity toEntity(StorageClass sc, Long clusterIdx) throws JsonProcessingException {
	        ObjectMapper mapper = new ObjectMapper();
	    	// k8s Object -> Entity
			String name = sc.getMetadata().getName();
			String uid = sc.getMetadata().getUid();
			String createdAt = sc.getMetadata().getCreationTimestamp();
			String provider = sc.getProvisioner();
			String type = null;
			if(sc.getParameters() != null) {
				type = sc.getParameters().get("type");
			}
			
			
			String annotations = mapper.writeValueAsString(sc.getMetadata().getAnnotations());
			String label = mapper.writeValueAsString(sc.getMetadata().getLabels());
			
			ClusterEntity clusterEntity = new ClusterEntity();
			clusterEntity.setClusterIdx(clusterIdx);
			
			StorageClassEntity clusterStorageClass = StorageClassEntity.builder().name(name).uid(uid)
					.createdAt(DateUtil.strToLocalDateTime(createdAt))
					.provider(provider).type(type)
					.cluster(clusterEntity)
					.annotation(annotations).label(label)
					.build();

	        return clusterStorageClass;
	    }

	@Override
	protected NonNamespaceDomainService getDomainService() {
		return storageClassDomainService;
	}
	
}
