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

import kr.co.strato.domain.machineLearning.model.MLEntity;
import kr.co.strato.domain.machineLearning.model.MLResourceEntity;
import kr.co.strato.domain.machineLearning.service.MLDomainService;
import kr.co.strato.domain.machineLearning.service.MLResourceDomainService;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
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
		Long clusterIdx = getCluster(stepCode, yamStr);		
		String now = DateUtil.currentDateTime("yyyy-MM-dd HH:mm:ss");
		
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
				
				String output = yaml.dump(map);
				
				Long resourceId = null;
				MLResourceEntity resEntity = null;
				if(!isNew) {
					resEntity = mlResourceDomainService.get(mlIdx, name);
					if(resEntity != null) {
						resourceId = resEntity.getResourceId();
					}
				} else {
					resEntity = new MLResourceEntity();
					resEntity.setCreatedAt(now);
				}
				
				MLServiceInterface serviceInterface = getServiceInterface(kind);
				if(serviceInterface != null) {
					resourceId = serviceInterface.mlResourceApply(clusterIdx, resourceId, output);
				}
				
				resEntity.setUpdatedAt(now);
				resEntity.setClusterIdx(clusterIdx);
				resEntity.setKind(kind);
				resEntity.setMlResName(name);
				resEntity.setMlIdx(mlIdx);
				resEntity.setYaml(output);
				resEntity.setResourceId(resourceId);
				
				mlResourceDomainService.save(resEntity);
				
				
			} else {
				log.error("yaml 파일이 잘못되었습니다.");
			}
		}
		
		//callback 발송
		//임시코드: async로 바꾼후 로직 변경 필요.
		String callbackUrl = applyDto.getCallbackUrl();
		if(callbackUrl != null) {
			MLDto.Detail detail = getMl(mlId);
			callbackService.sendCallback(callbackUrl, new ResponseWrapper<>(detail));
		}
		
		return mlId;
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
		entity.setUpdatedAt(DateUtil.currentDateTime("yyyy-MM-dd HH:mm:ss"));
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
	public Long getCluster(String stepCode, String yaml) {
		if(stepCode.equals(MLStepCode.SERVICE.getCode())) {
			//서비스인 경우 서비스 클러스터로 실행.
			
		} else {
			//나머지는 클러스터 생성 후 실행.
			
		}
		
		return 118L;
	}
	
	public String getPrometheusUrl(Long clusterId) {
		String url = "http://210.217.178.117:30005/";
		return url;
	}
	
	
	private MLServiceInterface getServiceInterface(String kind) {
		return serviceFactory.getMLServiceInterface(kind);
	}
	
	
}
 