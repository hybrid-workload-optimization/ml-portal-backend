package kr.co.strato.portal.networking.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.k8s.ingressController.model.CreateIngressControllerParam;
import kr.co.strato.adapter.k8s.ingressController.service.IngressControllerAdapterService;
import kr.co.strato.domain.IngressController.model.IngressControllerEntity;
import kr.co.strato.domain.IngressController.service.IngressControllerDomainService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.networking.model.IngressControllerDto;
import kr.co.strato.portal.networking.model.IngressControllerDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IngressControllerService {
	
	@Autowired
	private IngressControllerAdapterService ingressControllerAdapterService;
	
	@Autowired
	private IngressControllerDomainService ingressControllerDomainService;
	
	@Autowired
	private ClusterDomainService clusterDomainService;

	/**
	 * IngressController 타입 리턴.
	 * @param provider
	 * @return
	 */
	public List<String> types(Long clusterIdx) {
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		List<String> names = ingressControllerAdapterService.types(cluster.getProvider());
		List<IngressControllerEntity> list = ingressControllerDomainService.getList(cluster);
		List<String> installedNames = list.stream().map(ic -> ic.getName()).collect(Collectors.toList());
		names.removeAll(installedNames);
		return names;
	}
	
	/**
	 * 디폴트 컨트롤러 존재여부 반환.
	 * @param clusterIdx
	 * @return
	 */
	public boolean isExistDefaultController(Long clusterIdx) {
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		return ingressControllerDomainService.isExistDefaultController(cluster);
	}

	/**
	 * IngressController 설치
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public Long create(IngressControllerDto.ReqCreateDto param) throws IOException {
		log.info("Ingress Controller - Create.");
		log.info(param.toString());
		
		ClusterEntity cluster = clusterDomainService.get(param.getClusterIdx());
		Long clusterIdx = cluster.getClusterIdx();
		Long kubeConfigIdx = cluster.getClusterId();
		
		//k8s 리소스 생성.
		CreateIngressControllerParam createParam = IngressControllerDtoMapper.INSTANCE.toCreateParam(param, kubeConfigIdx);
		String str = ingressControllerAdapterService.create(createParam);
		
		if(str != null && str.length() > 0) {
			//DB저장
			IngressControllerEntity entity = IngressControllerDtoMapper.INSTANCE.toEntity(param, clusterIdx);
			entity.setCreatedAt(DateUtil.currentDateTime());
			return ingressControllerDomainService.registry(entity);
		}
		
		log.info("Ingress Controller - Create fail. k8s 리소스 생성 실패");
		return null;
	}
	
	/**
	 * IngressController 수정
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public Long update(IngressControllerDto.ReqCreateDto param) throws IOException {
		log.info("Ingress Controller - Update.");
		log.info(param.toString());
		
		ClusterEntity cluster = clusterDomainService.get(param.getClusterIdx());
		Long clusterIdx = cluster.getClusterIdx();
		Long kubeConfigIdx = cluster.getClusterId();
		
		//k8s 리소스 생성.
		CreateIngressControllerParam createParam = IngressControllerDtoMapper.INSTANCE.toCreateParam(param, kubeConfigIdx);
		String str = ingressControllerAdapterService.create(createParam);
		
		if(str != null && str.length() > 0) {
			//DB저장
			IngressControllerEntity entity = IngressControllerDtoMapper.INSTANCE.toEntity(param, clusterIdx);
			return ingressControllerDomainService.update(entity);
		}
		
		log.info("Ingress Controller - Update fail. k8s 리소스 수정 실패");
		return null;
	}
	
	/**
	 * IngressController 삭제
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public boolean remove(IngressControllerDto.SearchParam param) {	
		Long ingressControllerIdx = param.getIngressControllerIdx();
		log.info("Ingress Controller - Remove ID: {}", ingressControllerIdx);
		
		IngressControllerEntity entity = ingressControllerDomainService.getIngressControllerById(ingressControllerIdx);
		if(entity != null) {
			Long kubeConfigId = entity.getCluster().getClusterId();
			String name = entity.getName();
			
			CreateIngressControllerParam deleteParam = CreateIngressControllerParam.builder()
					.kubeConfigId(kubeConfigId)
					.ingressControllerType(name)
					.build();
			//k8s 리소스 삭제
			boolean isOk = ingressControllerAdapterService.remove(deleteParam);
			log.info("Ingress Controller - K8S resource remove result: {}", isOk);
			log.info("Ingress Controller - K8S resource remove ID: {}", ingressControllerIdx);
			
			//DB 정보 삭제
			ingressControllerDomainService.deleteById(ingressControllerIdx);
			return true;
		}
		
		throw new InternalServerException("Ingress Controller 삭제 실패, Ingress Controller를 찾을 수 없습니다. ID:" + ingressControllerIdx);
	}
	
	/**
	 * IngressController 목록 리턴.
	 * @param kubeConfigId
	 * @return
	 */
	public Page<IngressControllerDto.ResListDto> getList(Pageable pageable, Long clusterIdx) {		
		Page<IngressControllerEntity> list = ingressControllerDomainService.getList(pageable, clusterIdx);
		List<IngressControllerDto.ResListDto> dlist = list.getContent().stream().map(IngressControllerDtoMapper.INSTANCE::toResListDto).collect(Collectors.toList());
		Page<IngressControllerDto.ResListDto> page = new PageImpl<>(dlist, pageable, list.getTotalElements());
		return page;
	}
	
}
