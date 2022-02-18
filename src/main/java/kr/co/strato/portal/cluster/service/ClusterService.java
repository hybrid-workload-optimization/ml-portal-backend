package kr.co.strato.portal.cluster.service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.k8s.cluster.model.ClusterAdapterDto;
import kr.co.strato.adapter.k8s.cluster.service.ClusterAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.cluster.model.ClusterDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClusterService {

	@Autowired
	ClusterDomainService clusterDomainService;
	
	@Autowired
	ClusterAdapterService clusterAdapterService;
	
	public Page<ClusterDto> getClusterList(Pageable pageable) throws Exception {
		Page<ClusterEntity> clusterPage = clusterDomainService.getList(pageable);
		List<ClusterDto> clusterList = clusterPage.getContent().stream().map(c -> ClusterDtoMapper.INSTANCE.toDto(c)).collect(Collectors.toList());
		
		return new PageImpl<>(clusterList, pageable, clusterPage.getTotalElements());
	}
	
	/**
	 * Cluster 등록
	 * 
	 * @param clusterDto
	 * @return
	 * @throws Exception
	 */
	public Long registerCluster(ClusterDto clusterDto) throws Exception {
		ClusterAdapterDto clusterAdapterDto = ClusterAdapterDto.builder()
				.provider(clusterDto.getProvider())
				.configContents(Base64.getEncoder().encodeToString(clusterDto.getKubeConfig().getBytes()))
				.build();
		
		String clusterId = clusterAdapterService.registerCluster(clusterAdapterDto);
		if (StringUtils.isEmpty(clusterId)) {
			throw new PortalException("Cluster registration failed");
		}
		
		ClusterEntity clusterEntity = ClusterDtoMapper.INSTANCE.toEntity(clusterDto);
		clusterEntity.setClusterId(Long.valueOf(clusterId));
		
		clusterDomainService.register(clusterEntity);
		
		return clusterEntity.getClusterIdx();
	}

	public Long updateCluster(Long clusterIdx, ClusterDto clusterDto) throws Exception {
		ClusterAdapterDto clusterAdapterDto = ClusterAdapterDto.builder()
				.provider(clusterDto.getProvider())
				.configContents(Base64.getEncoder().encodeToString(clusterDto.getKubeConfig().getBytes()))
				.kubeConfigId(clusterDto.getClusterId())
				.build();
		
		String isUpdated = clusterAdapterService.updateCluster(clusterAdapterDto);
		if (!StringUtils.equals(isUpdated, "true")) {
			throw new PortalException("Cluster modification failed");
		}
		
		ClusterEntity clusterEntity = ClusterDtoMapper.INSTANCE.toEntity(clusterDto);
		clusterEntity.setClusterIdx(clusterIdx);
		
		clusterDomainService.update(clusterEntity);
		
		return clusterEntity.getClusterIdx();
	}

	public ClusterDto getCluster(Long clusterIdx) throws Exception {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		
		return ClusterDtoMapper.INSTANCE.toDto(clusterEntity);
	}

	public void deleteCluster(Long clusterIdx) throws Exception {
		ClusterEntity clusterEntity = ClusterEntity.builder()
				.clusterIdx(clusterIdx)
				.build();
		
		clusterDomainService.delete(clusterEntity);
	}

}
