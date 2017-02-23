package com.nexon.config;

import java.util.HashMap;

public class SimpleSession {
	private static HashMap<String, Status> simpleSet;
	
	public static HashMap<String, Status> getSession() {
		if (simpleSet == null) {
			simpleSet = new HashMap<String, Status>();
			return simpleSet;
		} else {
			return simpleSet;
		}
	}
}

class Status {
	private boolean logged;

	public boolean isLogged() {
		return logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}
}