package com.cyanoryx.uni.enigma.gui;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * A handler that deals with requests to log information to the log window
 * 
 * @author adammulligan
 *
 */
public class LogHandler extends Handler {
	public static LogHandler handler;
	
	private LogWindow window;
	
	private LogHandler() {
		setLevel(Level.INFO);
	    setFilter(new LogFilter());
	    setFormatter(new SimpleFormatter());
	    
		if (window==null) window = new LogWindow();
	}
	
	public static synchronized LogHandler getInstance() {
		// We are implementing the handler as a singleton
		return (handler==null) ? new LogHandler() : handler;
	}

	@Override
	public void publish(LogRecord r) {
		String message = null;
		
	    if (!isLoggable(r)) return;
	    
	    message = getFormatter().format(r);
	    window.log(message);
	}
	
	public LogWindow getWindow() {
		return this.window;
	}

	public void close() throws SecurityException {}
	public void flush() {}
}
