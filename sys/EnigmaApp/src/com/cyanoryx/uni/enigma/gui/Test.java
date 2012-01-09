    
/*
Logging In Java with the JDK 1.4 Logging API and Apache log4j
by Samudra Gupta    
Apress Copyright 2003 
ISBN:1590590996

*/

import java.util.logging.*;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

class LogWindow extends JFrame {
  private int width;

  private int height;

  private JTextArea textArea = null;

  private JScrollPane pane = null;

  public LogWindow(String title, int width, int height) {
    super(title);
    setSize(width, height);
    textArea = new JTextArea();
    pane = new JScrollPane(textArea);
    getContentPane().add(pane);
    setVisible(true);
  }

  /**
   * This method appends the data to the text area.
   * 
   * @param data
   *            the Logging information data
   */
  public void showInfo(String data) {
    textArea.append(data);
    this.getContentPane().validate();
  }
}

class WindowHandler extends Handler {
  //the window to which the logging is done
  private LogWindow window = null;

  private Formatter formatter = null;

  private Level level = null;

  //the singleton instance
  private static WindowHandler handler = null;

  /**
   * private constructor, preventing initialization
   */
  private WindowHandler() {
    configure();
    if (window == null)
      window = new LogWindow("Logging window", 500, 200);
  }

  /**
   * The getInstance method returns the singleton instance of the
   * WindowHandler object It is synchronized to prevent two threads trying to
   * create an instance simultaneously. @ return WindowHandler object
   */

  public static synchronized WindowHandler getInstance() {

    if (handler == null) {
      handler = new WindowHandler();
    }
    return handler;
  }

  /**
   * This method loads the configuration properties from the JDK level
   * configuration file with the help of the LogManager class. It then sets
   * its level, filter and formatter properties.
   */
  private void configure() {
    LogManager manager = LogManager.getLogManager();
    String className = this.getClass().getName();
    String level = manager.getProperty(className + ".level");
    String filter = manager.getProperty(className + ".filter");
    String formatter = manager.getProperty(className + ".formatter");

    //accessing super class methods to set the parameters
    setLevel(level != null ? Level.parse(level) : Level.INFO);
    setFilter(makeFilter(filter));
    setFormatter(makeFormatter(formatter));

  }

  /**
   * private method constructing a Filter object with the filter name.
   * 
   * @param filterName
   *            the name of the filter
   * @return the Filter object
   */
  private Filter makeFilter(String filterName) {
    Class c = null;
    Filter f = null;
    try {
      c = Class.forName(filterName);
      f = (Filter) c.newInstance();
    } catch (Exception e) {
      System.out.println("There was a problem to load the filter class: "
          + filterName);
    }
    return f;
  }

  /**
   * private method creating a Formatter object with the formatter name. If no
   * name is specified, it returns a SimpleFormatter object
   * 
   * @param formatterName
   *            the name of the formatter
   * @return Formatter object
   */
  private Formatter makeFormatter(String formatterName) {
    Class c = null;
    Formatter f = null;

    try {
      c = Class.forName(formatterName);
      f = (Formatter) c.newInstance();
    } catch (Exception e) {
      f = new SimpleFormatter();
    }
    return f;
  }

  /**
   * This is the overridden publish method of the abstract super class
   * Handler. This method writes the logging information to the associated
   * Java window. This method is synchronized to make it thread-safe. In case
   * there is a problem, it reports the problem with the ErrorManager, only
   * once and silently ignores the others.
   * 
   * @record the LogRecord object
   *  
   */
  public synchronized void publish(LogRecord record) {
    String message = null;
    //check if the record is loggable
    if (!isLoggable(record))
      return;
    try {
      message = getFormatter().format(record);
    } catch (Exception e) {
      reportError(null, e, ErrorManager.FORMAT_FAILURE);
    }

    try {
      window.showInfo(message);
    } catch (Exception ex) {
      reportError(null, ex, ErrorManager.WRITE_FAILURE);
    }

  }

  public void close() {
  }

  public void flush() {
  }
}

public class CustomHandlerDemo {
  private WindowHandler handler = null;

  private Logger logger = null;

  public CustomHandlerDemo() {
    handler = WindowHandler.getInstance();
    //obtaining a logger instance and setting the handler
    logger = Logger.getLogger("sam.logging.handler");
    logger.addHandler(handler);
  }

  /**
   * This method publishes the log message
   */
  public void logMessage() {
    logger.info("Hello from WindowHandler...");
  }

  public static void main(String args[]) {
    //logging with the help of a logger
    CustomHandlerDemo demo = new CustomHandlerDemo();
    demo.logMessage();
    //using the handler.publish() to log
    WindowHandler h = WindowHandler.getInstance();
    LogRecord r = new LogRecord(Level.WARNING,
        "The Handler publish method...");
    h.publish(r);
  }
}