package com.datastax.logging;

import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;

import com.datastax.logging.appender.CassandraAppender;

/**
 * Basic test for setting appender properties.
 */
public class BasicTest
{
	@Before
	public void setUp() throws Exception {
		// Programmatically set up out appender.
		Logger rootLogger = Logger.getRootLogger();
		Logger pkgLogger = rootLogger.getLoggerRepository().getLogger("com.datastax.logging");
		pkgLogger.setLevel(Level.INFO);
		CassandraAppender cassApp = new CassandraAppender();
		cassApp.setPort(9042);
		cassApp.setAppName("unittest");
		cassApp.activateOptions();
        cassApp.setConsistencyLevelWrite("QUORUM");
		pkgLogger.addAppender(cassApp);
	}

    @Test
    public void testSettingCorrectConsistencyLevels()
    {
        CassandraAppender cassApp = new CassandraAppender();
        cassApp.setConsistencyLevelWrite("QUORUM");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSettingWrongConsistencyLevel()
    {
        new CassandraAppender().setConsistencyLevelWrite("QIORUM");
    }

    @Test
    public void testThrowableSuccess() throws Exception
    {
        CassandraAppender appender = new CassandraAppender();
        LoggingEvent event = new LoggingEvent(BasicTest.class.getName(),
                                              Category.getInstance(BasicTest.class),
                                              Priority.WARN,
                                              "test 12",
                                              new Exception("boom"));
        appender.doAppend(event);
    }

    @Test
    public void testNoThrowableSuccess() throws Exception
    {
        CassandraAppender appender = new CassandraAppender();
        LoggingEvent event = new LoggingEvent(BasicTest.class.getName(),
                                              Category.getInstance(BasicTest.class),
                                              Priority.WARN,
                                              "test 12",
                                              null);
        appender.doAppend(event);
    }
}
