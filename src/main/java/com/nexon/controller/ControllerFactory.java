package com.nexon.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ControllerFactory {
	
	@Bean
	public Requester requester() {
		Requester requester = new Requester();
		requester.initialize();
		return requester;
	}
	
}
