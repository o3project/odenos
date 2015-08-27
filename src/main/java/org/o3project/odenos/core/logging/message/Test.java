package org.o3project.odenos.core.logging.message;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.o3project.odenos.core.logging.Log;

import java.util.UUID;

public class Test {
  
  public static void main(String[] args) {
    Logger logger = LogManager.getLogger(Test.class);
    logger.error(Log.bufferOverflow("overloaded"));
    logger.error(Log.raceCondition(java.util.UUID.randomUUID().toString()));
  }
}
