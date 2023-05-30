package kr.co.strato.portal.addon.adapter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.LoadBalancerIngress;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import kr.co.strato.adapter.k8s.node.service.NodeAdapterService;
import kr.co.strato.adapter.k8s.service.service.ServiceAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.portal.addon.model.Addon;
import kr.co.strato.portal.addon.model.EndPoint;
import kr.co.strato.portal.addon.service.AddonService;

public class ClusterMonitoringAdapter implements AddonAdapter {

	@Override
	public List<HasMetadata> setParameter(List<HasMetadata> resource, Map<String, Object> parameters) {
		String adminUser = (String)parameters.get("grafana-admin-user");
		String adminPassword = (String)parameters.get("grafana-admin-password");
		Integer grafanaNodePort = (Integer)parameters.get("grafana-nodePort");
		
		for(HasMetadata metadata : resource) {
			String kind = metadata.getKind();
			String resName = metadata.getMetadata().getName();
			
			if(adminUser != null && kind.equals("Secret") && resName.equals("grafana-account")) {
				setAdminUser((Secret) metadata, adminUser);
			}
			
			if(adminPassword != null && kind.equals("Secret") && resName.equals("grafana-account")) {
				setAdminPassword((Secret) metadata, adminPassword);
			}
			
			if(grafanaNodePort != null && kind.equals("Service") && resName.equals("grafana")) {
				setGrafanaNodePort((Service) metadata, grafanaNodePort);
			}
			
		}		
		return resource;
	}
	
	@Override
	public void setDetails(AddonService service, ClusterEntity cluster, Addon addon) {
		Long kubeConfigId = cluster.getClusterId();
		kr.co.strato.portal.addon.model.Package pkg = addon.getPackages().stream().filter(p -> p.getName().equals("grafana")).findFirst().get();
		EndPoint endpoint = pkg.getEndpoints().stream().filter(e -> e.getName().equals("grafana-dashboard")).findFirst().get();
		
		if(cluster.getProvider().toLowerCase().equals("kubernetes")) {			
			String namespace = endpoint.getNamespace();
			String serviceName = endpoint.getServiceName();
			
			ServiceAdapterService sService = service.getServiceAdapterService();
			
			Service svc = null;
			HasMetadata d = sService.get(kubeConfigId, namespace, serviceName);
			if(d != null) {
				svc = (Service) d;
				ServicePort servicePort = null;
				
				Optional<ServicePort> op = svc.getSpec().getPorts().stream()
						.filter(p -> p.getNodePort() != null)
						.findFirst();
				if(op.isPresent()) {
					servicePort = op.get();
				}
				
				if(servicePort != null) {
					List<String> endpoints = new ArrayList<>();
					
					String protocol = servicePort.getProtocol();
					Integer nodePort = servicePort.getNodePort();
					String uri = endpoint.getUri();
					
					
					NodeAdapterService nodeService = service.getNodeAdapterService();
					List<String> workerIps = nodeService.getWorkerNodeIps(kubeConfigId);
					for(String ip : workerIps) {
						String end = String.format("http://%s:%d%s", ip, nodePort, uri);
						endpoints.add(end);
					}
					endpoint.setEndpoints(endpoints);
				}
			}
		} else {
			
			//KB 데모
			String eUrl = null;
			if(cluster.getClusterName().equals("vsphere-cluster-demo")) {
				eUrl = "10.10.20.180:30007";
			} else if(cluster.getClusterName().equals("vsphere-cluster-create")) {
				eUrl = "10.10.20.185:30007"; 
			}
			
			if(eUrl != null) {
				
				String url1 = String.format("http://%s/grafana", eUrl);
				
				List<String> endpoints = new ArrayList<>();
				endpoints.add(url1);
				endpoint.setEndpoints(endpoints);
				return;
			}
			
			
			ServiceAdapterService sService = service.getServiceAdapterService();
			io.fabric8.kubernetes.api.model.Service d 
						= sService.get(kubeConfigId, "ingress-nginx", "ingress-nginx-controller");
			if(d != null) {
				Service svc = (Service) d;
				String externalUrl = null;
				List<LoadBalancerIngress> list = svc.getStatus().getLoadBalancer().getIngress();
				if(list != null && list.size() > 0) {
					LoadBalancerIngress loadBalancerIngres = list.get(0);
					externalUrl = loadBalancerIngres.getIp();
					if(externalUrl == null || externalUrl.isEmpty()) {
						externalUrl = loadBalancerIngres.getHostname();
					}
				} else {
					ServicePort servicePort = null;					
					Optional<ServicePort> op = svc.getSpec().getPorts().stream()
							.filter(p -> p.getNodePort() != null && p.getAppProtocol().equals("http"))
							.findFirst();
					if(op.isPresent()) {
						servicePort = op.get();
					}
					
					if(servicePort != null) {				
						Integer nodePort = servicePort.getNodePort();				
						NodeAdapterService nodeService = service.getNodeAdapterService();
						List<String> workerIps = nodeService.getWorkerNodeIps(kubeConfigId);
						for(String ip : workerIps) {
							String end = String.format("%s:%d", ip, nodePort);
							externalUrl = end;
							break;
						}
					}
				}
				String url = String.format("http://%s/grafana", externalUrl);
				
				List<String> endpoints = new ArrayList<>();
				endpoints.add(url);
				endpoint.setEndpoints(endpoints);
			}
			
			
		}
		
	}
	
	/**
	 * Secret에 AdminUser 적용
	 * @param secret
	 * @param adminUser
	 */
	private void setAdminUser(Secret secret, String adminUser) {
		String encodedStr = base64Encoding(adminUser);
		secret.getData().put("admin-user", encodedStr);
	}
	
	private void setAdminPassword(Secret secret, String adminPassword) {
		String encodedStr = base64Encoding(adminPassword);
		secret.getData().put("admin-password", encodedStr);
	}
	
	
	/**
	 * Grafana NodePort 적용
	 * @param service
	 * @param nodePort
	 */
	private void setGrafanaNodePort(Service service, Integer nodePort) {
		Stream<ServicePort> stream = service.getSpec().getPorts().stream()
			.filter(p -> p.getNodePort() != null);
		
		if(stream.findFirst().isPresent()) {
			stream.findFirst()
			.get()
			.setNodePort(nodePort);
			service.getSpec().setType("NodePort");
		}
	}
	
	/**
	 * base64 인코딩.
	 * @param text
	 * @return
	 */
	public String base64Encoding(String text) {
		return base64Encoding(text, "UTF-8");
	}
	
	public String base64Encoding(String text, String charset) {
		String encodedString = null;
		Encoder encoder = Base64.getEncoder();
		try {
			byte[] targetByte = text.getBytes(charset);	
			encodedString = encoder.encodeToString(targetByte);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodedString; 
	}
	
	public static void main(String[] args) {
		Decoder decoder = Base64.getDecoder();
		byte[] bb = decoder.decode("Z3JhZmFuYUluaXRpYWxQYXNzd29yZA==");
		System.out.println(new String(bb));
	}
	
}
