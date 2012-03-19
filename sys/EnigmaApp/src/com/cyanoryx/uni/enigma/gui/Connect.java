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
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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

import com.cyanoryx.uni.common.Base64;
import com.cyanoryx.uni.common.FileIO;
import com.cyanoryx.uni.crypto.cert.Certificate;
import com.cyanoryx.uni.crypto.cert.CertificateAuthority;
import com.cyanoryx.uni.crypto.cert.KeyAlgorithm;
import com.cyanoryx.uni.crypto.rsa.KeyGenerator;
import com.cyanoryx.uni.crypto.rsa.PrivateKey;
import com.cyanoryx.uni.crypto.rsa.PublicKey;
import com.cyanoryx.uni.enigma.net.protocol.CipherAlgorithm;
import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.User;
import com.cyanoryx.uni.enigma.net.server.Server;
import com.cyanoryx.uni.enigma.utils.AppPrefs;

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
  private JCheckBox encrypted_option;
  
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
    
    // Check if a default port has been set
    if (new AppPrefs().getPrefs().get("local_port", "").equalsIgnoreCase("")) {
	    port = rng.nextInt(100) + 60000;
    } else {
    	// And if it has, pick a random port in the event it's unavailable
    	try {
    		port = Integer.parseInt(new AppPrefs().getPrefs().get("local_port",""));
    		server = new Server(port,"localhost");
    	} catch (Exception e) {
    		port = rng.nextInt(100) + 60000;
    		server = new Server(port,"localhost");
    	}
    }
    
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
        
        JPanel encrypted_panel = new JPanel();
        encrypted_panel.setLayout(new FlowLayout());
        gbc_constraints.gridx = 0;
        gbc_constraints.gridy = 2;
        gbc_constraints.fill = GridBagConstraints.HORIZONTAL;
        gbc_constraints.gridwidth = 1;
        
        encrypted_option = new JCheckBox("Encrypted?");
        encrypted_option.setSelected(true);
        encrypted_panel.add(encrypted_option);
        
        contentPane.add(encrypted_panel,gbc_constraints);
        
        // == Connect button ==
        JButton connect = new JButton("Connect");
        gbc_constraints.gridx = 1;
        gbc_constraints.gridy = 2;
        gbc_constraints.fill = GridBagConstraints.HORIZONTAL;
        gbc_constraints.gridwidth = 2;
        getRootPane().setDefaultButton(connect);
        
        connect.addActionListener(new ActionListener() {
	      @Override
	      public void actionPerformed(ActionEvent arg0) {
	        Connect.this.connect(addressField.getText(),portField.getText());
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
    
    final JMenu recent_connections = new JMenu("Recent Connections");
    
    String[] ips = new AppPrefs().getLastConnections();
    
    for (final String i : ips) {
      if (i.equalsIgnoreCase("")) continue;
      JMenuItem ip = new JMenuItem(i);
      ip.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          String[] ip      = i.split(":");
          String   address = ip[0];
          String   port    = ip[1];
          Connect.this.connect(address, port);
        }
      });
      recent_connections.add(ip);
    }
    
    final JMenuItem clear_recent_connections = new JMenuItem("Clear");
    clear_recent_connections.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			recent_connections.removeAll();
			new AppPrefs().clearLastConnections();
			
			recent_connections.addSeparator();
		    recent_connections.add(clear_recent_connections);
		}
    });
    
    recent_connections.addSeparator();
    recent_connections.add(clear_recent_connections);
    
    file.add(recent_connections);
    
    menu.add(file);
    
    JMenu tools = new JMenu("Tools");
    
    JMenuItem key_gen = new JMenuItem("Generate Keys...");
    key_gen.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
    	java.util.prefs.Preferences prefs = new AppPrefs().getPrefs();
    	
    	if (prefs.get("ca_key_location","").equalsIgnoreCase("")) {
    		JOptionPane.showMessageDialog(Connect.this,
					"Could not find CA keys.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
    		return;
    	}
    	  
    	KeyGenerator kg = new KeyGenerator(1024);
  		
  		BigInteger[] pair = kg.generatePair();
  		
  		String priv_key = pair[0]+","+pair[2];
  		String pub_key  = pair[0]+","+pair[1];
  		
  		String output_loc = "./";
  		
  		try {
  			String pub_key_loc = prefs.get("ca_key_location","");
  			String priv_key_loc = pub_key_loc.split(".pub")[0];
  			
  			CertificateAuthority ca = new CertificateAuthority(new PublicKey(new File(pub_key_loc)),new PrivateKey(new File(priv_key_loc)));
  			
  			FileIO.writeFile(priv_key,new File(output_loc+"id_rsa"));
  			FileIO.writeFile(pub_key,new File(output_loc+"id_rsa.pub"));
  			
  			prefs.put("priv_key_location", output_loc+"id_rsa");
  			
  			Certificate c = ca.generate(prefs.get("default_asym_cipher", "rsa").getBytes(),
						  new PublicKey(pub_key),
						  pub_key.getBytes(),
						  prefs.get("local_user_name", "User"),
						  Calendar.getInstance(),
						  new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR)+3, 01, 01));
  			
  			FileIO.writeFile(c.toString(),new File(output_loc+"cert"));
  			
  			JOptionPane.showMessageDialog(Connect.this,
					"Keys saved to "+output_loc,
					"Saved",
					JOptionPane.INFORMATION_MESSAGE);
  		} catch (Exception e) {
  			e.printStackTrace();
  			JOptionPane.showMessageDialog(Connect.this,
						"Could not generate keys.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
  		}
      }
    });
    tools.add(key_gen);
    
    tools.addSeparator();
    
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
  
  private void connect(String address, String remote_port) {
    try {
      Connect.this.showLoading();
      
      String id = ""+(new Random().nextInt(100));
      
      java.util.prefs.Preferences prefs = new AppPrefs().getPrefs();
      
      boolean use_auth=encrypted_option.isSelected();
      if (!encrypted_option.isSelected()) {
    	  if (!prefs.getBoolean("allow_unauthenticated_conversations", false)) {
    		  use_auth=false;
    		  JOptionPane.showMessageDialog(Connect.this,
    				  						"Your preferences do not allow unencrypted conversations. Defaulting to encrypted.",
                      						"Error",
                      						JOptionPane.ERROR_MESSAGE);
    	  }
      }
      
      Session session = Server.createClient(address,
                        remote_port,
                        ""+port,
                        new User("remote user"),
                        id);
      Connect.this.server.getSessionIndex().addSession(session);
      
      session.setAuthenticated(use_auth);
      session.setAgreementType(KeyAlgorithm.searchByID(prefs.get("default_asym_cipher","RSA")));
      session.setCipherType(CipherAlgorithm.searchByID(prefs.get("default_sym_cipher","AES")));
      
      session.sendAuth("method",
                       "agreement",
                       prefs.get("default_asym_cipher","RSA"),
                       id);
      session.sendAuth("cert",
                       "agreement",
                       Base64.encodeBytes(new Certificate(new File(new AppPrefs().getPrefs().get("key_location","./cert")))
                                              .toString()
                                              .getBytes()),
                       id);
      
      prefs.put("last_connections",
                 address+":"+port+";"+prefs.get("last_connections",""));
    } catch (Exception e) {
      JOptionPane.showMessageDialog(Connect.this,
                                    "Could not connect - "+
                                    e.getCause()+" - "+
                                    e.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
    } finally {
      try {
        Connect.this.recreateUI();
      } catch (ParseException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
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
