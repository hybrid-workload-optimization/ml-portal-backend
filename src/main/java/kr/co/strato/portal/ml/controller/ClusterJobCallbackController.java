package kr.co.strato.portal.ml.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.portal.ml.model.CallbackData;

@RestController
public class ClusterJobCallbackController {

	@PostMapping("/api/v1/clusterJob/callback")
	public void callback(@RequestBody CallbackData data) { 
		
	}
}
