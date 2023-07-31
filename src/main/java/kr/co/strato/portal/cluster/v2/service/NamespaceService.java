package kr.co.strato.portal.cluster.v2.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceQuota;
import io.fabric8.kubernetes.api.model.ResourceQuotaStatus;
import kr.co.strato.adapter.k8s.namespace.service.NamespaceAdapterService;
import kr.co.strato.adapter.k8s.resourcequota.service.ResourceQuotaAdapterService;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.v2.model.NamespaceDto;
import kr.co.strato.portal.cluster.v2.model.NamespaceDto.ResourceQuotaDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NamespaceService {
	
	@Autowired
	private NamespaceAdapterService namespaceAdapterService;
	
	@Autowired
	private ResourceQuotaAdapterService resourceQuotaAdapterService;
	
	
	public List<NamespaceDto.ListDto> getList(Long kubeConfigId, List<Pod> podList) {
		List<NamespaceDto.ListDto> list = new ArrayList<>();
		List<Namespace> namespaceList = namespaceAdapterService.getNamespaceList(kubeConfigId);
		for(Namespace n : namespaceList) {
			String name = n.getMetadata().getName();
			
			List<Pod> pods = podList.stream()
					.filter(resource -> resource.getMetadata().getNamespace() != null)
					.filter(resource -> resource.getMetadata().getNamespace().equals(name))
					.collect(Collectors.toList());
			
			try {
				NamespaceDto.ListDto dto = getNamespaceDto(kubeConfigId, n, pods);
				list.add(dto);
			} catch (Exception e) {
				log.error("", e);
			}
		}
		return list;
		
	}
	
	public NamespaceDto.ListDto getNamespaceDto(Long kubeConfigId, Namespace n, List<Pod> pods) throws Exception {
        String uid = n.getMetadata().getUid();
		String name = n.getMetadata().getName();
		String status = n.getStatus().getPhase();
		Map<String, String> annotations = n.getMetadata().getAnnotations();
		Map<String, String> label = n.getMetadata().getLabels();
		String createdAt = DateUtil.strToNewFormatter(n.getMetadata().getCreationTimestamp());
		
		
		String podStatus = null;
		if(pods != null) {
			long runningSize = pods.stream().filter(p -> p.getStatus().getPhase().equals("Running")).count();
			podStatus = String.format("%d/%d", runningSize, pods.size());
		}
		
		//네임스페이스 사용량 제한 조회
		ResourceQuotaDto getResourceQuota = getResourceQuota(kubeConfigId, name);
		
		NamespaceDto.ListDto listDto = NamespaceDto.ListDto.builder()
				.uid(uid)
				.name(name)
				.status(status)
				.label(label)
				.annotation(annotations)
				.podStatus(podStatus)
				.createdAt(createdAt)
				.resourceQuota(getResourceQuota)
				.build();
		
        return listDto;
	}

	/**
	 * 네임스페이스 사용량 제한 조회
	 * @param namespace
	 * @return
	 */
	public ResourceQuotaDto getResourceQuota(Long kubeConfigId, String namespace) throws Exception {
		List<ResourceQuota> list = resourceQuotaAdapterService.getList(kubeConfigId, namespace);
		if(list != null && list.size() > 0) {
			BigDecimal hardRequestsCpu = null;
			BigDecimal hardRequestsMemory = null;
			BigDecimal hardLimitsCpu = null;
			BigDecimal hardLimitsMemory = null;
			
			BigDecimal usedRequestsCpu = null;
			BigDecimal usedRequestsMemory = null;
			BigDecimal usedLimitsCpu = null;
			BigDecimal usedLimitsMemory = null;
			
			for(ResourceQuota rq : list) {					
				ResourceQuotaStatus rqStatus = rq.getStatus();
				Quantity hardRequestCpu = rqStatus.getHard().get("requests.cpu");
				Quantity hardRequestMem = rqStatus.getHard().get("requests.memory");
				Quantity hardLimitCpu = rqStatus.getHard().get("limits.cpu");
				Quantity hardLimitMem = rqStatus.getHard().get("limits.memory");
				
				Quantity usedRequestCpu = rqStatus.getUsed().get("requests.cpu");
				Quantity usedRequestMem = rqStatus.getUsed().get("requests.memory");
				Quantity usedLimitCpu = rqStatus.getUsed().get("limits.cpu");
				Quantity usedLimitMem = rqStatus.getUsed().get("limits.memory");
				
				BigDecimal hardRequestCpuDecimal = Quantity.getAmountInBytes(hardRequestCpu);
				BigDecimal hardRequestMemDecimal = Quantity.getAmountInBytes(hardRequestMem);
				BigDecimal hardLimitCpuDecimal = Quantity.getAmountInBytes(hardLimitCpu);
				BigDecimal hardLimitMemDecimal = Quantity.getAmountInBytes(hardLimitMem);
				
				BigDecimal usedRequestCpuDecimal = Quantity.getAmountInBytes(usedRequestCpu);
				BigDecimal usedRequestMemDecimal = Quantity.getAmountInBytes(usedRequestMem);
				BigDecimal usedLimitCpuDecimal = Quantity.getAmountInBytes(usedLimitCpu);
				BigDecimal usedLimitMemDecimal = Quantity.getAmountInBytes(usedLimitMem);
				
				
				if(hardRequestsCpu == null || hardRequestCpuDecimal.compareTo(hardRequestCpuDecimal) < 0) {
					hardRequestsCpu = hardRequestCpuDecimal;
				}					
				if(hardRequestsMemory == null || hardRequestMemDecimal.compareTo(hardRequestsMemory) < 0) {
					hardRequestsMemory = hardRequestMemDecimal;
				}					
				if(hardLimitsCpu == null || hardLimitCpuDecimal.compareTo(hardLimitsCpu) < 0) {
					hardLimitsCpu = hardLimitCpuDecimal;
				}					
				if(hardLimitsMemory == null || hardLimitMemDecimal.compareTo(hardLimitsMemory) < 0) {
					hardLimitsMemory = hardLimitMemDecimal;
				}
				
				
				if(usedRequestsCpu == null || usedRequestCpuDecimal.compareTo(usedRequestsCpu) > 0) {
					usedRequestsCpu = usedRequestCpuDecimal;
				}					
				if(usedRequestsMemory == null || usedRequestMemDecimal.compareTo(usedRequestsMemory) > 0) {
					usedRequestsMemory = usedRequestMemDecimal;
				}
				if(usedLimitsCpu == null || usedLimitCpuDecimal.compareTo(usedLimitsCpu) > 0) {
					usedLimitsCpu = usedLimitCpuDecimal;
				}
				if(usedLimitsMemory == null || usedLimitMemDecimal.compareTo(usedLimitsMemory) > 0) {
					usedLimitsMemory = usedLimitMemDecimal;
				}
			}
			
			//CPU Request 사용량(%)
			BigDecimal cpuRequestsFraction = usedRequestsCpu.divide(hardRequestsCpu, MathContext.DECIMAL32)
					.multiply(new BigDecimal(100));
			//Memory Request 사용량(%)
			BigDecimal memoryRequestsFraction = usedRequestsMemory.divide(hardRequestsMemory, MathContext.DECIMAL32)
					.multiply(new BigDecimal(100));
			
			//CPU Limit 사용량(%)
			BigDecimal cpuLimitsFraction = usedLimitsCpu.divide(hardLimitsCpu, MathContext.DECIMAL32)
					.multiply(new BigDecimal(100));
			//Memory Limit 사용량(%)
			BigDecimal memoryLimitsFraction = usedLimitsMemory.divide(hardLimitsMemory, MathContext.DECIMAL32)
					.multiply(new BigDecimal(100));
			
			
			ResourceQuotaDto resourceQuotaDto = ResourceQuotaDto.builder()
					.hardRequestsCpu(hardRequestsCpu.doubleValue())
					.hardRequestsMemory(hardRequestsMemory.doubleValue())
					.hardLimitsCpu(hardLimitsCpu.doubleValue())
					.hardLimitsMemory(hardLimitsMemory.doubleValue())
					.usedRequestsCpu(usedRequestsCpu.doubleValue())
					.usedRequestsMemory(usedRequestsMemory.doubleValue())
					.usedLimitsCpu(usedLimitsCpu.doubleValue())
					.usedLimitsMemory(usedLimitsMemory.doubleValue())
					.cpuRequestsFraction(cpuRequestsFraction.doubleValue())
					.memoryRequestsFraction(memoryRequestsFraction.doubleValue())
					.cpuLimitsFraction(cpuLimitsFraction.doubleValue())
					.memoryLimitsFraction(memoryLimitsFraction.doubleValue())
					.build();
			return resourceQuotaDto;
		}
		return null;
	}
}
