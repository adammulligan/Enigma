package com.cyanoryx.uni.enigma.gui;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JToolBar;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import java.awt.Insets;
import javax.swing.JTextArea;

import com.cyanoryx.uni.enigma.utils.Strings;

public class Conversation {

	private JFrame frame;
	
	private Strings strings;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Conversation window = new Conversation();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Conversation() {
		Strings.initialise();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 550);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(350,350));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.insets = new Insets(5, 0, 0, 0);
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 0;
		gbc_toolBar.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc_toolBar.weightx = 0;
		gbc_toolBar.weighty = 0;
		gbc_toolBar.gridwidth = 3;
		gbc_toolBar.fill = GridBagConstraints.HORIZONTAL;
		
		
		JButton btnRegenerate = new JButton();
		btnRegenerate.setIcon(new ImageIcon("res/drawable/ic_menu_backup.png"));
		btnRegenerate.setBorder(BorderFactory.createEmptyBorder());
		btnRegenerate.setToolTipText(Strings.translate("conv.toolbar.regen"));
		toolBar.add(btnRegenerate);
		
		JButton btnToggleEnc = new JButton();
		btnToggleEnc.setIcon(new ImageIcon("res/drawable/ic_menu_enc.png"));
		btnToggleEnc.setBorder(BorderFactory.createEmptyBorder());
		btnToggleEnc.setToolTipText(Strings.translate("conv.toolbar.toggle_dh"));
		toolBar.add(btnToggleEnc);
		
		JButton btnViewCert = new JButton();
		btnViewCert.setIcon(new ImageIcon("res/drawable/ic_menu_cert.png"));
		btnViewCert.setBorder(BorderFactory.createEmptyBorder());
		btnViewCert.setToolTipText(Strings.translate("conv.toolbar.view_cert"));
		toolBar.add(btnViewCert);
		
		toolBar.addSeparator();
		
		JButton btnLog = new JButton();
		btnLog.setIcon(new ImageIcon("res/drawable/ic_menu_bug.png"));
		btnLog.setBorder(BorderFactory.createEmptyBorder());
		btnLog.setToolTipText(Strings.translate("conv.toolbar.view_log"));
		toolBar.add(btnLog);
		
		JButton btnBlock = new JButton();
		btnBlock.setIcon(new ImageIcon("res/drawable/ic_menu_block.png"));
		btnBlock.setBorder(BorderFactory.createEmptyBorder());
		btnBlock.setToolTipText(Strings.translate("conv.toolbar.block_user"));
		toolBar.add(btnBlock);
		
		frame.getContentPane().add(toolBar, gbc_toolBar);
		//Keep the frame background color consistent
        frame.getContentPane().setBackground(toolBar.getBackground());
        
        //The seperator Row
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        frame.getContentPane().add(new JSeparator(), gbc);
        
        JTextArea textArea = new JTextArea();
        textArea.setEnabled(false);
        textArea.setText("No messages...");
        GridBagConstraints gbc_textArea = new GridBagConstraints();
        gbc_textArea.gridx = 0;
        gbc_textArea.gridy = 2;
        gbc_textArea.anchor = GridBagConstraints.CENTER;
        gbc_textArea.insets = new Insets(0, 5, 5, 5);
        gbc_textArea.weightx = 1;
        gbc_textArea.weighty = 1;
        gbc_textArea.gridwidth = 3;
        gbc_textArea.fill = GridBagConstraints.BOTH;
        frame.getContentPane().add(textArea, gbc_textArea);
        
        JTextField msgField = new JTextField();
        GridBagConstraints gbc_msgField = new GridBagConstraints();
        gbc_msgField.gridx = 0;
        gbc_msgField.gridy = 3;
        gbc_msgField.anchor = GridBagConstraints.CENTER;
        gbc_msgField.insets = new Insets(0, 0, 5, 0);
        gbc_msgField.gridwidth = 2;
        gbc_msgField.fill = GridBagConstraints.HORIZONTAL;
        frame.getContentPane().add(msgField, gbc_msgField);
        
        JButton msgSend = new JButton("Send");
        GridBagConstraints gbc_msgSend = new GridBagConstraints();
        gbc_msgSend.gridx = 2;
        gbc_msgSend.gridy = 3;
        gbc_msgSend.anchor = GridBagConstraints.CENTER;
        gbc_msgSend.insets = new Insets(0, 0, 5, 0);
        gbc_msgSend.gridwidth = 1;
        gbc_msgSend.fill = GridBagConstraints.HORIZONTAL;
        frame.getContentPane().add(msgSend, gbc_msgSend);
	}

}
