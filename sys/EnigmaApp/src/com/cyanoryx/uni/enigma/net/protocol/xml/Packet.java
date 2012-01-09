package com.cyanoryx.uni.enigma.net.protocol.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import org.xml.sax.Attributes;

import com.cyanoryx.uni.enigma.net.protocol.Session;

/**
 * Represents an XML node as a packet, with applicable attributes
 * Parent-child relationships are used to represent nested XML nodes.
 * 
 * @author adammulligan
 *
 */
public class Packet {
	Packet parent;
	
	String element,namespace;
	LinkedList<Object> children = new LinkedList<Object>();
	
	Session session;
	Hashtable<String, String> attributes = new Hashtable<String, String>();
	
	public Packet(String el) {
		this.setElement(el);
	}
	
	public Packet(String el, String value) {
		this.setElement(el);
		this.children.add(value);
	}
	
	public Packet(Packet parent, String element, String namespace, Attributes atts){
		this.setElement(element);
	    this.setNamespace(namespace);
	    this.setParent(parent);

	    for (int i = 0; i < atts.getLength(); i++) {
	        this.attributes.put(atts.getQName(i), atts.getValue(i));
	    }
	}

	public void setElement(String el) { this.element = el; }
	public String getElement() { return this.element; }
	
	public void setNamespace(String n) { this.namespace = n; }
	public String getNamespace() { return this.namespace; }
	
	public void setParent(Packet p) {
		this.parent = p;
		if (parent !=null) {
			this.parent.children.add(this);
		}
	}
	public Packet getParent() { return this.parent; }
	
	public LinkedList<Object> getChildren() { return children; }
	public Packet getFirstChild(String subelement) {
	    Iterator<Object> childIterator = children.iterator();
	    
	    while (childIterator.hasNext()) {
	        Object child = childIterator.next();
	        
	        if (child instanceof Packet){
	            Packet childPacket = (Packet)child;
	            
	            if (childPacket.getElement().equals(subelement)) {
	                return childPacket;
	            }
	        }
	    }
	    
	    return null;
    }

	public String getChildValue(String subelement){
	    Packet child = getFirstChild(subelement);
	    
	    if (child == null){
	        return null;
	    }
	    
	    return child.getValue();
	}

	public String getValue(){
	    StringBuffer value = new StringBuffer();
	    Iterator<Object> childIterator = children.iterator();
	    
	    while (childIterator.hasNext()){
	        Object valueChild = childIterator.next();
	        
	        if (valueChild instanceof String){
	          value.append((String)valueChild);
	        }
	    }
	    
	    return value.toString().trim();
	}

	public void setSession(Session session) { this.session = session; }
	public Session getSession() {
	    if (session != null) return session;
	    if (parent != null) return parent.getSession();
	    
	    return null;
	}

	/**
	 * Returns the attribute value for the given key
	 * 
	 * @param attribute - attribute name
	 * @return attribute value, or empty string if attribute doesn't exist
	 */
	public String getAttribute(String attribute) {
		if (attributes.containsKey(attribute) && !attributes.get(attribute).isEmpty()) {
			return attributes.get(attribute);
		} else {
			return "";
		}
	}
	public void removeAttribute(String attribute) { attributes.remove(attribute); }
	public void clearAttributes() { attributes.clear(); }
	public void setAttribute(String attribute, String value) {
	    if (value == null){
	        removeAttribute(attribute);
	    } else {
	        attributes.put(attribute,value);
	    }
	}
	
	public String getTo() { return attributes.get("to"); }
	public void setTo(String recipient) { setAttribute("to",recipient); }

	public String getFrom() { return attributes.get("from"); }
	public void setFrom(String sender) { setAttribute("from",sender); }

	public String getType() { return attributes.get("type"); }
	public void setType(String type) { setAttribute("type",type); }

	public String getID() { return attributes.get("id"); }
	public void setID(String ID) { setAttribute("id",ID); }

	public void writeXML() throws IOException { writeXML(session.getWriter()); }

	public void writeXML(Writer out) throws IOException{
	    out.write("<");
	    out.write(element);
	    
	    Enumeration<String> keys = attributes.keys();
	    while (keys.hasMoreElements()){
	        String key = keys.nextElement();
	        out.write(" ");
	        out.write(key);
	        out.write("='");
	        out.write(attributes.get(key));
	        out.write("'");
	    }

	    if (children.size() == 0){
	        out.write("/>");
	        out.flush();
	        return;
	    }

	    out.write(">");
	    
	    Iterator<Object> childIterator = children.iterator();
	    while (childIterator.hasNext()){
	        Object child = childIterator.next();
	        if (child instanceof String){
	        	System.out.println((String)child);
	            out.write((String)child);
	        } else {
	            ((Packet)child).writeXML(out);
	        }
	    }
	    
	    out.write("</");
	    out.write(element);
	    out.write(">");
	    out.flush();
	}

	public String toString(){
	    try {
	        StringWriter reply = new StringWriter();
	        writeXML(reply);
	        return reply.toString();
	    } catch (Exception ex){
	    }
	    
	    return "<" + element + ">";
	}
}
