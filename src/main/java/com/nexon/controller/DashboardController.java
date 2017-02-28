package com.nexon.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	private Requester requester;
	
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	public String dashBoardMain(Model model, HttpServletRequest request) {
		ArrayList<Chatroom> list = null;
		list = requester.getAllChatroom("chatrooms").getObject();
		model.addAttribute("chatrooms", list);
		
		ArrayList<Chatroom> ownList = null;
		String userid = WebUtils.getCookie(request, "userid").getValue();
		ownList = requester.getOwnChatroom("users/" + userid + "/chatrooms").getObject();
		model.addAttribute("ownchatrooms", ownList);
		
		Response<User> response = requester.get("users/" + userid, User.class, new HttpHeaders());
		model.addAttribute("nickname", response.getObject().getNickname());
		
		ArrayList<User> userList = null;
		userList = requester.getAllUser("users").getObject();
		model.addAttribute("users", userList);
		
		return "dashboard_main";
	}
	
	@RequestMapping(value = "/chatrooms/{chatroomid}/messages", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> postMessage(@ModelAttribute(value = "message") Message message, @PathVariable(value = "chatroomid") int chatroomid, HttpServletRequest request) {
		String userid = WebUtils.getCookie(request, "userid").getValue();
		String sessionid = WebUtils.getCookie(request, "sessionid").getValue();
	
		message.setReceiverid(0);
		message.setSenderid(Integer.parseInt(userid, 10));
		
		Response<Message> response = requester.postWithSessionid("chatrooms/" + chatroomid + "/messages", message, Message.class, sessionid);
		if (response.getStatusCode().equals(HttpStatus.OK))
			return new ResponseEntity<>(response.getObject(), HttpStatus.OK);
		else
			return new ResponseEntity<>(response.getDetail(), response.getStatusCode());
	}
	
	@RequestMapping(value = "/chatrooms/{chatroomid}/messages", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getMessages(@PathVariable(value = "chatroomid") int chatroomid, HttpServletRequest request) {
		String userid = WebUtils.getCookie(request, "userid").getValue();
		String sessionid = WebUtils.getCookie(request, "sessionid").getValue();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("userid", userid);
		Response<SimpleResponse> response = requester.get("chatrooms/" + chatroomid + "/messages", SimpleResponse.class, headers);
		
		ArrayList<Message> messages = response.getObject().getMessages();
		
		return new ResponseEntity<>(messages, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/users/{nickname}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> putUser(@PathVariable(value = "nickname") String nickname, HttpServletRequest request) {
		String userid = WebUtils.getCookie(request, "userid").getValue();
		String sessionid = WebUtils.getCookie(request, "sessionid").getValue();
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
		String userid = WebUtils.getCookie(request, "userid").getValue();
		Response<?> response = requester.delete("chatrooms/" + chatroomid + "/users/" + userid);
		
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			return new ResponseEntity<>("OK", HttpStatus.OK);
		} else {
			return new ResponseEntity<>(response.getDetail(), response.getStatusCode());
		}
	}
	
	@RequestMapping(value = "/chatrooms/{chatroomid}/users", method = RequestMethod.GET)
	public ResponseEntity<?> getUserFromChatroom(@PathVariable(value = "chatroomid") int chatroomid, HttpServletRequest request) {
		String userid = WebUtils.getCookie(request, "userid").getValue();
		String sessionid = WebUtils.getCookie(request, "sessionid").getValue();
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
		String userid = WebUtils.getCookie(request, "userid").getValue();
		User user = new User(); 
		user.setUserid(Integer.parseInt(userid)); 
		Response<Chatroom> response = requester.post("chatrooms/" + chatroomid + "/users", user, Chatroom.class);
		
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			return new ResponseEntity<>(response.getObject(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(response.getDetail(), response.getStatusCode());
		}
	}
	
	
	@RequestMapping(value = "/chatrooms", method = RequestMethod.POST)
	public ResponseEntity<?> createChatroom(@ModelAttribute(value = "chatroom") Chatroom chatroom, HttpServletRequest request, HttpServletResponse response) {
		String sessionId = null;
		
		if (WebUtils.getCookie(request, "sessionid") == null) {
			return new ResponseEntity<String>("Session is end!", HttpStatus.UNAUTHORIZED);
		}
		
		String userid = WebUtils.getCookie(request, "userid").getValue();
		sessionId = WebUtils.getCookie(request, "sessionid").getValue();
		chatroom.setUserid(Integer.parseInt(userid, 10));
		
		Response<Chatroom> responseChatroom = requester.postWithSessionid("chatrooms", chatroom, Chatroom.class, sessionId);
		if (!responseChatroom.getStatusCode().equals(HttpStatus.OK)) {
			return new ResponseEntity<String>(responseChatroom.getDetail(), responseChatroom.getStatusCode());
		} else {
			return new ResponseEntity<Chatroom>(chatroom, responseChatroom.getStatusCode());
		}
	}
	
}
