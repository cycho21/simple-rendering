package com.nexon.controller;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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

import com.nexon.model.Chatroom;
import com.nexon.model.Response;
import com.nexon.model.SimpleResponse;
import com.nexon.model.User;

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
		this.HOST_PORT_BASE = (String) jsonObject.get("host") + ":"
				+ Integer.parseInt(String.valueOf(jsonObject.get("port"))) + (String) jsonObject.get("baseurl");
	}

	public Response<ArrayList<Chatroom>> getAllChatroom(String uri) {
		Response<ArrayList<Chatroom>> response = new Response<ArrayList<Chatroom>>();
		ArrayList<Chatroom> list = null;
		try {
			list = restTemplate.getForObject(HOST_PORT_BASE + uri, SimpleResponse.class).getChatrooms();
		} catch (HttpClientErrorException e) {
			response.setStatusCode(e.getStatusCode());
			response.setDetail(e.getResponseBodyAsString());
			return response;
		}
		response.setStatusCode(HttpStatus.OK);
		response.setObject(list);
		return response;
	}

	public Response<ArrayList<User>> getAllUser(String uri) {
		Response<ArrayList<User>> response = new Response<ArrayList<User>>();;
		ArrayList<User> list = null;
		try {
			list = restTemplate.getForObject(HOST_PORT_BASE + uri, ArrayList.class);
		} catch (HttpClientErrorException e) {
			response.setStatusCode(e.getStatusCode());
			response.setDetail(e.getResponseBodyAsString());
			return response;
		}
		response.setStatusCode(HttpStatus.OK);
		response.setObject(list);
		return response;
	}

	public Response<String> signOut(String uri, String sessionId) {
		Response<String> response = new Response<String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("sessionid", sessionId);

		HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(HOST_PORT_BASE + uri, HttpMethod.POST, httpEntity, String.class);
		} catch (HttpClientErrorException e) {
			response.setStatusCode(e.getStatusCode());
			response.setDetail(e.getResponseBodyAsString());
			return response;
		}
		response.setStatusCode(HttpStatus.OK);
		response.setDetail(responseEntity.getBody());
		return response;
	}

	public <T> Response<T> postWithSessionid(String uri, Object body, Class<T> type, String sessionId) {
		Response<T> response = new Response<T>(); 
		HttpHeaders headers = new HttpHeaders();
		headers.add("sessionid", sessionId);
		
		HttpEntity<Object> httpEntity = new HttpEntity<>(body, headers);
		ResponseEntity<T> responseEntity = null; 
		T obj = null;
		try {
			responseEntity = restTemplate.exchange(HOST_PORT_BASE + uri, HttpMethod.POST, httpEntity, type);
		} catch (HttpClientErrorException e) {
			response.setStatusCode(e.getStatusCode());
			response.setDetail(e.getResponseBodyAsString());
			return response;
		}
		
		obj = responseEntity.getBody();
		response.setObject(obj);
		response.setStatusCode(HttpStatus.OK);
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

	public <T> Response<T> signIn(String uri, Object body, Class<T> type) {
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

		String sessionId = responseEntity.getHeaders().get("sessionid").get(0);

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
		response.setStatusCode(HttpStatus.OK);
		return response;
	}

	public <T> Response<T> put(String uri, Object body, Class<T> type, String sessionid) {
		Response<T> response = new Response<T>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("sessionid", sessionid);
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
		response.setStatusCode(HttpStatus.OK);
		return response;
	}

	public Response<?> delete(String uri, String sessionid) {
		Response<String> response = new Response<String>();
		HttpHeaders headers = new HttpHeaders();
 		headers.add("sessionid", sessionid);
		HttpEntity<Object> entity = new HttpEntity<Object>(headers);
		String obj = null;
		
		try {
			restTemplate.exchange(HOST_PORT_BASE + uri, HttpMethod.DELETE, entity, String.class);
		} catch (HttpClientErrorException e) {
			response.setStatusCode(e.getStatusCode());
			response.setDetail(e.getResponseBodyAsString());
			return response;
		}
		
		response.setStatusCode(HttpStatus.OK);
		response.setObject(obj);
		return response;
	}

	public Response<ArrayList<Chatroom>> getOwnChatroom(String uri) {
		Response<ArrayList<Chatroom>> response = new Response<ArrayList<Chatroom>>();
		ArrayList<Chatroom> list = null;
		try {
			list = restTemplate.getForObject(HOST_PORT_BASE + uri, SimpleResponse.class).getChatrooms();
		} catch (HttpClientErrorException e) {
			response.setStatusCode(e.getStatusCode());
			response.setDetail(e.getResponseBodyAsString());
			return response;
		}
		response.setStatusCode(HttpStatus.OK);
		response.setObject(list);
		return response;
	}

	public Response<?> quitChatroom(String uri, String sessionid) {
		Response<String> response = new Response<String>();
		HttpHeaders headers = new HttpHeaders();
 		headers.add("sessionid", sessionid);
		HttpEntity<Object> entity = new HttpEntity<Object>(headers);
		response.setObject(null);
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(HOST_PORT_BASE + uri, HttpMethod.DELETE, entity, String.class);
			if (responseEntity.getHeaders().get("deleted").get(0).equals("true"))
				response.setObject("deleted");
				
		} catch (HttpClientErrorException e) {
			response.setStatusCode(e.getStatusCode());
			response.setDetail(e.getResponseBodyAsString());
			return response;
		}
		
		response.setStatusCode(HttpStatus.OK);
		return response;
	}

}
