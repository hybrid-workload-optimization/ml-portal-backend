package kr.co.strato.portal.common.service;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import kr.co.strato.portal.workload.service.DeploymentService;

public class CommonService {
	
	private KubernetesClient client;
	
	
	protected KubernetesClient getClient() { 
		if(client == null) {
			client = new DefaultKubernetesClient();
		}
		return client;
	}
	
	protected Long getMenuCode() {
		Long menuCode = null;
		
		//WORKLOAD
		if(this instanceof DeploymentService) {
			menuCode = 103010L;
		} 
		
		/*
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
		*/
		return menuCode;
	}
	
}
