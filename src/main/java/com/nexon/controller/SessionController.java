package com.nexon.controller;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.WebUtils;

import com.nexon.model.Response;
import com.nexon.model.User;

@Controller
public class SessionController {
	@Autowired
	private Requester requester;

	@RequestMapping("/")
	public String displayLoginView(Model model, HttpServletRequest request) {
		model.addAttribute("user", new User());
		return "simple_chat_login";
	}
	
	@RequestMapping(value = "/signout", method = RequestMethod.POST)
	public ResponseEntity<?> signOut(HttpServletRequest request, HttpServletResponse response) {
		String sessionId = null;
		System.out.println(WebUtils.getCookie(request, "sessionid").getValue() + " ????");
		if (WebUtils.getCookie(request, "sessionid") != null) {
			sessionId = WebUtils.getCookie(request, "sessionid").getValue();
		} else {
			return new ResponseEntity<String>("You are not logged in", HttpStatus.UNAUTHORIZED);
		}
		Response<String> resp = requester.signOut("users/signout", sessionId);
		
		if (resp.getStatusCode().equals(HttpStatus.OK)) {
			return new ResponseEntity<String>("Sing out!", HttpStatus.OK);
		} else {
			request.getSession().invalidate();
			return new ResponseEntity<String>(resp.getDetail(), resp.getStatusCode());
		}
	}
	
	@RequestMapping(value = "/signin", method = RequestMethod.POST)
	public ResponseEntity<?> signIn(@ModelAttribute(value = "user") User user, HttpServletRequest request, HttpServletResponse servletResponse) {
		Response<User> response = new Response<User>();
		
		response = requester.signIn("users/signin", user, User.class);
		
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			return new ResponseEntity<String>(response.getDetail(), response.getStatusCode());
		} else {
			Cookie cookie = new Cookie("sessionid", response.getSessionId());
			cookie.setMaxAge(60*60*24);
			servletResponse.addCookie(cookie);
			cookie = new Cookie("userid", String.valueOf(response.getObject().getUserid()));
			servletResponse.addCookie(cookie);
			cookie.setMaxAge(60*60*24);
			return new ResponseEntity<String>("Logged in!", HttpStatus.OK);
		}
	}
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ResponseEntity<?> signUp(@ModelAttribute(value = "user") User user, HttpServletRequest request, HttpServletResponse servletResponse) {
		Response<User> response = new Response<User>();

		response = requester.post("users", user, User.class);
		
		if (response.getObject() != null) {
			response = requester.post("users/signin", user, User.class);
			
			if (!response.getStatusCode().equals(HttpStatus.OK)) {
				return new ResponseEntity<String>(response.getDetail(), response.getStatusCode());
			} else {
				servletResponse.addCookie(new Cookie("JSESSIONID", response.getSessionId()));
				return new ResponseEntity<String>("Logged in!", HttpStatus.OK);
			}
		} else {
			return new ResponseEntity<String>(response.getDetail(), response.getStatusCode());
		}
	}

}
