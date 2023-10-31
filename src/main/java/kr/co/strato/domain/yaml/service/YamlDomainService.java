package kr.co.strato.domain.yaml.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.yaml.model.YamlEntity;
import kr.co.strato.domain.yaml.repository.YamlRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class YamlDomainService {
	
	@Autowired
	YamlRepository yamlRepository;
	
	public List<YamlEntity> getAll(){
		return yamlRepository.findAll();
	}
	
	public YamlEntity getById(Long idx){
		Optional<YamlEntity> yaml = yamlRepository.findById(idx);
		if (yaml.isPresent())
			return yaml.get();
		else
			throw new NotFoundResourceException("yamlIdx : " + idx);
	}
	
	public YamlEntity save(YamlEntity jobEntity){
		YamlEntity result = yamlRepository.save(jobEntity);
		return result;
	}
	
	public void delete(Long idx){
		yamlRepository.deleteById(idx);
	}
	
	public List<YamlEntity> get(Long clusterIdx, String kind, String name) {
		return yamlRepository.findByClusterIdxAndKindAndName(clusterIdx, kind, name);
	}
	
	public List<YamlEntity> get(Long clusterIdx, String kind, String name, String namespace) {
		return yamlRepository.findByClusterIdxAndKindAndNameAndNamespace(clusterIdx, kind, name, namespace);
	}
	
	public Integer deleteByClusterIdx(Long clusterIdx) {
		return yamlRepository.deleteByClusterIdx(clusterIdx);
	}
	
	public Integer delete(Long clusterIdx, String kind, String name) {
		return yamlRepository.deleteByClusterIdxAndKindAndName(clusterIdx, kind, name);
	}
	
	public Integer delete(Long clusterIdx, String kind, String name, String namespace) {
		return yamlRepository.deleteByClusterIdxAndKindAndNameAndNamespace(clusterIdx, kind, name, namespace);
	}
	
}
