package gr.watchful.permchecker.logging;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.*;

public class MyLogger {
	static private FileHandler fileHandler;
	static private ConsoleHandler consoleHandler;
	static private SimpleFormatter formatter;

	static public void setup() throws IOException {
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.setUseParentHandlers(false);

		logger.setLevel(Level.INFO);
		fileHandler = new FileHandler("Logging.txt");
		consoleHandler = new ConsoleHandler();

		formatter = new SimpleFormatter() {
			@Override
			public String format(LogRecord record) {
				return "("+
						new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).format(new Date(record.getMillis()))+
						") - "+
						record.getLevel()+
						" - "+
						record.getMessage()+
						"\n";
			}
		};
		fileHandler.setFormatter(formatter);
		logger.addHandler(fileHandler);
		consoleHandler.setFormatter(formatter);
		logger.addHandler(consoleHandler);
	}
}