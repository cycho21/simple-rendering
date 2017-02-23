package com.nexon.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexon.model.User;

@RestController
@RequestMapping("/api/v1/chatrooms")
public class ChatroomController {
	
	@RequestMapping("/")
	public String displayLoginView(Model model, HttpServletRequest request) {
		model.addAttribute("user", new User());
		return "simple_chat_login";
	}
}
