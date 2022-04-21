package kr.co.strato.portal.alert.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.alert.model.AlertDto;
import kr.co.strato.portal.alert.service.AlertService;
import kr.co.strato.portal.common.controller.CommonController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
public class AlertController extends CommonController {
	
	@Autowired
	AlertService alertService;
	
	/**
	 * 알림 리스트
	 * @return
	 */
	@GetMapping("/alert/list")
    public ResponseWrapper<List<AlertDto>> getAlertList() {
		String userId = getLoginUser().getUserId();
		List<AlertDto> list = alertService.getAlerts(userId);
		return new ResponseWrapper<>(list);
	}
	
	/**
	 * 알림 삭제
	 * @param alertIdx
	 * @return
	 */
	@DeleteMapping("/alert/{alertIdx}")
    public ResponseWrapper<Boolean> deleteAlert(@PathVariable(required = true) Long alertIdx) {
		Boolean isOk = alertService.deleteAlert(alertIdx);
		return new ResponseWrapper<>(isOk);
	}
	
	/**
	 * 알림 확인.
	 * @param alertIdx
	 * @return
	 */
	@PutMapping("/alert/{alertIdx}")
    public ResponseWrapper<Boolean> confirmAlert(@PathVariable(required = true) Long alertIdx) {
		Boolean isOk = alertService.confirm(alertIdx);
		return new ResponseWrapper<>(isOk);
	}
	
	/**
	 * Alert 수신 등록.
	 * @return
	 */
	@GetMapping(path = "/alert/receive", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<AlertDto>> receiveAlert() {
		String userId = getLoginUser().getUserId();
        return Flux.create(sink -> {
        	String loginUserId = userId;
        	
        	alertService.addConsumer(loginUserId, sink::next);
            
            sink.onCancel(() -> {
                System.out.println("** CANCELLED **");
                sink.complete();
            });

	        sink.onDispose(() -> {
	        	alertService.removeConsumer(loginUserId);
	            System.out.println("** DISPOSED **");
	        });
        });
    }
	
	@GetMapping(path = "/alert/test", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<AlertDto>> receiveTest() {
        return Flux.create(sink -> {
        	String loginUserId = "hclee@strato.co.kr";
        	
        	alertService.addConsumer(loginUserId, sink::next);
        	alertService.testSSE();
        	
            sink.onCancel(() -> {
                System.out.println("** CANCELLED **");
                sink.complete();
            });

	        sink.onDispose(() -> {
	        	alertService.removeConsumer(loginUserId);
	            System.out.println("** DISPOSED **");
	        });
        });
    }
}
