package com.cyanoryx.uni.enigma.net.protocol;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.LinkedList;
import java.util.ListIterator;

import com.cyanoryx.uni.enigma.net.io.XercesReader;

public class Session {
	Socket socket;
	User user;
	
	public Session(Socket s) {
		this.setSocket(s);
	}
	
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
	}
	
	Reader in;
	public Reader getReader() throws IOException {
	    if (in == null){
	        in = new XercesReader(this.socket.getInputStream());
	    }
	    return in;
	}
}
