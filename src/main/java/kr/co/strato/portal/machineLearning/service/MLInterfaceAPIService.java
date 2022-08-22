package kr.co.strato.portal.machineLearning.service;

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
import kr.co.strato.portal.machineLearning.model.MLClusterType;
import kr.co.strato.portal.machineLearning.model.MLDto;
import kr.co.strato.portal.machineLearning.model.MLDto.ListArg;
import kr.co.strato.portal.machineLearning.model.MLDtoMapper;
import kr.co.strato.portal.machineLearning.model.MLResourceDto;
import kr.co.strato.portal.machineLearning.model.MLStepCode;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MLInterfaceAPIService {
	
	@Autowired
	private ServiceFactory serviceFactory;
	
	@Autowired
	private MLDomainService mlDomainService;
	
	@Autowired
	private MLResourceDomainService mlResourceDomainService;
	
	@Autowired
	private MLClusterDomainService mlClusterDomainService;
	
	@Autowired
	private MLClusterMappingDomainService mlClusterMappingDomainService;
	
	@Autowired
	private CallbackService callbackService;
	
	public String apply(MLDto.ApplyArg applyDto) {
		boolean isNew = false;
		if(applyDto.getMlId() == null) {
			//새로운 mlId 생성
			String newMlId = genMlId();
			applyDto.setMlId(newMlId);
			
			isNew = true;
		}
		
		String mlId = applyDto.getMlId();
		String yamStr = Base64Util.decode(applyDto.getYaml());
		String stepCode = applyDto.getMlStepCode();
		
		MLClusterEntity mlClusterEntity = getCluster(stepCode, yamStr);
		Long clusterIdx = mlClusterEntity.getCluster().getClusterIdx();
		String now = DateUtil.currentDateTime();
		
		MLEntity entity = MLDtoMapper.INSTANCE.toEntity(applyDto);
		entity.setClusterIdx(clusterIdx);
		entity.setUpdatedAt(now);
		if(isNew) {
			entity.setCreatedAt(now);
			entity.setStatus("Started");
		}
		
		entity.setYaml(yamStr);
		
		Long mlIdx = mlDomainService.save(entity);
		
		
		Yaml yaml = new Yaml();
		for(Object object : yaml.loadAll(yamStr)) {
			if(object instanceof Map) {
				Map map = (Map) object;
				
				//Resource apply
				applyResource(map, mlIdx, clusterIdx, now, isNew);
			} else {
				log.error("yaml 파일이 잘못되었습니다.");
			}
		}
		
		
		//MLCluster, ML mapping 정보 저장
		now = DateUtil.currentDateTime();
		MLClusterMappingEntity mappingEntity = new MLClusterMappingEntity();
		mappingEntity.setMlCluster(mlClusterEntity);
		mappingEntity.setMl(entity);
		mappingEntity.setCreatedAt(now);
		mappingEntity.setUpdatedAt(now);
		mlClusterMappingDomainService.save(mappingEntity);
		
		
		//callback 발송
		//임시코드: async로 바꾼후 로직 변경 필요.
		String callbackUrl = applyDto.getCallbackUrl();
		if(callbackUrl != null) {
			MLDto.Detail detail = getMl(mlId);
			callbackService.sendCallback(callbackUrl, new ResponseWrapper<>(detail));
		}
		
		return mlId;
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
	@Transactional
	public boolean delete(String mlId) {
		MLDto.Detail mlDetail = getMl(mlId);
		if(mlDetail != null) {
			
			Long mlIdx = mlDetail.getId();
			List<MLResourceDto> resources = mlDetail.getResources();
			String stepCode = mlDetail.getMlStepCode();
			for(MLResourceDto resDto : resources) {
				String kind = resDto.getKind();
				Long resId = resDto.getResourceId();
				String yaml = resDto.getYaml();
				
				MLServiceInterface serviceInterface = getServiceInterface(kind);
				if(serviceInterface != null) {
					//실제 리소스 삭제.
					boolean isDelete = serviceInterface.delete(resId, yaml);
				}
			}
			
			//클러스터 맵핑 삭제
			List<MLClusterMappingEntity> list = mlClusterMappingDomainService.getByMlIdx(mlIdx);
			mlClusterMappingDomainService.deleteByMlIdx(mlIdx);
			
			//클러스터 삭제
			if(!stepCode.equals(MLStepCode.SERVICE.getCode())) {
				for(MLClusterMappingEntity mappingEntity : list) {
					mlClusterDomainService.deleteByMlClusterIdx(mappingEntity.getMlCluster().getId());
				}
			}
			
			//리소스 삭제
			mlResourceDomainService.deleteByMlIdx(mlIdx);
			
			//ml 삭제
			mlDomainService.delete(mlIdx);
			
			return true;
		}
		return false;
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
	public MLClusterEntity getCluster(String stepCode, String yaml) {
		MLClusterEntity mlClusterEntity = null;
		if(stepCode.equals(MLStepCode.SERVICE.getCode())) {
			//서비스인 경우 서비스 클러스터로 실행.
			//서비스 클러스터 목록 중 수용할 수 있는 클러스터 시뮬레이션 후 적절한 클러스터 선택.
			
		} else {
			//나머지는 클러스터 생성 후 실행.
			
			//AI를 이용한 클러스터 구성 추천 받은 후 클러스터 생성.
			Long clusterId = 118L;
			ClusterEntity clusterEntity = new ClusterEntity();
			clusterEntity.setClusterIdx(clusterId);
			
			
			String now = DateUtil.currentDateTime();	
			
			mlClusterEntity = new MLClusterEntity();
			mlClusterEntity.setCluster(clusterEntity);
			mlClusterEntity.setClusterType(MLClusterType.JOB_CLUSTER.getType());
			mlClusterEntity.setCreatedAt(now);
			mlClusterEntity.setUpdatedAt(now);
			mlClusterEntity.setStatus("RUN");
			
			mlClusterDomainService.save(mlClusterEntity);
		}
		
		
		return mlClusterEntity;
	}	
	
	private MLServiceInterface getServiceInterface(String kind) {
		return serviceFactory.getMLServiceInterface(kind);
	}
	
	
}
 