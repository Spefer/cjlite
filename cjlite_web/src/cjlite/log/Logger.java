package cjlite.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.ExtendedLogger;

/**
 * Simple Single Source File Logger <br>
 * default Log Leve is level.warn If you want to log other level message, please set new LogCofing you want <br>
 * <br>
 * 
 * @author kevin
 * @version 0.3
 */
public abstract class Logger {

	//
	final String loggerName;

	Logger(String loggerName) {
		this.loggerName = loggerName;
	}

	/**
	 * automatically log Method entry message with debug Level
	 */
	public void entry() {
		this.debug("___________ method - entry ___________");
	}

	/**
	 * automatically log Method exit message with debug Level
	 */
	public void exit() {
		this.debug("___________ method - exit ___________");
	}

	// ====================== Trace Part ======================
	/**
	 * isTraceEnabled
	 * 
	 * @return
	 */
	public abstract boolean isTraceEnabled();

	public void trace(String msg) {
		this.trace(msg, null, new Object[] {});
	}

	public void trace(String msg, Throwable throwable) {
		this.trace(msg, throwable, new Object[] {});
	}

	public void trace(String msg, Object... values) {
		this.trace(msg, null, values);
	}

	public abstract void trace(String msg, Throwable throwable, Object... values);

	// ====================== Trace Part ======================

	// ====================== Debug Part ======================

	public abstract boolean isDebugEnabled();

	public void debug(String msg) {
		this.debug(msg, null, new Object[] {});
	}

	public void debug(String msg, Throwable throwable) {
		this.debug(msg, throwable, new Object[] {});
	}

	public void debug(String msg, Object... values) {
		this.debug(msg, null, values);
	}

	public abstract void debug(String msg, Throwable throwable, Object... values);

	// ====================== Debug Part ======================
	// ====================== Info Part ======================

	public abstract boolean isInfoEnabled();

	public void info(String msg) {
		this.info(msg, null, new Object[] {});
	}

	public void info(String msg, Throwable throwable) {
		this.info(msg, throwable, new Object[] {});
	}

	public void info(String msg, Object... values) {
		this.info(msg, null, values);
	}

	public abstract void info(String msg, Throwable throwable, Object... values);

	// ====================== Info Part ======================
	// ====================== Warn Part ======================

	public abstract boolean isWarnEnabled();

	public void warn(String msg) {
		this.warn(msg, null, new Object[] {});
	}

	public void warn(String msg, Throwable throwable) {
		this.warn(msg, throwable, new Object[] {});
	}

	public void warn(String msg, Object... values) {
		this.warn(msg, null, values);
	}

	public abstract void warn(String msg, Throwable throwable, Object... values);

	// ====================== Warn Part ======================
	// ====================== Error Part ======================

	public abstract boolean isErrorEnabled();

	public void error(String msg) {
		this.error(msg, null, new Object[] {});
	}

	public void error(String msg, Throwable throwable) {
		this.error(msg, throwable, new Object[] {});
	}

	public void error(String msg, Object... values) {
		this.error(msg, null, values);
	}

	public abstract void error(String msg, Throwable throwable, Object... values);

	// ====================== Error Part ======================
	// ====================== Print Part ======================
	// leave for future
	public abstract void printf(String msg, Object... values);

	// ====================== Print Part ======================
	/**
	 * Get this class Logger
	 * 
	 * @return
	 */
	public static Logger thisClass() {
		String loggerName = Thread.currentThread().getStackTrace()[2].getClassName();
		return LoggerFactory.getInstance().getLogger(loggerName);
	}

	/**
	 * Get Logger by name
	 * 
	 * @return
	 */
	public static Logger getLogger(String loggerName) {
		return LoggerFactory.getInstance().getLogger(loggerName);
	}

	/**
	 * Get Logger by name
	 * 
	 * @return
	 */
	public static Logger getLogger(Class<?> logClass) {
		return LoggerFactory.getInstance().getLogger(logClass.getName());
	}

}

abstract class LoggerFactory {

	/**
	 * LoggerFactory Name
	 */
	private String name;

	LoggerFactory(String name) {
		this.name = name;
	}

	/**
	 * return Logger Factory name
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.name;
	}

	/**
	 * Initial the logger factory
	 */
	abstract void initial();

	/**
	 * Get Logger by given logger name
	 * 
	 * @param loggerName
	 * @return
	 */
	abstract Logger getLogger(String loggerName);

	// static ================================================================
	private static final String log4JClassName = "org.apache.logging.log4j.Logger";

	private static final String loggerPropsFile = "cjlite.logger.properties";

	private static LoggerFactory logFactory;

	private static boolean hasLog4J2 = checkLog4J();

	private static Object lock = new Object();

	private static boolean initialized = false;

	public static LoggerFactory getInstance() {
		if (logFactory == null) {
			synchronized (lock) {
				buildLogerFactory();
			}
		}
		return logFactory;
	}

	private synchronized static void buildLogerFactory() {
		if (initialized) {
			return;
		}

		Properties props = loadConfigProperties();

		boolean useSimpleLogger = false;
		if (props != null) {
			useSimpleLogger = Boolean.parseBoolean(props.getProperty("cjlite.logger.useSimpleLogger", "false"));
			if (useSimpleLogger) {
				logFactory = new SimpleLoggerFactory(props);
				logFactory.initial();
				initialized = true;
				return;
			}
		}

		if (!hasLog4J2) {
			logFactory = new SimpleLoggerFactory(props);
			logFactory.initial();
		} else {
			logFactory = new Log4jFactory();
			logFactory.initial();
		}

	}

	private static Properties loadConfigProperties() {
		try {
			InputStream inStream = LoggerFactory.class.getClassLoader().getResourceAsStream(loggerPropsFile);
			if (inStream == null) {
				return null;
			}
			Properties props = new Properties();
			props.load(inStream);
			return props;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Check whether have log4j logger in class path
	 * 
	 * @return
	 */
	private static boolean checkLog4J() {
		try {
			Class.forName(log4JClassName);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	// ================================================================
}

/**
 * @author kevin
 */
enum Level {

	Off(0), Error(200), Warn(300), Info(400), Debug(500), Trace(600), All(Integer.MAX_VALUE);

	private int level;

	private Level(int _level) {
		this.level = _level;
	}

	static Level from(String value) {
		for (Level level : Level.values()) {
			if (level.name().equalsIgnoreCase(value)) {
				return level;
			}
		}
		return Off;
	}

	boolean lessEqual(Level logLevel) {
		if (logLevel == null) {
			return false;
		}
		return level <= logLevel.level;
	}

	@Override
	public String toString() {
		return this.name();
	}

	public static Level parse(String name) {
		for (Level level : Level.values()) {
			if (level.name().equalsIgnoreCase(name)) {
				return level;
			}
		}
		return null;
	}

}

/**
 * Simple logger Factory
 * 
 * @author kevin
 * 
 */
class SimpleLoggerFactory extends LoggerFactory {

	static final String simplePrefix = "simplelogger";

	static final String propPrefix = "simplelogger.appender";

	private LogConfig logConfig;

	private ConcurrentMap<String, Logger> loggerMap = new ConcurrentHashMap<String, Logger>();

	private LogAppenderPipeline appenderPipeline;

	// Simple
	private Properties props;

	SimpleLoggerFactory(Properties props) {
		super("SimpleLoggerFactory");
		this.props = props;
	}

	@Override
	Logger getLogger(String loggerName) {
		Logger logger = loggerMap.get(loggerName);
		if (logger == null) {
			logger = new SimpleLogger(loggerName, this);
			loggerMap.putIfAbsent(loggerName, logger);
		}
		return logger;
	}

	@Override
	void initial() {
		Properties loaded = getDefaultProperties();
		if (props != null) {
			loaded.putAll(props);
		}
		logConfig = new LogConfig(loaded);
		appenderPipeline = new LogAppenderPipeline(this.logConfig);
	}

	private Properties getDefaultProperties() {
		Properties props = new Properties();
		props.put(simplePrefix + ".level", "trace");
		props.put(propPrefix, "stdout");
		props.put(propPrefix + ".stdout.asyn", "false");
		props.put(propPrefix + ".stdout.level", "trace");
		props.put(propPrefix + ".stdout.pattern", "%d - [%p][%t] %C#%M(L:%l): %m%n%e");
		props.put(propPrefix + ".stdout.timePattern", "MM-dd HH:mm:ss.SSS");
		

		return props;
	}

	public LogAppenderPipeline getAppenderPipeline() {
		return this.appenderPipeline;
	}

	public LogConfig getLogConfig() {
		return this.logConfig;
	}

}

class Log4jFactory extends LoggerFactory {

	private ConcurrentMap<String, Logger> loggerMap = new ConcurrentHashMap<String, Logger>();

	Log4jFactory() {
		super("Log4jFactory");
	}

	@Override
	void initial() {
	}

	@Override
	Logger getLogger(String loggerName) {
		Logger logger = loggerMap.get(loggerName);
		if (logger == null) {
			logger = new Log4jLogger(loggerName);
			loggerMap.putIfAbsent(loggerName, logger);
		}
		return logger;
	}

}

class LogConfig {

	private final Properties props;

	public LogConfig(Properties props) {
		this.props = props;
	}

	String[] getAppenders() {
		String appenders = this.props.getProperty(SimpleLoggerFactory.propPrefix);
		if (appenders != null) {
			String[] as = appenders.split(",");
			return as;
		}
		return new String[] {};
	}

	Level getLevel(String appender, Level defaultLevel) {
		String levelKey = SimpleLoggerFactory.propPrefix + "." + appender + ".level";
		String value = this.props.getProperty(levelKey);
		if (value == null) {
			return defaultLevel;
		}
		Level level = Level.parse(value.trim());
		if (level == null) {
			return defaultLevel;
		}
		return level;
	}

	Boolean getAsynLog(String appender, boolean defaultB) {
		String nameKey = SimpleLoggerFactory.propPrefix + "." + appender + ".asyn";
		String value = this.props.getProperty(nameKey);
		if (value == null) {
			return defaultB;
		}
		return Boolean.parseBoolean(value);
	}

	String getMsgPattern(String appender, String defaultS) {
		String nameKey = SimpleLoggerFactory.propPrefix + "." + appender + ".pattern";
		String value = this.props.getProperty(nameKey);
		if (value == null) {
			return defaultS;
		}
		return value;
	}

	String getTimePattern(String appender, String defaultS) {
		String nameKey = SimpleLoggerFactory.propPrefix + "." + appender + ".timePattern";
		String value = this.props.getProperty(nameKey);
		if (value == null) {
			return defaultS;
		}
		return value;
	}

	public Level getLoggerLevel() {
		String levelKey = SimpleLoggerFactory.simplePrefix + ".level";
		String value = this.props.getProperty(levelKey);
		if (value == null) {
			return Level.Error;
		}
		Level level = Level.parse(value.trim());
		if (level == null) {
			return Level.Error;
		}
		return level;
	}

	public String getValue(String appenderName, String key, String defaultValue) {
		String nameKey = SimpleLoggerFactory.propPrefix + "." + appenderName + "." + key;
		return this.props.getProperty(nameKey, defaultValue);
	}

	public Long getLong(String appenderName, String key, long defaultL) {
		String nameKey = SimpleLoggerFactory.propPrefix + "." + appenderName + "." + key;
		String value = this.props.getProperty(nameKey);
		if (value == null) {
			return defaultL;
		}
		try {
			return Long.parseLong(value);
		} catch (Exception e) {

		}
		return defaultL;
	}

}

class LogAppenderPipeline {

	private final LogConfig logConfig;

	private Map<Class<?>, Appender> appenderMap = new HashMap<Class<?>, Appender>();

	public LogAppenderPipeline(LogConfig logConfig) {
		this.logConfig = logConfig;
		this.initial();
	}

	public void destory() {
		for (Appender appender : this.appenderMap.values()) {
			appender.destory();
		}
	}

	private void initial() {
		String[] appenders = this.logConfig.getAppenders();
		if (appenders.length == 0) {
			return;
		}
		for (String appender_str : appenders) {
			String appender = appender_str.trim();
			if (ConsoleAppender.name.equalsIgnoreCase(appender)) {
				Appender appenderE = new ConsoleAppender(logConfig);
				appenderE.initial();
				appenderMap.put(ConsoleAppender.class, appenderE);
				continue;
			}

			if (FileAppender.name.equalsIgnoreCase(appender)) {
				Appender appenderE = new FileAppender(logConfig);
				appenderE.initial();
				appenderMap.put(FileAppender.class, appenderE);
				continue;
			}
		}
	}

	public void print(LogMessage lm) {
		for (Appender appender : this.appenderMap.values()) {
			appender.print(lm);
		}
	}
}

class SimpleLogger extends Logger {

	private final SimpleLoggerFactory simpleLoggerFactory;

	private final Level level;

	SimpleLogger(String loggerName, SimpleLoggerFactory simpleLoggerFactory) {
		super(loggerName);
		this.simpleLoggerFactory = simpleLoggerFactory;
		this.level = this.simpleLoggerFactory.getLogConfig().getLoggerLevel();
	}

	protected void log(Level level, String msg, Throwable throwable, Object[] objects) {
		try {
			LogMessage lm = new LogMessage(this, Thread.currentThread(), level, System.currentTimeMillis(), msg,
					throwable, objects, new Throwable().getStackTrace());
			simpleLoggerFactory.getAppenderPipeline().print(lm);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean isErrorEnabled() {
		return isLogEnabled(Level.Error);
	}

	public boolean isWarnEnabled() {
		return isLogEnabled(Level.Warn);
	}

	public boolean isInfoEnabled() {
		return isLogEnabled(Level.Info);
	}

	public boolean isDebugEnabled() {
		return isLogEnabled(Level.Debug);
	}

	public boolean isTraceEnabled() {
		return isLogEnabled(Level.Trace);
	}

	protected boolean isLogEnabled(Level level) {
		if (level.lessEqual(this.level)) {
			return true;
		}

		return false;
	}

	public void trace(String msg, Throwable throwable, Object... values) {
		if (!this.isTraceEnabled())
			return;

		this.log(Level.Trace, msg, throwable, values);
	}

	public void debug(String msg, Throwable throwable, Object... values) {
		if (!this.isDebugEnabled())
			return;

		this.log(Level.Debug, msg, throwable, values);
	}

	public void info(String msg, Throwable throwable, Object... values) {
		if (!this.isInfoEnabled())
			return;

		this.log(Level.Info, msg, throwable, values);
	}

	public void warn(String msg, Throwable throwable, Object... values) {
		if (!this.isWarnEnabled())
			return;

		this.log(Level.Warn, msg, throwable, values);
	}

	public void error(String msg, Throwable throwable, Object... values) {
		if (!this.isErrorEnabled())
			return;

		this.log(Level.Error, msg, throwable, values);
	}

	@Override
	public void printf(String msg, Object... values) {
		this.log(Level.Trace, msg, null, values);
	}

}

class Log4jLogger extends Logger {

	private static final String fqcn = Logger.class.getName();

	private final ExtendedLogger log;

	Log4jLogger(String loggerName) {
		super(loggerName);
		log = LogManager.getFactory().getContext(fqcn, null, null, false).getLogger(loggerName);
	}

	@Override
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	@Override
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}

	@Override
	public boolean isWarnEnabled() {
		return log.isWarnEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return log.isErrorEnabled();
	}

	@Override
	public void entry() {
		log.entry(fqcn);
	}

	@Override
	public void exit() {
		log.exit(fqcn);
	}

	@Override
	public void trace(String msg, Throwable throwable, Object... values) {
		if (!log.isTraceEnabled()) {
			return;
		}
		String format = LogMessage.formartMsg(msg, values);
		// log.trace(format, throwable);
		log.logIfEnabled(fqcn, org.apache.logging.log4j.Level.TRACE, null, format, throwable);
	}

	@Override
	public void debug(String msg, Throwable throwable, Object... values) {
		if (!log.isDebugEnabled()) {
			return;
		}
		String format = LogMessage.formartMsg(msg, values);
		// log.debug(format, throwable);
		log.logIfEnabled(fqcn, org.apache.logging.log4j.Level.DEBUG, null, format, throwable);
	}

	@Override
	public void info(String msg, Throwable throwable, Object... values) {
		if (!log.isInfoEnabled()) {
			return;
		}
		String format = LogMessage.formartMsg(msg, values);
		// log.info(format, throwable);
		log.logIfEnabled(fqcn, org.apache.logging.log4j.Level.INFO, null, format, throwable);
	}

	@Override
	public void warn(String msg, Throwable throwable, Object... values) {
		if (!log.isWarnEnabled()) {
			return;
		}
		String format = LogMessage.formartMsg(msg, values);
		// log.warn(format, throwable);
		log.logIfEnabled(fqcn, org.apache.logging.log4j.Level.WARN, null, format, throwable);
	}

	@Override
	public void error(String msg, Throwable throwable, Object... values) {
		if (!log.isErrorEnabled()) {
			return;
		}
		String format = LogMessage.formartMsg(msg, values);
		// log.error(format, throwable);
		log.logIfEnabled(fqcn, org.apache.logging.log4j.Level.ERROR, null, format, throwable);
	}

	@Override
	public void printf(String msg, Object... values) {
		log.printf(org.apache.logging.log4j.Level.TRACE, msg, values);
	}

}

abstract class Appender {

	protected final String name;

	//
	private ConcurrentLinkedQueue<LogMessage> logMsgQueue = new ConcurrentLinkedQueue<LogMessage>();

	PrintStream out;

	protected final LogConfig logConfig;

	protected Level level;

	protected Boolean asyn;

	protected String pattern;

	protected String timePattern;

	protected SimpleDateFormat cached_sdf;

	private char[] cached_formatChars;

	public Appender(String name, LogConfig logConfig) {
		this.name = name;
		this.logConfig = logConfig;
	}

	public void destory() {
		this.appenderThread.interrupt();
	}

	void initial() {
		this.level = this.logConfig.getLevel(name, Level.Error);
		this.asyn = this.logConfig.getAsynLog(name, false);
		this.pattern = this.logConfig.getMsgPattern(name, "%d - [%p][%t] %C#%M(L:%l): %m%n%e");
		this.timePattern = this.logConfig.getTimePattern(name, "MM-dd HH:mm:ss.SSS");
		this.cached_sdf = new SimpleDateFormat(this.timePattern);
		this.cached_formatChars = this.pattern.toCharArray();
		this.initialOwn();
		this.initialAsynLog();
	}

	Thread appenderThread;

	private void initialAsynLog() {
		if (!this.asyn) {
			return;
		}
		appenderThread = new Thread(Thread.currentThread().getThreadGroup(), "Logger.Appender." + this.name) {

			boolean running = true;

			@Override
			public void run() {
				while (running) {
					LogMessage lm = logMsgQueue.poll();
					if (lm != null) {
						StringBuilder builder = new StringBuilder();
						lm.addMessageInto(builder, cached_sdf, cached_formatChars);
						print(builder.toString());
					}
				}
			}

			@Override
			public void interrupt() {
				running = false;
				super.interrupt();
			}

		};
		appenderThread.setDaemon(true);
		appenderThread.start();
	}

	protected abstract void initialOwn();

	void print(String message) {
		beforePrint(message);
		synchronized (out) {
			out.println(message);
		}
		AfterPrint(message);
	}

	public void print(LogMessage lm) {
		if (!lm.level.lessEqual(this.level)) {
			return;
		}
		if (!asyn) {
			StringBuilder builder = new StringBuilder();
			lm.addMessageInto(builder, this.cached_sdf, this.cached_formatChars);
			print(builder.toString());
		} else {
			logMsgQueue.add(lm);
		}
	}

	abstract void beforePrint(String message);

	abstract void AfterPrint(String message);

}

class ConsoleAppender extends Appender {

	static final String name = "stdout";

	public ConsoleAppender(LogConfig logConfig) {
		super(name, logConfig);
	}

	@Override
	void beforePrint(String message) {
	}

	@Override
	void AfterPrint(String message) {
	}

	@Override
	protected void initialOwn() {
		this.out = System.out;
	}

}

class FileAppender extends Appender {

	/**
	 * Appender Name
	 */
	static final String name = "file";

	private String filename;

	private String logFolder;

	private Long logFileSize;

	private String logExtName;

	private String logFolderPath;

	private OutputStream outputStream;

	private AtomicLong atomicFileSize = new AtomicLong(0);

	private AtomicInteger fileCount = new AtomicInteger(0);

	// private int currentFileCount;

	public FileAppender(LogConfig logConfig) {
		super(name, logConfig);
	}

	@Override
	void beforePrint(String message) {

	}

	@Override
	void AfterPrint(String message) {
		this.out.flush();
		atomicFileSize.getAndAdd(message.getBytes().length);
		if (atomicFileSize.get() > this.logFileSize) {
			this.rotateFile(null);
		}
	}

	@Override
	protected void initialOwn() {
		this.initialVars();
		//
		File logFolderFile = this.mkLogDirs();
		this.buildCurrentLogFileStream(logFolderFile);
	}

	private void buildCurrentLogFileStream(File logFolderFile) {
		try {
			String[] files = logFolderFile.list(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					if (name.startsWith(filename) && name.endsWith(logExtName)) {
						return true;
					}
					return false;
				}
			});

			if (files == null || files.length == 0) {
				fileCount.set(0);
				this.rotateFile(null);
			} else {
				int listFileCount = 0;
				String lastFileName = null;
				for (String name : files) {
					int start = name.lastIndexOf('_');
					int end = name.lastIndexOf('.');
					listFileCount = Integer.parseInt(name.substring(start + 1, end));
					if (listFileCount >= this.fileCount.get()) {
						fileCount.set(listFileCount);
						lastFileName = name;
					}
				}
				File file = new File(this.logFolderPath + File.separator + lastFileName);
				if (file.exists()) {
					if (file.length() < this.logFileSize) {
						this.rotateFile(file);
					}
				} else {
					this.rotateFile(null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized void rotateFile(File _logFile) {
		try {
			File logFile = null;
			if (_logFile != null) {
				logFile = _logFile;
			} else {
				String cutFileName = this.buildLogFileName();
				logFile = new File(cutFileName);
				logFile.createNewFile();
			}

			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
			}

			if (out != null) {
				out.flush();
				out.close();
			}

			// close first before we new log file
			outputStream = new FileOutputStream(logFile, true);
			out = new PrintStream(outputStream, true);
			if (_logFile != null) {
				this.atomicFileSize.set(_logFile.length());
			} else {
				this.atomicFileSize.set(0);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String buildLogFileName() {
		if (this.logFolderPath.endsWith("\\") || this.logFolderPath.endsWith("/")) {
			return this.logFolderPath + this.filename + "_" + this.getNextFileCount() + ".log";
		}
		return this.logFolderPath + File.separator + this.filename + "_" + this.getNextFileCount() + ".log";
	}

	private int getNextFileCount() {
		return this.fileCount.incrementAndGet();
	}

	private File mkLogDirs() {
		File folderF = new File(this.logFolderPath);
		if (!folderF.exists()) {
			folderF.mkdirs();
		}
		if (!folderF.isDirectory()) {
			folderF.mkdirs();
		}
		return folderF;
	}

	private void initialVars() {
		filename = this.logConfig.getValue(name, "logFileName", "app");
		logExtName = this.logConfig.getValue(name, "logExtName", ".log");
		logFolder = this.logConfig.getValue(name, "logFolder", "logs");
		logFileSize = this.logConfig.getLong(name, "logFileSize", 1024l);
		if (logFileSize == 0) {
			logFileSize = 1024l;
		}
		logFileSize = logFileSize * 1024;
		boolean web = Boolean.parseBoolean(this.logConfig.getValue(name, "web", "false"));
		String userDir;
		if (web) {
			userDir = this.getWebPath();
		} else {
			userDir = System.getProperty("user.dir");
		}

		if (logFolder.isEmpty()) {
			logFolderPath = userDir + File.separator;
		} else {
			logFolderPath = buildFolderPath(userDir, logFolder);
		}

	}

	/**
	 * @return
	 */
	private String getWebPath() {
		URL url = this.getClass().getClassLoader().getResource("./");
		if (url == null) {
			url = this.getClass().getClassLoader().getResource(".");
			String newPath = url.getPath();
			File file = new File(newPath);
			return file.getParent() != null ? file.getParent() : file.getPath();
		} else {
			return url.getPath();
		}
	}

	private String buildFolderPath(String userDir, String logFolder2) {
		boolean hasSplash = logFolder2.startsWith("\\") || logFolder2.startsWith("/");
		if (userDir.endsWith("\\") || userDir.endsWith("/")) {
			if (hasSplash) {
				return userDir + logFolder2.substring(1);
			} else {
				return userDir + logFolder2;
			}
		} else {
			if (hasSplash) {
				return userDir + logFolder2;
			} else {
				return userDir + File.separator + logFolder2;
			}
		}
	}
}

/**
 * @author kevin
 */
class LogMessage {

	private final static String newLine = System.lineSeparator();

	Logger logger;

	Thread thread;

	Level level;

	long time;

	String msg;

	Throwable throwable;

	Object[] values;

	StackTraceElement location;

	StackTraceElement[] stackTraceElements;

	/**
	 * @param stackTraceElements
	 * @param logger
	 * @param values
	 * @param throwable
	 * @param msg
	 * @param l
	 * @param debug
	 * @param thread
	 */
	LogMessage(Logger _logger, Thread _thread, Level _level, long _time, String _msg, Throwable _throwable,
			Object[] _values, StackTraceElement[] _stackTraceElements) {
		this.logger = _logger;
		this.thread = _thread;
		this.level = _level;
		this.time = _time;
		this.msg = _msg;
		this.throwable = _throwable;
		this.values = _values;
		this.stackTraceElements = _stackTraceElements;
	}

	private void appendLogTime(StringBuilder sb, SimpleDateFormat sdf) {
		Date date = new Date(this.time);
		sb.append(sdf.format(date));
	}

	private void appendThreadName(StringBuilder sb) {
		String name = this.thread.getName();
		if (name.length() > 0) {
			sb.append(name);
		} else {
			sb.append("threadId=");
			sb.append(this.thread.getId());
		}
	}

	private void appendLoggerName(StringBuilder sb) {
		sb.append(this.logger.loggerName);
	}

	private void appendMethodName(StringBuilder sb) {
		// sb.append(this.);
		StackTraceElement source = this.getSource();

		if (source == null) {
			sb.append("<unknown-method-name>");
		} else {
			sb.append(source.getMethodName());
		}
	}

	/**
	 * * append style {0} {1}
	 * 
	 * @param msg
	 * @param values
	 * @return
	 */
	static String formartMsg(String msg, Object[] values) {
		StringBuilder sb = new StringBuilder();
		if (values == null || values.length == 0) {
			sb.append(msg);
			return sb.toString();
		}
		int i = 0;
		char lastc = 0;
		char c;
		char[] msgChars = msg.toCharArray();
		char[] numberC = new char[msgChars.length];
		int numberCIndex = 0;
		while (i < msgChars.length) {
			c = msgChars[i++];
			if (c == '{') {
				numberCIndex = 0;
				lastc = c;
				continue;
			}
			if (lastc == '{') {
				if (c == '}') {
					int cv = getInt(numberC, numberCIndex);
					if (cv >= 0 && cv < values.length) {
						sb.append(values[cv]);
					} else {
						sb.append('{');
						sb.append(numberC, 0, numberCIndex);
						sb.append('}');
					}
					Arrays.fill(numberC, '\0');
					numberCIndex = 0;
				} else {
					numberC[numberCIndex++] = c;
					continue;
				}
			} else {
				sb.append(c);
			}
			lastc = c;
		}

		return sb.toString();
	}

	/**
	 * append style {0} {1}
	 * 
	 * @param sb
	 */
	private void appendMessage(StringBuilder sb) {
		sb.append(formartMsg(this.msg, this.values));
	}

	private static int getInt(char[] numberC, int numberCIndex) {
		if (numberCIndex == 0)
			return -1;
		int r = 0;
		int result = 0;
		for (int i = numberCIndex - 1; i >= 0; i--) {
			if (numberC[i] >= '0' && numberC[i] <= '9') {
				result += (numberC[i] - '0') * (Math.pow(10, r++));
			}
		}

		return result;
	}

	private void appendLevelName(StringBuilder sb) {
		String name = this.level.name();
		int levelLen = name.length();
		int i = 0;
		while (i++ < (5 - levelLen)) {
			sb.append(' ');
		}
		sb.append(name);
	}

	private void appendNewLine(StringBuilder sb) {
		sb.append(newLine);
	}

	private void appendThrowable(StringBuilder sb, char lastOptionChar) {
		if (this.throwable != null) {
			sb.append("[Error Message]: ");
			sb.append(this.throwable.getMessage());
			this.printStackTrace(this.throwable, sb);
		}
	}

	private void printStackTrace(Throwable throwable, StringBuilder sb) {
		sb.append(newLine);
		sb.append("At:");
		StackTraceElement[] stes = throwable.getStackTrace();
		for (int i = 0; i < stes.length; i++) {
			sb.append("\t");
			sb.append(stes[i].toString());
			sb.append(newLine);
		}
		Throwable cause = throwable.getCause();
		if (cause != null) {
			sb.append("Cause at:\t");
			sb.append(cause.getMessage());
			printStackTrace(cause, sb);
		}
	}

	private void appendLineNum(StringBuilder sb) {
		StackTraceElement source = this.getSource();
		if (source == null) {
			sb.append("------");
			return;
		}

		int lineNumber = source.getLineNumber();
		if (lineNumber > 100000) {
			// if line number is larger than 100000
			// append line number directly
			sb.append(lineNumber);
		} else {
			// line number is 1~9 add '00000'
			// line number is 10~99 add '0000'
			// line number is 100~999 add '000'
			// and ....
			String lineStr = String.valueOf(source.getLineNumber());
			int i = 0;
			while (i++ < (6 - lineStr.length())) {
				sb.append('0');
			}
			sb.append(lineStr);
		}
	}

	private StackTraceElement getSource() {
		if (location == null) {
			boolean find = false;
			StackTraceElement last = null;
			StackTraceElement[] skes = this.stackTraceElements;
			// String FQCA = this.logger.FQCA != null ? this.logger.FQCA : Logger.class.getName();
			String FQCA = Logger.class.getName();
			for (int i = skes.length - 1; i >= 0; i--) {
				StackTraceElement ste = skes[i];
				if (ste.getClassName().equals(FQCA)) {
					find = true;
				} else {
					last = ste;
				}

				if (find && last != null) {
					location = last;
					break;
				}
			}
		}

		return location;
	}

	/**
	 * Append the log message into a StringBuilder with given SimpleDateFormat and format Chars
	 * 
	 * @param sb
	 * @param sdf
	 * @param formatChars
	 */
	public void addMessageInto(StringBuilder sb, SimpleDateFormat sdf, char[] formatChars) {
		char[] formator = formatChars;
		int i = 0;
		char lastChar = 0;
		char c;
		char lastOptionChar = 0;
		while (i < formator.length) {
			c = formator[i++];
			if (c == '%') {
				lastChar = c;
				continue;
			}
			if (lastChar == '%') {
				if (c == 'd') {
					// log date
					this.appendLogTime(sb, sdf);
				} else if (c == 't') {
					// log thread name
					this.appendThreadName(sb);
				} else if (c == 'c') {
					// logger name
					this.appendLoggerName(sb);
				} else if (c == 'C') {
					// log Class Name
					this.appendLoggerClassName(sb);
				} else if (c == 'M') {
					// method name
					this.appendMethodName(sb);
				} else if (c == 'p') {
					// Level name
					this.appendLevelName(sb);
				} else if (c == 'm') {
					// message
					this.appendMessage(sb);
				} else if (c == 'n') {
					// new line
					this.appendNewLine(sb);
				} else if (c == 'e') {
					// throwable or exception
					this.appendThrowable(sb, lastOptionChar);
				} else if (c == 'l') {
					// line number
					this.appendLineNum(sb);
				} else {
					// no config elmemt
					sb.append(lastChar);
					sb.append(c);
				}
				lastOptionChar = c;
			} else {
				sb.append(c);
			}
			lastChar = c;
		}

		int msgLen = sb.length();
		if (sb.charAt(msgLen - 1) == '\n') {
			sb.deleteCharAt(msgLen - 1);
			if (sb.charAt(msgLen - 2) == '\r') {
				sb.deleteCharAt(msgLen - 2);
			}
		}
	}

	private void appendLoggerClassName(StringBuilder sb) {
		// sb.append(this.);
		StackTraceElement source = this.getSource();
		if (source == null) {
			sb.append("<unknown-Class-Name>");
		} else {
			sb.append(source.getClassName());
		}
	}

}