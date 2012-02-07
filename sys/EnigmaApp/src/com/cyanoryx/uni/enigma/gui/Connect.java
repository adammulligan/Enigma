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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.MaskFormatter;

import com.cyanoryx.uni.enigma.net.protocol.User;
import com.cyanoryx.uni.enigma.net.server.Server;

/**
 * Base window for Enigma application.
 * Requires no inputs or parameters.
 * 
 * @author adammulligan
 *
 */
public class Connect extends JFrame {
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
	 * Create the underlying frame and start up the local server.
	 * 
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public Connect() throws ParseException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		Random rng = new Random();
		port = rng.nextInt(100) + 60000;
		server = new Server(port,"localhost");
		new Thread(server).start();
		
		System.out.println("Starting server on port "+port+"...");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 221, 181);
		
		this.setTitle("localhost:"+port);
		contentPane = new JPanel();
		setContentPane(contentPane);
		
		this.createUI();
		
		setVisible(true);
	}
	
	/**
	 * Creates basic window UI
	 * 
	 * GridBagLayout[
	 * 
	 * @throws ParseException
	 */
	private void createUI() throws ParseException {
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		// == Description text ==
		JTextPane description = new JTextPane();
        description.setEnabled(false);
        description.setText("Enter the IP address of another running Enigma client:\n");
        description.setBackground(contentPane.getBackground());
        description.setForeground(Color.black);
        
        GridBagConstraints gbc_constraints = new GridBagConstraints();
        
		gbc_constraints.insets = new Insets(5, 5, 0, 5);
		gbc_constraints.gridwidth = 3;
		gbc_constraints.gridx = 0;
		gbc_constraints.gridy = 0;
		gbc_constraints.fill = GridBagConstraints.BOTH;
		contentPane.add(description, gbc_constraints);
		
		// == Panel for inputs ==
		JPanel input_panel = new JPanel();
		input_panel.setLayout(new FlowLayout());
		gbc_constraints.gridx = 0;
		gbc_constraints.gridy = 1;
		gbc_constraints.fill = GridBagConstraints.BOTH;
		gbc_constraints.gridwidth = 3;
		gbc_constraints.anchor = GridBagConstraints.WEST;
		
		// == IP address field ==
		addressField = new JFormattedTextField(new MaskFormatter("###.###.###.###"));
		addressField.setText("127.000.000.001");
		addressField.setPreferredSize(new Dimension(90,30));
        input_panel.add(addressField);

        JLabel address_port_separator = new JLabel(":");
        input_panel.add(address_port_separator);
        
        // == Port field ==
        portField = new JTextField();
        portField.setPreferredSize(new Dimension(60,30));
        input_panel.add(portField);
        
        contentPane.add(input_panel, gbc_constraints);
        
        // == Connect button ==
        JButton connect = new JButton("Connect");
        gbc_constraints.gridx = 0;
        gbc_constraints.gridy = 2;
        gbc_constraints.fill = GridBagConstraints.HORIZONTAL;
        gbc_constraints.gridwidth = 3;
        getRootPane().setDefaultButton(connect);
        
        connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Connect.this.showLoading();
					Connect.this.server.getSessionIndex()
									   .addSession(Server.createClient(addressField.getText(),
																       portField.getText(),
																       ""+port, new User("adam"),
																       ""+(new Random().nextInt(100))));
				} catch (Exception e) {
					JOptionPane.showMessageDialog(Connect.this, "Could not connect - "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				} finally {
					try {
						Connect.this.recreateUI();
					} catch (ParseException e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			}
        });
        
        contentPane.add(connect, gbc_constraints);
        
        setJMenuBar(this.createMenu());
	}
	
	/**
	 * Clear the window and recreate the UI
	 * 
	 * @throws ParseException 
	 * 
	 */
	private void recreateUI() throws ParseException {
		contentPane.removeAll();
		contentPane.revalidate();
		
		this.createUI();
		
		contentPane.revalidate();
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
	
	/**
	 * Clears the window and displays a loading txt to indicate a connection 
	 * attempt is being made
	 * 
	 * @throws ParseException
	 */
	private void showLoading() throws ParseException {
		contentPane.removeAll();
		contentPane.revalidate();
		
        contentPane.setLayout(new GridLayout(1, 1));
        
        JLabel filler = new JLabel("Connecting...");
        filler.setHorizontalAlignment(JLabel.CENTER);
        contentPane.add(filler);
	}
}
