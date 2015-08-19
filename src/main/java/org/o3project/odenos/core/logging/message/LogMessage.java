package org.o3project.odenos.core.logging.message;

import java.text.MessageFormat;

/**
 * Logging message DTO builder.
 */
public class LogMessage {

  int number = -1;
  private String txid = null;
  private Object[] parameters;
  private String format = null;

  /**
   * Sets a logging message number.
   * 
   * @param number
   */
  public LogMessage setNumber(int number) {
    this.number = number;
    return this;
  }

  /**
   * Sets a transaction ID (txid).
   * 
   * @param txid
   */
  public LogMessage setTxid(String txid) {
    this.txid = txid;
    return this;
  }

  /**
   * Sets a log message as a plain text.
   * 
   * @param msg
   */
  public LogMessage setMessage(String msg) {
    this.parameters = new Object[1];
    this.parameters[0] = (Object) msg;
    return this;
  }

  /**
   * Sets a formatted log message.
   * 
   * @param fmt format
   * @param msg
   */
  public LogMessage setFormatedMessage(String fmt, String... msg) {
    this.format = fmt;
    int len = msg.length;
    this.parameters = new Object[len];
    for (int i = 0; i < len; i++) {
      this.parameters[i] = (Object) msg[i];
    }
    return this;
  }

  /**
   * Generates DTO to be sent to log4j appenders. 
   */
  public ILogMessage build() {
    
    class LogMessageImmutable implements ILogMessage {

      private static final long serialVersionUID = 1L;
      private final int number;
      private final String txid;
      private final Object[] parameters;
      private final String format;
      
      LogMessageImmutable(int number, String txid, String format, Object[] parameters) {
        this.number = number;
        this.txid = txid;
        this.format = format;
        this.parameters = parameters;
      }

      @Override
      public int getNumber() {
        return this.number;
      }

      @Override
      public String getTxid() {
        return this.txid;
      }

      @Override
      public String getFormat() {
        return null;
      }
      
      @Override
      public String getFormattedMessage() {
        String formattedMessage = null;
        if (this.format == null) {
          formattedMessage = (String) this.parameters[0];
        } else {
          formattedMessage = MessageFormat.format(this.format, this.parameters);
        }
        if (this.txid != null) {
          formattedMessage =
              MessageFormat.format("[{0}] txid: {1}, {2}", this.number, this.txid, formattedMessage);
        } else {
          formattedMessage = MessageFormat.format("[{0}] {1}", this.number, formattedMessage);
        }
        return formattedMessage;
      }

      @Override
      public Object[] getParameters() {
        return this.parameters; 
      }
      @Override
      public Throwable getThrowable() {
        return null;
      }

    }

    return new LogMessageImmutable(number, txid, format, parameters);
    
  }
}
