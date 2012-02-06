package com.cyanoryx.uni.enigma.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.cyanoryx.uni.enigma.net.client.Client;
import com.cyanoryx.uni.enigma.net.protocol.User;
import com.cyanoryx.uni.enigma.utils.Strings;

public class Conversation {
	private JFrame frame;
	
	private boolean    log_open;
	private LogHandler handler;
	private Logger     logger;
	
	private User user;
	
	private Client client;
	
	//private Strings strings;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Conversation window = new Conversation(new User("Adam"));
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public Conversation(Client c) throws IOException {
		//Strings.initialise();
		
		handler = LogHandler.getInstance();
		logger = Logger.getLogger(this.getClass().toString()+"."+c.getUser().getName());
		logger.addHandler(handler);
		
		user   = c.getUser();
		client = c;
		
		initialize();
		
		frame.setVisible(true);
	}
	
	private JTextField messageInput;
	private JTextPane  messages;

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	private void initialize() throws IOException {
		frame = new JFrame("Conversation with "+user.getName());
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
		btnLog.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				log_open=!log_open;
				handler.getWindow().setState(log_open);
			}
		});
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
        
        messages = new JTextPane();
        messages.setEnabled(false);
        
        messages.setText("Conversation started on "+new Date()+"\n\n");
        
        GridBagConstraints gbc_textArea = new GridBagConstraints();
        gbc_textArea.gridx = 0;
        gbc_textArea.gridy = 2;
        gbc_textArea.anchor = GridBagConstraints.CENTER;
        gbc_textArea.insets = new Insets(0, 5, 5, 5);
        gbc_textArea.weightx = 1;
        gbc_textArea.weighty = 1;
        gbc_textArea.gridwidth = 3;
        gbc_textArea.fill = GridBagConstraints.BOTH;
        frame.getContentPane().add(messages, gbc_textArea);
        
        messageInput = new JTextField();
        messageInput.addKeyListener(new KeyListener(){
        	@Override
			public void keyReleased(KeyEvent k) {
				if (k.getKeyCode()==KeyEvent.VK_ENTER) {
					Conversation.this.handleMessageSend();
				}
			}

			@Override
			public void keyPressed(KeyEvent arg0) {}
			@Override
			public void keyTyped(KeyEvent arg0) {}
        	
        });
        GridBagConstraints gbc_msgField = new GridBagConstraints();
        gbc_msgField.gridx = 0;
        gbc_msgField.gridy = 3;
        gbc_msgField.anchor = GridBagConstraints.CENTER;
        gbc_msgField.insets = new Insets(0, 0, 5, 0);
        gbc_msgField.gridwidth = 2;
        gbc_msgField.fill = GridBagConstraints.HORIZONTAL;
        frame.getContentPane().add(messageInput, gbc_msgField);
        
        JButton msgSend = new JButton("Send");
        msgSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Conversation.this.handleMessageSend();
			}
        });
        
        GridBagConstraints gbc_msgSend = new GridBagConstraints();
        gbc_msgSend.gridx = 2;
        gbc_msgSend.gridy = 3;
        gbc_msgSend.anchor = GridBagConstraints.CENTER;
        gbc_msgSend.insets = new Insets(0, 0, 5, 0);
        gbc_msgSend.gridwidth = 1;
        gbc_msgSend.fill = GridBagConstraints.HORIZONTAL;
        frame.getContentPane().add(msgSend, gbc_msgSend);
	}
	
	public void handleMessageSend() {
		if (messageInput.getText().trim().equalsIgnoreCase("")) return;
		
		try {
			// TODO actually send the message
			client.sendMessage("adam", "test", "1", null, messageInput.getText());
			Conversation.this.updateMessage("You",messageInput.getText());
			logger.info("Sent message");
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		messageInput.setText("");
		messageInput.requestFocusInWindow();
	}
	
	public void update(String message) throws BadLocationException {
		StyledDocument d = messages.getStyledDocument();
		d.insertString(d.getLength(), message+"\n", new SimpleAttributeSet());
	}

	public synchronized void updateMessage(String name, String message) throws BadLocationException {
		StyledDocument d = messages.getStyledDocument();
        
        SimpleAttributeSet kw = new SimpleAttributeSet();
        StyleConstants.setBold(kw,true);
        
		d.insertString(d.getLength(),name+": ",kw);
		d.insertString(d.getLength(), message+"\n", new SimpleAttributeSet());
	}
}
