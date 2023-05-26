package kr.co.strato.portal.cluster.service;

import java.math.BigDecimal;
import java.math.MathContext;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Quantity;
import kr.co.strato.adapter.k8s.node.service.NodeAdapterService;
import kr.co.strato.adapter.k8s.pod.model.PodMapper;
import kr.co.strato.adapter.k8s.pod.service.PodAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.node.service.NodeDomainService;
import kr.co.strato.domain.pod.model.PodEntity;
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
	
	@Autowired
    private PodAdapterService podAdapterService;
	

	/**
	 * Node 목록 조회(By ClusterIdx)
	 * 
	 * @param clusterIdx
	 * @param pageable
	 * @return
	 */
	public Page<ClusterNodeDto.ResListDto> getClusterNodeList(Long clusterIdx, Pageable pageable) {
		ClusterEntity clusterEntity = new ClusterEntity();
		clusterEntity.setClusterIdx(clusterIdx);
		
		Page<NodeEntity> nodePage = nodeDomainService.findByClusterIdx(clusterEntity, pageable);
		
		List<ClusterNodeDto.ResListDto> nodeList = nodePage.getContent().stream()
				.map(c -> ClusterNodeDtoMapper.INSTANCE.toResListDto(c))
				.collect(Collectors.toList());
		
		Page<ClusterNodeDto.ResListDto> page = new PageImpl<>(nodeList, pageable, nodePage.getTotalElements());
		
		return page;
	}
	
	/**
	 * Cluster에 속한 노드 배열 반환.
	 * @param clusterId
	 * @return
	 */
	public List<NodeEntity> getNodeList(Long clusterId) {
		return nodeDomainService.getNodeList(clusterId);
	}
	
	public Page<ClusterNodeDto.ResListDto> getClusterNodes(Pageable pageable, ClusterNodeDto.SearchParam searchParam){
        Page<NodeEntity> nodes = nodeDomainService.getNodeList(pageable, searchParam.getClusterIdx(), searchParam.getName());
        List<ClusterNodeDto.ResListDto> dtos = nodes.stream().map(e -> ClusterNodeDtoMapper.INSTANCE.toResListDto(e)).collect(Collectors.toList());
        Page<ClusterNodeDto.ResListDto> pages = new PageImpl<>(dtos, pageable, nodes.getTotalElements());
        return pages;
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
        Long clusterId = n.getCluster().getClusterId();
        String nodeName = n.getName();

        boolean isDeleted = nodeAdapterService.deleteNode(clusterId.intValue(), nodeName);
        if(isDeleted){
            return nodeDomainService.delete(id.longValue());
        }else{
            throw new InternalServerException("k8s Node 삭제 실패");
        }
    }
	
	
    public ClusterNodeDto.ResDetailDto getClusterNodeDetail(Long id){
    	NodeEntity nodeEntity = nodeDomainService.getDetail(id); 
    	
    	Node node = nodeAdapterService.getNodeDetail(nodeEntity.getCluster().getClusterId(), nodeEntity.getName());
    	
    	List<Pod> k8sPods = podAdapterService.getList(nodeEntity.getCluster().getClusterId(), nodeEntity.getName(), null, null, null);
    	
    	List<PodEntity> pods =  k8sPods.stream().map( s -> {
    		PodEntity pod = PodMapper.INSTANCE.toEntity(s);
    		return pod;
    	}).collect(Collectors.toList());
    	
    	ClusterNodeDto.ResDetailChartDto chartNode = new ClusterNodeDto.ResDetailChartDto();
    	
    	setUsage(node,pods,chartNode);
    	
    	ClusterNodeDto.ResDetailDto clusterNodeDto = ClusterNodeDtoMapper.INSTANCE.toResDetailDto(nodeEntity);
    	clusterNodeDto.setChartDto(chartNode);
    	
    	return clusterNodeDto;
    }
	
	
	public List<Long> registerClusterNode(ClusterNodeDto.ReqCreateDto yamlApplyParam) {
		String decodeYaml = Base64Util.decode(yamlApplyParam.getYaml());
		
		List<Node> clusterNodes = nodeAdapterService.registerNode(yamlApplyParam.getKubeConfigId(),decodeYaml);
		List<Long> ids = synClusterNodeSave(clusterNodes,yamlApplyParam.getKubeConfigId());
		return ids;
	}
	

	public List<Long> synClusterNodeSave(List<Node> clusterNodes, Long clusterId) {
		//기존 노드 삭제
		nodeDomainService.deleteByClusterIdx(clusterId);
		
		
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
	
	public NodeEntity toEntity(Node n, Long clusterId) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
     // k8s Object -> Entity
        List<NodeCondition> conditions = n.getStatus().getConditions();
		// k8s Object -> Entity
		String name = n.getMetadata().getName();
		String uid = n.getMetadata().getUid();
		
		List<String> ips = n.getStatus().getAddresses().stream().filter(addr -> addr.getType().equals("InternalIP"))
			.map(addr -> addr.getAddress()).collect(Collectors.toList());
		
		String ip = null;
		for(String i : ips) {
			if(ip == null) {
				ip = i;
			}
			
			if(ip.length() < i.length()) {
				ip = i;
			}
		}

		//String ip = n.getStatus().getAddresses().stream().filter(addr -> addr.getType().equals("InternalIP"))
		//		.map(addr -> addr.getAddress()).findFirst().orElse(null);

		boolean status = conditions.stream().filter(condition -> condition.getType().equals("Ready"))
				.map(condition -> condition.getStatus().equals("True")).findFirst().orElse(false);

		String createdAt = n.getMetadata().getCreationTimestamp();

		String k8sVersion = n.getApiVersion();
		String podCidr = n.getSpec().getPodCIDR();

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
		if(roles.size() == 0) {
			roles.add("worker");
		}
		
		if(roles.contains("control-plane")) {
			roles.remove("control-plane");
			if(!roles.contains("master")) {
				roles.add("master");
			}
		}
		
		
		String role = mapper.writeValueAsString(roles);
		
		ClusterEntity clusterEntity = new ClusterEntity();
		clusterEntity.setClusterIdx(clusterId);
		
		NodeEntity clusterNode = NodeEntity.builder().name(name).uid(uid).ip(ip).status(String.valueOf(status))
				.k8sVersion(k8sVersion).allocatedCpu(cpuCapacity).allocatedMemory(memoryCapacity)
				.createdAt(DateUtil.strToLocalDateTime(createdAt))
				.podCidr(podCidr).osImage(image)
				.kernelVersion(kernelVersion).architecture(architecture).kubeletVersion(kubeletVersion)
				.cluster(clusterEntity)
				.annotation(annotations).label(label).condition(condition)
				.role(role)
				.build();

        return clusterNode;
    }
    public String getNodeYaml(Long kubeConfigId,String name){
     	String nodeYaml = nodeAdapterService.getNodeYaml(kubeConfigId,name); 
         return nodeYaml;
     }
    
    public void setUsage(Node node , List<PodEntity> pods, ClusterNodeDto.ResDetailChartDto chartNode) {
		int allocatedPods = pods.size();
		BigDecimal podCapacity = Quantity.getAmountInBytes(node.getStatus().getCapacity().get("pods"));
		BigDecimal cpuCapacity = Quantity.getAmountInBytes(node.getStatus().getCapacity().get("cpu"));
		BigDecimal memoryCapacity = Quantity.getAmountInBytes(node.getStatus().getCapacity().get("memory"));
		
		
		BigDecimal sumCpuRequests = new BigDecimal(0);
		BigDecimal sumCpuLimits = new BigDecimal(0);
		BigDecimal sumMemoryRequests = new BigDecimal(0);
		BigDecimal sumMemoryLimits = new BigDecimal(0);
		for(PodEntity pod : pods) {
			List<Quantity> cpuRequests = pod.getCpuRequests();
			List<Quantity> cpuLimits = pod.getCpuLimits();
			List<Quantity> memoryRequests = pod.getMemoryRequests();
			List<Quantity> memoryLimits = pod.getMemoryLimits();
			
			
			sumCpuRequests = sumCpuRequests.add(sum(cpuRequests));
			sumCpuLimits = sumCpuLimits.add(sum(cpuLimits));
			sumMemoryRequests = sumMemoryRequests.add(sum(memoryRequests));
			sumMemoryLimits = sumMemoryLimits.add(sum(memoryLimits));
		}
		
		cpuCapacity = cpuCapacity.multiply(new BigDecimal(1000));
		sumCpuRequests = sumCpuRequests.multiply(new BigDecimal(1000));
		sumCpuLimits = sumCpuLimits.multiply(new BigDecimal(1000));
		
		
		BigDecimal cpuRequestsFraction = sumCpuRequests.divide(cpuCapacity, MathContext.DECIMAL32).multiply(new BigDecimal(100));
		BigDecimal cpuLimitsFraction = sumCpuLimits.divide(cpuCapacity, MathContext.DECIMAL32).multiply(new BigDecimal(100));
		
		BigDecimal memoryRequestsFraction = sumMemoryRequests.divide(memoryCapacity, MathContext.DECIMAL32).multiply(new BigDecimal(100));
		BigDecimal memoryLimitsFraction = sumMemoryLimits.divide(memoryCapacity, MathContext.DECIMAL32).multiply(new BigDecimal(100));
		
		BigDecimal podFraction = new BigDecimal(allocatedPods).divide(podCapacity, MathContext.DECIMAL32).multiply(new BigDecimal(100));
		
		
		chartNode.setPodCapacity(podCapacity.doubleValue());
		chartNode.setAllocatedPods(allocatedPods);
		chartNode.setPodFraction(podFraction.doubleValue());
		
		chartNode.setCpuCapacity(cpuCapacity.doubleValue());
		chartNode.setCpuLimits(sumCpuLimits.doubleValue());
		chartNode.setCpuLimitsFraction(cpuLimitsFraction.doubleValue());
		chartNode.setCpuRequests(sumCpuRequests.doubleValue());
		chartNode.setCpuRequestsFraction(cpuRequestsFraction.doubleValue());
		
		chartNode.setMemoryCapacity(memoryCapacity.doubleValue());
		chartNode.setMemoryLimits(sumMemoryLimits.doubleValue());
		chartNode.setMemoryLimitsFraction(memoryLimitsFraction.doubleValue());
		chartNode.setMemoryRequests(sumMemoryRequests.doubleValue());
		chartNode.setMemoryRequestsFraction(memoryRequestsFraction.doubleValue());
	}
	
	public BigDecimal sum(List<Quantity> list) {
		BigDecimal val = new BigDecimal(0);
		for(Quantity value : list) {			
			if(value != null) {
				BigDecimal v = Quantity.getAmountInBytes(value);
				val = val.add(v);
			}
		}
		return val;
	}
	/**
	* Node 이름으로 NodeEntity를 구해 반환
	* @param clusterIdx
	* @param nodeName
	* @return
	*/
	public NodeEntity getNodeByName(Long clusterIdx, String nodeName) {
		NodeEntity nodeEntity= nodeDomainService.findNodeName(clusterIdx,nodeName);
		
		return nodeEntity;
		}
	
	public List<String> getWorkerNodeIps(Long clusterIdx) {
		List<String> ips = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		List<NodeEntity> nodes = getNodeList(clusterIdx);
		for(NodeEntity n : nodes) {
			String role = n.getRole();
			
			try {
				List<String> map = mapper.readValue(role, new TypeReference<List<String>>() {});
				if(map.size() == 0 || map.contains("worker")) {
					ips.add(n.getIp());
				}
				
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		return ips;
	}
	
	public List<String> getMasterNodeIps(Long clusterIdx) {
		List<String> ips = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		List<NodeEntity> nodes = getNodeList(clusterIdx);
		for(NodeEntity n : nodes) {
			String role = n.getRole();
			
			try {
				List<String> map = mapper.readValue(role, new TypeReference<List<String>>() {});
				if(map.size() == 0 || map.contains("master")) {
					ips.add(n.getIp());
				}
				
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		return ips;
	}
    
}
