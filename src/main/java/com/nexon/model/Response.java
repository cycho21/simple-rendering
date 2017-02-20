package com.nexon.model;


import java.util.ArrayList;

/**
 * Created by chan8 on 2017-02-06.
 */
public class Response {
    
    private int statusCode;
    private User user;
    private Chatroom chatroom;
    private Message message;
    private ArrayList<User> userArrayList;
    private ArrayList<Chatroom> chatroomArrayList;
    private ArrayList<Message> messagesArrayList;

    public Response() {
        this.user = new User();
        this.chatroom = new Chatroom();
        this.message = new Message();
    }
    
    public Response(int statusCode) {
        this.statusCode = statusCode;
        this.user = new User();
    }

    public ArrayList<Chatroom> getChatroomArrayList() {
        return chatroomArrayList;
    }

    public void setChatroomArrayList(ArrayList<Chatroom> chatroomArrayList) {
        this.chatroomArrayList = chatroomArrayList;
    }

    public ArrayList<User> getUserArrayList() {
        return userArrayList;
    }

    public void setUserArrayList(ArrayList<User> userArrayList) {
        this.userArrayList = userArrayList;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
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
        return messagesArrayList;
    }

    public void setMessagesArrayList(ArrayList<Message> messagesArrayList) {
        this.messagesArrayList = messagesArrayList;
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
}
