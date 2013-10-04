package com.datastax.logging;


import java.io.IOException;

import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
		cassApp.setPort(9160);
		cassApp.setAppName("unittest");
		cassApp.activateOptions();
        cassApp.setConsistencyLevelWrite(ConsistencyLevel.QUORUM.toString());
		pkgLogger.addAppender(cassApp);
	}

    @Test
    public void testSettingCorrectConsistencyLevels()
    {
        CassandraAppender cassApp = new CassandraAppender();

        for (ConsistencyLevel level : ConsistencyLevel.values())
            cassApp.setConsistencyLevelWrite(level.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSettingWrongConsistencyLevel()
    {
        new CassandraAppender().setConsistencyLevelWrite("QIORUM");
    }

    @Test
    public void testSettingPlacementStrategy()
    {
        CassandraAppender cassApp = new CassandraAppender();
        cassApp.setPlacementStrategy("org.apache.cassandra.locator.SimpleStrategy");
        cassApp.setPlacementStrategy("org.apache.cassandra.locator.NetworkTopologyStrategy");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSettingNullPlacementStrategy()
    {
        new CassandraAppender().setPlacementStrategy(null);
    }

    @Test
    public void testSettingStrategyOptions()
    {
        CassandraAppender appender = new CassandraAppender();

        appender.setStrategyOptions("{ \"DC1\" : \"2\"}");
        appender.setStrategyOptions("{ \"1\" : \"2\", \"DC1\" : \"15\" }");
        appender.setStrategyOptions("{ \"hello\" : \"2\", \"500\" : \"Rack12\" }");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSettingNullStrategyOptions()
    {
        new CassandraAppender().setStrategyOptions(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSettingInvalidStrategyOptions()
    {
        new CassandraAppender().setStrategyOptions("{ \"hell : \"o1\" }");
    }
}
