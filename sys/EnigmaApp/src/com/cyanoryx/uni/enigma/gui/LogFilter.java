package com.cyanoryx.uni.enigma.gui;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

/**
 * We have no need to filter logged information, but
 * the handler requires a filter.
 * 
 * @author adammulligan
 *
 */
public class LogFilter implements Filter {
	@Override
	public boolean isLoggable(LogRecord arg0) {
		return true;
	}
}
