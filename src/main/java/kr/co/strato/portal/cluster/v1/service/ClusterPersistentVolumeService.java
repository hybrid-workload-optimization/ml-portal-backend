package kr.co.strato.portal.cluster.v1.service;

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
import io.fabric8.kubernetes.api.model.ObjectReference;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.persistentVolume.service.PersistentVolumeAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.common.service.NonNamespaceDomainService;
import kr.co.strato.domain.persistentVolume.model.PersistentVolumeEntity;
import kr.co.strato.domain.persistentVolume.service.PersistentVolumeDomainService;
import kr.co.strato.domain.storageClass.model.StorageClassEntity;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.v1.model.ClusterPersistentVolumeDto;
import kr.co.strato.portal.cluster.v1.model.ClusterPersistentVolumeDtoMapper;
import kr.co.strato.portal.common.service.NonNamespaceService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClusterPersistentVolumeService extends NonNamespaceService {

	@Autowired
	private PersistentVolumeAdapterService persistentVolumeAdapterService;
	@Autowired
	private PersistentVolumeDomainService 	persistentVolumeDomainService;
	@Autowired
	private ClusterDomainService clusterDomainService;
	
	
	public Page<ClusterPersistentVolumeDto.ResListDto> getClusterPersistentVolumeList(Pageable pageable, ClusterPersistentVolumeDto.SearchParam searchParam) {
		Page<PersistentVolumeEntity> persistentVolumePage = persistentVolumeDomainService.getPersistentVolumeList(pageable, searchParam.getClusterIdx(), searchParam.getName());
		List<ClusterPersistentVolumeDto.ResListDto> persistentVolumeList = persistentVolumePage.getContent().stream().map(c -> ClusterPersistentVolumeDtoMapper.INSTANCE.toResListDto(c)).collect(Collectors.toList());
		
		Page<ClusterPersistentVolumeDto.ResListDto> page = new PageImpl<>(persistentVolumeList, pageable, persistentVolumePage.getTotalElements());
		return page;
	}

	@Transactional(rollbackFor = Exception.class)
	public List<PersistentVolume> getClusterPersistentVolumeListSet(Long clusterId) {
		List<PersistentVolume> persistentVolumeList = persistentVolumeAdapterService.getPersistentVolumeList(clusterId);
		
		synClusterPersistentVolumeSave(persistentVolumeList, clusterId);
		return persistentVolumeList;
	}

	
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteClusterPersistentVolume(Long id){
    	PersistentVolumeEntity pv = persistentVolumeDomainService.getDetail(id.longValue());
        Long clusterId = pv.getCluster().getClusterId();
        String persistentVolumeName = pv.getName();

        boolean isDeleted = persistentVolumeAdapterService.deletePersistentVolume(clusterId.intValue(), persistentVolumeName);
        //if(isDeleted){
            return persistentVolumeDomainService.delete(id.longValue());
        //}else{
        //    throw new InternalServerException("k8s persistentVolume 삭제 실패");
        //}
    }

    public ClusterPersistentVolumeDto.ResDetailDto getClusterPersistentVolumeDetail(Long id){
    	PersistentVolumeEntity persistentVolumeEntity = persistentVolumeDomainService.getDetail(id); 

    	ClusterPersistentVolumeDto.ResDetailDto clusterPersistentVolumeDto = ClusterPersistentVolumeDtoMapper.INSTANCE.toResDetailDto(persistentVolumeEntity);
        return clusterPersistentVolumeDto;
    }
	
	
    public String getClusterPersistentVolumeYaml(Long kubeConfigId,String name){
     	String yaml = persistentVolumeAdapterService.getPersistentVolumeYaml(kubeConfigId,name); 
     	yaml = Base64Util.encode(yaml);
     	return yaml;
     }
    
    
    public String getYaml(Long id){
    	PersistentVolumeEntity eEntity = persistentVolumeDomainService.getDetail(id);
        String yaml = eEntity.getYaml();
        
        if(yaml == null) {
        	 String name = eEntity.getName();
             Long clusterId = eEntity.getCluster().getClusterId();

             yaml = persistentVolumeAdapterService.getPersistentVolumeYaml(clusterId, name);
        }
        yaml = Base64Util.encode(yaml);
        return yaml;
    }
    
	
	public List<Long> registerClusterPersistentVolume(ClusterPersistentVolumeDto.ReqCreateDto yamlApplyParam) {
		Long clusterIdx = yamlApplyParam.getClusterIdx();
		
		//이름 중복채크
		duplicateCheckResourceCreation(clusterIdx, yamlApplyParam.getYaml());
		
		
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		
		
		String yamlDecode = Base64Util.decode(yamlApplyParam.getYaml());
		List<PersistentVolume> persistentVolumeList = persistentVolumeAdapterService.registerPersistentVolume(clusterEntity.getClusterId(),	yamlDecode);
		
		//yamlApplyParam.getKubeConfigId() -> clusterId 임시저장
		List<Long> ids = synClusterPersistentVolumeSave(persistentVolumeList, clusterIdx, yamlDecode);
		return ids;
	}
	
	public List<Long> updateClusterPersistentVolume(Long PersistentVolumeId, YamlApplyParam yamlApplyParam){
        String yaml = Base64Util.decode(yamlApplyParam.getYaml());
        ClusterEntity cluster = persistentVolumeDomainService.getCluster(PersistentVolumeId);
        Long clusterId = cluster.getClusterId();

        List<PersistentVolume> persistentVolumes = persistentVolumeAdapterService.updatePersistentVolume(clusterId, yaml);

        List<Long> ids = persistentVolumes.stream().map( pv -> {
            try {
                PersistentVolumeEntity updatePersistentVolume = toEntity(pv,clusterId);
                updatePersistentVolume.setYaml(yaml);
                
                Long id = persistentVolumeDomainService.update(updatePersistentVolume, PersistentVolumeId);

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
	
	public List<Long> synClusterPersistentVolumeSave(List<PersistentVolume> persistentVolumeList, Long clusterIdx) {
		return synClusterPersistentVolumeSave(persistentVolumeList, clusterIdx, null);
	}
	
	public List<Long> synClusterPersistentVolumeSave(List<PersistentVolume> persistentVolumeList, Long clusterIdx, String yaml) {
		List<Long> ids = new ArrayList<>();
		for (PersistentVolume pv : persistentVolumeList) {
			try {
				// k8s Object -> Entity
				PersistentVolumeEntity clusterPersistentVolume = toEntity(pv,clusterIdx);
				clusterPersistentVolume.setYaml(yaml);
				
				// save
				Long id = persistentVolumeDomainService.register(clusterPersistentVolume);
				ids.add(id);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new InternalServerException("parsing error");
			}
		}

		return ids;
	}
	
	 /**
     * k8s  model ->  entity
     * @param pv
     * @return
     * @throws JsonProcessingException
     */
    private PersistentVolumeEntity toEntity(PersistentVolume pv, Long clusterIdx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
     // k8s Object -> Entity
		String name = pv.getMetadata().getName();
		String uid = pv.getMetadata().getUid();
		PersistentVolumeStatus PVstatus = pv.getStatus();
		String status = PVstatus.getPhase();
		
		String createdAt = pv.getMetadata().getCreationTimestamp();
		
		//List<String> accessModes = pv.getSpec().getAccessModes();
		String accessModes = pv.getSpec().getAccessModes().get(0);

		ObjectReference claimRef = pv.getSpec().getClaimRef();
		String claim = "";
		if(claimRef != null) {
			claim = claimRef.getNamespace() + "/" + claimRef.getName();
		}
		
		String reclaim = claim;
		
		String reclaimPolicy = pv.getSpec().getPersistentVolumeReclaimPolicy();
		String storageClassName = pv.getSpec().getStorageClassName();
		String type = "";
		String path = "";
		HostPathVolumeSource hostPath = pv.getSpec().getHostPath();
		if(hostPath != null) {
			type = "Hostpath";
			path = hostPath.getPath();
		}
		
		NFSVolumeSource nfs = pv.getSpec().getNfs();
		if(nfs != null) {
			type =  "NFS";
			path = nfs.getPath();
		}
		
		String resourceName = "";
		String size = "";
		Map<String, Quantity> capacity = pv.getSpec().getCapacity();
		Set<String> keys = capacity.keySet();
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			Quantity v = capacity.get(key);
			
			resourceName = key;
			
			String storageAmount = v.getAmount().replaceAll("[^0-9]", "");
			String formatAmount = v.getFormat();
			String c = storageAmount + formatAmount;
			
			size = c;
		}
		pv.getSpec().getCapacity().size();
		
		String annotations = mapper.writeValueAsString(pv.getMetadata().getAnnotations());
		String label = mapper.writeValueAsString(pv.getMetadata().getLabels());
		
		ClusterEntity clusterEntity = new ClusterEntity();
		clusterEntity.setClusterIdx(clusterIdx);
		
		StorageClassEntity storageClassEntity = null;
		if(storageClassName!=null) {
			storageClassEntity = persistentVolumeDomainService.getStorageClassId(storageClassName);			
		}
		

		PersistentVolumeEntity clusterPersistentVolume = PersistentVolumeEntity.builder().name(name).uid(uid).status(String.valueOf(status))
				.createdAt(DateUtil.strToLocalDateTime(createdAt))
				.accessMode(accessModes).claim(claim).reclaim(reclaim).reclaimPolicy(reclaimPolicy)
				.storageClass(storageClassEntity)
				.resourceName(resourceName)
				.type(type)
				.path(path)
				.size(size)
				.cluster(clusterEntity)
				.annotation(annotations).label(label)
				.build();

        return clusterPersistentVolume;
    }
	@Override
	protected NonNamespaceDomainService getDomainService() {
		return persistentVolumeDomainService;
	}
}
