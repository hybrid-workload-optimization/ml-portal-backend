package kr.co.strato.portal.cluster.v2.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.Quantity;
import kr.co.strato.adapter.k8s.node.service.NodeAdapterService;
import kr.co.strato.adapter.k8s.pod.service.PodAdapterService;
import kr.co.strato.portal.cluster.v2.model.NodeDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NodeService {
	
	@Autowired
	private PodAdapterService podAdapterService;
	
	@Autowired
	private NodeAdapterService nodeAdapterService;
	
	public List<NodeDto.ListDto> getList(Long kubeConfigId) {
		List<Pod> podList = podAdapterService.getList(kubeConfigId, null, null, null, null);
		return getList(kubeConfigId, podList);
	}
	
	public NodeDto.DetailDto getNode(Long kubeConfigId, String nodeName) {
		List<Pod> podList = podAdapterService.getList(kubeConfigId, nodeName, null, null, null);
		Node node = nodeAdapterService.getNodeDetail(kubeConfigId, nodeName);
		NodeDto.DetailDto detail = new NodeDto.DetailDto();
		try {
			getNodeDto(node, podList, detail);
		} catch (Exception e) {
			log.error("", e);
		}
		return detail;
	}
	
	public List<NodeDto.ListDto> getList(Long kubeConfigId, List<Pod> podList) {
		List<NodeDto.ListDto> list = new ArrayList<>();
		List<Node> nodeList = nodeAdapterService.getNodeList(kubeConfigId);
		for(Node n : nodeList) {
			String nodeName = n.getMetadata().getName();
			
			List<Pod> pods = podList.stream()
					.filter(resource -> resource.getSpec().getNodeName() != null)
					.filter(resource -> resource.getSpec().getNodeName().equals(nodeName))
					.collect(Collectors.toList());
			
			try {
				NodeDto.ListDto dto = new NodeDto.ListDto();
				getNodeDto(n, pods, dto);
				list.add(dto);
			} catch (Exception e) {
				log.error("", e);
			}
		}
		return list;
	}
	
	public void getNodeDto(Node n, List<Pod> pods, NodeDto.ListDto dto) throws Exception {
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
		String podCidr = n.getSpec().getPodCIDR();
		
		
		String image = n.getStatus().getNodeInfo().getOsImage();
		String kernelVersion = n.getStatus().getNodeInfo().getKernelVersion();
		String architecture = n.getStatus().getNodeInfo().getArchitecture();
		String kubeletVersion = n.getStatus().getNodeInfo().getKubeletVersion();

		Map<String, String> annotations = n.getMetadata().getAnnotations();
		Map<String, String> labels = n.getMetadata().getLabels();

		List<String> roles = new ArrayList<>();
		n.getMetadata().getLabels().keySet().stream().filter(l -> l.contains("node-role"))
				.map(l -> l.split("/")[1]).iterator().forEachRemaining(roles::add);
		
		if(roles.size() == 0) {
			roles.add("worker");
		}
		
		String podStatus = null;
		NodeDto.NodeUsageDto usageDto = null;
		if(pods != null) {
			long runningSize = pods.stream().filter(p -> p.getStatus().getPhase().equals("Running")).count();
			podStatus = String.format("%d/%d", runningSize, pods.size());
			usageDto = setUsage(n, pods);
		}
		
		dto.setUid(uid);
		dto.setName(name);
		dto.setIp(ip);
		dto.setStatus(Boolean.toString(status));
		dto.setPodStatus(podStatus);
		dto.setRole(roles);
		dto.setLabels(labels);
		dto.setUsageDto(usageDto);
		dto.setCreatedAt(createdAt);
		
		if(dto instanceof NodeDto.DetailDto) {
			NodeDto.DetailDto detail = (NodeDto.DetailDto) dto;
			detail.setK8sVersion(k8sVersion);
			detail.setPodCidr(podCidr);
			detail.setOsImage(image);
			detail.setKernelVersion(kernelVersion);
			detail.setArchitecture(architecture);
			detail.setKubeletVersion(kubeletVersion);
			detail.setAnnotation(annotations);
			detail.setLabel(labels);
			detail.setConditions(conditions);
		}
		
		/*
		NodeDto.ListDto listDto = NodeDto.ListDto.builder()
				.uid(uid)
				.name(name)
				.ip(ip)
				.status(Boolean.toString(status))
				.podStatus(podStatus)
				.role(roles)
				.labels(labels)
				.usageDto(usageDto)
				.createdAt(createdAt)
				.build();
		
        return listDto;
        */
	}

	public NodeDto.NodeUsageDto setUsage(Node node, List<Pod> pods) {
		int allocatedPods = pods.size();
		BigDecimal podCapacity = Quantity.getAmountInBytes(node.getStatus().getCapacity().get("pods"));
		BigDecimal cpuCapacity = Quantity.getAmountInBytes(node.getStatus().getCapacity().get("cpu"));
		BigDecimal memoryCapacity = Quantity.getAmountInBytes(node.getStatus().getCapacity().get("memory"));

		BigDecimal sumCpuRequests = new BigDecimal(0);
		BigDecimal sumCpuLimits = new BigDecimal(0);
		BigDecimal sumMemoryRequests = new BigDecimal(0);
		BigDecimal sumMemoryLimits = new BigDecimal(0);
		for (Pod pod : pods) {
			PodSpec spec = pod.getSpec();
			List<Quantity> cpuRequests = spec.getContainers().stream()
	    				.map(container -> container.getResources().getRequests())
	    				.filter(map -> map != null)
	    				.map(map -> map.get("cpu"))
	    				.filter(quantity -> quantity != null)
	    				.map(map -> {
	    					map.setAmount(map.getAmount().replaceAll("[^0-9]", ""));
	    					map.setFormat(map.getFormat().replaceAll("[^a-zA-Z]", ""));
	    					return map;
	    				})
	    				.collect(Collectors.toList());
			 
	    	List<Quantity> cpuLimits = spec.getContainers().stream()
	    				.map(container -> container.getResources().getLimits())
	    				.filter(map -> map != null)
	    				.map(map -> map.get("cpu"))
	    				.filter(quantity -> quantity != null)
	    				.map(map -> {
	    					map.setAmount(map.getAmount().replaceAll("[^0-9]", ""));
	    					map.setFormat(map.getFormat().replaceAll("[^a-zA-Z]", ""));
	    					return map;
	    				})
	    				.collect(Collectors.toList());
	    		
	    	List<Quantity> memRequests = spec.getContainers().stream()
	    				.map(container -> container.getResources().getRequests())
	    				.filter(map -> map != null)
	    				.map(map -> map.get("memory"))
	    				.filter(quantity -> quantity != null)
	    				.map(map -> {
	    					map.setAmount(map.getAmount().replaceAll("[^0-9]", ""));
	    					map.setFormat(map.getFormat().replaceAll("[^a-zA-Z]", ""));
	    					return map;
	    				})
	    				.collect(Collectors.toList());	
	    	List<Quantity> memLimits = spec.getContainers().stream()
	    				.map(container -> container.getResources().getLimits())
	    				.filter(map -> map != null)
	    				.map(map -> map.get("memory"))
	    				.filter(quantity -> quantity != null)
	    				.map(map -> {
	    					map.setAmount(map.getAmount().replaceAll("[^0-9]", ""));
	    					map.setFormat(map.getFormat().replaceAll("[^a-zA-Z]", ""));
	    					return map;
	    				})
	    				.collect(Collectors.toList());

			sumCpuRequests = sumCpuRequests.add(sum(cpuRequests));
			sumCpuLimits = sumCpuLimits.add(sum(cpuLimits));
			sumMemoryRequests = sumMemoryRequests.add(sum(memRequests));
			sumMemoryLimits = sumMemoryLimits.add(sum(memLimits));
		}

		cpuCapacity = cpuCapacity.multiply(new BigDecimal(1000));
		sumCpuRequests = sumCpuRequests.multiply(new BigDecimal(1000));
		sumCpuLimits = sumCpuLimits.multiply(new BigDecimal(1000));

		BigDecimal cpuRequestsFraction = sumCpuRequests.divide(cpuCapacity, MathContext.DECIMAL32)
				.multiply(new BigDecimal(100));
		BigDecimal cpuLimitsFraction = sumCpuLimits.divide(cpuCapacity, MathContext.DECIMAL32)
				.multiply(new BigDecimal(100));

		BigDecimal memoryRequestsFraction = sumMemoryRequests.divide(memoryCapacity, MathContext.DECIMAL32)
				.multiply(new BigDecimal(100));
		BigDecimal memoryLimitsFraction = sumMemoryLimits.divide(memoryCapacity, MathContext.DECIMAL32)
				.multiply(new BigDecimal(100));

		BigDecimal podFraction = new BigDecimal(allocatedPods).divide(podCapacity, MathContext.DECIMAL32)
				.multiply(new BigDecimal(100));

		NodeDto.NodeUsageDto usageDto = NodeDto.NodeUsageDto.builder()
				.podCapacity(podCapacity.doubleValue())
				.allocatedPods(allocatedPods)
				.podFraction(podFraction.doubleValue())

				.cpuCapacity(cpuCapacity.doubleValue())
				.cpuLimits(sumCpuLimits.doubleValue())
				.cpuLimitsFraction(cpuLimitsFraction.doubleValue())
				.cpuRequests(sumCpuRequests.doubleValue())
				.cpuRequestsFraction(cpuRequestsFraction.doubleValue())

				.memoryCapacity(memoryCapacity.doubleValue())
				.memoryLimits(sumMemoryLimits.doubleValue())
				.memoryLimitsFraction(memoryLimitsFraction.doubleValue())
				.memoryRequests(sumMemoryRequests.doubleValue())
				.memoryRequestsFraction(memoryRequestsFraction.doubleValue()).build();
		return usageDto;
	}

	public BigDecimal sum(List<Quantity> list) {
		BigDecimal val = new BigDecimal(0);
		for (Quantity value : list) {
			if (value != null) {
				BigDecimal v = Quantity.getAmountInBytes(value);
				val = val.add(v);
			}
		}
		return val;
	}
}
