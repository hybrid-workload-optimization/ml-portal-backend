package kr.co.strato.portal.cluster.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cluster.model.Cluster;
import kr.co.strato.domain.cluster.service.ClusterService;
import kr.co.strato.portal.cluster.model.PortalClusterDto;
import kr.co.strato.portal.cluster.model.PortalClusterDtoMapper;

@Service
public class PortalClusterService {

	@Autowired
	ClusterService clusterService;
	
	public Page<PortalClusterDto> getClusterList(Pageable pageable) {
		Page<Cluster> clusterPage = clusterService.getList(pageable);
		List<PortalClusterDto> clusterList = clusterPage.getContent().stream().map(c -> PortalClusterDtoMapper.INSTANCE.toDto(c)).collect(Collectors.toList());
		
		return new PageImpl<>(clusterList, pageable, clusterPage.getTotalElements());
	}

}
