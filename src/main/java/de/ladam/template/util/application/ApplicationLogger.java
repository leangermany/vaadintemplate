package de.ladam.template.util.application;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum ApplicationLogger {

	INSTANCE;

	private ApplicationLogger() {
	}

	private Logger logger = null;

	public static Logger getLogger() {
		return INSTANCE.logger == null ? INSTANCE.logger = LogManager.getLogger("ApplicationLogger") : INSTANCE.logger;
	}

	public static synchronized void fatal(String msg) {
		getLogger().fatal(msg);
	}

	public static synchronized void error(String msg) {
		getLogger().error(msg);
	}

	public static synchronized void error(Throwable t, String msg) {
		getLogger().log(Level.ERROR, msg, t);
	}

	public static synchronized void warn(String msg) {
		getLogger().warn(msg);
	}

	public static synchronized void info(String msg) {
		getLogger().info(msg);
	}

	public static synchronized void debug(String msg) {
		getLogger().debug(msg);
	}

	public static synchronized void trace(String msg) {
		getLogger().trace(msg);
	}

	public static synchronized void log(Level level, String message) {
		getLogger().log(level, message);
	}

}
