package kr.co.strato.portal.ml.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.machineLearning.model.MLEntity;
import kr.co.strato.domain.machineLearning.model.MLResourceEntity;
import kr.co.strato.domain.machineLearning.service.MLDomainService;
import kr.co.strato.domain.machineLearning.service.MLResourceDomainService;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.cluster.service.ClusterService;
import kr.co.strato.portal.ml.model.MLDto;
import kr.co.strato.portal.ml.model.MLDto.ListArg;
import kr.co.strato.portal.ml.model.MLDtoMapper;
import kr.co.strato.portal.ml.model.MLResourceDto;

@Service
public class MLPortalService {
	
	@Autowired
	private MLDomainService mlDomainService;
	
	@Autowired
	private MLResourceDomainService mlResourceDomainService;
	
	@Autowired
	private ClusterService clusterService;
	
	/**
	 * ML 리스트 반환.
	 * @param param
	 * @return
	 */
	public Object getMlList(ListArg param) {
		String userId = param.getUserId();
		String name = param.getName();
		
		List<MLEntity> list = mlDomainService.getList(userId, name);
		
		List<MLDto.ListDtoForPortal> mlList = list.stream().map(c -> {
			return MLDtoMapper.INSTANCE.toListDtoForPortal(c);
		}).collect(Collectors.toList());
		
		for(MLDto.ListDtoForPortal l : mlList) {
			List<MLResourceEntity> resEntitys = mlResourceDomainService.getList(l.getId());
			l.setResourceCount(resEntitys.size());
		}
		
		PageRequest pageRequest = param.getPageRequest();
		if(pageRequest != null) {
			Page<MLDto.ListDtoForPortal> pages = new PageImpl<>(mlList, pageRequest.of(), mlList.size());
			return pages;
		} else {
			return mlList;
		}
	}
	
	/**
	 * ml 상세
	 * @param mlId
	 * @return
	 */
	public MLDto.DetailForPortal getMl(String mlId) {
		MLEntity entity = mlDomainService.get(mlId);
		
		List<MLResourceEntity> resEntitys = mlResourceDomainService.getList(entity.getId());		
		List<MLResourceDto> resources = new ArrayList<>();
		for(MLResourceEntity resEntity : resEntitys) {			
			MLResourceDto resDto = MLDtoMapper.INSTANCE.toResDto(resEntity);
			resources.add(resDto);
		}
		
		ClusterDto.Detail clusterDetail = null;
		try {
			clusterDetail = clusterService.getCluster(entity.getClusterIdx());
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		MLDto.DetailForPortal detail = MLDtoMapper.INSTANCE.toDetailDtoForPortal(entity);
		detail.setCluster(clusterDetail);
		detail.setResources(resources);
		return detail;
	}

}
