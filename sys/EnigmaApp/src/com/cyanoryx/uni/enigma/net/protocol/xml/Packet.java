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

	Hashtable<String, String> attributes = new Hashtable<String, String>();

	/**
	 * Creates a packet "el".
	 * 
	 * @param el
	 */
	public Packet(String el) {
		this.setElement(el);
	}

	/**
	 * Creates a packet "el", with child "value".
	 * 
	 * @param el
	 * @param value
	 */
	public Packet(String el, String value) {
		this.setElement(el);
		this.children.add(value);
	}

	/**
	 * Creates a packet with the given set of attributes, and namespace
	 * 
	 * @param parent
	 * @param element
	 * @param namespace
	 * @param atts
	 */
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

	/**
	 * Returns the first child packet of the given subelement, if 
	 * one exists.
	 * 
	 * @param subelement
	 * @return
	 */
	public Packet getFirstChild(String subelement) {
		Iterator<Object> childIterator = children.iterator();

		// Loop through children..
		while (childIterator.hasNext()) {
			Object child = childIterator.next();

			// If it is a sub-packet, not a value..
			if (child instanceof Packet){
				// Check to see if the packet is the one we are looking for
				if (((Packet)child).getElement().equals(subelement)) {
					return (Packet)child;
				}
			}
		}

		return null;
	}

	/**
	 * Returns the element value of the given child.
	 * 
	 * "<stream><body>test</body></stream>".getChildValue("body"): "test"
	 * 
	 * @param subelement
	 * @return
	 */
	public String getChildValue(String subelement){
		return (getFirstChild(subelement) == null) ? null : getFirstChild(subelement).getValue();
	}

	/**
	 * Get the value of this packet. 
	 * 
	 * @return
	 */
	public String getValue(){
		StringBuffer value = new StringBuffer();
		Iterator<Object> childIterator = children.iterator();

		while (childIterator.hasNext()){
			Object child = childIterator.next();

			if (child instanceof String){
				value.append((String)child);
			}
		}

		return value.toString().trim();
	}

	Session session;
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
		// Null values remove the attribute entirely
		if (value == null){ removeAttribute(attribute);}
		else { attributes.put(attribute,value); }
	}

	/* Shortcuts for common attributes */

	public String getTo() { return attributes.get("to"); }
	public void setTo(String recipient) { setAttribute("to",recipient); }

	public String getFrom() { return attributes.get("from"); }
	public void setFrom(String sender) { setAttribute("from",sender); }

	public String getType() { return attributes.get("type"); }
	public void setType(String type) { setAttribute("type",type); }

	public String getID() { return attributes.get("id"); }
	public void setID(String ID) { setAttribute("id",ID); }

	/* End shortcuts for common attributes */

	/**
	 * Convert the packet to a textual representation and write it to
	 * a buffer.
	 * 
	 * @param out
	 * @throws IOException
	 */
	public void writeXML(Writer out) throws IOException{
		out.write("<"+element);

		Enumeration<String> keys = attributes.keys();
		while (keys.hasMoreElements()){
			String key = keys.nextElement();
			out.write(" "+key+"='"+attributes.get(key)+"'");
		}

		out.write(">");

		Iterator<Object> childIterator = children.iterator();
		while (childIterator.hasNext()){
			Object child = childIterator.next();
			// If the child is a string value, print it
			if (child instanceof String){
				out.write((String)child);
			} else { // Otherwise recurse through the child until we can print it
				((Packet)child).writeXML(out);
			}
		}

		out.write("</"+element+">");
		out.flush();
	}

	public String toString(){
		try {
			StringWriter reply = new StringWriter();
			writeXML(reply);
			return reply.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "<" + element + ">";
	}
}
