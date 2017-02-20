package com.nexon.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.nexon.model.User;


public class Requester {

	private String HOST_PORT_BASE = "http://127.0.0.1:8080/api/v1/";
	private RestTemplate restTemplate;
	
	public static void main(String[] args) {
		Requester req = new Requester();
		req.initialize();
		User user = new User();
		user.setNickname("THISISNEW");
		User user2 = req.put("users/2000", user, User.class);
		System.out.println(user2.getNickname());
	}

	public Requester() {
		super();
	}

	public void initialize() {
		this.restTemplate = new RestTemplate();
	}
	

	public <T> T post(String uri, Object body, Class<T> type) {
		T obj = null;
		try {
			obj = restTemplate.postForObject(HOST_PORT_BASE + uri, body, type);
		} catch (HttpClientErrorException e) {
			System.out.println(e.getResponseBodyAsString());
			return null;
		}
		return obj;
	}
	
	public <T> T get(String uri, Class<T> type, HttpHeaders headers) {
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		T obj = null;
		ResponseEntity<T> responseEntity = null;
		try {
			if (headers == null) {
				obj = restTemplate.getForObject(HOST_PORT_BASE + uri, type);
			} else {
				responseEntity = restTemplate.exchange(HOST_PORT_BASE + uri, HttpMethod.GET, entity, type);
				obj = responseEntity.getBody();
			}
		} catch (HttpClientErrorException e) {
			System.out.println(e.getResponseBodyAsString());
			return null;
		}
		return obj;
	}
	
	public <T> T put(String uri, Object body, Class<T> type) {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Object> entity = new HttpEntity<Object>(body, headers);
		
		T obj = null;
		ResponseEntity<T> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(HOST_PORT_BASE + uri, HttpMethod.PUT, entity, type);
			obj = responseEntity.getBody();
		} catch (HttpClientErrorException e) {
			System.out.println(e.getResponseBodyAsString());
			return null;
		}
		
		return obj;
	}

	public void delete(String uri) {
		restTemplate.delete(uri);
	}
}
