package kr.co.strato.adapter.sso.receiver;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PortalEventReceiver {
	
	/**
	 * Portal에서 발생하는 Event 수신
	 * @param message
	 */
	@KafkaListener(
			topics = "${strato.portal.event}", 
			groupId = "${plugin.kafka.paas-portal.consumer.group}", 
			containerFactory = "kafkaListenerContainerFactory")
    public void eventReceiver(String message) {
		log.info(message);
	}
	
}
