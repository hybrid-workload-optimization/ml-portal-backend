package kr.co.strato.portal.cluster.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeCondition;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.node.service.NodeAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.node.service.NodeDomainService;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.model.ClusterNodeDto;
import kr.co.strato.portal.cluster.model.ClusterNodeDtoMapper;

@Service
public class ClusterNodeService {

	@Autowired
	private NodeAdapterService nodeAdapterService;
	@Autowired
	private NodeDomainService 	nodeDomainService;
	
	
	public Page<ClusterNodeDto> getClusterNodeList(String name,Pageable pageable) {
		Page<NodeEntity> clusterNodePage = nodeDomainService.findByName(name,pageable);
		List<ClusterNodeDto> clusterList = clusterNodePage.getContent().stream().map(c -> ClusterNodeDtoMapper.INSTANCE.toDto(c)).collect(Collectors.toList());
		
		Page<ClusterNodeDto> page = new PageImpl<>(clusterList, pageable, clusterNodePage.getTotalElements());
		return page;
	}

	@Transactional(rollbackFor = Exception.class)
	public List<Node> getClusterNodeList(Long clusterId) {
		List<Node> nodeList = nodeAdapterService.getNodeList(clusterId);
		
		synClusterNodeSave(nodeList,clusterId);
		return nodeList;
	}
	

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteClusterNode(Long id){
    	NodeEntity n = nodeDomainService.getDetail(id.longValue());
        Long clusterId = n.getClusterIdx().getClusterId();
        String nodeName = n.getName();

        boolean isDeleted = nodeAdapterService.deleteNode(clusterId.intValue(), nodeName);
        if(isDeleted){
            return nodeDomainService.delete(id.longValue());
        }else{
            throw new InternalServerException("k8s Node 삭제 실패");
        }
    }
	
	
    public ClusterNodeDto getClusterNodeDetail(Long id){
    	NodeEntity nodeEntity = nodeDomainService.getDetail(id); 

    	ClusterNodeDto clusterNodeDto = ClusterNodeDtoMapper.INSTANCE.toDto(nodeEntity);
        return clusterNodeDto;
    }
	
	
	public List<Long> registerClusterNode(YamlApplyParam yamlApplyParam, Long clusterId) {
		String decodeYaml = Base64Util.decode(yamlApplyParam.getYaml());
		
		List<Node> clusterNodes = nodeAdapterService.registerNode(yamlApplyParam.getKubeConfigId(),decodeYaml);
		List<Long> ids = synClusterNodeSave(clusterNodes,clusterId);
		return ids;
	}
	

	public List<Long> synClusterNodeSave(List<Node> clusterNodes, Long clusterId) {
		List<Long> ids = new ArrayList<>();
		for (Node n : clusterNodes) {
			try {
				NodeEntity clusterNode = toEntity(n,clusterId);
				
				// save
				Long id = nodeDomainService.register(clusterNode);
				ids.add(id);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new InternalServerException("parsing error");
			}
		}

		return ids;
	}
	
	private NodeEntity toEntity(Node n, Long clusterId) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
     // k8s Object -> Entity
        List<NodeCondition> conditions = n.getStatus().getConditions();
		// k8s Object -> Entity
		String name = n.getMetadata().getName();
		String uid = n.getMetadata().getUid();

		String ip = n.getStatus().getAddresses().stream().filter(addr -> addr.getType().equals("InternalIP"))
				.map(addr -> addr.getAddress()).findFirst().orElse(null);

		boolean status = conditions.stream().filter(condition -> condition.getType().equals("Ready"))
				.map(condition -> condition.getStatus().equals("True")).findFirst().orElse(false);

		String createdAt = n.getMetadata().getCreationTimestamp();

		String k8sVersion = n.getApiVersion();

		String podCapacity = n.getStatus().getCapacity().get("pods").getAmount().replaceAll("[^0-9]", "");
		float cpuCapacity = Float.parseFloat(n.getStatus().getCapacity().get("cpu").getAmount().replaceAll("[^0-9]", ""));
		float memoryCapacity = Float.parseFloat(n.getStatus().getCapacity().get("memory").getAmount().replaceAll("[^0-9]", ""));
		String image = n.getStatus().getNodeInfo().getOsImage();
		String kernelVersion = n.getStatus().getNodeInfo().getKernelVersion();
		String architecture = n.getStatus().getNodeInfo().getArchitecture();
		String kubeletVersion = n.getStatus().getNodeInfo().getKubeletVersion();

		String annotations = mapper.writeValueAsString(n.getMetadata().getAnnotations());
		String label = mapper.writeValueAsString(n.getMetadata().getLabels());
		String condition = mapper.writeValueAsString(conditions);

		List<String> roles = new ArrayList<>();
		n.getMetadata().getLabels().keySet().stream().filter(l -> l.contains("node-role"))
				.map(l -> l.split("/")[1]).iterator().forEachRemaining(roles::add);
		String role = mapper.writeValueAsString(roles);

		ClusterEntity clusterEntity = new ClusterEntity();
		clusterEntity.setClusterIdx(clusterId);

		NodeEntity clusterNode = NodeEntity.builder().name(name).uid(uid).ip(ip).status(String.valueOf(status))
				.k8sVersion(k8sVersion).allocatedCpu(cpuCapacity).allocatedMemory(memoryCapacity)
				.createdAt(DateUtil.strToLocalDateTime(createdAt))
				.podCidr(podCapacity).osImage(image)
				.kernelVersion(kernelVersion).architecture(architecture).kubeletVersion(kubeletVersion)
				.clusterIdx(clusterEntity)
				.annotation(annotations).label(label).condition(condition).role(role)
				.build();

        return clusterNode;
    }
    public String getNodeYaml(Long kubeConfigId,String name){
     	String nodeYaml = nodeAdapterService.getNodeYaml(kubeConfigId,name); 
         return nodeYaml;
     }
    
}
