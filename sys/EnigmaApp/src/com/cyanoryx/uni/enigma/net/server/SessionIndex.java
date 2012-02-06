package com.cyanoryx.uni.enigma.net.server;

import java.util.Hashtable;

import com.cyanoryx.uni.enigma.net.protocol.Session;

public class SessionIndex {
	Hashtable<String, Session> idIndex = new Hashtable<String, Session>();

	public Session getSession(String id){
		return (Session)idIndex.get(id);
	}

	public void removeSession(Session session){
		String id = session.getID();
		this.removeSession(id);
	}
	
	public void removeSession(String id) {
		if (idIndex.containsKey(id)) idIndex.remove(id);
	}

	public void addSession(Session session){
		System.out.println("Adding "+session.getID()+" to the jidlist");
		idIndex.put(session.getID(),session);
	}
}
