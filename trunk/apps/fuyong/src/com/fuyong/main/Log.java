package com.fuyong.main;

import org.apache.log4j.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-16
 * Time: 下午5:41
 * To change this template use File | Settings | File Templates.
 */
public class Log extends Logger {
    public static final String MY_APP = "MyApp";
    public static final String CRASH = "crash_report";
    private String name;

    protected Log(String name) {
        super(name);
    }

    public static void init() {
        LogManager.getLoggerRepository().resetConfiguration();
        initMyAppLog();
        initCrashLog();
    }

    private static void initMyAppLog() {
        final Logger logger = getLogger(MY_APP);
        final DailyRollingFileAppender dailyRollingFileAppender;
        final Layout fileLayout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p/%t/%C:%m%n");
        try {
            String filePath = MyAppDirs.getLogDir() + TelephonyUtil.getDeviceId() + "-log";
            FileUtil.createNewFile(filePath);
            dailyRollingFileAppender = new DailyRollingFileAppender(fileLayout, filePath, "yyyy-MM-dd");
        } catch (final IOException e) {
            throw new RuntimeException("Exception configuring log system", e);
        }
        dailyRollingFileAppender.setImmediateFlush(true);

        logger.addAppender(dailyRollingFileAppender);
    }

    private static void initCrashLog() {
        final Logger logger = getLogger(CRASH);
        final DailyRollingFileAppender dailyRollingFileAppender;
        final Layout fileLayout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p/%t/%C:%m%n");
        try {
            String filePath = MyAppDirs.getLogDir() + TelephonyUtil.getDeviceId() + "-CrashReport";
            FileUtil.createNewFile(filePath);
            dailyRollingFileAppender = new DailyRollingFileAppender(fileLayout, filePath, "yyyy-MM-dd");
        } catch (final IOException e) {
            throw new RuntimeException("Exception configuring log system", e);
        }
        dailyRollingFileAppender.setImmediateFlush(true);

        logger.addAppender(dailyRollingFileAppender);
    }

    public static void exception(Throwable e) {
        StringWriter stack = new StringWriter();
        e.printStackTrace(new PrintWriter(stack));
        Log.getLogger(MY_APP).error(stack.toString());
    }

//
//    private static void initErrorLog() {
//        final Logger logger = getLogger(CRASH);
//        final RollingFileAppender rollingFileAppender;
//        final Layout fileLayout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p/%t/%C:%m%n");
//
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            rollingFileAppender = new RollingFileAppender(fileLayout, MyAppDirs.getLogDir() + "error" + df.format(new Date()) + ".log");
//        } catch (final IOException e) {
//            throw new RuntimeException("Exception configuring log system", e);
//        }
//
//        rollingFileAppender.setMaxBackupIndex(10);
//        rollingFileAppender.setMaximumFileSize(1024 * 1024);
//        rollingFileAppender.setImmediateFlush(true);
//
//        logger.addAppender(rollingFileAppender);
//    }
}
