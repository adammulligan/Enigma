package com.adammulligan.uni.net.server;

import java.io.Writer;

import com.adammulligan.uni.net.protocol.Session;
import com.adammulligan.uni.net.protocol.xml.Packet;
import com.adammulligan.uni.net.protocol.xml.PacketListener;

public class OpenStreamHandler implements PacketListener{
  public void notify(Packet packet){
    try {
      Session session = packet.getSession();
      
      Writer out = session.getWriter();
      out.write("<?xml version='1.0' encoding='UTF-8' ?><stream:stream xmlns='jabber:client' from='");
      out.write("test");
      out.write("' id=1'");
      out.write("' xmlns:stream='http://etherx.jabber.org/streams'>");
      out.flush();

      session.setStatus(Session.STREAMING);
    } catch (Exception ex){
      ex.printStackTrace();
    }
  }
}