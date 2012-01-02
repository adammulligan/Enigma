package com.adammulligan.uni.net;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.wb.swt.SWTResourceManager;

public class Preferences {

	protected Shell shlPreferences;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Preferences window = new Preferences();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlPreferences.open();
		shlPreferences.layout();
		while (!shlPreferences.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlPreferences = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN);
		shlPreferences.setSize(600, 520);
		shlPreferences.setText("Preferences");
		shlPreferences.setLayout(new GridLayout(1, false));
		
		TabFolder tabFolder = new TabFolder(shlPreferences, SWT.NONE);
		GridData gd_tabFolder = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
		gd_tabFolder.heightHint = 600;
		gd_tabFolder.widthHint = 550;
		tabFolder.setLayoutData(gd_tabFolder);
		
		TabItem tbtmGeneral = new TabItem(tabFolder, SWT.NONE);
		tbtmGeneral.setText("General");
		tbtmGeneral.setImage(null);
		
		TabItem tbtmPersonal = new TabItem(tabFolder, SWT.NONE);
		tbtmPersonal.setText("Personal");
		
		TabItem tbtmEncryption = new TabItem(tabFolder, SWT.NONE);
		tbtmEncryption.setText("Security");

	}

	protected void focus() {
		System.out.println("Test");
	}
}
