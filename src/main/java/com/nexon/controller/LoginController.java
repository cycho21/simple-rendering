package com.nexon.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nexon.model.User;

@Controller
public class LoginController {
	
	private String API_SERVER_URL = "http://127.0.0.1:8080/api/v1";

    @RequestMapping("/")
    public String greeting(Model model) {
    	model.addAttribute("user", new User());
        return "simple_chat_login";
    }
    
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> signUp(@ModelAttribute(value = "user") User user) {
    	
    	return null;
//    	return new ResponseEntity<String>(detail, HttpStatus.OK);
    }

}
