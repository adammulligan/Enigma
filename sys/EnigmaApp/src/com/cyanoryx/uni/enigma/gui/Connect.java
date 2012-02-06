package com.cyanoryx.uni.enigma.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.MaskFormatter;

import com.cyanoryx.uni.enigma.net.client.Client;
import com.cyanoryx.uni.enigma.net.protocol.User;
import com.cyanoryx.uni.enigma.net.server.Server;

public class Connect extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8107133128033113815L;
	private JPanel contentPane;
	private JTextField portField;
	private JTextField addressField;
	private int port;
	
	private Server server;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new Connect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public Connect() throws ParseException, IOException {
		Random rng = new Random();
		port = rng.nextInt(100) + 60000;
		server = new Server(port,"localhost");
		new Thread(server).start();
		
		System.out.println("Starting server on port "+port+"...");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 221, 181);
		
		contentPane = new JPanel();
		setContentPane(contentPane);
		
		this.createUI();
		
		setVisible(true);
	}
	
	private void createUI() throws ParseException {
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JTextPane description = new JTextPane();
        description.setEnabled(false);
        description.setText("Enter the IP address of another running Enigma client:\n");
        description.setBackground(contentPane.getBackground());
        description.setForeground(Color.black);
        
		GridBagConstraints gbc_description = new GridBagConstraints();
		gbc_description.insets = new Insets(5, 5, 0, 5);
		gbc_description.gridwidth = 3;
		gbc_description.gridx = 0;
		gbc_description.gridy = 0;
		gbc_description.fill = GridBagConstraints.BOTH;
		contentPane.add(description, gbc_description);
		
		JPanel input_panel = new JPanel();
		input_panel.setLayout(new FlowLayout());
		GridBagConstraints gbc_input_panel = new GridBagConstraints();
		gbc_input_panel.gridx = 0;
		gbc_input_panel.gridy = 1;
		gbc_input_panel.fill = GridBagConstraints.BOTH;
		gbc_input_panel.gridwidth = 3;
		gbc_input_panel.anchor = GridBagConstraints.WEST;
		
		addressField = new JFormattedTextField(new MaskFormatter("###.###.###.###"));
		addressField.setPreferredSize(new Dimension(90,30));
        input_panel.add(addressField);

        JLabel address_port_separator = new JLabel(":");
        input_panel.add(address_port_separator);
        
        portField = new JFormattedTextField(new MaskFormatter("#####"));
        portField.setPreferredSize(new Dimension(60,30));
        input_panel.add(portField);
        
        contentPane.add(input_panel, gbc_input_panel);
        
        JButton connect = new JButton("Connect");
        GridBagConstraints gbc_connect = new GridBagConstraints();
        gbc_connect.gridx = 0;
        gbc_connect.gridy = 2;
        gbc_connect.fill = GridBagConstraints.HORIZONTAL;
        gbc_connect.gridwidth = 3;
        
        connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Connect.this.showLoading();
					System.out.println(addressField.getText()+":"+portField.getText());
					
					Client c = Server.createClient(addressField.getText(), portField.getText(), ""+port, new User("adam"), ""+(new Random().nextInt(100)));
					Connect.this.server.getSessionIndex().addSession(c.getSession());
						
					Connect.this.hideLoading();
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        });
        
        contentPane.add(connect, gbc_connect);
        
        setJMenuBar(this.createMenu());
	}
	
	private JMenuBar createMenu() {
		JMenuBar menu = new JMenuBar();
		
		JMenu file = new JMenu("File");
		file.addSeparator();
		JMenu recent_connections = new JMenu("Recent Connections");
		file.add(recent_connections);
		menu.add(file);
		
		JMenu tools = new JMenu("Tools");
		JMenuItem options = new JMenuItem("Options");
		options.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Preferences();
			}
		});
		tools.add(options);
		menu.add(tools);
		
		return menu;
	}
	
	private void showLoading() throws ParseException {
		contentPane.removeAll();
		contentPane.revalidate();
		
        contentPane.setLayout(new GridLayout(1, 1));
        
        JLabel filler = new JLabel("Connecting...");
        filler.setHorizontalAlignment(JLabel.CENTER);
        contentPane.add(filler);
	}
	
	private void hideLoading() throws ParseException {
		contentPane.removeAll();
		contentPane.revalidate();
		
		this.createUI();
		contentPane.revalidate();
	}
}
