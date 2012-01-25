package com.cyanoryx.uni.enigma.gui;

import java.util.logging.Logger;

public class LogTest {
	private LogHandler handler = null;

	  private Logger logger = null;

	  public LogTest() {
	    handler = LogHandler.getInstance();
	    logger = Logger.getLogger("com.cyanoryx.uni.enigma.gui.LogHandler");
	    logger.addHandler(handler);
	  }

	  /**
	   * This method publishes the log message
	   */
	  public void logMessage() {
	    logger.info("Hello from WindowHandler...");
	  }

	  public static void main(String args[]) {
	    LogTest demo = new LogTest();
	    demo.logMessage();
	  }
}