package kr.co.strato.portal.cluster.v1.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.adapter.cloud.cluster.model.ClusterCloudDto;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.node.model.NodeEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClusterDtoMapper {

	ClusterDtoMapper INSTANCE = Mappers.getMapper(ClusterDtoMapper.class);
	
	public ClusterEntity toEntity(ClusterDto.Form dto);
	
	public ClusterDto toDto(ClusterEntity cluster);
	
	@Mapping(target = "nodeCount", 		source = "c.nodes",		qualifiedByName = "nodeCount")
    @Mapping(target = "problem",		source = "c.problem",	qualifiedByName = "jsonToList")
	@Mapping(target = "createdAt",		source = "c.createdAt", qualifiedByName = "createdAt")
	public ClusterDto.List toList(ClusterEntity c);
	
	@Mapping(target = "description",	source = "c.description")
    @Mapping(target = "problem",		source = "c.problem",	qualifiedByName = "jsonToList")
	@Mapping(target = "createdAt",		source = "c.createdAt", qualifiedByName = "createdAt")
	public ClusterDto.Detail toDetail(ClusterEntity c);
	
	@Mapping(target = "description",	source = "c.description")
    @Mapping(target = "problem",		source = "c.problem",	qualifiedByName = "jsonToList")
	@Mapping(target = "createdAt",		source = "c.createdAt", qualifiedByName = "createdAt")
	public ClusterDto.DetailForMonitoring toDetailForMonitoring(ClusterEntity c);
	
	@Mapping(target = "description",	source = "c.description")
    @Mapping(target = "problem",		source = "c.problem",	qualifiedByName = "jsonToList")
	@Mapping(target = "createdAt",		source = "c.createdAt", qualifiedByName = "createdAt")
	public ClusterDto.DetailForDevOps toDetailForDevOps(ClusterEntity c);
	
	@Mapping(target = "userName",		source = "dto.provisioningUser")
	@Mapping(target = "nodes",			source = "dto.nodes",	qualifiedByName = "dtoToClusterCloudNodeList")
	public ClusterCloudDto toClusterCloudDto(ClusterDto.Form dto);
	
	@Mapping(target = "userName",		source = "c.provisioningUser")
	@Mapping(target = "nodes",			source = "c.nodes",		qualifiedByName = "entityToClusterCloudNodeList")
	public ClusterCloudDto toClusterCloudDto(ClusterEntity c);
	
	
	@Named("createdAt")
    default String createdAt(String createdAt) {
		if(createdAt != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
			try {
				Date date = formatter.parse(createdAt);
				return formatter.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}	
		}	
        return createdAt;
    }
	
	
	@Named("nodeCount")
    default int nodeCount(List<NodeEntity> nodes) {
        return nodes.size();
    }
	
	@Named("jsonToMap")
    default HashMap<String, Object> jsonToMap(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(json, HashMap.class);
		} catch (Exception e) {
			return new HashMap<>();
		}
    }
	
	@Named("jsonToList")
    default ArrayList<String> jsonToList(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(json, ArrayList.class);
		} catch (Exception e) {
			return new ArrayList<>();
		}
    }
    
	@Named("dtoToClusterCloudNodeList")
    default ArrayList<ClusterCloudDto.Node> dtoToClusterCloudNodeList(ArrayList<ClusterDto.Node> nodes) {
		try {
			ArrayList<ClusterCloudDto.Node> results = new ArrayList<>();
			
			for (ClusterDto.Node node : nodes) {
				ClusterCloudDto.Node result = new ClusterCloudDto.Node();  
				result.setName(node.getName());
				result.setIp(node.getIp());
				result.setNodeTypes(node.getNodeTypes());
				
				results.add(result); 
			}
			
			return results;
		} catch (Exception e) {
			return new ArrayList<>();
		}
    }
	
	@Named("entityToClusterCloudNodeList")
    default ArrayList<ClusterCloudDto.Node> entityToClusterCloudNodeList(List<NodeEntity> nodes) {
		try {
			ArrayList<ClusterCloudDto.Node> results = new ArrayList<>();
			
			ObjectMapper mapper = new ObjectMapper();
			
			for (NodeEntity node : nodes) {
				ClusterCloudDto.Node result = new ClusterCloudDto.Node();  
				result.setName(node.getName());
				result.setIp(node.getIp());
				result.setNodeTypes(mapper.readValue(node.getRole(), ArrayList.class));
				
				results.add(result); 
			}
			
			return results;
		} catch (Exception e) {
			return new ArrayList<>();
		}
    }
}
