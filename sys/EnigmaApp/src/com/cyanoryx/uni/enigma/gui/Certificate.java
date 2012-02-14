package com.cyanoryx.uni.enigma.gui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Certificate {

	private JFrame      frame;
	private JTextArea   certText;
	private JScrollPane pane;

	/**
	 * Create the application.
	 */
	public Certificate(String data) {
		initialize();
		setText(data);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 550, 300);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		certText = new JTextArea();
		certText.setEnabled(false);
		
		pane  = new JScrollPane(certText);
		
		frame.getContentPane().add(pane);
	}
	
	public void setState(boolean state) {
		frame.setVisible(state);
	}

	public void setText(String data) {
	    certText.setText(data);
	    frame.getContentPane().validate();
	}
}
