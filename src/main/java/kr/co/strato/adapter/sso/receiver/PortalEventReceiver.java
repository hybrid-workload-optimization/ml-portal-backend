package kr.co.strato.adapter.sso.receiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import kr.co.strato.adapter.sso.model.MessageModel;
import kr.co.strato.adapter.sso.service.PortalEventService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PortalEventReceiver {
	
	@Autowired
	private PortalEventService eventService;
	
    public static final String TYPE_USER = "user";
    public static final String TYPE_ROLE = "role";
    public static final String TYPE_COMPANY = "company";
    public static final String TYPE_SERVICE_GROUP = "service_group";
    
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
		
		Gson gson = new Gson();
		MessageModel messageObj = gson.fromJson(message, MessageModel.class);
		String typeName = messageObj.getType();
		
		if(TYPE_USER.equals(typeName)) {
			
			eventService.userEvent(messageObj);
		
		} else if(TYPE_ROLE.equals(typeName)) {
			
			eventService.roleEvent(messageObj);
			
		} else if(TYPE_COMPANY.equals(typeName)) {
			
		} else if(TYPE_SERVICE_GROUP.equals(typeName)) {
			
		}
		
	}
	
}
