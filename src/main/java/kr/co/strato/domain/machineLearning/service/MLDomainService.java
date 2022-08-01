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
	
	public List<MLEntity> getList(String userId, String name) {
		return mlRepository.getMLList(userId, name);
    }
	
	public MLEntity get(String mlId) {
		Optional<MLEntity> ml = mlRepository.findByMlId(mlId);
		if (ml.isPresent()) {
			return ml.get();
		} else {
			throw new NotFoundResourceException("ML : " + mlId);
		}
	}
	
	public void deleteByMlId(String mlId) {
		mlRepository.deleteByMlId(mlId);
	}
	
	public void delete(Long mlIdx) {
		mlRepository.deleteById(mlIdx);
	}
	
}
