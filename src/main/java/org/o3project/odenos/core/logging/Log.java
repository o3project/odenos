package org.o3project.odenos.core.logging;

import org.apache.logging.log4j.message.Message;
import org.o3project.odenos.core.logging.message.LogMessage;

public class Log {
  
  /**
   * [1] Class load error.
   */
  public static Message errorClassLoad() {
    return new LogMessage()
               .setNumber(1)
               .setMessage("class load error")
               .build();
  }
  
  /**
   * [2] Message Dispatcher started. 
   */
  public static Message infoMessageDispatcherStarted() {
    return new LogMessage()
               .setNumber(2)
               .setMessage("Message Dispatcher started")
               .build();
  }
  
  /**
   * [101] Buffer Overflow
   * 
   * Explanation: ...
   */
  public static Message bufferOverflow(String cause) {
    return new LogMessage()
               .setNumber(101)
               .setFormatedMessage("cause: {0}", cause)
               .build();
  }

  /**
   * [102] Race Condition 
   * 
   * Explanation: ...
   */
  public static Message raceCondition(String txid) {
    return new LogMessage()
               .setNumber(102)
               .setMessage("Concurrent access")
               .setTxid(txid)
               .build();
  }
}
