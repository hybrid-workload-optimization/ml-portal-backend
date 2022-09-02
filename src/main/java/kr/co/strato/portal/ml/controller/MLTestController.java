package kr.co.strato.portal.ml.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.adapter.ml.model.ForecastDto;
import kr.co.strato.adapter.ml.model.SimulationDto;
import kr.co.strato.portal.ml.service.MLTestService;

@RestController
public class MLTestController {
	
	@Autowired
	private MLTestService mlTestService;

	@PostMapping("/v1/forecast")
    public ForecastDto.ResForecastDto forecast() {
		return mlTestService.forecast();
	}
    

    @PostMapping("/v1/simulation")
    public SimulationDto.ResSimulationDto simulation() {    	
    	return mlTestService.simulation();
    }
}
