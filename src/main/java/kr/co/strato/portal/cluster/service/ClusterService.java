package kr.co.strato.portal.cluster.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.cluster.model.ClusterDtoMapper;

@Service
public class ClusterService {

	@Autowired
	ClusterDomainService clusterService;
	
	public Page<ClusterDto> getClusterList(Pageable pageable) {
		Page<ClusterEntity> clusterPage = clusterService.getList(pageable);
		List<ClusterDto> clusterList = clusterPage.getContent().stream().map(c -> ClusterDtoMapper.INSTANCE.toDto(c)).collect(Collectors.toList());
		
		return new PageImpl<>(clusterList, pageable, clusterPage.getTotalElements());
	}

}
