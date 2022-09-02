package kr.co.strato.portal.ml.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.portal.common.service.CommonService;
import kr.co.strato.portal.networking.service.K8sServiceService;
import kr.co.strato.portal.workload.service.CronJobService;
import kr.co.strato.portal.workload.service.DaemonSetService;
import kr.co.strato.portal.workload.service.DeploymentService;
import kr.co.strato.portal.workload.service.JobService;
import kr.co.strato.portal.workload.service.PodService;
import kr.co.strato.portal.workload.service.ReplicaSetService;
import kr.co.strato.portal.workload.service.StatefulSetService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ServiceFactory {	
	public static final String RES_TYPE_DEPLOYMENT = "deployment";
	public static final String RES_TYPE_STATEFUL_SET = "statefulSet";
	public static final String RES_TYPE_POD = "pod";	
	public static final String RES_TYPE_CRON_JOB = "cronJob";	
	public static final String RES_TYPE_JOB = "job";	
	public static final String RES_TYPE_REPLICA_SET = "replicaSet";
	public static final String RES_TYPE_DAEMON_SET = "daemonSet";
	
	public static final String RES_TYPE_SERVICE = "service";
	public static final String RES_TYPE_INGRESS = "ingress";
	public static final String RES_TYPE_ENDPOINTS = "endpoints";
	
	public static final String RES_TYPE_PERSISTENT_VOLUME_CLAIM = "pvc";
	public static final String RES_TYPE_CONFIG_MAP = "configMap";
	public static final String RES_TYPE_SECRET = "secret";
	
	public static final String RES_TYPE_SERVICE_ACCOUNT = "serviceAccount";
	public static final String RES_TYPE_ROLE = "role";
	public static final String RES_TYPE_CLUSTER_ROLE = "clusterRole";
	public static final String RES_TYPE_ROLE_BINDING = "roleBinding";
	public static final String RES_TYPE_CLUSTER_ROLE_BINDING = "clusterRoleBinding";
	public static final String RES_TYPE_INGRESS_CLASS = "ingressClass";
	
	
	
	
	
	
	@Autowired
	private CronJobService cronJobService;	
	@Autowired
	private DeploymentService deploymentService;	
	@Autowired
	private JobService jobService;	
	@Autowired
	private PodService podService;	
	@Autowired
	private StatefulSetService statefulSetService;	
	@Autowired
	private ReplicaSetService replicaSetSetService;	
	@Autowired
	private DaemonSetService daemonSetService;
	
	@Autowired
	private K8sServiceService serviceService;
	
	@Autowired
	private CommonService commonService;
	
	/**
	 * ML Interface 공통 서비스 리턴.
	 * @param kind
	 * @return
	 */
	public MLServiceInterface getMLServiceInterface(String kind) {
		MLServiceInterface serviceInterface = null;
		String resourceType = kind.toLowerCase();
		
		if(resourceType.equals(RES_TYPE_JOB.toLowerCase())) {
			serviceInterface = jobService;
		} else if(resourceType.equals(RES_TYPE_DEPLOYMENT.toLowerCase())) {
			serviceInterface = deploymentService;
		} else if(resourceType.equals(RES_TYPE_SERVICE.toLowerCase())) {
			serviceInterface = serviceService;
		}
		
		
		
		
		/*
		else if(resourceType.equals(RES_TYPE_STATEFUL_SET.toLowerCase())) {
			service = statefulSetService;
		} else if(resourceType.equals(RES_TYPE_POD.toLowerCase())) {
			service = podService;
		} else if(resourceType.equals(RES_TYPE_REPLICA_SET.toLowerCase())) {
			service = replicaSetSetService;
		} else if(resourceType.equals(RES_TYPE_DAEMON_SET.toLowerCase())) {
			service = daemonSetService;
		} else if(resourceType.equals(RES_TYPE_INGRESS.toLowerCase())) {
			service = ingressService;
		} else if(resourceType.equals(RES_TYPE_ENDPOINTS.toLowerCase())) {
			service = endpointsService;
		} else if(resourceType.equals(RES_TYPE_PERSISTENT_VOLUME_CLAIM.toLowerCase())) {
			service = pvcService;
		} else if(resourceType.equals(RES_TYPE_CONFIG_MAP.toLowerCase())) {
			service = configMapService;
		} else if(resourceType.equals(RES_TYPE_SECRET.toLowerCase())) {
			service = secretService;
		} else if(resourceType.equals(RES_TYPE_SERVICE_ACCOUNT.toLowerCase())) {
			service = serviceAccountService;
		} else if(resourceType.equals(RES_TYPE_ROLE.toLowerCase())) {
			service = roleService;
		} else if(resourceType.equals(RES_TYPE_CLUSTER_ROLE.toLowerCase())) {
			service = clusterRoleService;
		} else if(resourceType.equals(RES_TYPE_ROLE_BINDING.toLowerCase())) {
			service = roleBindingService;
		} else if(resourceType.equals(RES_TYPE_CLUSTER_ROLE_BINDING.toLowerCase())) {
			service = clusterRoleBindingService;
		} else if(resourceType.equals(RES_TYPE_INGRESS_CLASS.toLowerCase())) {
			service = ingressClassService;
		}
		*/
		
		if(serviceInterface == null) {
			log.error("지원되는 서비스가 존재하지 않아 공통 서비스로 대체 됩니다.");
			log.error("kind: {}", kind);
			serviceInterface = commonService;
		}
		return serviceInterface;
	}
	
	
}
