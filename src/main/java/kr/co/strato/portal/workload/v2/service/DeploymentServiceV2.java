package kr.co.strato.portal.workload.v2.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.portal.workload.v2.model.DeploymentDto;
import kr.co.strato.portal.workload.v2.model.ReplicaSetDto;
import kr.co.strato.portal.workload.v2.model.WorkloadCommonDto;

@Service
public class DeploymentServiceV2 extends WorkloadCommonV2 {
	
	@Autowired
	private ReplicaSetServiceV2 replicaService;

	@Override
	public WorkloadCommonDto toDto(ClusterEntity clusterEntity, HasMetadata data) throws Exception {
		DeploymentDto deploymentDto = new DeploymentDto();
		setMetadataInfo(data, deploymentDto);		
		
		Deployment d = (Deployment) data;
        
        String strategy = null;
        String selector = null;
        String maxSurge = null;
        String maxUnavailable = null;
        Integer podUpdated = null;
        Integer podReplicas = null;
        Integer podReady = null;
        String condition = null;
        String image = null;
        
        ObjectMapper mapper = new ObjectMapper();
		DeploymentSpec deploymentSpec = d.getSpec();
        
        if(deploymentSpec != null) {
        	io.fabric8.kubernetes.api.model.apps.DeploymentStrategy deploymentStrategy = deploymentSpec.getStrategy();
        	if(deploymentStrategy != null) {
        		strategy = deploymentStrategy.getType();
        		if(strategy.equals("RollingUpdate") && deploymentStrategy.getRollingUpdate() != null) {
        			maxSurge = deploymentStrategy.getRollingUpdate().getMaxSurge().getStrVal();
        			maxUnavailable = deploymentStrategy.getRollingUpdate().getMaxUnavailable().getStrVal();
        		}
        		
        	}
        	if(deploymentSpec.getSelector() != null) {
				selector = mapper.writeValueAsString(deploymentSpec.getSelector().getMatchLabels());
			}

			image = deploymentSpec.getTemplate().getSpec().getContainers().get(0).getImage();
        }
        
        DeploymentStatus deploymentStatus = d.getStatus();
        if(deploymentStatus != null) {
        	podUpdated = deploymentStatus.getUpdatedReplicas();
        	podReplicas = deploymentStatus.getReplicas();
        	podReady = deploymentStatus.getReadyReplicas();
        	condition = mapper.writeValueAsString(deploymentStatus.getConditions());
        }
        
        float fMaxSurge = 0f;
        if(maxSurge != null && !maxSurge.isEmpty()) {
        	fMaxSurge = Float.parseFloat(maxSurge);
        }	
        
        float fMaxUnavailable = 0f;
        if(maxUnavailable != null && !maxUnavailable.isEmpty()) {
        	fMaxUnavailable = Float.parseFloat(maxUnavailable);
        }
        
        List<ReplicaSetDto> replicaSets = replicaService.getJobByOwnerUid(clusterEntity.getClusterId(), deploymentDto.getUid());
        deploymentDto.setImage(image);
        deploymentDto.setStrategy(strategy);
        deploymentDto.setSelector(selector);
        deploymentDto.setMaxSurge(fMaxSurge);
        deploymentDto.setMaxUnavailable(fMaxUnavailable);
        deploymentDto.setPodUpdated(podUpdated);
        deploymentDto.setPodReplicas(podReplicas);
        deploymentDto.setPodReady(podReady);
        deploymentDto.setCondition(condition);
        deploymentDto.setReplicaSets(replicaSets);
        return deploymentDto;
	}

}
