/***************************************************************
 * Copyright (c) Benjamin Kormeier 2006-2008.                  *
 * All rights reserved.                                        *
 ***************************************************************/
package configurations;

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.xml.DOMConfigurator;


/**
 * @author Benjamin Kormeier
 * @version 1.00 05.07.2008
 */
public class ConfigureLog4j
{
	private static final PatternLayout LAYOUT=new PatternLayout("%d{ISO8601} %-5p [%t] %c: %m%n");
	private static final String DEFAULT_MAX_FILE_SIZE=new String("12MB");
	
	private static String network_editor_logfile=new String("logfiles/network_editor.log");
	private static String axis_logfile=new String("logfiles/apache_axis.log");
	private static String axiom_logfile=new String("logfiles/apache_axiom.log" );
	private static String commons_logfile=new String("logfiles/apache_commons.log");
	private static String http_logfile=new String("logfiles/http_client.log");
	
	private static Logger network_editor_logger = Logger.getRootLogger();
	private static Logger axis_logger=Logger.getLogger("org.apache.axis2");
	private static Logger commons_logger=Logger.getLogger("org.apache.axiom");
	private static Logger axiom_logger=Logger.getLogger("org.apache.commons");
	private static Logger http_logger=Logger.getLogger("httpclient");
	
	/**
	 * Default configuration for file logging using file appender.
	 * @param logger
	 * @param logfile
	 * @param maxFileSize
	 * @param level
	 * @throws IOException
	 */
	private static void configureFileLogging(Logger logger, String logfile, String maxFileSize, Level level) throws IOException
	{
		RollingFileAppender appender=new RollingFileAppender(LAYOUT, logfile, true);
		// TODO MF: appender.setMaxFileSize(maxFileSize);

		logger.setLevel(level);
		logger.addAppender(appender);
	}
	
	/**
	 * Default configuration for console logging.
	 * @param logger
	 * @param level
	 */
	private static void configureConsoleLogging(Logger logger, Level level)
	{
		ConsoleAppender appender=new ConsoleAppender();
		appender.setLayout(LAYOUT);

		logger.setLevel(level);
		logger.addAppender(appender);
	}
	
	/**
	 * Default logging configuration for network editor. Including logger for Apache Axis, Apache Axiom,
	 * Apache Commons and Apache HTTP Client.
	 * @param log2file - If <b>true</b> all log information will be stored in the <i>logfiles</i> directory.<br>
	 * Else console logging is activated.
	 * @throws IOException
	 */
	public static void defaultLogging(boolean log2file) throws IOException
	{
		if(log2file)
		{
			configureFileLogging(axis_logger, axis_logfile,DEFAULT_MAX_FILE_SIZE,Level.WARN);
			configureFileLogging(axiom_logger, axiom_logfile,DEFAULT_MAX_FILE_SIZE,Level.WARN);
			configureFileLogging(commons_logger, commons_logfile,DEFAULT_MAX_FILE_SIZE,Level.WARN);
			configureFileLogging(http_logger, http_logfile,DEFAULT_MAX_FILE_SIZE,Level.WARN);
			configureFileLogging(network_editor_logger, network_editor_logfile,DEFAULT_MAX_FILE_SIZE,Level.INFO);
		}
		else
		{
			configureConsoleLogging(axis_logger, Level.WARN);
			configureConsoleLogging(axiom_logger,Level.WARN);
			configureConsoleLogging(commons_logger,Level.WARN);
			configureConsoleLogging(http_logger, Level.WARN);
			configureConsoleLogging(network_editor_logger, Level.INFO);
		}
	}
	
	/**
	 * Load log4j XML configuration from given file.
	 * @param file XML - configuration file.
	 */
	public static void loadLog4jXMLConfiguration(String file)
	{
		DOMConfigurator.configure(file);
	}
}
