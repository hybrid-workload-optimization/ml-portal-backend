package kr.co.strato.portal.workload.service;

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

import io.fabric8.kubernetes.api.model.Pod;
import kr.co.strato.adapter.k8s.pod.service.PodAdapterService;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.pod.service.PodDomainService;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.workload.model.PodDto;
import kr.co.strato.portal.workload.model.PodDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PodService {
    @Autowired
    private PodDomainService podDomainService;
    
    @Autowired
    private PodAdapterService podAdapterService;
    
    @Transactional(rollbackFor = Exception.class)
    public List<Long> createPod(PodDto.ReqCreateDto reqCreateDto){
        Long clusterId = reqCreateDto.getClusterId();
        String yaml = Base64Util.decode(reqCreateDto.getYaml());

        List<Pod> pods = podAdapterService.create(clusterId, yaml);

        List<Long> ids = pods.stream().map( s -> {
        	try {
                String namespaceName = s.getMetadata().getNamespace();
                // ownerReferences 추가
                PodEntity pod = toEntity(s);

                Long id = podDomainService.register(pod, clusterId, namespaceName, null);

                return id;
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("json 파싱 에러");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("pod register error");
            }
        }).collect(Collectors.toList());

        return ids;
    }
    
    public Page<PodDto.ResListDto> getPods(Pageable pageable, PodDto.SearchParam searchParam) {
    	Page<PodEntity> pods = podDomainService.getPods(pageable, searchParam.getProjectId(), searchParam.getClusterId(), searchParam.getNamespaceId(), searchParam.getNodeId());
        List<PodDto.ResListDto> dtos = pods.stream().map(e -> PodDtoMapper.INSTANCE.toResListDto(e)).collect(Collectors.toList());
        Page<PodDto.ResListDto> pages = new PageImpl<>(dtos, pageable, pods.getTotalElements());
        
    	return pages;
    }
    
    /**
     * k8s pod model -> pod entity
     * @param pod
     * @return
     * @throws JsonProcessingException
     */
    @Transactional(rollbackFor = Exception.class)
    private PodEntity toEntity(Pod pod) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        String name = pod.getMetadata().getName();
        String uid = pod.getMetadata().getUid();
        String label = mapper.writeValueAsString(pod.getMetadata().getLabels());
        String annotations = mapper.writeValueAsString(pod.getMetadata().getAnnotations());
        String createAt = pod.getMetadata().getCreationTimestamp();
        
        // Owner UID
        String ownerUid = pod.getMetadata().getOwnerReferences().get(0).getUid();

        PodEntity podEntity = PodEntity.builder()
                .podName(name)
                .podUid(uid)
                .label(label)
                .ownerUid(ownerUid)
                .annotation(annotations)
                .createdAt(DateUtil.strToLocalDateTime(createAt))
                .build();

        return podEntity;
    }
    
    public PodDto.ResDetailDto getPodDetail(Long podId){
    	// get pod entity
    	PodEntity entity = podDomainService.get(podId);
    	
    	// get k8s pod model
//    	String podName = entity.getPodName();
//    	String namespaceName = entity.getNamespace().getName();
//    	Long clusterId = entity.getNamespace().getClusterIdx().getClusterId();
//    	Pod k8sPod = podAdapterService.get(clusterId, namespaceName, podName);
//    	
//    	PodDto.ResDetailDto dto = PodDtoMapper.INSTANCE.toResDetailDto(entity, k8sPod);
    	PodDto.ResDetailDto dto = PodDtoMapper.INSTANCE.toResDetailDto(entity);
    	return dto;
    }
    
    public List<Pod> getOwnerToPodList(Long mappingId, PodDto.OwnerSearchParam searchParam) {
    	/**
    	 * TODO
    	 * 0. 파라미터 세팅(owner 정보, ownerUid)
    	 * 1. k8s 인터페이스 pod list 조회
    	 * 2. pod list에서 kind 정보를 보고 어느 controller mapping 테이블에 저장할 지 판단 
    	 */
    	Long clusterId = searchParam.getClusterId();
    	String ownerUid = searchParam.getOwnerUid();
    	// get k8s -> ownerUid to Pod list
    	List<Pod> pods = podAdapterService.getList(clusterId, ownerUid);
    	
    	// pod list에서 owner info의 kind 정보 추출
    	
//    	for(Pod pod : pods) {
//    		try {
//    			String uid = pod.getMetadata().getUid();
//    			String namespaceName = pod.getMetadata().getNamespace();
//        		String kind = pod.getMetadata().getOwnerReferences().get(0).getKind();
//        		
//        		PodEntity podEntity = toEntity(pod);
//        		
//        		// save
//        		Long id = podDomainService.register(podEntity, clusterId, namespaceName);
//			} catch (JsonProcessingException e) {
//				// TODO: handle exception
//				throw new InternalServerException("json 파싱 에러");
//            } catch (Exception e) {
//                log.error(e.getMessage(), e);
//                throw new InternalServerException("pod save error");
//            }
//    	}
    	
    	return pods;
    }
    
//    private List<Long> synPodSave()
}
