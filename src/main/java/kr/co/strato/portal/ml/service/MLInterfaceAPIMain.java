package kr.co.strato.portal.ml.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import kr.co.strato.global.util.Base64Util;

public class MLInterfaceAPIMain {

	public static void main(String[] args) {
		int min = 10000;
		int max = 99999;
		int random = (int) ((Math.random() * (max - min)) + min);
		System.out.println(random);
		
		/*
		File f = new File("C:\\Users\\sptek\\Desktop\\ml.yaml");
		
		Yaml yaml = new Yaml();
		InputStream is = null;
		try {
			is = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		KubernetesClient client = new DefaultKubernetesClient();
		List<HasMetadata> resoures = client.load(is).get();
		
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		for(HasMetadata h : resoures) {
			
			String json = gson.toJson(h);
			System.out.println(json);
			
			getHWSpec(h);
		}
		
		
		client.close();
		*/
		
		/*
		for(Object object : yaml.loadAll(is)) {
			if(object instanceof Map) {
				Map<String, Object> map = (Map<String, Object>) object;
				String kind = null;
				if(map.get("kind") != null) {
					kind = (String)map.get("kind");
				}
				
				String output = yaml.dump(map);
			    System.out.println(output);
				
				String path = "/spec/template/spec/containers/resources";				
				Map<String, Object> resources = getResource(path, map);
				
				
			    
			    //System.out.println(map.get("kind"));
			    System.out.println("------------------------------------------");
			} else {
				System.out.println("No Map...!!!");
			}
		}
		*/
	}
	
	private static Map<String, Float[]> getHWSpec(HasMetadata h) {
		PodSpec podSpec = null;
		if(h instanceof Deployment) {
			Deployment deployment  = (Deployment) h;
			podSpec = deployment.getSpec().getTemplate().getSpec();
		} else if(h instanceof Job) {
			Job job = (Job) h;
			podSpec = job.getSpec().getTemplate().getSpec();
		}
		
		Map<String, Float[]> map = new HashMap<>();
		if(podSpec != null) {
			String kind = h.getKind();
			String resName = h.getMetadata().getName();
			
			
			
			List<Container> containers =  podSpec.getContainers();
			for(Container c : containers) {
				String containerName = c.getName();
				ResourceRequirements resReq = c.getResources();
				
				Map<String, Quantity> request = resReq.getRequests();
				Map<String, Quantity> limits = resReq.getLimits();
				
				Quantity rCpu = request.get("cpu");
				Quantity rMemory = request.get("memory");
				Quantity rStorage = request.get("ephemeral-storage");
				
				Quantity lCpu = limits.get("cpu");
				Quantity lMemory = limits.get("memory");
				Quantity lStorage = limits.get("ephemeral-storage");
				
				//Default값 설정(0.5Core, 1GB, 10GB)
				//1Core
				Float defaultCpu = 0.5f;
				Float defaultMemory = 1.0f;
				Float defaultStorage = 10f;
				Float defaultGpu = 1f;
				Float defaultGpuMemory = 1f;
				
				Float cpu = getCpuLimits(rCpu, lCpu, defaultCpu);
				Float memory = getMemStorageLimits(rMemory, lMemory, defaultMemory);
				Float storage = getMemStorageLimits(rStorage, lStorage, defaultStorage);
				
				Float[] spec = new Float[5];
				spec[0] = cpu;
				spec[1] = memory;
				spec[2] = storage;
				spec[3] = defaultGpu;
				spec[4] = defaultGpuMemory;
				
				map.put(containerName, spec);
			}
		}
		return map;
	}
	
	/**
	 * CPU 요구사항을 구해 반환 한다.
	 * limits이 존재하는 경우 limits의 값을 구해 리턴.
	 * limits이 null 이고 request가 존재하는 경우 디폴트 cpu 사양과 비교를 하여 request가 큰 경우 request * 2를 한다.
	 * request, limits이 null인 경우 디폴트 cpu 사양을 반환.
	 * @param request
	 * @param limits
	 * @return
	 */
	private static float getCpuLimits(Quantity request, Quantity limits, float defaultValue) {
		//default cpu
		float cpu = defaultValue;
		if(limits != null) {
			BigDecimal bCpu = Quantity.getAmountInBytes(limits);
			cpu = bCpu.floatValue();
			
		} else {
			if(request != null) {
				//request cpu가 존재하는 경우 디폴트 cpu 값과 비교한다.
				BigDecimal bCpu = Quantity.getAmountInBytes(request);
				float reqCpu = bCpu.floatValue();
				if(cpu < reqCpu) {
					//디폴트 cpu 사양이 요구 사항보다 작은 경우 요구사항 *2를 한다.
					cpu = reqCpu * 2;
				}
			}
		}
		return cpu;
	}
	
	/**
	 * Memory, Storage 요구사항을 구해 GB 단위로 변환하여 반환 한다.
	 * limits이 존재하는 경우 limits의 값을 구해 리턴.
	 * limits이 null 이고 request가 존재하는 경우 디폴트 사양과 비교를 하여 request가 큰 경우 request * 2를 한다.
	 * request, limits이 null인 경우 디폴트 cpu 사양을 반환.
	 * @param request
	 * @param limits
	 * @return
	 */
	private static float getMemStorageLimits(Quantity request, Quantity limits, float defaultValue) {
		//default cpu
		float value = defaultValue;
		if(limits != null) {
			BigDecimal bCpu = Quantity.getAmountInBytes(limits);
			value = bCpu.floatValue();
			
		} else {
			if(request != null) {
				//request cpu가 존재하는 경우 디폴트 cpu 값과 비교한다.
				BigDecimal bCpu = Quantity.getAmountInBytes(request);
				float reqCpu = bCpu.floatValue();
				if(value < reqCpu) {
					//디폴트 cpu 사양이 요구 사항보다 작은 경우 요구사항 *2를 한다.
					value = reqCpu * 2;
				}
			}
		}
		
		//GB 단위로 변경
		float i = (float)(value / (1024.0 * 1024.0 * 1024.0));
		return i;
	}
}
