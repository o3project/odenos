package org.o3project.odenos.core.logging.message;

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

  /**
   * Init parameters.
   *
   * @param offset base number of transactioin ID
   */
  public static void initParameters(int offset) {
    txidOffset = offset;
    txidSerial = 0;
  }

  /**
   * Set a transactionID.
   * @param id transaction ID
   */
  public static void setSavedTxid(String id) {
    String txid = id;
    if(txid == null || txid.length() <= 0 || txid.equals("-")) {
      txid = createTxid();
    }
    ThreadContext.put("txid", txid);
  }

  /**
   * Get a transactionID.
   *
   * @return saved transaction ID
   */
  public static String getSavedTxid() {
    String txid = ThreadContext.get("txid");
    if(txid == null || txid.length() <= 0) {
      txid = "-";
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
        long rLong = rnd.nextLong() & 0xFFFF_FFFF_FFFFL;
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
    ThreadContext.clearMap();
  }
}
