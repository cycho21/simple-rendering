package com.nexon.websocket;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.nexon.controller.Requester;
import com.nexon.model.Message;
import com.nexon.model.Response;

@Controller
public class WebSocketController {
	
	@Autowired
	private SimpMessagingTemplate template;
	
	@Autowired
	private Requester requester;
	
	@MessageMapping("/")
	public void test(String message) {
		template.convertAndSend("/topic/test", "THIS IS TEST!");
	}
	
	@MessageMapping("/chatrooms/{chatroomid}/messages")
	public void postMessage(@DestinationVariable(value = "chatroomid") int chatroomid, Message message, MessageHeaders headers) {
		Map<String, String> cookie = (Map<String, String>) headers.get("simpSessionAttributes");
		
		String userid = cookie.get("userid");
		String sessionid = cookie.get("sessionid");
		
		message.setSenderid(Integer.parseInt(userid, 10));
		Response<Message> response = requester.postWithSessionid("chatrooms/" + chatroomid + "/messages", message, Message.class, sessionid);
		if (response.getStatusCode().equals(HttpStatus.OK))
			template.convertAndSend("/chatrooms/" + chatroomid, response.getObject());
	}
	
	@MessageMapping("/chatrooms")
	public void postChatroom() {
		
	}
}
