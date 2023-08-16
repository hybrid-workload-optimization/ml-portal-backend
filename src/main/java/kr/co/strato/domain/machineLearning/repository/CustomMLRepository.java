package kr.co.strato.domain.machineLearning.repository;

import java.util.List;

import kr.co.strato.domain.machineLearning.model.MLEntity;

public interface CustomMLRepository {
	public List<MLEntity> getMLList(String userId, String name);
	public List<MLEntity> getMLList();
	public List<MLEntity> getByClusterIdx(Long clusterIdx);
}
