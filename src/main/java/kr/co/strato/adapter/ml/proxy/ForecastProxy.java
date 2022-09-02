package kr.co.strato.adapter.ml.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.adapter.ml.model.ForecastDto;

@FeignClient(value="ml-forecast", url = "${ml.ai.forecast}")
public interface ForecastProxy {

    @PostMapping("/v1/forecast")
    public ForecastDto.ResForecastDto forecast(@RequestBody ForecastDto.ReqForecastDto param);
}
