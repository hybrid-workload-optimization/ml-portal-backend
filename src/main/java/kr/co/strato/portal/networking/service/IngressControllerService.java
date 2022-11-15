package kr.co.strato.portal.networking.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.fabric8.kubernetes.api.model.LoadBalancerIngress;
import kr.co.strato.adapter.k8s.ingressController.model.CreateIngressControllerParam;
import kr.co.strato.adapter.k8s.ingressController.model.ServicePort;
import kr.co.strato.adapter.k8s.ingressController.service.IngressControllerAdapterService;
import kr.co.strato.adapter.k8s.service.service.ServiceAdapterService;
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
	private IngressService ingressService;
	
	@Autowired
	private ClusterDomainService clusterDomainService;
	
	@Autowired
	private ServiceAdapterService serviceAdapterService;

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
			
			Long id = ingressControllerDomainService.registry(entity);
			
			//IngressRule 업데이트
			updateIngressRule(entity);
			
			return id;
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
		
		IngressControllerEntity oldEntity = ingressControllerDomainService.getIngressControllerById(param.getId());
		IngressControllerDto.ResListDto listDto = IngressControllerDtoMapper.INSTANCE.toResListDto(oldEntity);
		
		//변경된 정보만 반영
		IngressControllerDto.ReqCreateDto newParam = new IngressControllerDto.ReqCreateDto();
		newParam.setName(param.getName());
		newParam.setClusterIdx(cluster.getClusterId());
		
		//변경 사항 채크
		if(param.getIsDefault() != listDto.isDefault()) {
			newParam.setIsDefault(param.getIsDefault());
		}		
		
		if(param.getReplicas() != oldEntity.getReplicas()) {
			//replicas 변경사항이 있는 경우.
			newParam.setReplicas(param.getReplicas());
		}
		
		newParam.setServiceType(param.getServiceType());
		if(!param.getServiceType().equals(oldEntity.getServiceType())) {
			//ServiceType 변경점이 있는 경우			
			newParam.setHttpPort(param.getHttpPort());
			newParam.setHttpsPort(param.getHttpsPort());
			newParam.setExternalIp(param.getExternalIp());
		} else {
			//ServiceType 변경점이 없는 경우.
			if(param.getServiceType().equals(IngressControllerEntity.SERVICE_TYPE_NODE_PORT)) {
				List<ServicePort> ports = listDto.getPort();
				for(ServicePort p : ports) {
					if(p.getProtocol().equals("http") && !param.getHttpPort().equals(p.getPort())) {
						newParam.setHttpPort(param.getHttpPort());
					} else if(p.getProtocol().equals("https") && !param.getHttpsPort().equals(p.getPort())) {
						newParam.setHttpsPort(param.getHttpsPort());
					}
				}
			} else if(param.getServiceType().equals(IngressControllerEntity.SERVICE_TYPE_EXTERNAL_IPS)) {			
				if(param.getExternalIp().size() != listDto.getExternalIp().size() || 						
						!param.getExternalIp().containsAll(listDto.getExternalIp())) {
					newParam.setExternalIp(param.getExternalIp());
				}
				
			}			
		}
		Long clusterIdx = cluster.getClusterIdx();
		Long kubeConfigIdx = cluster.getClusterId();
		
		//k8s 리소스 생성.
		CreateIngressControllerParam createParam = IngressControllerDtoMapper.INSTANCE.toCreateParam(newParam, kubeConfigIdx);
		String str = ingressControllerAdapterService.update(createParam);
		
		if(str != null && str.length() > 0) {
			//DB저장
			IngressControllerEntity entity = IngressControllerDtoMapper.INSTANCE.toEntity(param, clusterIdx);
			entity.setCreatedAt(oldEntity.getCreatedAt());
			
			Long id = ingressControllerDomainService.update(entity);
			
			//IngressRule 업데이트
			updateIngressRule(entity);			
			
			return id;
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
			
			//IngressRule 업데이트
			updateIngressRule(entity);
			
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
		List<IngressControllerDto.ResListDto> dlist = list.getContent()
				.stream()
				.map(IngressControllerDtoMapper.INSTANCE::toResListDto)
				.collect(Collectors.toList());
		Page<IngressControllerDto.ResListDto> page = new PageImpl<>(dlist, pageable, list.getTotalElements());
		return page;
	}
	
	public IngressControllerEntity getIngressController(ClusterEntity cluster, String name) {	
		IngressControllerEntity ingressController = ingressControllerDomainService.getIngressController(cluster, name);
		return ingressController;
	}
	
	/**
	 * ingressController와 연결된 Ingress 리스트 반환.
	 * @param ingressController
	 * @return
	 */
	public void updateIngressRule(IngressControllerEntity ingressController) {
		ingressService.updateIngressRule(ingressController);
	}
	
	public Long create(Long clusterIdx) throws IOException {
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		Long id = create(cluster);
		
		//생성 대기.
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return id;
	}
	
	/**
	 * Cloud Provider 별 기 IngressController 설치
	 * @param clusterEntity
	 * @return
	 * @throws IOException
	 */
	public Long create(ClusterEntity clusterEntity) throws IOException {
		Long id = null;
		IngressControllerDto.ReqCreateDto param = null;
		String cloudProvider = clusterEntity.getProvider().toLowerCase();
		
		if(cloudProvider.equals("azure")) {			
			log.info("Azure IngressController Create.");
			param = IngressControllerDto.ReqCreateDto.builder()
					.clusterIdx(clusterEntity.getClusterIdx())
					.name("nginx")
					.replicas(2)
					.isDefault(true)
					.serviceType("LoadBalancer")
					.build();
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(param);
			
			log.info("IngressController Create Param:");
			log.info(json);
			
			
			Long clusterIdx = clusterEntity.getClusterIdx();
			Long kubeConfigIdx = clusterEntity.getClusterId();
			
			//k8s 리소스 생성.
			CreateIngressControllerParam createParam = IngressControllerDtoMapper.INSTANCE.toCreateParam(param, kubeConfigIdx);
			String str = ingressControllerAdapterService.create(createParam);
			
			
			if(str != null && str.length() > 0) {
				
				String externalIp = null;
				
				try {					
					io.fabric8.kubernetes.api.model.Service s 
							= serviceAdapterService.get(kubeConfigIdx, "ingress-nginx", "ingress-nginx-controller");
			
					List<LoadBalancerIngress> list = s.getStatus().getLoadBalancer().getIngress();
					if(list != null && list.size() > 0) {
						LoadBalancerIngress loadBalancerIngres = list.get(0);
						externalIp = loadBalancerIngres.getIp();
					}
					
				} catch (Exception e) {
					log.error("Ingress Controller 접속 주소 가져오기 실패!");
					log.error("", e);
				}
				
				
				//DB저장
				IngressControllerEntity entity = IngressControllerDtoMapper.INSTANCE.toEntity(param, clusterIdx);
				entity.setCreatedAt(DateUtil.currentDateTime());
				entity.setExternalIp(externalIp);
				
				id = ingressControllerDomainService.registry(entity);
				
				//IngressRule 업데이트
				updateIngressRule(entity);
			}
			return id;
		}
		
		log.error("IngressController Create Fail.");
		log.error("Unknown cloudProvider: {}", cloudProvider);
		
		return null;
		
	}
}
