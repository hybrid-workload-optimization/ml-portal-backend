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
import io.fabric8.kubernetes.api.model.ObjectReference;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.persistentVolume.service.PersistentVolumeAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.persistentVolume.model.PersistentVolumeEntity;
import kr.co.strato.domain.persistentVolume.service.PersistentVolumeDomainService;
import kr.co.strato.domain.storageClass.model.StorageClassEntity;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.model.ClusterPersistentVolumeDto;
import kr.co.strato.portal.cluster.model.ClusterPersistentVolumeDtoMapper;

@Service
public class ClusterPersistentVolumeService {

	@Autowired
	private PersistentVolumeAdapterService persistentVolumeAdapterService;
	@Autowired
	private PersistentVolumeDomainService 	persistentVolumeDomainService;
	
	
	public Page<ClusterPersistentVolumeDto> getClusterPersistentVolumeList(String name,Pageable pageable) {
		Page<PersistentVolumeEntity> persistentVolumePage = persistentVolumeDomainService.findByName(name,pageable);
		List<ClusterPersistentVolumeDto> persistentVolumeList = persistentVolumePage.getContent().stream().map(c -> ClusterPersistentVolumeDtoMapper.INSTANCE.toDto(c)).collect(Collectors.toList());
		
		Page<ClusterPersistentVolumeDto> page = new PageImpl<>(persistentVolumeList, pageable, persistentVolumePage.getTotalElements());
		return page;
	}

	@Transactional(rollbackFor = Exception.class)
	public List<PersistentVolume> getClusterPersistentVolumeListSet(Integer clusterId) {
		List<PersistentVolume> persistentVolumeList = persistentVolumeAdapterService.getPersistentVolumeList(clusterId);
		
		synClusterPersistentVolumeSave(persistentVolumeList,clusterId);
		return persistentVolumeList;
	}

	public List<Long> synClusterPersistentVolumeSave(List<PersistentVolume> persistentVolumeList, Integer clusterId) {
		List<Long> ids = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		for (PersistentVolume pv : persistentVolumeList) {
			try {
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
				int size = 0;
				Map<String, Quantity> capacity = pv.getSpec().getCapacity();
				Set<String> keys = capacity.keySet();
				Iterator<String> iter = keys.iterator();
				while (iter.hasNext()) {
					String key = iter.next();
					Quantity v = capacity.get(key);
					
					resourceName = key;
					
					String storageAmount = v.getAmount().replaceAll("[^0-9]", "");
					//String formatAmount = v.getFormat();
					//String c = storageAmount + formatAmount;
					
					size = Integer.parseInt(storageAmount);
				}
				pv.getSpec().getCapacity().size();
				
				String annotations = mapper.writeValueAsString(pv.getMetadata().getAnnotations());
				String label = mapper.writeValueAsString(pv.getMetadata().getLabels());
				
				ClusterEntity clusterEntity = new ClusterEntity();
				clusterEntity.setClusterIdx(Integer.toUnsignedLong(clusterId));
				
				StorageClassEntity storageClassEntity = persistentVolumeDomainService.getStorageClassId(storageClassName);
	
				PersistentVolumeEntity clusterPersistentVolume = PersistentVolumeEntity.builder().name(name).uid(uid).status(String.valueOf(status))
						.createdAt(DateUtil.strToLocalDateTime(createdAt))
						.accessMode(accessModes).claim(claim).reclaim(reclaim).reclaimPolicy(reclaimPolicy)
						.storageClassIdx(storageClassEntity)
						.resourceName(resourceName)
						.type(type).path(path).size(size)
						.clusterIdx(clusterEntity)
						.annotation(annotations).label(label)
						.build();

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
	
	public void deleteClusterPersistentVolume(Integer clusterId, PersistentVolumeEntity persistentVolumeEntity) throws Exception {
		persistentVolumeDomainService.delete(persistentVolumeEntity);
		persistentVolumeAdapterService.deletePersistentVolume(clusterId, persistentVolumeEntity.getName());
	}
	
    public ClusterPersistentVolumeDto getClusterPersistentVolumeDetail(Long id){
    	PersistentVolumeEntity persistentVolumeEntity = persistentVolumeDomainService.getDetail(id); 

    	ClusterPersistentVolumeDto clusterPersistentVolumeDto = ClusterPersistentVolumeDtoMapper.INSTANCE.toDto(persistentVolumeEntity);
        return clusterPersistentVolumeDto;
    }
	
	
    public String getClusterPersistentVolumeYaml(Integer kubeConfigId,String name){
     	String namespaceYaml = persistentVolumeAdapterService.getPersistentVolumeYaml(kubeConfigId,name); 
         return namespaceYaml;
     }
    
	
	public List<Long> registerClusterPersistentVolume(YamlApplyParam yamlApplyParam, Integer clusterId) {
		List<PersistentVolume> persistentVolumeList = persistentVolumeAdapterService.registerPersistentVolume(yamlApplyParam.getKubeConfigId(),
				yamlApplyParam.getYaml());
		List<Long> ids = new ArrayList<>();

		ObjectMapper mapper = new ObjectMapper();
		for (PersistentVolume pv : persistentVolumeList) {
			try {
				// k8s Object -> Entity
				String name = pv.getMetadata().getName();
				String uid = pv.getMetadata().getUid();
				PersistentVolumeStatus status = pv.getStatus();
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
				int size = 0;
				Map<String, Quantity> capacity = pv.getSpec().getCapacity();
				Set<String> keys = capacity.keySet();
				Iterator<String> iter = keys.iterator();
				while (iter.hasNext()) {
					String key = iter.next();
					Quantity v = capacity.get(key);
					
					resourceName = key;
					
					String storageAmount = v.getAmount();
					//String formatAmount = v.getFormat();
					//String c = storageAmount + formatAmount;
					
					size = Integer.parseInt(storageAmount);
				}
				pv.getSpec().getCapacity().size();
				
				String annotations = mapper.writeValueAsString(pv.getMetadata().getAnnotations());
				String label = mapper.writeValueAsString(pv.getMetadata().getLabels());
				
				ClusterEntity clusterEntity = new ClusterEntity();
				clusterEntity.setClusterIdx(Integer.toUnsignedLong(clusterId));
				StorageClassEntity storageClassEntity = persistentVolumeDomainService.getStorageClassId(storageClassName);

				PersistentVolumeEntity clusterPersistentVolume = PersistentVolumeEntity.builder().name(name).uid(uid).status(String.valueOf(status))
						.createdAt(DateUtil.strToLocalDateTime(createdAt))
						.accessMode(accessModes).claim(claim).reclaim(reclaim).reclaimPolicy(reclaimPolicy)
						.storageClassIdx(storageClassEntity)
						.resourceName(resourceName)
						.type(type).path(path).size(size)
						.clusterIdx(clusterEntity)
						.annotation(annotations).label(label)
						.build();

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

}
