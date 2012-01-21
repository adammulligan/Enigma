package com.cyanoryx.uni.enigma.net.server;

import java.io.Writer;
import java.util.prefs.Preferences;

import com.cyanoryx.uni.enigma.utils.AppPrefs;
import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketListener;

public class OpenStreamHandler implements PacketListener{
  public void notify(Packet packet){
    try {
      Session session = packet.getSession();
      
      // Always default to authenticated
      String auth = packet.getAttribute("authenticated").isEmpty() ? "true" : packet.getAttribute("authenticated");
      
      // If no auth is requested,
      // check preferences to see if we allow that
      if (auth.equalsIgnoreCase("false")) {
    	  Preferences p = new AppPrefs().getPrefs();
    	  
    	  // Drop packets; TODO return error
    	  if (!p.getBoolean("allow_unauthenticated_conversations", false)) return; 
      }
      
      session.setAuthenticated(auth.equalsIgnoreCase("true"));
      
      Writer out = session.getWriter();
      
      out.write("<?xml " +
      			"version='1.0' " +
      		    "encoding='UTF-8' ?>");
      
      out.write("<stream " +
      			"xmlns='enigma:client' " +
      			"from='xxx' " +
      			"id='x' " +
      			"xmlns='enigma:server'" +
      			"xmlns:stream='http://cyanoryx.com' " +
      			"authenticated='"+session.getAuthenticated()+"'>");
      
      out.flush();

      session.setStatus(Session.STREAMING);
    } catch (Exception ex){
      ex.printStackTrace();
    }
  }
}