package kr.co.strato.portal.ml.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.ml.model.CallbackData;
import kr.co.strato.portal.ml.service.ClusterJobCallbackService;

@RestController
public class ClusterJobCallbackController {
	
	@Autowired
	private ClusterJobCallbackService callbackService;

	@PostMapping("/api/v1/clusterJob/callback")
	public ResponseWrapper<Void> callback(@RequestBody CallbackData data) { 
		callbackService.callback(data);
		return new ResponseWrapper<Void>();
	}
}
