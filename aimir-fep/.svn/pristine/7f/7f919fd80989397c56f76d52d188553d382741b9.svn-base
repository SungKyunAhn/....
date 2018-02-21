package com.aimir.fep.protocol.security.frame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;

//import com.aimir.fep.protocol.security.frame.CRC16;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * @author 
 *
 */
public class AuthFrameUtil {

	private static Log log = LogFactory.getLog(AuthFrameUtil.class);

	/**
	 * @param in
	 * @return
	 */
	public static boolean checkCRC(IoBuffer in)
    {
        byte[] tail = new byte[2];
 //       CRC16 crc16 = new CRC16();
        
        int len = in.limit() - 2;
        byte[] body = new byte[len];
        
        in.get(body, 0, len);     
        in.rewind();
        
        log.debug("checkCRC:: len :  " + len );
////// CRC Left        
//        crc16.reset();
//        crc16.update(body,0,len);
//        int  checkSum = crc16.getValue();
//        byte[] checkb = DataUtil.get2ByteToInt(checkSum);
        
        tail[0] = in.get(len);
        tail[1] = in.get(len+1);
/////////  CRC Right
       	CRCR crcr = new CRCR();
       	crcr.update(body, 0, body.length);
       	int checkSum = (int)crcr.getValue();
        byte[] tmp = DataUtil.get2ByteToInt(checkSum);
        byte[] crc = new byte[2];
        crc[0] = tmp[1];
        crc[1] = tmp[0];

        log.debug("checkCRC:: tail  : " + Hex.getHexDump(tail));
        log.debug("checkCRC:: calc  : " + Hex.getHexDump(crc));
       
       if ( crc[0] == tail[0] && crc[1] == tail[1]){
    	   return true;
       }
       else {
    	   return false;
       }
    }
	
    /**
     * @param option
     * @return
     */
    public static byte getOptionPending(byte option)
	{
		int b = DataUtil.getIntToByte(option);
		b = ( b >> 7) & 0x01;
		return(DataUtil.getByteToInt(b));
	}
	
	/**
	 * @param option
	 * @return
	 */
	public static byte getOptionReserved(byte option)
	{
		int b = DataUtil.getIntToByte(option);
		b = ( b >> 6) & 0x01;
		return(DataUtil.getByteToInt(b));
	}
	
	/**
	 * @param option
	 * @return
	 */
	public static byte getOptionFrameCount(byte option)
	{
		int b = DataUtil.getByteToInt(option);
		b = ( b >> 4) & 0x03;
		return (byte)b;
	}
	/**
	 * @param option
	 * @return
	 */
	public static byte getOptionFrametype(byte option)
	{
		int b = DataUtil.getByteToInt(option);
		b = b & 0x0F;
		return (byte)b;
	}
	
    /**
     * @param buff
     * @param pos
     * @return
     */
    static public boolean isValidFrame(IoBuffer buff,int pos)
    {
        if(buff.get(pos) != AuthFrameConstants.SOH[0] ||
        		buff.get(pos+1) != AuthFrameConstants.SOH[1] ){

            return false;
        }
        return true;
    }
	
}
