package org.o3project.odenos.core.logging.message;

import java.text.MessageFormat;
import org.apache.logging.log4j.message.Message;
import java.util.Random;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.ThreadContext;

/**
 * Logging message DTO builder.
 */
public class LogMessage {

  private static int txidOffset = 0;
  private static int txidSerial = 0;

  private static Random rnd = new Random(System.currentTimeMillis());

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
    if(txid == null || txid.length() <= 1) {
      txid = createTxid();
    }
    savedTxid.set(txid);
    ThreadContext.put("txid", txid);
  }

  /**
   * Get a transactionID.
   *
   * @return saved transaction ID
   */
  public static String getSavedTxid() {
    return savedTxid.get();
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
    return uuid_str;
  }

  /**
   * Delete a transactionID.
   *
   */
  public static void delSavedTxid() {
    savedTxid.set("");
    ThreadContext.clearAll();
  }
}
