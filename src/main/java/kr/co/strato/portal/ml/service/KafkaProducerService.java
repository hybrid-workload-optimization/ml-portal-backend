package kr.co.strato.portal.ml.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
	
    private final KafkaTemplate<String, Object> kafkaTemplate;	

    public void sendMessage(String topic, String message) {
    	try {
            kafkaTemplate.send(topic, message);
            log.info("Message: " + message + " sent to topic: " + topic);
    	} catch(Exception e) {
    		log.error("", e);
    	}
    }
	
}
