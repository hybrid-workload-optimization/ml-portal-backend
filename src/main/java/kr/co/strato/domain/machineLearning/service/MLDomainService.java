package kr.co.strato.domain.machineLearning.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.machineLearning.model.MLEntity;
import kr.co.strato.domain.machineLearning.repository.MLRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class MLDomainService {

	@Autowired
	private MLRepository mlRepository;
	
	public Long save(MLEntity mlEntity) {
		Long id = mlEntity.getId();
		if(id == null) {
			Optional<MLEntity> entityOptional = mlRepository.findByMlId(mlEntity.getMlId());
			if(entityOptional.isPresent()) {
				id = entityOptional.get().getId();
			}		
			mlEntity.setId(id);
		}
		mlRepository.save(mlEntity);
		return mlEntity.getId();
	}
	
	public List<MLEntity> getList() {
		return mlRepository.getMLList();
    }
	
	public List<MLEntity> getList(String userId, String name) {
		return mlRepository.getMLList(userId, name);
    }
	
	public MLEntity get(Long id) {
		Optional<MLEntity> ml = mlRepository.findById(id);
		if (ml.isPresent()) {
			return ml.get();
		} else {
			throw new NotFoundResourceException("mlIdx : " + id);
		}
	}
	
	public MLEntity getByClusterIdx(Long clusterIdx) {
		Optional<MLEntity> ml = mlRepository.findByClusterIdx(clusterIdx);
		if (ml.isPresent()) {
			return ml.get();
		}
		return null;
	}
	
	public MLEntity get(String mlId) {
		Optional<MLEntity> ml = mlRepository.findByMlId(mlId);
		if (ml.isPresent()) {
			return ml.get();
		} else {
			throw new NotFoundResourceException("ML : " + mlId);
		}
	}
	
	public List<MLEntity> getCronsByClusterIdx(Long clusterIdx) {
		List<MLEntity> entity = mlRepository.getByClusterIdx(clusterIdx);
		return entity;
	}
	
	public void deleteByMlId(String mlId) {
		mlRepository.deleteByMlId(mlId);
	}
	
	public void delete(Long mlIdx) {
		mlRepository.deleteById(mlIdx);
	}
	
}
