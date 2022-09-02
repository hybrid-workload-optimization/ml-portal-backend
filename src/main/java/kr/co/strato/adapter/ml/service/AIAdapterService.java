package kr.co.strato.adapter.ml.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.ml.model.ForecastDto;
import kr.co.strato.adapter.ml.model.SimulationDto;
import kr.co.strato.adapter.ml.proxy.ForecastProxy;
import kr.co.strato.adapter.ml.proxy.SimulationProxy;

@Service
public class AIAdapterService {

	@Autowired
	private ForecastProxy proxy;
	
	@Autowired
	private SimulationProxy simulationProxy;
	
	
	
	/**
	 * 클러스터 구성 추천
	 * @param param
	 * @return
	 */
    public ForecastDto.ResForecastDto forecast(ForecastDto.ReqForecastDto param) {
    	return proxy.forecast(param);
    }
    

    /**
     * 파드 배치 시뮬레이션
     * @param param
     * @return
     */
    public SimulationDto.ResSimulationDto simulation(SimulationDto.ReqSimulationDto param) {
    	return simulationProxy.simulation(param);
    }
    
}
