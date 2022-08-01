package kr.co.strato.portal.machineLearning.service;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.machineLearning.model.MLDto;

@Service
public class CallbackService {
	
	private static final Logger logger = LoggerFactory.getLogger(CallbackService.class);
	
	private final long RETRY_SLEEP_MILLIS = 20000;	// 2 초
	private final long RETRY_COUNT = 3;	// 6번.
	
	@Autowired
	@Qualifier("restService")
	RestTemplate restService;
	
	
	
	private <T> ResponseEntity<String> sendCallbackPost(String url , T param) {
		return  restService.postForEntity(url, param, String.class);
		
	}
	
	//  BIZ에 callback를 던질때 BIZ에서 동기화중이면 deadlock이 발생하는 이슈가 있었음.
	//  callback 실패시 여러번 callback를 보내도록 수정함.
	//  동기화가 끝나는 시간은 대략 1분 30초 정도. 총 3분 정도 retry를 한다.
	protected void sendCallback(String callBackUrl, ResponseWrapper<MLDto.Detail> result) {
		try {
			String mlId = result.getResult().getMlId();
			
			for(int cnt = 0; cnt < RETRY_COUNT; cnt++) {	
				try {
					logger.info("[Send Callback MLId :"+ mlId  +"] >>> URL: " + callBackUrl);
					logger.info("[Send Callback MLId :"+ mlId +"] >>> ResultCode: " + result.getCode());
					logger.debug("[Send Callback MLId :"+ mlId +"] >>> ResultBody: "+ new Gson().toJson(result).toString());
					
					URI url = new URI(callBackUrl);
					ResponseEntity<String> resp = sendCallbackPost(url.toString(), result);
					String status 	= resp.getStatusCode().toString(); 
		
					logger.info("[Send Callback MLId Result:"+ mlId +"] >>> Resp status: " + status );		
					break;
				} catch(Exception e){
					logger.error("[Send Callback MLId :"+ mlId +"] >>> SEND CALLBACK FAIL : Exception " + e.getMessage()+" retry cnt = "+cnt);
					logger.info("[retry -sleep({}msec) Send Callback MLId : {}, url : {}", RETRY_SLEEP_MILLIS, mlId, callBackUrl);
					Thread.sleep(RETRY_SLEEP_MILLIS);	
				}
			}
		} catch(Exception ex) {
			logger.error("retry all fail.. callback:{}", callBackUrl);
			logger.error(ex.getMessage(), ex.fillInStackTrace());
		}
	}
	

}
