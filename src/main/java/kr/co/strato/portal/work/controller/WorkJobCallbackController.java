package kr.co.strato.portal.work.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.work.model.WorkJobCallback;
import kr.co.strato.portal.work.service.WorkJobCallbackService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class WorkJobCallbackController {

	@Autowired
	WorkJobCallbackService workJobCallbackService;
	
	
	@PostMapping("/api/v1/work-job/callback")
    public ResponseWrapper<Void> callbackWorkJob(@RequestBody WorkJobCallback<Map<String, Object>> workJobCallback){
        workJobCallbackService.callbackWorkJob(workJobCallback);
        return new ResponseWrapper<Void>();
    }
	
}
