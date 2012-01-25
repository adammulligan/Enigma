package com.cyanoryx.uni.enigma.net.protocol;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;

import com.cyanoryx.uni.enigma.net.client.Client;
import com.cyanoryx.uni.enigma.net.io.XercesReader;

public class Session {
	Socket socket;
	User user;
	String ID;
	
	public Session(Socket s) {
		this.setSocket(s);
	}
	public Session() {
		this.setStatus(Session.DISCONNECTED);
	}
	
	Hashtable<String,Client> clients = new Hashtable<String,Client>();
	public void addClient(String id, Client c) { this.clients.put(id,c); }
	public Client getClient(String id) { return this.clients.get(id); }
	
	public void setSocket(Socket s) {
		this.socket = s;
		this.in = null;
		this.out = null;
		this.setStatus(CONNECTED);
	}
	public Socket getSocket() { return this.socket; }
	
	public User getUser() {
		return this.user;
	}
	
	public void setUser(User u) {
		this.user = u;
	}
	
	public String getID() { return this.ID; }
	public void setID(String ID) { this.ID = ID; }
	
	LinkedList<StatusListener> statusListeners = new LinkedList<StatusListener>();
	public boolean addStatusListener(StatusListener listener){
	    return statusListeners.add(listener);
	}
	public boolean removeStatusListener(StatusListener listener){
	    return statusListeners.remove(listener);
	}
	
	boolean authenticated = true;
	public void setAuthenticated(boolean a) {
		this.authenticated = a;
	}
	
	/**
	 * Returns whether or not the session requires authentication
	 * 
	 * @return
	 */
	public boolean getAuthenticated() {
		return this.authenticated;
	}
	
	public static final int DISCONNECTED  = 1;
	public static final int CONNECTED     = 2;
	public static final int STREAMING     = 3;
	public static final int AUTHENTICATED = 4;

	int status;
	public int getStatus() { return status;  }

	public synchronized void setStatus(int newStatus){
	    status = newStatus;
	    ListIterator<StatusListener> iter = statusListeners.listIterator();
	    while (iter.hasNext()){
	      StatusListener listener = (StatusListener)iter.next();
	      listener.notify(status);
	    }
	}

	public void closeStream() throws IOException {
	    getWriter().write("</stream>");
	    out.flush();
	    setStatus(CONNECTED);
	}

	public void disconnect() throws IOException{
	    this.socket.close();
	    setStatus(DISCONNECTED);
	}
	
	Writer out;
	public Writer getWriter() throws IOException {
		if (out == null){
			out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
	    }
	    return out;
		//return new BufferedWriter(new OutputStreamWriter(System.out))
	}
	
	Reader in;
	public Reader getReader() throws IOException {
	    if (in == null){
	        in = new XercesReader(this.socket.getInputStream());
	    }
	    return in;
	}
	
	private String local_port;
	public void setLocalPort(String port) { this.local_port = port; }
	public String getLocalPort() { return this.local_port; }
}
