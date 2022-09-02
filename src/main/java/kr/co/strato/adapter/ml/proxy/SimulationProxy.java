package kr.co.strato.adapter.ml.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.adapter.ml.model.SimulationDto;

@FeignClient(value="ml-simulation", url = "${ml.ai.simulation}")
public interface SimulationProxy {

    @PostMapping("/v1/simulation")
    public SimulationDto.ResSimulationDto simulation(@RequestBody SimulationDto.ReqSimulationDto param);
}
