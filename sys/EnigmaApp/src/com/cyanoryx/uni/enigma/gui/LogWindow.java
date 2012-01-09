package com.cyanoryx.uni.enigma.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class LogWindow {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		LogWindow window = new LogWindow();
		window.frame.setVisible(true);
	}

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
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTextArea logText = new JTextArea();
		JScrollPane pane  = new JScrollPane(logText);
		
		frame.getContentPane().add(pane);
	}

}
