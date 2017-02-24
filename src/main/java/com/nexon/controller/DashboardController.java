package com.nexon.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nexon.model.Chatroom;

@Controller
public class DashboardController {
	
	@Autowired
	private Requester requester;
	
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	public String dashBoardMain(HttpServletRequest request) {
		
		return "dashboard_main";
	}
	
	@RequestMapping("/dashboard/test")
	public String listChatroom(Model model) {
		List<Chatroom> list = new ArrayList<Chatroom>();
		
		for (int i = 0; i < 10; ++i) {
			Chatroom chatroom = new Chatroom();
			chatroom.setChatroomid(i);
			chatroom.setChatroomname(i + " ASDF ");
			list.add(chatroom);
		}
		
		model.addAttribute("chatrooms", list);
		
		return "dashboard_main";
	}
	
	@RequestMapping("/dashboard/test2")
	public String listChatroom2(Model model) {
		ArrayList<Chatroom> list = new ArrayList<Chatroom>();
		ArrayList<Chatroom> dummy = new ArrayList<Chatroom>();
		
		for (int i = 0; i < 10; ++i) {
			Chatroom chatroom = new Chatroom();
			chatroom.setChatroomid(i);
			chatroom.setChatroomname(i + " FDSA ");
			dummy.add(chatroom);
		}
		list = requester.getAllChatroom("chatrooms").getObject();
		model.addAttribute("dummy", dummy);
		model.addAttribute("chatrooms", list);
		
		return "dashboard_main";
	}
	
	
}
