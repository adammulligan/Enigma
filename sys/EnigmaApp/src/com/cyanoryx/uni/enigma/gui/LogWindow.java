package com.cyanoryx.uni.enigma.gui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class LogWindow {

	private JFrame      frame;
	private JTextArea   logText;
	private JScrollPane pane;

	/**
	 * Create the application.
	 */
	public LogWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 550, 300);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		logText = new JTextArea();
		logText.setEnabled(false);
		
		pane  = new JScrollPane(logText);
		
		frame.getContentPane().add(pane);
	}
	
	public void setState(boolean state) {
		frame.setVisible(state);
	}

	public void log(String data) {
	    logText.append(data);
	    frame.getContentPane().validate();
	}
}
