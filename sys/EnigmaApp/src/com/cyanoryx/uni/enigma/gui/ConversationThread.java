package com.cyanoryx.uni.enigma.gui;

import java.io.IOException;

import com.cyanoryx.uni.enigma.net.client.Client;
import com.cyanoryx.uni.enigma.net.client.ClientThread;
import com.cyanoryx.uni.enigma.net.protocol.User;

public class ConversationThread extends Thread {
	public static void main(String[] args) {
		ClientThread client_thread = new ClientThread();
		Client		 client		   = new Client(client_thread);
		
		client.setServerName("localhost");
		client.setServerAddress("localhost");
		client.setPort("65000");
		client.setResource("test");
		client.setUser(new User("bill"));
		
		client_thread.setModel(client);
		client_thread.start();
		
		try {
			client.connect();
			Conversation conv = new Conversation(client);
			
			/*ConversationThread ct = new ConversationThread(client);
			ct.start();*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Conversation conv;
	public ConversationThread(Client c) throws IOException {
		conv = new Conversation(c);
	}
	
	public Conversation getWindow() { return this.conv; }
	
	@Override
	public void run() {}
}
