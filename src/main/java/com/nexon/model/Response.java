package com.nexon.model;


import java.util.ArrayList;

import org.springframework.http.HttpStatus;

/**
 * Created by chan8 on 2017-02-06.
 * @param <T>
 */
public class Response<T> {
    
	private T object;
	private String detail;
    private HttpStatus statusCode;
    private String sessionId;
    private User user;
    private Chatroom chatroom;
    private Message message;
    private ArrayList<User> users;
    private ArrayList<Chatroom> chatrooms;
    private ArrayList<Message> messages;

    public Response() {
        this.user = new User();
        this.chatroom = new Chatroom();
        this.message = new Message();
        this.chatrooms = new ArrayList<Chatroom>();
    }
    
    public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public ArrayList<Chatroom> getChatroomArrayList() {
        return chatrooms;
    }

    public void setChatroomArrayList(ArrayList<Chatroom> chatroomArrayList) {
        this.chatrooms = chatroomArrayList;
    }

    public ArrayList<User> getUserArrayList() {
        return users;
    }

    public void setUserArrayList(ArrayList<User> userArrayList) {
        this.users = userArrayList;
    }
    public User getUser() {
        return user;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public ArrayList<Message> getMessagesArrayList() {
        return messages;
    }

    public void setMessagesArrayList(ArrayList<Message> messagesArrayList) {
        this.messages = messagesArrayList;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Chatroom getChatroom() {
        return chatroom;
    }

    public void setChatroom(Chatroom chatroom) {
        this.chatroom = chatroom;
    }

	public HttpStatus getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(HttpStatus statusCode) {
		this.statusCode = statusCode;
	}
    
}
