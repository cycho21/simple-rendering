package com.nexon.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String dashBoardMain(HttpServletRequest request) {
		return "dashboard_main";
	}
	
	
}
