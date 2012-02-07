package com.cyanoryx.uni.enigma.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

public class Preferences extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7720258639613136149L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Preferences frame = new Preferences();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Preferences() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane preference_wrapper = new JTabbedPane();
		preference_wrapper.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		JComponent panel1 = makeTextPanel("General");
		preference_wrapper.addTab("General", null, panel1, "");
		
		JComponent panel2 = makeTextPanel("Personal");
		preference_wrapper.addTab("Personal", null, panel2, "");

			JTabbedPane security_wrapper = new JTabbedPane();
			security_wrapper.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			
			JComponent security_panel1 = makeTextPanel("General");
			security_wrapper.addTab("General", null, security_panel1, "");
			
			JComponent security_panel2 = makeTextPanel("Key Agreement");
			security_wrapper.addTab("Key Agreement", null, security_panel2, "");
	
			JComponent security_panel3 = makeTextPanel("Cipher");
			security_wrapper.addTab("Cipher", null, security_panel3, "");
		
		preference_wrapper.addTab("Security", null, security_wrapper, "");
		
		contentPane.add(preference_wrapper);
		
		setVisible(true);
	}

	private JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        
        return panel;
    }
}
