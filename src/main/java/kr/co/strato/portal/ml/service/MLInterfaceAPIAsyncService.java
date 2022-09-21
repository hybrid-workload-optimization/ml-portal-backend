package kr.co.strato.portal.ml.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.Yaml;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.machineLearning.model.MLClusterEntity;
import kr.co.strato.domain.machineLearning.model.MLClusterMappingEntity;
import kr.co.strato.domain.machineLearning.model.MLEntity;
import kr.co.strato.domain.machineLearning.model.MLResourceEntity;
import kr.co.strato.domain.machineLearning.service.MLClusterDomainService;
import kr.co.strato.domain.machineLearning.service.MLClusterMappingDomainService;
import kr.co.strato.domain.machineLearning.service.MLDomainService;
import kr.co.strato.domain.machineLearning.service.MLResourceDomainService;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.ml.model.MLDto;
import kr.co.strato.portal.ml.model.MLDto.ListArg;
import kr.co.strato.portal.ml.model.MLDtoMapper;
import kr.co.strato.portal.ml.model.MLResourceDto;
import kr.co.strato.portal.ml.model.MLStepCode;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MLInterfaceAPIAsyncService {
	
	@Autowired
	private ServiceFactory serviceFactory;
	
	@Autowired
	private MLDomainService mlDomainService;
	
	@Autowired
	private MLResourceDomainService mlResourceDomainService;
	
	@Autowired
	private ClusterDomainService clusterDomainService;
	
	@Autowired
	private MLClusterDomainService mlClusterDomainService;
	
	@Autowired
	private MLClusterMappingDomainService mlClusterMappingDomainService;
	
	@Autowired
	private CallbackService callbackService;
	
	@Autowired
	private MLClusterAPIAsyncService mlClusterAPIService;
	
	
	public String apply(MLDto.ApplyArg applyDto) {
		log.info("ML apply start.");
		boolean isNew = false;
		Long mlClusterIdx = null;
		if(applyDto.getMlId() == null) {
			//새로운 mlId 생성
			String newMlId = genMlId();
			applyDto.setMlId(newMlId);
			
			isNew = true;
		} else {
			MLEntity entity = mlDomainService.get(applyDto.getMlId());
			mlClusterIdx = entity.getMlClusterIdx();
		}
		
		String mlName = applyDto.getName();
		String mlId = applyDto.getMlId();
		String yamStr = Base64Util.decode(applyDto.getYaml());
		String stepCode = applyDto.getMlStepCode();
		
		log.info("ML ID: {}", mlId);
		log.info("ML Name: {}", mlName);
		log.info("StepCode: {}", stepCode);
		log.info("Yaml: {}", yamStr);
		
		//클러스터 선택
		MLClusterEntity mlClusterEntity = null;
		
		if(mlClusterIdx == null) {
			mlClusterEntity = getCluster(mlName, stepCode, yamStr);
		} else {
			mlClusterEntity = mlClusterDomainService.get(mlClusterIdx);
		}
		
		
		if(mlClusterEntity != null) {
			ClusterEntity clusterEntity = mlClusterEntity.getCluster();			
			String provisioningStatus = clusterEntity.getProvisioningStatus();
			
			mlClusterIdx = mlClusterEntity.getId();
			Long clusterIdx = clusterEntity.getClusterIdx();
			String now = DateUtil.currentDateTime();
			
			//ML 저장
			MLEntity entity = MLDtoMapper.INSTANCE.toEntity(applyDto);
			entity.setMlClusterIdx(mlClusterIdx);
			entity.setClusterIdx(clusterIdx);
			entity.setUpdatedAt(now);
			if(isNew) {
				entity.setCreatedAt(now);
				entity.setStatus(MLEntity.STATUS_PENDING);
			}
			
			entity.setYaml(yamStr);
			Long mlIdx = mlDomainService.save(entity);
			
			
			//MLCluster, ML mapping 정보 저장
			now = DateUtil.currentDateTime();
			MLClusterMappingEntity mappingEntity = new MLClusterMappingEntity();
			mappingEntity.setMlCluster(mlClusterEntity);
			mappingEntity.setMl(entity);
			mappingEntity.setCreatedAt(now);
			mappingEntity.setUpdatedAt(now);
			mlClusterMappingDomainService.save(mappingEntity);
			
			
			if(provisioningStatus.equals(ClusterEntity.ProvisioningStatus.STARTED.name())) {
				//Async 동작 - Kafka로 메세지 푸시
				log.info("클러스터 배포 대기");
				log.info("Cluster name: {}", clusterEntity.getClusterName());
				log.info("ML apply 종료");
					
			} else if(provisioningStatus.equals(ClusterEntity.ProvisioningStatus.FINISHED.name())) {				
				applyResource(entity, isNew);			
			}
			
			return mlId;
		}
		return null;
	}
	
	/**
	 * 클러스터 생성 완료 후 ML
	 * @param mlClusterEntity
	 */
	public void applyContinue(MLClusterEntity mlClusterEntity) {
		List<MLClusterMappingEntity> list = mlClusterMappingDomainService.getByMlClusterIdx(mlClusterEntity.getId());
		for(MLClusterMappingEntity mappingEntity : list) {
			MLEntity mlEntity = mappingEntity.getMl();
			applyResource(mlEntity);
		}
	}
	
	
	public void applyResource(MLEntity entity) {
		applyResource(entity, true);
	}
	
	public void applyResource(MLEntity entity, boolean isNew) {
		Long mlIdx = entity.getId();
		String mlId = entity.getMlId();
		Long clusterIdx = entity.getClusterIdx();
		String yamlStr = entity.getYaml();
		String now = DateUtil.currentDateTime();
		
		log.info("ML Resource 실행.");
		log.info("ML Name: {}", entity.getName());
		log.info("ML ID: {}", entity.getMlId());
		log.info("Cluster Name: {}", clusterIdx);
		
		Yaml yaml = new Yaml();
		for(Object object : yaml.loadAll(yamlStr)) {
			if(object instanceof Map) {
				Map map = (Map) object;
				
				//Resource apply
				applyResource(map, mlIdx, clusterIdx, now, isNew);
			} else {
				log.error("yaml 파일이 잘못되었습니다.");
			}
		}
		
		//시작으로 상태 업데이트
		entity.setStatus(MLEntity.STATUS_STARTED);
		mlDomainService.save(entity);
		
		String callbackUrl = entity.getCallbackUrl();
		if(callbackUrl != null) {
			MLDto.Detail detail = getMl(mlId);
			callbackService.sendCallback(callbackUrl, new ResponseWrapper<>(detail));
		}
	}
	
	private void applyResource(Map map, Long mlIdx, Long clusterIdx, String createAt, boolean isNew) {
		String kind = null;
		if(map.get("kind") != null) {
			kind = (String)map.get("kind");
		}
		
		String name = null;
		if(map.get("metadata") != null) {
			if(((Map)map.get("metadata")).get("name") != null) {
				name = (String)((Map)map.get("metadata")).get("name");
			}
		}
		
		String output = new Yaml().dump(map);
		
		Long resourceId = null;
		MLResourceEntity resEntity = null;
		if(!isNew) {
			resEntity = mlResourceDomainService.get(mlIdx, name);
			if(resEntity != null) {
				resourceId = resEntity.getResourceId();
			}
		} else {
			resEntity = new MLResourceEntity();
			resEntity.setCreatedAt(createAt);
		}
		
		MLServiceInterface serviceInterface = getServiceInterface(kind);
		if(serviceInterface != null) {
			resourceId = serviceInterface.mlResourceApply(clusterIdx, resourceId, output);
		}
		
		log.info("Apply Resource");
		log.info("ID: {}, Kind: {}", resourceId, kind);
		log.info("Yaml: {}", output);
		
		resEntity.setUpdatedAt(createAt);
		resEntity.setClusterIdx(clusterIdx);
		resEntity.setKind(kind);
		resEntity.setMlResName(name);
		resEntity.setMlIdx(mlIdx);
		resEntity.setYaml(output);
		resEntity.setResourceId(resourceId);
		
		mlResourceDomainService.save(resEntity);
	}
	
	
	
	/**
	 * ml 삭제
	 * @param mlId
	 * @return
	 */
	public void delete(String mlId) {
		log.info("ML delete start.");
		log.info("ML ID: {}.", mlId);
		MLDto.Detail mlDetail = getMl(mlId);
		if(mlDetail != null) {
			List<MLClusterMappingEntity> list = mlClusterMappingDomainService.getByMlIdx(mlDetail.getId());
			for(MLClusterMappingEntity entity : list) {
				Long mlClusterIdx = entity.getMlCluster().getId();
				mlClusterAPIService.deleteMlCluster(mlClusterIdx);
			}
		}
	}
	
	@Transactional
	public boolean deletePre(Long clusterIdx) {		
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		MLClusterEntity mlClusterEntity = mlClusterDomainService.get(clusterEntity);
		
		List<MLClusterMappingEntity> list = mlClusterMappingDomainService.getByMlClusterIdx(mlClusterEntity.getId());
		for(MLClusterMappingEntity mappingEntity : list) {
			MLEntity mlEntity = mappingEntity.getMl();
			Long mlIdx = mlEntity.getId();
			
			//리소스 삭제
			log.info("ML 리소스 삭제. ML ID: {}", mlIdx);
			mlResourceDomainService.deleteByMlIdx(mlIdx);
			
			//클러스터 맵핑 정보 삭제.
			mlClusterMappingDomainService.deleteByMlClusterIdx(mlClusterEntity.getId());
			
			//ml 삭제
			log.info("ML 삭제. ML ID: {}", mlIdx);
			mlDomainService.delete(mlIdx);
		}
		return true;
	}
	
	/**
	 * ml 상세
	 * @param mlId
	 * @return
	 */
	public MLDto.Detail getMl(String mlId) {
		MLEntity entity = mlDomainService.get(mlId);
		
		List<MLResourceEntity> resEntitys = mlResourceDomainService.getList(entity.getId());		
		List<MLResourceDto> resources = new ArrayList<>();
		for(MLResourceEntity resEntity : resEntitys) {			
			MLResourceDto resDto = MLDtoMapper.INSTANCE.toResDto(resEntity);
			resources.add(resDto);
		}	
		
		MLDto.Detail detail = MLDtoMapper.INSTANCE.toDetailDto(entity);
		detail.setResources(resources);
		return detail;
	}
	
	/**
	 * ml 완료
	 * @param mlId
	 */
	public void finish(String mlId) {
		MLEntity entity = mlDomainService.get(mlId);
		entity.setUpdatedAt(DateUtil.currentDateTime());
		entity.setStatus("finished");
		mlDomainService.save(entity);
		
		//클러스터 삭제 처리
		mlClusterAPIService.deleteMlCluster(entity.getMlClusterIdx());
	}
	
	/**
	 * ML 리스트 반환.
	 * @param param
	 * @return
	 */
	public Object getMlList(ListArg param) {
		String userId = param.getUserId();
		String name = param.getName();
		
		List<MLEntity> list = mlDomainService.getList(userId, name);
		
		List<MLDto.ListDto> mlList = list.stream().map(c -> {
			return MLDtoMapper.INSTANCE.toListDto(c);
		}).collect(Collectors.toList());
		
		PageRequest pageRequest = param.getPageRequest();
		if(pageRequest != null) {
			Page<MLDto.ListDto> pages = new PageImpl<>(mlList, pageRequest.of(), mlList.size());
			return pages;
		} else {
			return mlList;
		}
	}
	
	
	/**
	 * 새로운 ID 생성.
	 * @return
	 */
	public String genMlId() {
		String uuid = UUID.randomUUID().toString();
		return uuid;
	}
	
	/**
	 * 배포하려는 적잘한 클러스터를 선택 또는 생성하여 ID 반환.
	 * @param yaml
	 * @return
	 */
	public MLClusterEntity getCluster(String mlName, String stepCode, String yaml) {
		MLClusterEntity mlClusterEntity = null;
		if(stepCode.equals(MLStepCode.SERVICE.getCode())) {
			//서비스인 경우 서비스 클러스터로 실행.
			//서비스 클러스터 목록 중 수용할 수 있는 클러스터 시뮬레이션 후 적절한 클러스터 선택.
			
		} else {
			//나머지는 클러스터 생성 후 실행.			
			mlClusterEntity = mlClusterAPIService.provisioningJobCluster(mlName, yaml);
		}
		
		return mlClusterEntity;
	}
	
	
	private MLServiceInterface getServiceInterface(String kind) {
		return serviceFactory.getMLServiceInterface(kind);
	}
	
	
}
 