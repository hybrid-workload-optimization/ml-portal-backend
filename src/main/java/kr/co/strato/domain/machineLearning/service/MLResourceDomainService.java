package kr.co.strato.domain.machineLearning.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.machineLearning.model.MLResourceEntity;
import kr.co.strato.domain.machineLearning.repository.MLResourceRepository;

@Service
public class MLResourceDomainService {

	@Autowired
	private MLResourceRepository mlResourceRepository;
	
	public Long save(MLResourceEntity mlResourceEntity) {
		mlResourceRepository.save(mlResourceEntity);
		return mlResourceEntity.getId();
	}
	
	public MLResourceEntity get(Long mlIdx, String mlResName) {
		Optional<MLResourceEntity> mlRes = mlResourceRepository.findByMlIdxAndMlResName(mlIdx, mlResName);
		if (mlRes.isPresent()) {
			return mlRes.get();
		}
		return null;
	}
	
	public MLResourceEntity get(Long resId) {
		Optional<MLResourceEntity> mlRes = mlResourceRepository.findById(resId);
		if (mlRes.isPresent()) {
			return mlRes.get();
		}
		return null;
	}
	
	public List<MLResourceEntity> getList(Long mlIdx) {
		return mlResourceRepository.findByMlIdx(mlIdx);
	}
	
	public void deleteByMlIdx(Long mlIdx) {
		mlResourceRepository.deleteByMlIdx(mlIdx);
	}
	
	
	public void deleteById(Long resId) {
		mlResourceRepository.deleteById(resId);
	}
	
}
