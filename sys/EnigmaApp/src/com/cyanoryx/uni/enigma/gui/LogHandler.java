package com.cyanoryx.uni.enigma.gui;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogHandler extends Handler {
	public static LogHandler handler;
	
	private LogWindow window;
	
	private LogHandler() {
		init();
		if (window==null) window = new LogWindow();
	}
	
	public static synchronized LogHandler getInstance() {
		return (handler==null) ? new LogHandler() : handler;
	}
	
	private void init() {
		setLevel(Level.INFO);
	    setFilter(new LogFilter());
	    setFormatter(new SimpleFormatter());
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

	@Override
	public void close() throws SecurityException {}
	@Override
	public void flush() {}
}
