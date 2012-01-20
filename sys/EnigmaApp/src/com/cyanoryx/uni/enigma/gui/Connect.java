package com.cyanoryx.uni.enigma.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;

public class Connect extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Connect frame = new Connect();
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
	public Connect() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 250);
		contentPane = new JPanel();
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblEnterAnIp = new JLabel("Enter the IP address of another running Enigma client:\n");
		GridBagConstraints gbc_lblEnterAnIp = new GridBagConstraints();
		gbc_lblEnterAnIp.gridx = 0;
		gbc_lblEnterAnIp.gridy = 0;
		contentPane.add(lblEnterAnIp, gbc_lblEnterAnIp);
		
		JTextField msgField = new JTextField();
        GridBagConstraints gbc_msgField = new GridBagConstraints();
        gbc_msgField.gridx = 0;
        gbc_msgField.gridy = 1;
        gbc_msgField.anchor = GridBagConstraints.CENTER;
        gbc_msgField.insets = new Insets(0, 0, 5, 0);
        gbc_msgField.gridwidth = 1;
        gbc_msgField.weightx = 1;
        gbc_msgField.weighty = 1;
        gbc_msgField.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(msgField, gbc_msgField);
        
        JTextField portField = new JTextField();
        GridBagConstraints gbc_portField = new GridBagConstraints();
        gbc_portField.gridx = 2;
        gbc_portField.gridy = 1;
        gbc_portField.anchor = GridBagConstraints.CENTER;
        gbc_portField.insets = new Insets(0, 0, 5, 0);
        gbc_portField.gridwidth = 1;
        gbc_portField.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(portField, gbc_portField);
	}

}
