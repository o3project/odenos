package org.o3project.odenos.core.logging.message;

import java.text.MessageFormat;
import org.apache.logging.log4j.message.Message;
import java.util.Random;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;

/**
 * Logging message DTO builder.
 */
public class LogMessage {

  final static int txidNumberSystem = 1000000;

  static int txidSerial = 0;
  static String txidStack = "";

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
  public static String getTxid() {
    return txidStack;
  }

 /**
   * Get a base of transactionID (systemManager).
   */
  public static int getSystemBaseTxid() {
    return txidNumberSystem;
  }

 /**
  * Create a transactionID (txid).
  *
  * @param offset
  */
  public static void createTxid(int offset) {
    long now = System.currentTimeMillis();
    Random rnd = new Random(now);
    long rLong = rnd.nextLong() % 0x10000_0000_0000L;
    byte[] padd = {(byte)0,(byte)0};
    String uuid = Long.toHexString(rLong);

    String serial = "";
    if( offset != 0) {
      int serial_int = offset + txidSerial;
      serial = serial + serial_int;
      txidSerial++;
    } else {
      serial = "0000000";
    }

    int ethNum = 10;
    try {
      for(int i = 0; i < ethNum; i++) {
        NetworkInterface nic = NetworkInterface.getByName("eth" + i);
        if(nic != null) {
          uuid = Long.toHexString(ByteBuffer.allocate(8).put(padd).put(nic.getHardwareAddress()).getLong(0));
        }
      }
    } catch (SocketException ex) {
    }

    String uuid_str = new String();
    uuid_str = uuid + "-" + serial;
    txidStack = uuid_str;
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
