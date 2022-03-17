package kr.co.strato.domain.cluster.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClusterMapper {
	
	int deleteClusterAll(Long clusterIdx);

}
