package com.nexon.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import com.nexon.model.Chatroom;
import com.nexon.model.Message;
import com.nexon.model.Response;
import com.nexon.model.SimpleResponse;
import com.nexon.model.User;

@Controller
public class DashboardController {
	
	@Autowired
	private SimpMessagingTemplate template;
	
	@Autowired
	private Requester requester;
	
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	public String dashBoardMain(Model model, HttpServletRequest request) {
		ArrayList<Chatroom> list = null;
		list = requester.getAllChatroom("chatrooms").getObject();
		model.addAttribute("chatrooms", list);
		
		ArrayList<Chatroom> ownList = null;
		
		String sessionid = null;
		String userid = null;
		
		if (WebUtils.getCookie(request, "sessionid") == null || WebUtils.getCookie(request, "userid") == null)
			return "redirect:simple_chat_login";
		
		sessionid = WebUtils.getCookie(request, "sessionid").getValue();
		userid = WebUtils.getCookie(request, "userid").getValue();
		
		ownList = requester.getOwnChatroom("users/" + userid + "/chatrooms").getObject();
		model.addAttribute("ownchatrooms", ownList);
		
		Response<User> response = requester.get("users/" + userid, User.class, new HttpHeaders());
		
		model.addAttribute("nickname", response.getObject().getNickname() == null ? "UnknowUser" : response.getObject().getNickname());
		model.addAttribute("userid", response.getObject().getUserid() == 0 ? "0" : response.getObject().getUserid());
		
		ArrayList<User> userList = requester.getAllUser("users").getObject();
		model.addAttribute("users", userList);
		
		return "dashboard_main";
	}
	
	@RequestMapping(value = "/chatrooms/{chatroomid}/messages", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getMessages(@PathVariable(value = "chatroomid") int chatroomid, HttpServletRequest request) {
		String sessionid = null;
		String userid = null;
		
		if (WebUtils.getCookie(request, "sessionid") == null || WebUtils.getCookie(request, "userid") == null)
			return new ResponseEntity<>("NOT AUTHORIZED", HttpStatus.UNAUTHORIZED);
		
		sessionid = WebUtils.getCookie(request, "sessionid").getValue();
		userid = WebUtils.getCookie(request, "userid").getValue();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("userid", userid);
		headers.add("sessionid", sessionid);
		Response<SimpleResponse> response = requester.get("chatrooms/" + chatroomid + "/messages", SimpleResponse.class, headers);
		
		ArrayList<Message> messages = response.getObject().getMessages();
		
		return new ResponseEntity<>(messages, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/chatrooms/{chatroomid}/messages/whisper", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getWhisperMessage(@PathVariable(value= "chatroomid") int chatroomid, HttpServletRequest request) {
		String sessionid = null;
		String userid = null;
		
		if (WebUtils.getCookie(request, "sessionid") == null || WebUtils.getCookie(request, "userid") == null)
			return new ResponseEntity<>("NOT AUTHORIZED", HttpStatus.UNAUTHORIZED);
		
		sessionid = WebUtils.getCookie(request, "sessionid").getValue();
		userid = WebUtils.getCookie(request, "userid").getValue();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("userid", userid);
		headers.add("sessionid", sessionid);
		
		Response<SimpleResponse> response = requester.get("chatrooms/" + chatroomid + "/messages/whisper", SimpleResponse.class, headers);
		ArrayList<Message> messages = response.getObject().getMessages();
		
		return new ResponseEntity<>(messages, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/chatrooms/{chatroomid}/messages/whisper/{receiverid}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getPrivateWhisper(@PathVariable(value= "chatroomid") int chatroomid, @PathVariable(value= "receiverid") int receiverid, HttpServletRequest request) {
		String sessionid = null;
		String userid = null;
		
		if (WebUtils.getCookie(request, "sessionid") == null || WebUtils.getCookie(request, "userid") == null)
			return new ResponseEntity<>("NOT AUTHORIZED", HttpStatus.UNAUTHORIZED);
		
		sessionid = WebUtils.getCookie(request, "sessionid").getValue();
		userid = WebUtils.getCookie(request, "userid").getValue();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("userid", userid);
		headers.add("sessionid", sessionid);
		
		Response<SimpleResponse> response = requester.get("chatrooms/" + chatroomid + "/messages/whisper", SimpleResponse.class, headers);
		ArrayList<Message> messages = response.getObject().getMessages();
		
		ArrayList<Message> retMessages = new ArrayList<Message>();
		for (int i = 0; i < messages.size(); ++i) {
			Message message = messages.get(i);
			if (message.getSenderid() == Integer.parseInt(userid, 10) && message.getReceiverid() == receiverid) {
				retMessages.add(message);
				continue;
			}
			if (message.getSenderid() == receiverid && message.getReceiverid() == Integer.parseInt(userid, 10)) {
				retMessages.add(message);
				continue;
			}
		}
		
		return new ResponseEntity<>(retMessages, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/users/{nickname}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> putUser(@PathVariable(value = "nickname") String nickname, HttpServletRequest request) {
		String sessionid = null;
		String userid = null;
		
		if (WebUtils.getCookie(request, "sessionid") == null || WebUtils.getCookie(request, "userid") == null)
			return new ResponseEntity<>("NOT AUTHORIZED", HttpStatus.UNAUTHORIZED);
		
		sessionid = WebUtils.getCookie(request, "sessionid").getValue();
		userid = WebUtils.getCookie(request, "userid").getValue();
		
		User user = new User();
		user.setNickname(nickname);
		Response<User> response = requester.put("users/" + userid, user, User.class, sessionid);
		if (response.getStatusCode().equals(HttpStatus.OK))
			return new ResponseEntity<>(response.getObject(), HttpStatus.OK);
		else
			return new ResponseEntity<>(response.getDetail(), response.getStatusCode());
	}
	
	@RequestMapping(value = "/chatrooms/{chatroomid}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<?> quitChatroom(@PathVariable(value = "chatroomid") int chatroomid, HttpServletRequest request) {
		String sessionid = null;
		String userid = null;
		
		if (WebUtils.getCookie(request, "sessionid") == null || WebUtils.getCookie(request, "userid") == null)
			return new ResponseEntity<>("NOT AUTHORIZED", HttpStatus.UNAUTHORIZED);
		
		sessionid = WebUtils.getCookie(request, "sessionid").getValue();
		userid = WebUtils.getCookie(request, "userid").getValue();
		
		Response<?> response = requester.quitChatroom("chatrooms/" + chatroomid + "/users/" + userid, sessionid);
		Chatroom chatroom = new Chatroom();
		chatroom.setChatroomid(chatroomid);
		
		if (response.getObject() == null) {
			chatroom.setIsexist(true);
		} else { 
			chatroom.setIsexist(false);
			template.convertAndSend("/chatrooms/change", chatroom);
		}
		
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			return new ResponseEntity<>("OK", HttpStatus.OK);
		} else {
			return new ResponseEntity<>(response.getDetail(), response.getStatusCode());
		}
	}
	
	@RequestMapping(value = "/chatrooms/{chatroomid}/users", method = RequestMethod.GET)
	public ResponseEntity<?> getUserFromChatroom(@PathVariable(value = "chatroomid") int chatroomid, HttpServletRequest request) {
		String sessionid = null;
		String userid = null;
		
		if (WebUtils.getCookie(request, "sessionid") == null || WebUtils.getCookie(request, "userid") == null)
			return new ResponseEntity<>("NOT AUTHORIZED", HttpStatus.UNAUTHORIZED);
		
		sessionid = WebUtils.getCookie(request, "sessionid").getValue();
		userid = WebUtils.getCookie(request, "userid").getValue();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("sessionid", sessionid);
		headers.add("userid", userid);
		Response<SimpleResponse> response = requester.get("chatrooms/" + chatroomid + "/users", SimpleResponse.class, headers);
		if (response.getStatusCode().equals(HttpStatus.OK))
			return new ResponseEntity<>(response.getObject().getUsers(), HttpStatus.OK);
		else
			return new ResponseEntity<>(response.getDetail(), response.getStatusCode());
	}
	

	@RequestMapping(value = "/chatrooms/{chatroomid}/users", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> joinChatroom(Message message, @PathVariable(value = "chatroomid") int chatroomid, HttpServletRequest request) {
		String sessionid = null;
		String userid = null;
		
		if (WebUtils.getCookie(request, "sessionid") == null || WebUtils.getCookie(request, "userid") == null)
			return new ResponseEntity<>("NOT AUTHORIZED", HttpStatus.UNAUTHORIZED);
		
		sessionid = WebUtils.getCookie(request, "sessionid").getValue();
		userid = WebUtils.getCookie(request, "userid").getValue();
		
		User user = new User(); 
		user.setUserid(Integer.parseInt(userid));
		
		Response<Chatroom> response = requester.postWithSessionid("chatrooms/" + chatroomid + "/users", user, Chatroom.class, sessionid);
		
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			return new ResponseEntity<>(response.getObject(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(response.getDetail(), response.getStatusCode());
		}
	}
	
	
	@RequestMapping(value = "/chatrooms", method = RequestMethod.POST)
	public ResponseEntity<?> createChatroom(@ModelAttribute(value = "chatroom") Chatroom chatroom, HttpServletRequest request, HttpServletResponse response) {
		String sessionid = null;
		String userid = null;
		
		if (WebUtils.getCookie(request, "sessionid") == null || WebUtils.getCookie(request, "userid") == null)
			return new ResponseEntity<>("NOT AUTHORIZED", HttpStatus.UNAUTHORIZED);
		
		sessionid = WebUtils.getCookie(request, "sessionid").getValue();
		userid = WebUtils.getCookie(request, "userid").getValue();
		
		chatroom.setUserid(Integer.parseInt(userid, 10));
		
		Response<Chatroom> responseChatroom = requester.postWithSessionid("chatrooms", chatroom, Chatroom.class, sessionid);
		
		Chatroom retChat = responseChatroom.getObject();
		retChat.setChatroomname(chatroom.getChatroomname());
		retChat.setIsexist(true);
		
		template.convertAndSend("/chatrooms/change", retChat);
		
		if (!responseChatroom.getStatusCode().equals(HttpStatus.OK)) {
			return new ResponseEntity<String>(responseChatroom.getDetail(), responseChatroom.getStatusCode());
		} else {
			return new ResponseEntity<Chatroom>(retChat, responseChatroom.getStatusCode());
		}
	}
	
}
