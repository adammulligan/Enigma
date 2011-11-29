package com.adammulligan.uni.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.adammulligan.uni.net.protocol.Session;
import com.adammulligan.uni.net.protocol.xml.PacketQueue;
import com.adammulligan.uni.net.protocol.xml.ProcessThread;
import com.adammulligan.uni.net.protocol.xml.QueueThread;

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
			q.addListener(new MessageHandler(), "message");
		q.start();
		
		try {
			ss = new ServerSocket(this.port);
		} catch (IOException ioe) {
			System.out.println("Could not connect to port");
			ioe.printStackTrace();
		}
		
		while (true) {
			System.out.println("Waiting for client...");
			try {
				Socket clientSock = ss.accept();
				Session s = new Session(clientSock);
				
				ProcessThread p = new ProcessThread(s,this.pq);
				p.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		new Server(6055,"localhost");
	}
}
