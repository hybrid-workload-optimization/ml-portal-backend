package kr.co.strato.portal.cluster.v2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.storageClass.service.StorageClassAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.v2.model.PersistentVolumeDto;
import kr.co.strato.portal.cluster.v2.model.StorageClassDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageClassService {
	
	@Autowired
	private ClusterDomainService clusterDomainService;
	
	@Autowired
	private StorageClassAdapterService storageClassAdapterService;
	
	@Autowired
	private PersistentVolumeService persistentVolumeService;

	/**
	 * 리스트
	 * @param clusterIdx
	 * @return
	 */
	public List<StorageClassDto.ListDto> getList(Long clusterIdx) {
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		Long kubeConfigId = cluster.getClusterId();
		
		List<StorageClassDto.ListDto> list = new ArrayList<>();
		List<StorageClass> scs = storageClassAdapterService.getStorageClassList(kubeConfigId);
		for(StorageClass sc  : scs) {
			StorageClassDto.ListDto dto = new StorageClassDto.ListDto();
			getStorageClassDto(sc, dto);
			list.add(dto);
		}
		return list;
	}
	
	/**
	 * 상세
	 * @param clusterIdx
	 * @param name
	 * @return
	 */
	public StorageClassDto.DetailDto getDetail(Long clusterIdx, String name) {
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		Long kubeConfigId = cluster.getClusterId();
		
		StorageClass sc = storageClassAdapterService.getStorageClass(kubeConfigId, name);
		StorageClassDto.DetailDto dto = new StorageClassDto.DetailDto();
		getStorageClassDto(sc, dto);
		
		//pv 리스트
		ResourceListSearchInfo param = ResourceListSearchInfo.builder()
				.kubeConfigId(kubeConfigId)
				.storageClass(dto.getName())
				.build();
		List<PersistentVolumeDto.ListDto> pvList = persistentVolumeService.getList(param);
		dto.setPvList(pvList);
		return dto;
	}
	
	/**
	 * 삭제
	 * @param param
	 * @return
	 */
	public boolean delete(StorageClassDto.DeleteDto param) {
		Long clusterIdx = param.getClusterIdx();
		String name = param.getName();		
		
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		boolean isOk = storageClassAdapterService.deleteStorageClass(cluster.getClusterId().intValue(), name);
		return isOk;
	}
	
	public void getStorageClassDto(StorageClass storageClass, StorageClassDto.ListDto dto) {
		String name = storageClass.getMetadata().getName();
		String uid = storageClass.getMetadata().getUid();
		String createdAt = DateUtil.strToNewFormatter(storageClass.getMetadata().getCreationTimestamp());
		String provisioner = storageClass.getProvisioner();
		String type = null;
		if(storageClass.getParameters() != null) {
			type = storageClass.getParameters().get("type");
		}
		
		Map<String, String> annotations = storageClass.getMetadata().getAnnotations();
		Map<String, String> labels = storageClass.getMetadata().getLabels();
		
		dto.setUid(uid);
		dto.setName(name);
		dto.setProvisioner(provisioner);
		dto.setType(type);
		dto.setAnnotations(annotations);
		dto.setLabels(labels);
		dto.setCreatedAt(createdAt);
	}
}
