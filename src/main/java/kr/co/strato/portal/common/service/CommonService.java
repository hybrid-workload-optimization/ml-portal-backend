package kr.co.strato.portal.common.service;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Base64.Decoder;

import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import kr.co.strato.adapter.k8s.common.service.CommonAdapterService;
import kr.co.strato.portal.config.service.ConfigMapService;
import kr.co.strato.portal.config.service.PersistentVolumeClaimService;
import kr.co.strato.portal.config.service.SecretService;
import kr.co.strato.portal.ml.v1.service.MLServiceInterface;
import kr.co.strato.portal.networking.service.IngressService;
import kr.co.strato.portal.networking.service.K8sServiceService;
import kr.co.strato.portal.workload.v1.service.CronJobService;
import kr.co.strato.portal.workload.v1.service.DaemonSetService;
import kr.co.strato.portal.workload.v1.service.DeploymentService;
import kr.co.strato.portal.workload.v1.service.JobService;
import kr.co.strato.portal.workload.v1.service.PodService;
import kr.co.strato.portal.workload.v1.service.ReplicaSetService;
import kr.co.strato.portal.workload.v1.service.StatefulSetService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonService implements MLServiceInterface {
	
	private KubernetesClient client;
	
	private CommonAdapterService commonAdapterService;
	
	
	protected KubernetesClient getClient() { 
		if(client == null) {
			client = new DefaultKubernetesClient();
		}
		return client;
	}
	
	public String base64Decoding(String encodedString) {
		return base64Decoding(encodedString, "UTF-8");
	}
	
	public String base64Decoding(String encodedString, String charset) {
		Decoder decoder = Base64.getDecoder();
		byte[] decodedBytes1 = decoder.decode(encodedString.getBytes());
		String decodedString = null;
		try {
			decodedString = new String(decodedBytes1, charset);
		} catch (UnsupportedEncodingException e) {
			log.error("", e);
		}
		return decodedString;
	}
	
	protected Long getMenuCode() {
		Long menuCode = null;
		
		//WORKLOAD
		if(this instanceof DeploymentService) {
			menuCode = 103010L;
		} 
		
		
		else if(this instanceof CronJobService) {
			menuCode = 103040L;
		} else if(this instanceof DaemonSetService) {
			menuCode = 103070L;
		} else if(this instanceof JobService) {
			menuCode = 103050L;
		} else if(this instanceof PodService) {
			menuCode = 103030L;
		} else if(this instanceof ReplicaSetService) {
			menuCode = 103060L;
		} else if(this instanceof StatefulSetService) {
			menuCode = 103020L;
		} 
		
		//CONFIG		
		else if(this instanceof PersistentVolumeClaimService) {
			menuCode = 105010L;
		}
		
		else if(this instanceof ConfigMapService) {
			menuCode = 105020L;
		}
		
		else if(this instanceof SecretService) {
			menuCode = 105030L;
		}
		
		//NETWORKING
		else if(this instanceof IngressService) {
			menuCode = 104020L;
		} else if(this instanceof K8sServiceService) {
			menuCode = 104010L;
		}
		
		return menuCode;
	}

	@Override
	public Long mlResourceApply(Long clusterIdx, Long resourceId, String yaml) {
		commonAdapterService.create(clusterIdx, yaml);
		return -1L;
	}

	@Override
	public boolean delete(Long resourceId, String yaml) {		
		return commonAdapterService.delete(resourceId, yaml);
	}

	@Override
	public Object getEntity(Long resourceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResourceUid(Long resourceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasMetadata getResource(Long resourceId) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
