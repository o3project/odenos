package org.o3project.odenos.core.logging.message;

import java.text.MessageFormat;
import org.apache.logging.log4j.message.Message;
import java.util.Random;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import javax.xml.bind.DatatypeConverter;

/**
 * Logging message DTO builder.
 */
public class LogMessage {

  public static final int TXID_SYSTEMMGR_OFFSET = 1000000;
  private static int txidSystemMgrSerial = 0;

  public static final int TXID_OFFSET = 0;
  private static int txidSerial = 0;

  private static String savedTxid = null;
  private static Random rnd;

  int number = -1;
  private String txid = null;
  private Object[] parameters;
  private String format = null;

  /**
   * Init parameters.
   *
   * @param number
   */
  public static void initParameters() {
    long now = System.currentTimeMillis();
    rnd = new Random(now);
  }

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
  public LogMessage setFormatedMessage(String fmt, Object... msg) {
    this.format = fmt;
    int len = msg.length;
    this.parameters = new Object[len];
    for (int i = 0; i < len; i++) {
      this.parameters[i] = msg[i];
    }
    return this;
  }

  /**
   * Get a transactionID.
   */
  public static String getSavedTxid() {
    return savedTxid;
  }

 /**
  * Create a transactionID (txid).
  *
  * @param offset
  */
  public static void createTxid(int offset) {
    String uuid = "";

    int serial = 0;
    if( offset == TXID_SYSTEMMGR_OFFSET) {
      serial = offset + txidSystemMgrSerial;
      txidSystemMgrSerial++;
    } else { // TXID_OFFSET
      serial = offset + txidSerial;
      txidSerial++;
    }

    int ethNum = 9;
    try {
      for(int i = 0; i <= ethNum; i++) {
        NetworkInterface nic = NetworkInterface.getByName("eth" + i);
        if(nic != null) {
          uuid = DatatypeConverter.printHexBinary(nic.getHardwareAddress());
          break;
        } 
      }
    } catch (SocketException ex) {
    } finally {
      if(uuid == ""){
        long rLong = rnd.nextLong() % 0x10000_0000_0000L;
        uuid = Long.toHexString(rLong);
      }
    }

    String uuid_str = new String();
    uuid_str = uuid.toLowerCase() + "-" + String.format("%07d", serial);
    savedTxid = uuid_str;
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

  public static Message buildLogMessage(int msgid, String txid,
                                        String fmt, Object... parameters) {
    int len = parameters.length;
    for(int i = 0; i < len; i++) {
      fmt = fmt.replaceFirst("\\{\\}", "{" + i + "}");
    }

    return new LogMessage()
      .setNumber(msgid)
      .setFormatedMessage(fmt, parameters)
      .setTxid(txid)
      .build();
  }
}
