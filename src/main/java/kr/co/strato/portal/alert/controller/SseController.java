package kr.co.strato.portal.alert.controller;


import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.portal.alert.model.AlertDto;
import kr.co.strato.portal.alert.service.AlertService;
import kr.co.strato.portal.common.controller.CommonController;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/sse/v1")
public class SseController extends CommonController {
	
	@Autowired
	AlertService alertService;
	
	/**
	 * Alert 수신 등록.
	 * @return
	 */
	@GetMapping(path = "/alert/receive", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<AlertDto>> receiveAlert(@RequestParam String userId) {
        return Flux.create(sink -> {
        	String loginUserId = userId;
        	
        	log.info("SSE - registry");
        	log.info("SSE - UserId: {}", loginUserId);
        	
        	Consumer<ServerSentEvent<AlertDto>> consumer = sink::next;
        	AlertService.addConsumer(loginUserId, consumer);
            
            sink.onCancel(() -> {
            	log.info("SSE - canceled.");
            	log.info("SSE - UserId: {}", loginUserId);
                sink.complete();
            });

	        sink.onDispose(() -> {
	        	log.info("SSE - disposed.");
            	log.info("SSE - Remove consumer userId: {}", loginUserId);
	        	AlertService.removeConsumer(loginUserId, consumer);
	        });
        });
    }
	
	@GetMapping(path = "/alert/send/test")
	public void sendTest() {
		alertService.sendTest();
	}
}
