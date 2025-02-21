package util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.nio.file.Path;

public class LogConfig {
	private static boolean initialized = false;

	private LogConfig() {
	}

	public static void configure(final Path path) {
		final ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		final LayoutComponentBuilder standard = builder.newLayout("PatternLayout");
		standard.addAttribute("pattern", "%d{ISO8601} %-5p [%t] %c: %m%n");

		final FilterComponentBuilder consoleThresholdFilter = builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT,
				Filter.Result.DENY);
		consoleThresholdFilter.addAttribute("level", "ERROR");
		final AppenderComponentBuilder consoleAppender = builder.newAppender("console", "Console");
		consoleAppender.add(consoleThresholdFilter);
		consoleAppender.add(standard);
		builder.add(consoleAppender);

		final AppenderComponentBuilder fileAppender = builder.newAppender("vanesa_logfile", "File");
		fileAppender.addAttribute("fileName", path.resolve("vanesa.log").toString());
		fileAppender.addAttribute("append", "false");
		fileAppender.add(standard);
		builder.add(fileAppender);

		final RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.INFO);
		rootLogger.add(builder.newAppenderRef("console"));
		rootLogger.add(builder.newAppenderRef("vanesa_logfile"));
		builder.add(rootLogger);

		if (initialized) {
			Configurator.reconfigure(builder.build());
		} else {
			//noinspection resource
			Configurator.initialize(builder.build());
			initialized = true;
		}
	}
}
