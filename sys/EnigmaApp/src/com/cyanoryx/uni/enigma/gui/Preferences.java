package com.cyanoryx.uni.enigma.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.cyanoryx.uni.enigma.utils.AppPrefs;

/**
 * Preferences Swing GUI
 * 
 * @author adammulligan
 *
 */
public class Preferences extends JFrame {
	private static final long serialVersionUID = -7720258639613136149L;
	private JPanel contentPane;
	
	private java.util.prefs.Preferences prefs;

	/**
	 * Create the frame.
	 */
	public Preferences() {
		prefs = new AppPrefs().getPrefs();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane preference_wrapper = new JTabbedPane();
		preference_wrapper.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		// GENERAL
		
		JPanel locale_panel = new JPanel(new GridLayout(7,2));
		
			JLabel lbl_locale = new JLabel("Locale (locale file must exist; restart required): ");
			final JTextField locale = new JTextField(prefs.get("locale", "en"));
			locale.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent arg0) { update_prefs(); }
				@Override
				public void insertUpdate(DocumentEvent arg0) { update_prefs(); }
				@Override
				public void removeUpdate(DocumentEvent arg0) { update_prefs(); }
				
				public void update_prefs() {
					Preferences.this.prefs.put("locale", locale.getText());
				}
			});
			locale_panel.add(lbl_locale);
			locale_panel.add(locale);
		
		preference_wrapper.addTab("General", null, locale_panel, "");
		
		// PERSONAL
		
		JPanel personal_panel = new JPanel(new GridLayout(7,2));
		
			JLabel lbl_local_user_name = new JLabel("Name: ");
			final JTextField local_user_name = new JTextField(prefs.get("local_user_name", "Kim"));
			local_user_name.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent arg0) { update_prefs(); }
				@Override
				public void insertUpdate(DocumentEvent arg0) { update_prefs(); }
				@Override
				public void removeUpdate(DocumentEvent arg0) { update_prefs(); }
				
				public void update_prefs() {
					Preferences.this.prefs.put("local_user_name", local_user_name.getText());
				}
			});
			personal_panel.add(lbl_local_user_name);
			personal_panel.add(local_user_name);
		
		preference_wrapper.addTab("Personal", null, personal_panel, "");

		// SECURITY

		JTabbedPane security_wrapper = new JTabbedPane();
		security_wrapper.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		JPanel security_panel1 = new JPanel();
	
			JCheckBox allow_unauthenticated_conversations = new JCheckBox("Allow unauthenticated conversations?");
			allow_unauthenticated_conversations.setSelected(prefs.getBoolean("allow_unauthenticated_conversations", false));
			allow_unauthenticated_conversations.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					Preferences.this.prefs.putBoolean("allow_unauthenticated_conversations", (arg0.getStateChange()==1) ? true : false);
				}
			});
			security_panel1.add(allow_unauthenticated_conversations);
			
			JCheckBox require_cert = new JCheckBox("Require valid certificate?");
			require_cert.setSelected(prefs.getBoolean("require_cert", false));
			require_cert.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					Preferences.this.prefs.putBoolean("require_cert", (arg0.getStateChange()==1) ? true : false);
					System.out.println("Changed require_cert to "+Preferences.this.prefs.getBoolean("require_cert", false));
				}
			});
			security_panel1.add(require_cert);
	
		security_wrapper.addTab("General", null, security_panel1, "");

		JPanel security_panel2 = new JPanel(new GridLayout(7,2));
		
				  String[]  default_asym_cipher_choices = { "RSA" };
				  JLabel    default_asym_cipher_label   = new JLabel("Default agreement method: ");
			final JComboBox default_asym_cipher         = new JComboBox(default_asym_cipher_choices);
			
			default_asym_cipher.setSelectedItem(Preferences.this.prefs.get("default_asym_cipher","RSA"));
			default_asym_cipher.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Preferences.this.prefs.put("default_asym_cipher", default_asym_cipher.getSelectedItem().toString());
				}
			});
			
			// NOTE
			// key_location is a misnomer due to changes in design. key_location is actually the location
			// of the user's certificate
				  JLabel     key_location_label = new JLabel("Certificate Location: ");
			final JTextField key_location		= new JTextField(new AppPrefs().getPrefs().get("key_location",""));
			
			key_location.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent arg0) { update_prefs(); }
				@Override
				public void insertUpdate(DocumentEvent arg0) { update_prefs(); }
				@Override
				public void removeUpdate(DocumentEvent arg0) { update_prefs(); }
				
				public void update_prefs() {
					Preferences.this.prefs.put("key_location", key_location.getText());
				}
			});
			
				  JLabel     priv_key_location_label = new JLabel("Private Key Location: ");
			final JTextField priv_key_location       = new JTextField(new AppPrefs().getPrefs().get("priv_key_location",""));
			
			priv_key_location.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent arg0) { update_prefs(); }
				@Override
				public void insertUpdate(DocumentEvent arg0) { update_prefs(); }
				@Override
				public void removeUpdate(DocumentEvent arg0) { update_prefs(); }
				
				public void update_prefs() {
					Preferences.this.prefs.put("priv_key_location", priv_key_location.getText());
				}
			});
			
			      JLabel     ca_key_location_label  = new JLabel("CA Public-Key Location: ");
			final JTextField ca_key_location		= new JTextField(new AppPrefs().getPrefs().get("ca_key_location",""));
				
			ca_key_location.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent arg0) { update_prefs(); }
				@Override
				public void insertUpdate(DocumentEvent arg0) { update_prefs(); }
				@Override
				public void removeUpdate(DocumentEvent arg0) { update_prefs(); }
				
				public void update_prefs() {
					Preferences.this.prefs.put("ca_key_location", ca_key_location.getText());
				}
			});
			
			security_panel2.add(default_asym_cipher_label);
			security_panel2.add(default_asym_cipher);
			security_panel2.add(key_location_label);
			security_panel2.add(key_location);
			security_panel2.add(priv_key_location_label);
			security_panel2.add(priv_key_location);
			security_panel2.add(ca_key_location_label);
			security_panel2.add(ca_key_location);
		
		security_wrapper.addTab("Key Agreement", null, security_panel2, "");

		JPanel security_panel3 = new JPanel(new GridLayout(7,2));
		
				  String[]  default_sym_cipher_choices = { "AES" };
				  JLabel    default_sym_cipher_label   = new JLabel("Default key agreement method: ");
			final JComboBox default_sym_cipher         = new JComboBox(default_sym_cipher_choices);
			
			default_sym_cipher.setSelectedItem(Preferences.this.prefs.get("default_sym_cipher","AES"));
			default_sym_cipher.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Preferences.this.prefs.put("default_sym_cipher", default_sym_cipher.getSelectedItem().toString());
				}
			});
			
			security_panel3.add(default_sym_cipher_label);
			security_panel3.add(default_sym_cipher);
		
		security_wrapper.addTab("Cipher", null, security_panel3, "");

		preference_wrapper.addTab("Security", null, security_wrapper, "");
		
		contentPane.add(preference_wrapper);
		
		setVisible(true);
	}
}
