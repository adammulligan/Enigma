package com.cyanoryx.uni.enigma.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketQueue;
import com.cyanoryx.uni.enigma.net.protocol.xml.ProcessThread;
import com.cyanoryx.uni.enigma.net.protocol.xml.QueueThread;

public class Server {
	private int port;
	
	PacketQueue pq = new PacketQueue();
	
	public Server(int port,String server) {
		this.port = port;
		
		ServerSocket ss = null;
		
		QueueThread q = new QueueThread(this.pq);
			q.setDaemon(true);
			q.addListener(new OpenStreamHandler(),"stream");
			q.addListener(new CloseStreamHandler(),"/stream");
			q.addListener(new AuthHandler(),"auth");
			q.addListener(new MessageHandler(), "message");
		q.start();
		
		try {
			ss = new ServerSocket(this.port);
		} catch (IOException ioe) {
			System.out.println("Could not connect to port");
			ioe.printStackTrace();
		}
		
		System.out.println("Starting server on port "+port+"...");
		
		while (true) {
			try {
				Socket clientSock = ss.accept();
				Session s = new Session(clientSock);
				
				System.out.println("Client connected ("+clientSock.getInetAddress()+":"+clientSock.getPort()+")");
				
				ProcessThread p = new ProcessThread(s,this.pq);
				p.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		int port = 65000;
		
		new Server(port,"localhost");
	}
}
