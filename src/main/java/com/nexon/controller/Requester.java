package com.nexon.controller;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.nexon.model.Response;

public class Requester {

	private String HOST_PORT_BASE = "http://10.10.44.71:8080/api/v1/";
	private RestTemplate restTemplate;
	
	public static void main(String[] args) {
		Requester req = new Requester();
		req.initialize();
	}

	public Requester() {
		super();
	}

	public void initialize() {
		this.restTemplate = new RestTemplate();
		JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        
        try {
            jsonObject = (JSONObject) jsonParser.parse(new FileReader(System.getProperty("user.dir") + "/config.json"));
        } catch (IOException e) {
        } catch (ParseException e) {
        }
        this.HOST_PORT_BASE = (String) jsonObject.get("host") + ":" + Integer.parseInt(String.valueOf(jsonObject.get("port"))) + (String) jsonObject.get("baseurl");
        System.out.println(HOST_PORT_BASE);
	}
	
	public Response<String> signOut(String uri, String jSessionId) {
		Response<String> response = new Response<String>(); 
		HttpHeaders headers = new HttpHeaders();
		headers.add("JSESSIONID", jSessionId);
		
		HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(HOST_PORT_BASE + uri, HttpMethod.POST, httpEntity, String.class);
		
		response.setStatusCode(responseEntity.getStatusCode());
		response.setDetail(responseEntity.getBody());
		return response;
	}
	

	public <T> Response<T> post(String uri, Object body, Class<T> type) {
		Response<T> response = new Response<T>();
		T obj = null;
		try {
			obj = restTemplate.postForObject(HOST_PORT_BASE + uri, body, type);
		} catch (HttpClientErrorException e) {
			response.setStatusCode(e.getStatusCode());
			response.setDetail(e.getResponseBodyAsString());
			return response;
		}
		response.setStatusCode(HttpStatus.OK);
		response.setObject(obj);
		return response;
	}
	
	public <T> Response<T> signIn (String uri, Object body, Class<T> type) {
		Response<T> response = new Response<T>();
		
		T obj = null;
		ResponseEntity<T> responseEntity = null;
		
		try {
			responseEntity = restTemplate.postForEntity(HOST_PORT_BASE + uri, body, type);
		} catch (HttpClientErrorException e) {
			response.setStatusCode(e.getStatusCode());
			response.setDetail(e.getResponseBodyAsString());
			return response;
		}
		
		String sessionId = responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE).get(0).split(";")[0].split("=")[1];
		
		obj = responseEntity.getBody(); 
		response.setStatusCode(HttpStatus.OK);
		response.setSessionId(sessionId);
		response.setObject(obj);
		return response;
	}
	
	public <T> Response<T> get(String uri, Class<T> type, HttpHeaders headers) {
		Response<T> response = new Response<T>();
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
			response.setStatusCode(e.getStatusCode());
			response.setDetail(e.getResponseBodyAsString());
			return response;
		}
		response.setObject(obj);
		return response;
	}
	
	public <T> Response<T> put(String uri, Object body, Class<T> type) {
		Response<T> response = new Response<T>();
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Object> entity = new HttpEntity<Object>(body, headers);
		
		T obj = null;
		ResponseEntity<T> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(HOST_PORT_BASE + uri, HttpMethod.PUT, entity, type);
			obj = responseEntity.getBody();
		} catch (HttpClientErrorException e) {
			response.setStatusCode(e.getStatusCode());
			response.setDetail(e.getResponseBodyAsString());
			return response;
		}
		response.setObject(obj);
		return response;
	}

	public void delete(String uri) {
		restTemplate.delete(HOST_PORT_BASE + uri);
	}
}
