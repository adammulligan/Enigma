package com.cyanoryx.uni.enigma.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import com.cyanoryx.uni.enigma.gui.Conversation;
import com.cyanoryx.uni.enigma.net.client.Client;
import com.cyanoryx.uni.enigma.net.client.ClientThread;
import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.User;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketQueue;
import com.cyanoryx.uni.enigma.net.protocol.xml.ProcessThread;
import com.cyanoryx.uni.enigma.net.protocol.xml.QueueThread;

public class Server {
	private int port;
	
	PacketQueue pq = new PacketQueue();
	
	public Server(int port,String server) throws IOException {
		this.port = port;
		
		ServerSocket ss = null;
		
		QueueThread q = new QueueThread(this.pq);
			q.setDaemon(true);
			q.addListener(new OpenStreamHandler(),"stream");
			q.addListener(new CloseStreamHandler(),"/stream");
			q.addListener(new AuthHandler(),"auth");
			q.addListener(new MessageHandler(), "message");
		q.start();
		
		ss = new ServerSocket(this.port);
		
		System.out.println("Starting server on port "+port+"...");
		
		while (true) {
			try {
				Socket clientSock = ss.accept();
				Session s = new Session(clientSock);
				
				s.setLocalPort(this.port+"");
				
				System.out.println("Client connected ("+clientSock.getInetAddress()+":"+clientSock.getPort()+")");
				
				ProcessThread p = new ProcessThread(s,this.pq);
				p.start();
				
				if (this.port==65001) {
					ClientThread client_thread = new ClientThread();
					Client		 client		   = new Client(client_thread);
					
					client.setServerName("22");
					client.setServerAddress(server);
					client.setPort("65000");
					client.setResource("test");
					client.setUser(new User("adam2"));
					
					s.addClient("22", client);
					
					Conversation conv = new Conversation(client);
					client.setWindow(conv);
					
					client_thread.setModel(client);
					client_thread.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void createClient(String id, String server, String port, User u, Session s) throws IOException {
		ClientThread client_thread = new ClientThread();
		Client		 client		   = new Client(client_thread);
		
		client.setServerName(id);
		client.setServerAddress(server);
		client.setPort(port);
		client.setResource("test");
		client.setUser(u);
		
		s.addClient("22", client);
		
		Conversation conv = new Conversation(client);
		client.setWindow(conv);
		
		client_thread.setModel(client);
		client_thread.start();
	}
	
	public static void main(String[] args) {
		int port = 65000;
		
		Scanner kb = new Scanner(System.in);
		
		System.out.print("Choose port: ");
		port = kb.nextInt();
		
		while (true) {
			try {
				new Server(port,"localhost");
				break;
			} catch (IOException e) {
				System.out.println("Could not connect to port");
				System.out.print("Choose port: ");
				port = kb.nextInt();
				continue;
			}
		}
	}
}
