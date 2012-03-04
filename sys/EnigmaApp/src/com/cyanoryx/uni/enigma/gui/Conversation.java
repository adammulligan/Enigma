package com.cyanoryx.uni.enigma.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.cyanoryx.uni.common.Base64;
import com.cyanoryx.uni.enigma.net.protocol.CipherAlgorithm;
import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.utils.AppPrefs;
import com.cyanoryx.uni.enigma.utils.Drawable;
import com.cyanoryx.uni.enigma.utils.Strings;

public class Conversation implements WindowListener{
	private JFrame frame;
	
	private boolean    log_open;
	private LogHandler handler;
	private Logger     logger;
	
	private boolean     cert_open;
	private Certificate cert_window;
	
	private Session session;
	
	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public Conversation(Session s) throws IOException {
		handler = LogHandler.getInstance();
		logger = Logger.getLogger(this.getClass().toString()+"."+s.getID());
		logger.addHandler(handler);
		
		session = s;
		
		Strings.initialise();
		initialise();
		
		getFrame().setVisible(true);
		getFrame().addWindowListener(this);
		getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	private JTextField messageInput;
	private JTextPane  messages;

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	private void initialise() throws IOException {
		this.frame = new JFrame();
		getFrame().setBounds(100, 100, 450, 550);
		getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getFrame().setMinimumSize(new Dimension(350,350));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		getFrame().getContentPane().setLayout(gridBagLayout);
		
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
		btnRegenerate.setIcon(Drawable.loadImage("ic_menu_backup.png"));
		btnRegenerate.setBorder(BorderFactory.createEmptyBorder());
		btnRegenerate.setToolTipText(Strings.translate("conv.toolbar.regen"));
		btnRegenerate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// Force remote user to generate a new key by sending our public key 
					session.sendAuth("cert"  , "agreement", Base64.encodeBytes(new com.cyanoryx.uni.crypto.cert.Certificate(new File("./cert")).toString().getBytes()), Conversation.this.session.getID());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		toolBar.add(btnRegenerate);
		
		JButton btnToggleEnc = new JButton();
		btnToggleEnc.setIcon(Drawable.loadImage("ic_menu_enc.png"));
		btnToggleEnc.setBorder(BorderFactory.createEmptyBorder());
		btnToggleEnc.setToolTipText(Strings.translate("conv.toolbar.toggle_dh"));
		final java.util.prefs.Preferences prefs = new AppPrefs().getPrefs();
		btnToggleEnc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (Conversation.this.session.getAuthenticated()) {
						if (prefs.getBoolean("allow_unauthenticated_conversations",false)) {
							Conversation.this.session.sendAuth("toggle", "streaming", "off", Conversation.this.session.getID());
							Conversation.this.session.setAuthenticated(false);
							Conversation.this.session.setStatus(Session.STREAMING);
							Conversation.this.session.getWindow().update("Conversation no longer encrypted");
						} else {
							JOptionPane.showMessageDialog(Conversation.this.getFrame().getContentPane(),
														  "Your preferences do not allow unecrypted conversations",
														  "Cannot toggle encryption",
														  JOptionPane.WARNING_MESSAGE);
						}
					} else {
						Conversation.this.session.sendAuth("toggle", "streaming", "on", Conversation.this.session.getID());
						Conversation.this.session.setAuthenticated(true);
						session.setCipherType(CipherAlgorithm.AES);
						session.sendAuth("method", "agreement", new AppPrefs().getPrefs().get("default_asym_cipher","RSA"), Conversation.this.session.getID());
						session.sendAuth("cert"  , "agreement", Base64.encodeBytes(new com.cyanoryx.uni.crypto.cert.Certificate(new File("./cert")).toString().getBytes()), Conversation.this.session.getID());
						Conversation.this.session.getWindow().update("Conversation now encrypted");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		toolBar.add(btnToggleEnc);
		
		JButton btnViewCert = new JButton();
		btnViewCert.setIcon(Drawable.loadImage("ic_menu_cert.png"));
		btnViewCert.setBorder(BorderFactory.createEmptyBorder());
		btnViewCert.setToolTipText(Strings.translate("conv.toolbar.view_cert"));
		btnViewCert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					cert_open=!cert_open;
					
					com.cyanoryx.uni.crypto.cert.Certificate cert = new com.cyanoryx.uni.crypto.cert.Certificate(new File("./cert"));
					
					if (cert_window==null) cert_window = new Certificate(cert.toReadable()); 
					cert_window.setState(cert_open);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		toolBar.add(btnViewCert);
		
		toolBar.addSeparator();
		
		JButton btnLog = new JButton();
		btnLog.setIcon(Drawable.loadImage("ic_menu_bug.png"));
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
		
		getFrame().getContentPane().add(toolBar, gbc_toolBar);
        getFrame().getContentPane().setBackground(toolBar.getBackground());
        
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
        getFrame().getContentPane().add(new JSeparator(), gbc);
        
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
        getFrame().getContentPane().add(messages, gbc_textArea);
        
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
        getFrame().getContentPane().add(messageInput, gbc_msgField);
        
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
        getFrame().getContentPane().add(msgSend, gbc_msgSend);
	}
	
	public void handleMessageSend() {
		if (messageInput.getText().trim().equalsIgnoreCase("")) return;
		
		try {
			session.sendMessage(session.getUser().getName(), null, messageInput.getText());
			Conversation.this.updateMessage("You",messageInput.getText());
			logger.info("Sent message");
		} catch (Exception e) {
			logger.info(e.getMessage());
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
	
	public synchronized void updateUser(String name) {
		getFrame().setTitle("Conversation with "+name+" ("+session.getLocalPort()+")");
	}
	
	@Override
	public void windowClosing(WindowEvent arg0) {
		try {
			session.closeStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void windowDeactivated(WindowEvent arg0) {}
	public void windowDeiconified(WindowEvent arg0) {}
	public void windowIconified(WindowEvent arg0) {}
	public void windowOpened(WindowEvent arg0) {}
	public void windowActivated(WindowEvent arg0) {}
	public void windowClosed(WindowEvent arg0) {}

	public JFrame getFrame() { return frame; }
}