package org.o3project.odenos.core.logging.message;

import org.apache.logging.log4j.message.Message;

/**
 * log4j appenders use these methods to get parameters from a
 * logging message DTO.
 */
public interface ILogMessage extends Message {
  
  /**
   * Returns a transaction ID (txid).
   * 
   * @return txid
   */
  public String getTxid();

}
