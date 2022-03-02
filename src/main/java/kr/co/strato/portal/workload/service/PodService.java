package kr.co.strato.portal.workload.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.pod.service.PodDomainService;
import kr.co.strato.portal.workload.model.PodDto;
import kr.co.strato.portal.workload.model.PodDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PodService {
    @Autowired
    private PodDomainService podDomainService;
    
    public Page<PodDto.ResListDto> getPods(Pageable pageable, PodDto.SearchParam searchParam) {
    	Page<PodEntity> pods = podDomainService.getPods(pageable, searchParam.getProjectId(), searchParam.getClsuterId(), searchParam.getNamespaceId());
        List<PodDto.ResListDto> dtos = pods.stream().map(e -> PodDtoMapper.INSTANCE.toResListDto(e)).collect(Collectors.toList());
        Page<PodDto.ResListDto> pages = new PageImpl<>(dtos, pageable, pods.getTotalElements());
        
    	return pages;
    }
}
