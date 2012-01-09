package com.cyanoryx.uni.enigma.gui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.eclipse.nebula.widgets.formattedtext.*;

/**
 * 
 * @author adammulligan
 *
 */
@SuppressWarnings("unused")
public class ConnectWindowParent extends Composite {
	static Image oldImage;
	
	FormattedText ipField;
    FormattedText portField;

    ArrayList<Text> fields = new ArrayList<Text>();

    /**
     * Constructor.
     */
    public ConnectWindowParent(Composite parent) {
        this(parent, SWT.NONE);  // must always supply parent
    }
    /**
     * Constructor.
     */
    public ConnectWindowParent(Composite parent, int style) {
        super(parent, style);   // must always supply parent and style
        createGui(parent);
    }

    // partial selection listener
    class MySelectionAdapter implements SelectionListener {
        public void widgetSelected(SelectionEvent e) {
            // default is to do nothing
        }
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    };

	protected void createGui(Composite shell) {
        setLayout(new GridLayout(1, true));
        
        Label l = new Label(this, SWT.WRAP);
		l.setText("Enigma Chat Client");
		FontData[] fD = l.getFont().getFontData();
		fD[0].setHeight(16);
		l.setForeground(new Color(shell.getDisplay(),255,255,255));
		l.setFont( new Font(shell.getDisplay(),fD[0]));
		//l.setBounds(wSize.x, wSize.y, wSize.x, wSize.y);
		
		Label blurb = new Label(this, SWT.WRAP);
		blurb.setText("Enter the IP address and port number of a \nfriend running an Enigma client.");
		blurb.setForeground(new Color(shell.getDisplay(),255,255,255));

        Group entryGroup = new Group(this, SWT.SHADOW_NONE);
        
      //  FontData[] fD = entryGroup.getFont().getFontData();
		//fD[0].setHeight(16);
		//entryGroup.setFont( new Font(shell.getDisplay(),fD[0]));
		//entryGroup.setText("Enigma Chat Client");
		
        GridLayout entryLayout = new GridLayout(2, false);
        entryGroup.setLayout(entryLayout);
        entryGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        //entryGroup.setBackground(new Color(shell.getDisplay(),255,255,255));

        Label lipField = new Label(entryGroup, SWT.LEFT);
        lipField.setText("IP Address:");
        ipField = new FormattedText(entryGroup, SWT.BORDER | SWT.LEFT);
        ipField.setFormatter(new MaskFormatter("###.###.###.###"));
        lipField.setForeground(new Color(shell.getDisplay(),255,255,255));
        
        Label lPortField = new Label(entryGroup, SWT.LEFT);
        lPortField.setForeground(new Color(shell.getDisplay(),255,255,255));
        lPortField.setText("Port:");
        portField = new FormattedText(entryGroup, SWT.BORDER | SWT.LEFT);
        portField.setFormatter(new NumberFormatter("#####"));

        Composite buttons = new Composite(this, SWT.NONE);
        buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        FillLayout buttonLayout = new FillLayout();
        buttons.setLayout(buttonLayout);

        Button okButton = createButton(buttons, "&Connect", "Connect to client",
                                       new MySelectionAdapter() {
                                           public void widgetSelected(SelectionEvent e) {
                                               System.out.println(ipField.getValue());
                                               System.out.println(portField.getValue());
                                           }
                                       });
    }

    protected Button createButton(Composite parent, String label, SelectionListener l) {
        return createButton(parent, label, l);
    }
    
    protected Button createButton(Composite parent, String label, String tip, SelectionListener l) {
        Button b = new Button(parent, SWT.NONE);
        b.setText(label);
        
        if (tip != null) {
            b.setToolTipText(tip);
        }
        
        if (l != null) {
            b.addSelectionListener(l);
        }
        
        return b;
    }

    public static void main(String[] args) {
        final Display display = new Display();
        final Shell shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);
        
        Point wSize = new Point(300,100);
        
        shell.setText("Connect to a friend"); 
		shell.setSize(wSize);
		shell.setMinimumSize(wSize);
		shell.setBackgroundMode(SWT.INHERIT_DEFAULT);

        shell.setLayout(new FillLayout());

        ConnectWindowParent basic = new ConnectWindowParent(shell);
        
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
        
        shell.pack();
        shell.open();

        while ( !shell.isDisposed()) {
            if (!display.readAndDispatch()) { 
                display.sleep();
            }
        }
        display.dispose();
    }
}