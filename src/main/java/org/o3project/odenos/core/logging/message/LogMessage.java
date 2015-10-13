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

  private static int txidOffset = 0;
  private static int txidSerial = 0;
  private static String txidOwn = "";

  private static Random rnd;

  private String txid = null;
  private Object[] parameters;
  private String format = null;

  /**
   * Alloc ThreadLocal variable.
   *
   */
  private static ThreadLocal<String> savedTxid = new ThreadLocal<String>() {
    @Override
    protected String initialValue() {
      return new String();
    }
  };

  /**
   * Init parameters.
   *
   * @param offset base number of transactioin ID
   */
  public static void initParameters(int offset) {
    txidOffset = offset;
    long now = System.currentTimeMillis();
    rnd = new Random(now);
  }

  /**
   * Sets a transaction ID (txid).
   * 
   * @param txid a transaction ID
   * @return LogMessage
   */
  public LogMessage setTxid(String txid) {
    this.txid = txid;
    return this;
  }

  /**
   * Sets a log message as a plain text.
   * 
   * @param msg string of log message
   * @return LogMessage
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
   * @param msg message parameter
   * @return LogMessage
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
   * Set a transactionID.
   * @param id transaction ID
   */
  public static void setSavedTxid(String id) {
    String txid = id;
    if(txid == null || txid.length() == 0) {
      txid = createTxid();
    }
    savedTxid.set(txid);
  }

  /**
   * Get a transactionID.
   *
   * @return saved transaction ID
   */
  public static String getSavedTxid() {
    String txid = savedTxid.get();
    if(txid == null || txid.length() == 0) {
      txid = txidOwn;
    }
    return txid;
  }

  /**
   * Create a transactionID (txid).
   *
   * @return created transaction ID
   */
  public static String createTxid() {
    String uuid = "";

    txidSerial++;
    int serial = txidOffset + txidSerial;

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

    String uuid_str = uuid.toLowerCase() + "-" + String.format("%07d", serial);
    if(txidOwn.length() == 0) {
      txidOwn = uuid_str;
    }
    return uuid_str;
  }

  /**
   * Generates DTO to be sent to log4j appenders. 
   *
   * @return ILogMessage
   */
  public ILogMessage build() {
    
    class LogMessageImmutable implements ILogMessage {

      private static final long serialVersionUID = 1L;
      private final String txid;
      private final Object[] parameters;
      private final String format;
      
      LogMessageImmutable(String txid, String format, Object[] parameters) {
        this.txid = txid;
        this.format = format;
        this.parameters = parameters;
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
              MessageFormat.format("txid: {0}, {1}", this.txid, formattedMessage);
        } else {
          formattedMessage = MessageFormat.format("{0}", formattedMessage);
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

    return new LogMessageImmutable(txid, format, parameters);
    
  }

  public static Message buildLogMessage(String txid,
                                        String fmt, Object... parameters) {
    int len = parameters.length;
    for(int i = 0; i < len; i++) {
      fmt = fmt.replaceFirst("\\{\\}", "{" + i + "}");
    }

    return new LogMessage()
      .setFormatedMessage(fmt, parameters)
      .setTxid(txid)
      .build();
  }
}
