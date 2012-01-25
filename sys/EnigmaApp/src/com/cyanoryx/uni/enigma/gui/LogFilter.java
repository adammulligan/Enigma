package com.cyanoryx.uni.enigma.gui;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class LogFilter implements Filter {
	@Override
	public boolean isLoggable(LogRecord arg0) {
		return true;
	}
}
