package com.adammulligan.uni;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

/**
 * 
 * 
 * @author adammulligan
 *
 */
public class ConnectWindow {
	static Image oldImage;

	public static void main (String [] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);
		Point wSize = new Point(350,200);
		
		shell.setSize(wSize);
		shell.setText("Connect to a friend");
		shell.setMinimumSize(wSize);
		shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		RowLayout rl = new RowLayout();
		rl.type = SWT.VERTICAL;
		rl.marginLeft = 5;
		rl.marginTop = 5;
		rl.marginRight = 5;
		rl.marginBottom = 5;
		rl.spacing = 0;
		
		shell.setLayout(new GridLayout());
		
		Label l = new Label(shell, SWT.WRAP);
		l.setText("Enigma Chat Client");
		FontData[] fD = l.getFont().getFontData();
		fD[0].setHeight(16);
		l.setForeground(new Color(display,255,255,255));
		l.setFont( new Font(display,fD[0]));
		l.setBounds(wSize.x, wSize.y, wSize.x, wSize.y);
		
		Label blurb = new Label(shell, SWT.WRAP);
		blurb.setText("Enter the IP address of someone who is also running an Enigma client.");
		blurb.setBounds(wSize.x, wSize.y, wSize.x, wSize.y);
		Rectangle clientArea = shell.getClientArea();
		blurb.setBounds(0,0,clientArea.width,clientArea.height);
		blurb.setSize(wSize);
		
		shell.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				Image newImage = new Image(display, "./bg.png");
				GC gc = new GC(newImage);
	        
				gc.dispose();
				shell.setBackgroundImage(newImage);
	        
				if (oldImage != null) oldImage.dispose();
				oldImage = newImage;
			}
	    });
		
		shell.addPaintListener(new PaintListener(){ 
	        public void paintControl(PaintEvent e){ 
	            Rectangle clientArea = shell.getClientArea();

	        	e.gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE)); 
	        	e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
	        	e.gc.setAlpha(200);
	        	
	            e.gc.fillRectangle(5,30,clientArea.width-10,clientArea.height-35); // Background
	            e.gc.drawRectangle(5,30,clientArea.width-10,clientArea.height-35); // Border
	        } 
	    });
		
        //shell.pack();
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
		if (oldImage != null) oldImage.dispose();
		
		display.dispose ();
	}
} 