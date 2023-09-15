package kr.co.strato.portal.ml.v1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.ml.v1.model.CallbackData;
import kr.co.strato.portal.ml.v1.service.ClusterJobCallbackService;

//@RestController
@Api(tags = {"Cluster Job Callback API(Async 응답 전용)"})
public class ClusterJobCallbackController {
	
	@Autowired
	private ClusterJobCallbackService callbackService;

	@PostMapping("/api/v1/clusterJob/callback")
	public ResponseWrapper<Void> callback(@RequestBody CallbackData data) { 
		callbackService.callback(data);
		return new ResponseWrapper<Void>();
	}
}
