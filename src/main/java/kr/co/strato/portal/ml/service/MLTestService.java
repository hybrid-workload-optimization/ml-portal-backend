package kr.co.strato.portal.ml.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.co.strato.adapter.ml.model.ForecastDto;
import kr.co.strato.adapter.ml.model.NodeSpecDto;
import kr.co.strato.adapter.ml.model.PodSpecDto;
import kr.co.strato.adapter.ml.model.SimulationDto;
import kr.co.strato.adapter.ml.proxy.AzureInterfaceProxy;
import kr.co.strato.adapter.ml.service.AIAdapterService;

@Service
public class MLTestService {

	@Autowired
	private AIAdapterService mlAdapterService;
	
	public ForecastDto.ResForecastDto forecast() {		
		PodSpecDto podSpec = PodSpecDto.builder()
				.name("pod-01")
				.cpu(20f)
				.memory(60f)
				.storage(1500f)
				.gpu(1f)
				.gpuMemory(16f)
				.build();
		
		List<PodSpecDto> list = new ArrayList<>();
		list.add(podSpec);
		
		ForecastDto.ReqForecastDto param = new ForecastDto.ReqForecastDto();
		param.setModel("azure");
		param.setCategory("gpu");
		param.setPodSpec(list);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
    	String json = gson.toJson(param);
    	System.out.println(json);
		
    	return mlAdapterService.forecast(param);
    }
    
    public SimulationDto.ResSimulationDto simulation() {
    	SimulationDto.ReqSimulationDto param = new SimulationDto.ReqSimulationDto();
    	param.setModel("simulation test");
    	
    	List<PodSpecDto> podSpec = new ArrayList<>();
    	
    	PodSpecDto pod1 = new PodSpecDto();
    	pod1.setName("pod-service-01");
    	pod1.setCpu((float) 8);
    	pod1.setMemory((float) 32);
    	pod1.setStorage((float) 500);
    	pod1.setGpu((float) 1);
    	pod1.setGpuMemory((float) 8);
    	
    	PodSpecDto pod2 = new PodSpecDto();
    	pod2.setName("pod-service-02");
    	pod2.setCpu((float) 8);
    	pod2.setMemory((float) 32);
    	pod2.setStorage((float) 500);
    	pod2.setGpu((float) 1);
    	pod2.setGpuMemory((float) 4);
    	
    	
    	NodeSpecDto node = new NodeSpecDto();
    	node.setCpu((float)24);
    	node.setMemory((float)220);
    	node.setStorage((float)1123);
    	node.setProduct("Virtual Machine");
    	node.setCount(2);
    	node.setInstance("NC24ads_A100_v4");
    	node.setName("azure");
    	node.setGpu((float) 1);
    	node.setGpuMemory((float) 16);
    	
    	
    	
    	podSpec.add(pod1);
    	podSpec.add(pod2);
    	
    	
    	param.setPodSpec(podSpec);
    	param.setNodeSpec(node);
    	
    	Gson gson = new GsonBuilder().setPrettyPrinting().create();
    	String json = gson.toJson(param);
    	System.out.println(json);
    	
    	
    	return mlAdapterService.simulation(param);
    }
}
