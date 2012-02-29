package com.cyanoryx.uni.enigma.net.server;

import java.util.Hashtable;

import com.cyanoryx.uni.enigma.net.protocol.Session;

/**
 * Structure for storing all current sessions maintained by a Server instance.
 * 
 * @author adammulligan
 */
public class SessionIndex {
  Hashtable<String, Session> index = new Hashtable<String, Session>();

  /**
   * Return session from given ID
   * 
   * @param id
   * @return
   */
  public Session getSession(String id){
    return (Session)index.get(id);
  }

  /**
   * Remove given session
   * 
   * @param session
   */
  public void removeSession(Session session){
    String id = session.getID();
    this.removeSession(id);
  }
  
  /**
   * Remove session from index directly with session ID
   * 
   * @param id
   */
  public void removeSession(String id) {
    if (index.containsKey(id)) index.remove(id);
  }

  /**
   * Add a session object to the index
   * 
   * @param session
   */
  public void addSession(Session session){
    index.put(session.getID(),session);
  }
}
