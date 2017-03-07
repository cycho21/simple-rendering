package com.nexon.websocket;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.WebUtils;

public class SessionInterceptor implements HandshakeInterceptor {

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		ServletServerHttpRequest servletServerRequest = (ServletServerHttpRequest) request;
        HttpServletRequest servletRequest = servletServerRequest.getServletRequest();
        Cookie sessionid = WebUtils.getCookie(servletRequest, "sessionid");
        attributes.put("sessionid", sessionid.getValue());
		Cookie userid = WebUtils.getCookie(servletRequest, "userid");
		attributes.put("userid", userid.getValue());
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
	}
	
}
