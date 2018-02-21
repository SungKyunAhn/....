/*
 * Created on 2005. 1. 20.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.aimir.fep.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.regexp.RE;

import com.aimir.fep.protocol.fmp.datatype.BCD;
import com.aimir.fep.protocol.fmp.datatype.BOOL;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.CHAR;
import com.aimir.fep.protocol.fmp.datatype.DataType;
import com.aimir.fep.protocol.fmp.datatype.FMPVariable;
import com.aimir.fep.protocol.fmp.datatype.HEX;
import com.aimir.fep.protocol.fmp.datatype.INT;
import com.aimir.fep.protocol.fmp.datatype.IPADDR;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.OPAQUE;
import com.aimir.fep.protocol.fmp.datatype.SHORT;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMEDATE;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.datatype.VER;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.ErrorCode;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.Entry;
import com.aimir.notification.FMPTrap;
import com.aimir.notification.VarBinds;
import com.aimir.util.TimeUtil;

/**
 * @author Park Yeon Kyoung
 */
public class DataFormat {
    private static Log log = LogFactory.getLog(DataFormat.class);
    
	public static double ba2double(byte[] b) throws Exception {
        double ret = 0;

        try{
            ret = Double.parseDouble( new String( b ));
        }catch(NumberFormatException e){
            throw e;
        }catch(Exception e){
            throw e;
        }
        return ret;
    }

    public static double ba2double(byte[] b, int offset, int length) throws Exception {
        double ret = 0;
        byte[] dest = new byte[length];

        try{
            if(b == null) {
				throw new Exception("Buffer Null");
			}
            if(b.length < length) {
				throw new Exception("Length Error->Buffer size");
			}
            if(offset < 0 || offset > b.length-1) {
				throw new Exception("Offset Define Error");
			}
            System.arraycopy(b,offset,dest,0,length);
            ret = Double.parseDouble( new String( dest ));
        }catch(NumberFormatException e){
            throw e;
        }catch(Exception e){
            throw e;
        }
        return ret;
    }

    public static double ba2float(byte[] b) throws Exception {
        double ret = 0;

        try{
            ret = Double.parseDouble( new String( b ));
        }catch(NumberFormatException e){
            throw e;
        }catch(Exception e){
            throw e;
        }
        return ret;
    }

    public static double ba2float(byte[] b, int offset, int length) throws Exception {
        float ret = 0;
        byte[] dest = new byte[length];

        try{
            if(b == null) {
				throw new Exception("Buffer Null");
			}
            if(b.length < length) {
				throw new Exception("Length Error->Buffer size");
			}
            if(offset < 0 || offset > b.length-1) {
				throw new Exception("Offset Define Error");
			}
            System.arraycopy(b,offset,dest,0,length);
            ret = Float.parseFloat( new String( dest ));
        }catch(NumberFormatException e){
            throw e;
        }catch(Exception e){
            throw e;
        }
        return ret;
    }
    public static float bytesToFloat(byte[] bar) throws Exception
	{
    	float f;
    	try{
			int result=0;
			for(int i=0;i<bar.length;i++)
			{
				int temp=bar[bar.length-i-1];
				if(temp<0){
					temp=((temp * (-1)) ^ 0xff) +1;
				}
				result=result | (temp << i*8);
			}
			f=Float.intBitsToFloat (result);
    	}catch(NumberFormatException e){
            throw e;
        }catch(Exception e){
            throw e;
        }
		return f;
	}
	public static double bytesToDouble(byte[] bar) throws Exception
	{
    	double d;
    	try{
    		d=Double.longBitsToDouble(hex2long(bar));
    	}catch(NumberFormatException e){
            throw e;
        }catch(Exception e){
            throw e;
        }
		return d;
	}
	
	/**
	 * BCD CODE transform to Decimal Value(int type).<p>
	 * ex) 0x99 0x99 0x99 0x99 -> 99999999 <p>
	 * @param b - bcd type byte data
	 * @return
	 * @throws Exception
	 */
	public static int bcd2dec(byte[] b) throws Exception {

		int ret = 0;

		try{
			ret = Integer.parseInt(Util.getNHexString(b));
		}catch(NumberFormatException e){
			throw e;
		}catch(Exception e){
			throw e;
		}
		return ret;

	}

	/**
	 * BCD CODE transform to Decimal Value(int type).<p>
	 * ex) 0x99 0x99 0x99 0x99 -> 99999999 <p>
	 * @param b - bcd type byte data
	 * @return
	 * @throws Exception
	 */
	public static int bcd2dec(byte[] b, int offset, int length)
						throws Exception{

		int ret = 0;
		byte[] dest = new byte[length];

		try{
			if(b == null) {
				throw new Exception("Buffer Null");
			}
			if(b.length < length) {
				throw new Exception("Length Error->Buffer size");
			}
			if(offset < 0 || offset > b.length-1) {
				throw new Exception("Offset Define Error");
			}
			System.arraycopy(b,offset,dest,0,length);
			ret = Integer.parseInt(Util.getNHexString(dest));
		}catch(NumberFormatException e){
			throw e;
		}catch(Exception e){
			throw e;
		}
		return ret;
	}

	/**
	 * BCD code byte transform to hex type byte array.<p>
	 * ex) 0x99 -> 0x63
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public static byte[] bcd2hex(byte[] b) throws Exception {
		return dec2hex(bcd2dec(b));
	}

	/**
	 * BCD code byte transform to hex type byte array.<p>
	 * ex) 0x99 -> 0x63
	 * @param b
	 * @param offset
	 * @param len
	 * @return
	 * @throws Exception
	 */
	public static byte[] bcd2hex(byte[] b, int offset, int len)
							throws Exception {
		return dec2hex(bcd2dec(b,offset,len));
	}

	/**
	 * BCD CODE transform to Decimal Value(long type).<p>
	 * ex)
	 * 0x99 0x99 0x99 0x99 0x99 0x99 0x99 0x99
	 * -> 9999999999999999 <p>
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public static long bcd2long(byte[] b) throws Exception{

		long ret = 0;

		try{
			ret = Long.parseLong(Util.getNHexString(b));
		}catch(NumberFormatException e){
			throw e;
		}catch(Exception e){
			throw e;
		}
		return ret;
	}

	/**
	 * BCD CODE transform to Decimal Value(long type).<p>
	 * ex)
	 * 0x99 0x99 0x99 0x99 0x99 0x99 0x99 0x99
	 * -> 9999999999999999 <p>
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public static long bcd2long(byte[] b, int offset, int length)
						throws Exception {
		long ret = 0;

		try{
			byte[] dest = new byte[length];
			System.arraycopy(b,offset,dest,0,length);
			ret = Long.parseLong(Util.getNHexString(dest));
		}catch(NumberFormatException e){
			throw e;
		}catch(Exception e){
			throw e;
		}
		return ret;
	}


	public static String bcd2str(byte[] b)
						 throws Exception {
		return bcd2str(b,0,b.length);
	}


	/**
	 * BCD to string value
	 * get Value.
	 * @param sb
	 * @param start
	 * @param len
	 * @return
	 */
	public static String bcd2str(byte[] b, int start, int len)
								throws Exception {

		String ret = new String();

		if(b == null || start < 0 || len < 1) {
			throw new Exception("Input Param Error");
		}
		if(b.length < len) {
			throw new Exception("Input Param Error");
		}

		try{
			for(int i = start; i < start+len; i++){
				int v = b[i] & 0xFF;
				if(v > 0x99) {
					throw new Exception("BCD Format Error");
				}
			}
			byte[] dest = new byte[len];
			System.arraycopy(b,start,dest,0,len);

			ret = Util.getNHexString(dest);
		}catch(Exception e){
			throw e;
		}
		return ret;
	}


	public static boolean chkStrNumberForm(String s, String paramname)
							throws Exception {

		boolean ret = false;

		if(s == null || s.equals("")) {
			throw new Exception(paramname+" STRING NULL");
		}

		for(int i = 0; i < s.length(); i++){
			if(!Character.isDigit(s.charAt(i))){
				throw new Exception(paramname
					+" NON NUMERIC VALUE STRING! VAL->"+s);
			}
			else{
				ret = true;
			}

		}

		return ret;
	}


	/**
	 * Decimal (int type) transform
	 * to BCD CODE byte array.<p>
	 * ex) 99 -> 0x99
	 * @param n
	 * @return
	 * @throws Exception
	 */
	public static byte[] dec2bcd(int n) throws Exception {

		if(n < 0) {
			throw new Exception("Negative value not avaliable");
		}

		String s = Integer.toString(n);

		if(s.length()%2 != 0) {
			s = Util.frontAppendNStr('0',s,s.length()+1);
		}

		byte[] b = new byte[s.length()/2];

		int k = 0;
		for(int i = 0; i < s.length()/2; i++){
			int val = 0;
			val  = s.charAt(k++) << 4;
			val += s.charAt(k++) & 0x0f;
			b[i] = (byte)(val & 0xff);
		}

		return b;
	}

	/**
	 * Decimal (long type) transform
	 * to BCD CODE byte array.<p>
	 * ex) 99 -> 0x99
	 * @param n
	 * @return
	 * @throws Exception
	 */
	public static byte[] dec2bcd(long n) throws Exception {

		if(n < 0) {
			throw new Exception("Negative value not avaliable");
		}

		String s = Long.toString(n);

		if(s.length()%2 != 0) {
			s = Util.frontAppendNStr('0',s,s.length()+1);
		}

		byte[] b = new byte[s.length()/2];

		int k = 0;
		for(int i = 0; i < s.length()/2; i++){
			int val = 0;
			val  = s.charAt(k++) << 4;
			val += s.charAt(k++) & 0x0f;
			b[i] = (byte)(val & 0xff);
		}

		return b;
	}


	/**
	 * 0~65535 decimal value transform to 2byte data.<p>
	 * byte order - little endian.
	 * @param c (0~ 65535)
	 * @return
	 */
	public static byte[] dec2hex(char c) {

		byte[] buf = new byte[2];

		buf[0] = (byte) ((c >> 8 ) & 0xff);
		buf[1] = (byte) (c & 0xff);

		return buf;
	}

	/**
	 * Decimal (int type) value transform
	 * to hex type byte array.<p>
	 * byte order - little endian.
	 * @param num
	 * @return
	 */
	public static byte[] dec2hex(int num) {

		byte[] buf = new byte[4];

		buf[0] = (byte) ((num >> 24) & 0xff);
		buf[1] = (byte) ((num >> 16) & 0xff);
		buf[2] = (byte) ((num >> 8 ) & 0xff);
		buf[3] = (byte) (num & 0xff);

		return buf;
	}


	/**
	 * Decimal (long type) value transform
	 * to hex type byte array.<p>
	 * byte order - little endian.
	 * @param num
	 * @return
	 */
	public static byte[] dec2hex(long num) {

		byte[] buf = new byte[8];

		buf[0] = (byte) ((num >> 56) & 0xff);
		buf[1] = (byte) ((num >> 48) & 0xff);
		buf[2] = (byte) ((num >> 40) & 0xff);
		buf[3] = (byte) ((num >> 32) & 0xff);
		buf[4] = (byte) ((num >> 24) & 0xff);
		buf[5] = (byte) ((num >> 16) & 0xff);
		buf[6] = (byte) ((num >> 8 ) & 0xff);
		buf[7] = (byte) (num & 0xff);

		return buf;

	}

	/**
	 * -32768 ~ 32767 decimal value transform to 2byte data.<p>
	 * byte order - little endian.
	 * @param n (-32768 ~ 32767)
	 * @return
	 */
	public static byte[] dec2hex(short n){

		byte[] buf = new byte[2];

		buf[0] = (byte) ((n >> 8 ) & 0xff);
		buf[1] = (byte) (n & 0xff);

		return buf;
	}


	public static byte[] except(byte[] b, byte x)
						 throws Exception {

		return except(b,0,b.length,x);
	}


	public static byte[] except(byte[] b, int offset, int len, byte x)
						 throws Exception {

		if(b == null || offset < 0 || offset > b.length ||
			 len < 1 || b.length < len) {
			throw new Exception("Wrong Parameter Input");
		}

		int cnt = 0;
		for(int i = offset; i < offset+len; i++){
			if(b[i] == x) {
				cnt++;
			}
		}

		if(len == cnt) {
			throw new Exception("All includes 'x'");
		}

		byte[] ret = new byte[len-cnt];
		int idx = 0;
		for(int i = offset; i < offset+len; i++){
			if(b[i] != x){
				ret[idx++] = b[i];
			}
		}
		return ret;
	}


	/**
	 *
	 * @param f
	 * @return
	 */
	public static byte[] float32_2_hex(float f){

		byte[] b = new byte[4];

		char point1 = (char)f;
		char point2 = (char) (f%1 * Math.pow(2,16));

		byte[] p1 = dec2hex(point1);
		byte[] p2 = dec2hex(point2);

		b[0] = p1[0];
		b[1] = p1[1];
		b[2] = p2[0];
		b[3] = p2[1];

		return b;
	}

	/**
	 * signed int 8bit change unsigned int 8bit.
	 * @param b
	 * @return
	 */
	public static byte getSign(byte b) throws Exception {

		int ret = 0;
		int c = (b&0xff);

		try {
			if(c <= 127){
				ret = c;
			} else {
				ret = (-1)*(~c)-256-1;
				ret = (-1)*ret;
			}

			return (byte)(ret&0xff);

		} catch(Exception e){
			throw e;
		}

	}


	/**
	 * signed int 8bit change and get pow(10,n) value.
	 * @param c
	 * @return
	 */
	public static double getSignPow(byte b) throws Exception {

		int ret = 0;
		int c = (b&0xff);
		try {
			if(c <= 127) {
				ret = c;
			}
			else {
				ret = (-1)*(~c)-256-1;
			}

			return Math.pow(10.0,ret);

		} catch(Exception e){
			throw e;
		}

	}

	/**
	 * Hex type byte array transform to BCD code byte array.<p>
	 * ex) 0x63 -> 0x99
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public static byte[] hex2bcd(byte[] b) throws Exception {
		return(dec2bcd(hex2long(b)));
	}


	/**
	 * Hex type byte array transform
	 * to int value.<p>
	 * byte order - little endian.<p>
	 *
	 * @param b - src byte array
	 * @return
	 * @throws Exception
	 */
	public static int hex2dec(byte[] b) throws Exception {
		return hex2dec(b,0,b.length);
	}

	/**
	 * Hex type byte array transform to decimal value(int type).<p>
	 * byte order - default little endian.<p>
	 * @param b - src array
	 * @param offset - start offset
	 * @param len - to transform src byte length.
	 * @return - int type value.
	 * @throws Exception
	 */
	public static int hex2dec(byte[] b, int offset, int len)
								throws Exception {

		if(b == null || offset < 0 || offset > b.length || len < 1) {
			throw new Exception("Wrong Parameter Input");
		}
		if(len == 0) {
			throw new Exception("Length Zero");
		}
		if(len > 4) {
			throw new Exception("Integer Overflow Value");
		}

		int ret = 0;
		int idx = offset;
		for(int i = len; i > 0; i--){
			ret |= (b[idx++] & 0xff) << (i-1)*8;
		}

		return ret;
	}


	public static float hex2float32(byte[] b)
						throws Exception {
		return hex2float32(b,0);
	}

	/**
	 *
	 * @param b
	 * @param offset
	 * @return
	 * @throws Exception
	 */
	public static float hex2float32(byte[] b, int offset)
						throws Exception {

		float val = 0f;

		if( b == null || offset < 0 || offset > b.length || b.length < 4) {
			throw new Exception("Wrong Parameter Input");
		}

		float point1 = hex2dec(b,offset,2);
		float point2 = hex2dec(b,offset+2,2);

		val = point1+(float)(point2/Math.pow(2,16));

		return val;
	}

	/**
	 * Hex type byte array transform
	 * to long value.<p>
	 * byte order - Big endian.<p>
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public static long hex2long(byte[] b) throws Exception {
		return hex2long(b,0,b.length);
	}

	/**
	 * Hex type byte array transform
	 * to long value.<p>
	 * byte order - little endian.<p>
	 *
	 * @param b - src array
	 * @param offset - src start offset
	 * @param len - src length to transform.
	 * @return
	 * @throws Exception
	 */
	public static long hex2long(byte[] b,int offset, int len)
								throws Exception {

		if(b == null || offset < 0 || offset > b.length || len < 1) {
			throw new Exception("Wrong Parameter Input");
		}
		if(len == 0) {
			return 0;
		}
		if(len > 8) {
			throw new Exception("Integer Overflow Value");
		}

		long ret = 0L;

		int idx = offset;
		for(long i = len; i > 0; i--){
			long test = (long)((b[idx++] & 0xFFL) << (long)(i-1)*8);
			ret += test;
		}
		return ret;
	}

	public static short hex2signed16(byte[] b) throws Exception {

		if(b == null || b.length < 1 || b.length > 2) {
			throw new Exception("Wrong Parameter Input");
		}

		short ret = 0;
		int idx = 0;
		for(int i = b.length; i > 0; i--){
			ret |= (b[idx++] & 0xff) << (i-1)*8;
		}
		return ret;
	}

	public static short hex2signed16(byte[] b, int offset, int len)
							throws Exception {

		if(b == null || offset < 0 || offset > b.length || len < 2) {
			throw new Exception("Wrong Parameter Input");
		}
		if(len == 0) {
			return 0;
		}
		if(len > 2) {
			throw new Exception("2 byte Overflow Value");
		}

		short ret = 0;
		int idx = offset;
		for(int i = len; i > 0; i--){
				ret |= (b[idx++] & 0xff) << (i-1)*8;
		}
		return ret;
	}

	/**
	 * 8bit hex change signed int.
	 * @param b
	 * @return
	 */
	public static int hex2signed8(byte b){
		return b;
	}

	/**
	 * 8bit hex change signed int.
	 * @param c
	 * @return
	 */
	public static int hex2signed8(char c){
		byte b = (byte) c;
		return b;
	}

	/**
	 * Get Signed Value.<p>
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public static long hex2signeddec(byte[] b)
						throws Exception {
		return hex2signeddec(b,0,b.length);
	}

	/**
	 * Get Signed Value.<p>
	 * @param b
	 * @param offset
	 * @param len
	 * @return
	 * @throws Exception
	 */
	public static long hex2signeddec(byte[] b, int offset, int len)
						throws Exception {

		if(b == null || offset < 0 || offset > b.length || len < 1) {
			throw new Exception("Wrong Parameter Input");
		}
		if(len == 0) {
			return 0;
		}
		if(len > 8) {
			throw new Exception("Integer Overflow Value");
		}

		long ret = 0;

		BigInteger bi
			= new BigInteger(DataFormat.select(b,offset,len));
		ret = bi.longValue();

		return ret;
	}


	/**
	 *
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public static long hex2unsigned32(byte[] b) throws Exception {

		if(b == null || b.length < 4 || b.length > 4) {
			throw new Exception("Wrong Parameter Input");
		}

		long ret = 0;
		int idx = 0;
		for(int i = b.length; i > 0; i--){
			ret |= (b[idx++] & 0xff) << (i-1)*8;
		}
		return ret;
	}
	/**
	 *
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public static char hex2unsigned16(byte[] b) throws Exception {

		if(b == null || b.length < 1 || b.length > 2) {
			throw new Exception("Wrong Parameter Input");
		}

		char ret = 0;
		int idx = 0;
		for(int i = b.length; i > 0; i--){
			ret |= (b[idx++] & 0xff) << (i-1)*8;
		}
		return ret;
	}

	/**
	 * 8bit hex change unsigned int.
	 * @param b
	 * @return
	 */
	public static int hex2unsigned8(byte b){
		return (b & 0xff);
	}

	/**
	 * 8bit hex change unsigned int.
	 * @param c
	 * @return
	 */
	public static int hex2unsigned8(char c){
		byte b = (byte)c;
		return (b & 0xff);
	}


	public static byte hexstr2byte(String s) throws Exception {

		byte b;

		if(s.length() != 2) {
			throw new Exception("String type error");
		}

		int tmp1 = Character.getNumericValue(s.charAt(0));
		int tmp2 = Character.getNumericValue(s.charAt(1));

		if(tmp1 == -1 || tmp2 == -1) {
			throw new Exception("Non numeric value");
		}

		b = (byte) (tmp1 << 4);
		b |= tmp2;
		return b;
	}

	public static byte[] LSB2MSB(byte[] b) throws Exception {

		if(b == null || b.length == 0) {
			throw new Exception("NULL Input Param");
		}

		byte[] ret = new byte[b.length];

		for(int i = 0; i < b.length; i++){
			ret[b.length-i-1] = b[i];
		}

		return ret;
	}

	public static byte nibble(byte x){

		int low = (x & 0x0F);
		int high = (x >> 4) & 0x0F;

		byte ret = (byte) ((low << 4 | high) & 0xFF);

		return ret;
	}

	public static byte[] nibble(byte[] b){
		byte[] ret = new byte[b.length];

		for(int i = 0; i < b.length; i++){
			ret[b.length-i-1] = nibble(b[i]);

		}

		return ret;
	}

   public static byte[] big_endian(byte[] b){

        byte[] ret = new byte[b.length];

        for(int i = 0; i < b.length; i++){
            ret[b.length-i-1] = b[i];
        }

        return ret;
    }

	public static byte[] select(byte[] b, int offset, int len)
						 throws Exception {

		if(b == null || offset < 0 || offset > b.length ||
			 len < 1 || b.length < len) {
			throw new Exception("Wrong Parameter Input");
		}

		byte[] des = new byte[len];

		System.arraycopy(b,offset,des,0,len);
		return des;
	}

    /*
     *  meter id check
     */
    public static boolean checkMeterIdFormat(String meterId) {
        if (meterId == null) {
			return false;
		}

        byte[] _meterId = meterId.getBytes();
        for (int i = 0; i < _meterId.length; i++) {
            if ((48 <= _meterId[i] && _meterId[i] <= 57)
                    || (65 <= _meterId[i] && _meterId[i] <= 90)
                    || (97 <= _meterId[i] && _meterId[i] <= 122)) {
                continue;
            }
			else {
				return false;
			}
        }
        return true;
    }

    /*
     * (Utility Method)
     * Get yymmddHHMMss Value String.
     */
    public static String getYymmddhhmm(byte[] time) throws Exception {

        StringBuffer b = new StringBuffer();
        StringBuffer sb = new StringBuffer();
        char c = 0x00;

        if(time.length < 6) {
			throw new Exception("Length Error");
		}

        try {
            for(int i = 0;i<time.length; i++){

                if((time[i]&0xff) == 0xff) {
					sb.append(c);
				}
				else {
					sb.append((char)(time[i]&0xff));
				}
            }

            int year  = sb.charAt(0)*16*16 + sb.charAt(1);
            int month = sb.charAt(2);
            int day   = sb.charAt(3);
            //index 4 is Day of Week bit
            int hh    = sb.charAt(4);
            int mm    = sb.charAt(5);

            if(month < 1 || month > 12) {
				throw new Exception("Month Format Error");
			}
            if(day < 1 || day >  31) {
				throw new Exception("Day Format Error");
			}
            if(hh > 24) {
				throw new Exception("Hour Format Error");
			}
            if(mm > 59) {
				throw new Exception("Min Format Error");
			}
            b.append(Util.frontAppendNStr('0',Integer.toString(year),4));
            b.append(Util.frontAppendNStr('0',Integer.toString(month),2));
            b.append(Util.frontAppendNStr('0',Integer.toString(day),2));
            b.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
            b.append(Util.frontAppendNStr('0',Integer.toString(mm),2));

        }catch(Exception e) {
            throw e;
        }

        return b.toString();
    }


    /**
     * Check Array and fix to size.<p>
     * post add 0x00.<p>
     * @param array
     * @param size
     * @return - byte array (input size)
     */
    public static byte[] checkArrayBPost(byte[] array, int size)
                            throws Exception {

        if(array == null || array.length < 1 || size < 1) {
			throw new Exception("Input Param Error");
		}

        byte[] b = new byte[size];

        if(array.length <= size){
            System.arraycopy(array,0,b,0,array.length);
        } else if(array.length > size){
            System.arraycopy(array,0,b,0,size);
        }
        return b;
    }

    /**
     * Check Array and fix to size.<p>
     * front add 0x00.<p>
     * @param array
     * @param size
     * @return
     */
    public static byte[] checkArrayFPost(byte[] array, int size)
                            throws Exception {

        if(array == null || array.length < 1 || size < 1) {
			throw new Exception("Input Param Error");
		}

        byte[] b = new byte[size];

        if(array.length < size){
            System.arraycopy(array,0,b,size - array.length,array.length);
        } else if(array.length >= size){
            System.arraycopy(array,0,b,0,size);
        }
        return b;
    }


    /**
     * Get Hex Type 6byte date from String data "yyyyMMddhhmm" <p>
     * @param date
     * @return
     * @throws Exception
     */
    public static String hexDateToStr(byte[] date) throws Exception {

        int yyyy  = 0;
        int MM    = 0;
        int dd    = 0;
        int hh    = 0;
        int mm    = 0;

        String dateString = new String();

        try {
            if(date.length < 4) {
				throw new Exception("date length error!");
			}

            yyyy = DataFormat.hex2dec(date,0,2);
            MM   = DataFormat.hex2unsigned8(date[2]);
            dd   = DataFormat.hex2unsigned8(date[3]);

            if(MM < 1 || MM > 12) {
				throw new Exception("date month format error!");
			}
            if(dd < 1 || dd > 31) {
				throw new Exception("date day format error!");
			}

            dateString = ""+yyyy + (MM < 10? "0"+MM:""+MM) +
                         (dd < 10? "0"+dd:""+dd) + "0000";

        }catch(Exception e){
            //System.err.println(e);
            throw new Exception(e);
        }

        return dateString;
    }


    /**
     * Get Hex Type retsize byte date from String data "yyyyMMddhhmmss" <p>
     * @param date
     * @return
     * @throws Exception
     */
    public static byte[] strDate2Hex(String date,int retsize)
                            throws Exception {

        int yyyy  = 0;
        int MM    = 0;
        int dd    = 0;
        int hh    = 0;
        int mm    = 0;
        int ss    = 0;

        try {

            byte[] b = new byte[8];

            yyyy = Integer.parseInt(date.substring(0 , 4));
            MM   = Integer.parseInt(date.substring(4 , 6));
            dd   = Integer.parseInt(date.substring(6 , 8));

            try {
                hh   = Integer.parseInt(date.substring(8 , 10));
            }catch(Exception e){
            }
            try{
                mm   = Integer.parseInt(date.substring(10, 12));
            }catch(Exception e){
            }
            try {
                ss   = Integer.parseInt(date.substring(12, 14));
            }catch(Exception e){

            }

            b[0] = (byte)((yyyy >> 8) & 0xff);
            b[1] = (byte)(yyyy & 0xff);
            b[2] = (byte) MM;
            b[3] = (byte) dd;
            b[4] = (byte) hh;
            b[5] = (byte) mm;
            b[6] = (byte) ss;

            byte[] ret = new byte[retsize];

            System.arraycopy(b,0,ret,0,retsize);

            return ret;
        }catch(Exception e){
            throw e;
        }

    }
    /**
     * Get Hex Type 6byte date from String data "yyyyMMddhhmm" <p>
     * @param date
     * @return
     * @throws Exception
     */
    public static byte[] strDate2Hex(String date) throws Exception {

        int yyyy  = 0;
        int MM    = 0;
        int dd    = 0;
        int hh    = 0;
        int mm    = 0;

        byte yymmddhhmm[] = new byte[6];

        try {

            if(date.length() < 8) {
				throw new Exception("data length error!");
			}

            yyyy = Integer.parseInt(date.substring(0 , 4));
            MM   = Integer.parseInt(date.substring(4 , 6));
            dd   = Integer.parseInt(date.substring(6 , 8));

            if(MM < 1 || MM > 12) {
				throw new Exception("data month format error!");
			}
            if(dd < 1 || dd > 31) {
				throw new Exception("data day format error!");
			}

            if(date.length() >= 12){
                hh   = Integer.parseInt(date.substring(8 , 10));
                mm   = Integer.parseInt(date.substring(10, 12));
            }

            yymmddhhmm[0] = (byte)((yyyy >> 8) & 0xff);
            yymmddhhmm[1] = (byte)(yyyy & 0xff);
            yymmddhhmm[2] = (byte) MM;
            yymmddhhmm[3] = (byte) dd;
            yymmddhhmm[4] = (byte) hh;
            yymmddhhmm[5] = (byte) mm;

        }catch(Exception e){
            throw new Exception(e);
        }

        return yymmddhhmm;
    }


    /**
     * get Period (day) startdate and enddate. <p>
     * @param startdate
     * @param enddate
     * @return
     */
    public static int getPeriod(String startdate, String enddate){

        int returnValue = 0;
        long temp = 0;
        int year=0,month=0,day=0,year1=0,month1=0,day1=0;
        int year2 = 0, month2 = 0;

        try {
            year  = Integer.parseInt(startdate.substring(0,4));
            month = Integer.parseInt(startdate.substring(4,6));
            day   = Integer.parseInt(startdate.substring(6,8));

            year1  = Integer.parseInt(enddate.substring(0,4));
            month1 = Integer.parseInt(enddate.substring(4,6));
            day1   = Integer.parseInt(enddate.substring(6,8));

            Calendar cal=Calendar.getInstance();
            cal.set((year-1900),(month-1),day);

            Calendar cal2=Calendar.getInstance();
            cal2.set((year1-1900),(month1-1),day1);

            java.util.Date temp1 = cal.getTime();
            java.util.Date temp2 = cal2.getTime();

            temp = temp2.getTime() - temp1.getTime();

            if ( ( temp % 10 ) < 5 ) {
				temp = temp - ( temp % 10 );
			}
			else {
				temp = temp + ( 10 - ( temp % 10 ) );
			}

            returnValue = (int)( temp / ( 1000 * 60 * 60 * 24 ) );

        }
        catch (Exception ex)
        {
            //throw new Exception("JspCybus.datediff(\""+returnValue+"\")\r\n"+ex.getMessage());
        }

        return returnValue;

    }


    /**
     * Check Array and fix to size.<p>
     * post add 0x00.<p>
     * @param array
     * @param size
     * @return
     */
    public static char[] checkArrayBPost(char[] array, int size){

        StringBuffer sb = new StringBuffer();
        try{
            if(array.length < size){
                sb.append(array);   // order reverse to class12.
                for(int i = size - array.length; i > 0; i-- ) {
					sb.append((char)0x00);
				}
                return sb.toString().toCharArray();
            } else if(array.length > size){
                for(int i = 0; i < size; i++ ) {
					sb.append(array[i]);
				}
                return sb.toString().toCharArray();
            }
        }catch(Exception e){
            System.err.println("checkArrayBPost()"+e.getMessage());

        }

        return array;
    }

    /**
     * Check Array and fix to size.<p>
     * front add 0x00.<p>
     * @param array
     * @param size
     * @return
     */
    public static char[] checkArrayFPost(char[] array, int size){

        StringBuffer sb = new StringBuffer();

        try{
            if(array.length < size){
                for(int i = size - array.length; i > 0; i-- ) {
					sb.append((char)0x00);
				}
                sb.append(array);
                return sb.toString().toCharArray();
            } else if(array.length > size){
                for(int i = 0; i < size; i++ ) {
					sb.append(array[i]);
				}
                return sb.toString().toCharArray();
            }
        }catch(Exception e){
            System.err.println("checkArrayFPost()"+e.getMessage());
        }

        return array;
    }



    /**
     * Get Substring array from StringBuffer (startpoint to length).<p>
     * @param sb - buffer
     * @param startpoint - start offset
     * @param length - length from start
     * @return
     */
    public static char[] parseArray(StringBuffer sb,int startpoint,int length){

        char[] array = new char[length];

        try{
            int i = 0;
            while(length-- > 0){
                array[i++] = sb.charAt(startpoint++);
            }
        }catch(Exception e){

        }

        return array;

    }

    /**
     * Get Hex Type 6byte date from String data "yyyyMMddhhmm" <p>
     * @param date
     * @return
     * @throws Exception
     */
    public static String hexDateToStr(char[] date) throws Exception {

        int yyyy  = 0;
        int MM    = 0;
        int dd    = 0;
        int hh    = 0;
        int mm    = 0;

        String dateString = new String();

        try {
            if(date.length < 4) {
				throw new Exception("date length error!");
			}

            yyyy = date[0]*256+date[1];
            MM   = date[2];
            dd   = date[3];

            if(MM < 1 || MM > 12) {
				throw new Exception("date month format error!");
			}
            if(dd < 1 || dd > 31) {
				throw new Exception("date day format error!");
			}

            dateString = ""+yyyy + (MM < 10? "0"+MM:""+MM) +
            (dd < 10? "0"+dd:""+dd) + "0000";
        }catch(Exception e){
            //System.err.println(e);
            throw new Exception(e);
        }

        return dateString;
    }




    /**
     * Get Hex Type 6byte date from String data "yyyyMMddhhmm" <p>
     * @param date
     * @return
     * @throws Exception
     */
    public static char[] strDateToHex(String date) throws Exception {

        int yyyy  = 0;
        int MM    = 0;
        int dd    = 0;
        int hh    = 0;
        int mm    = 0;

        char yymmddhhmm[] = new char[6];

        try {

            if(date.length() < 8) {
				throw new Exception("data length error!");
			}

            yyyy = Integer.parseInt(date.substring(0 , 4));
            MM   = Integer.parseInt(date.substring(4 , 6));
            dd   = Integer.parseInt(date.substring(6 , 8));

            if(MM < 1 || MM > 12) {
				throw new Exception("data month format error!");
			}
            if(dd < 1 || dd > 31) {
				throw new Exception("data day format error!");
			}

            if(date.length() >= 12){
                hh   = Integer.parseInt(date.substring(8 , 10));
                mm   = Integer.parseInt(date.substring(10, 12));
            }

            yymmddhhmm[0] = (char)((yyyy >> 8) & 0xff);
            yymmddhhmm[1] = (char)(yyyy & 0xff);
            yymmddhhmm[2] = (char) MM;
            yymmddhhmm[3] = (char) dd;
            yymmddhhmm[4] = (char) hh;
            yymmddhhmm[5] = (char) mm;

        }catch(Exception e){
            throw new Exception(e);
        }

        return yymmddhhmm;
    }
    /**
     * Number transform to Char Array.<p>
     * @param num
     * @return
     */
    public static char[] toCharArray(int num){

        StringBuffer sb = new StringBuffer();
        int tmp = 0;

        try{
            if(num  == 0){
                sb.append((char)0x00);
            }else {
                while(num > 0) {
                    tmp = num % 256;
                    sb.append((char)(tmp));
                    num = (num - tmp) / 256;
                 }
                 sb.reverse();
            }
        }catch(Exception e){

        }
        return sb.toString().toCharArray();
    }


    /**
     * Check Array Count. <p>
     * array.length < size post append '0x00' .<p>
     * @param array
     * @param size
     * @return
     */
    public static char[] checkArray(char[] array, int size){

        StringBuffer sb = new StringBuffer();

        try{
            if(array.length < size){
                for(int i = size - array.length; i > 0; i-- ) {
					sb.append((char)0x00);
				}
                sb.append(array);
                return sb.toString().toCharArray();
            } else if(array.length > size){
                for(int i = 0; i < size; i++ ) {
					sb.append(array[i]);
				}
                return sb.toString().toCharArray();
            }
        }catch(Exception e){
            System.err.println("checkArray"+e.getMessage());
        }

        return array;
    }


    public static double hex2double64(byte[] b, int offset)
                        throws Exception {

        double val = 0d;

        if( b == null || offset < 0 || offset > b.length || b.length < 8) {
			throw new Exception("Wrong Parameter Input");
		}

        long dec   = DataFormat.hex2long(
                            DataFormat.LSB2MSB(
                                DataFormat.select(b,offset,4)));
        long round = DataFormat.hex2long(
                            DataFormat.LSB2MSB(
                                DataFormat.select(b,offset+4,4)));
        int scale = Long.toString(round).length();

        val = dec+(round*Math.pow(10,-scale));

        return val;
    }


    /**
     * decode IP address
     *
     * @param fepId <code>byte[]</code>
     * @return fepId <code>String</code>
     */
    public static String getDateTime(byte[] b)
        throws Exception
    {

        int blen = b.length;
        if(blen != 7) {
			throw new Exception("YYMMDDHHMMSS LEN ERROR : "+blen);
		}

        int idx = 0;

        int yyyy = DataFormat.hex2signed16(DataFormat.LSB2MSB(DataFormat.select(b,idx,2)));
        idx += 2;
        int mm = DataFormat.hex2unsigned8(b[idx++]);
        int dd = DataFormat.hex2unsigned8(b[idx++]);
        int hh = DataFormat.hex2unsigned8(b[idx++]);
        int MM = DataFormat.hex2unsigned8(b[idx++]);
        int ss = DataFormat.hex2unsigned8(b[idx++]);

        StringBuffer ret = new StringBuffer();
        ret.append(yyyy);
        if(mm < 10) {
			ret.append("0"+mm);
		}
		else {
			ret.append(mm);
		}
        if(dd < 10) {
			ret.append("0"+dd);
		}
		else {
			ret.append(dd);
		}
        if(hh < 10) {
			ret.append("0"+hh);
		}
		else {
			ret.append(hh);
		}
        if(MM < 10) {
			ret.append("0"+MM);
		}
		else {
			ret.append(MM);
		}
        if(ss < 10) {
			ret.append("0"+ss);
		}
		else {
			ret.append(ss);
		}

        if(ret.length() != 14){
            throw new Exception("DateTime Format Error : "+ret.toString());
        }

        return ret.toString();

    }

    /**
     * decode IP address
     *
     * @param fepId <code>byte[]</code>
     * @return fepId <code>String</code>
     */
    public static byte[] encodeDateTime(String datetime)
        throws Exception
    {

        byte[] time = new byte[]{0,0,0,0,0,0,0};
        int blen = datetime.length();
        if(blen < 8) {
			throw new Exception("YYMMDDHHMMSS LEN ERROR : "+blen);
		}

        int idx = 0;

        int yyyy = Integer.parseInt(datetime.substring(0,4));
        int mm = Integer.parseInt(datetime.substring(4,6));
        int dd = Integer.parseInt(datetime.substring(6,8));

        time[0] = (byte)yyyy;
        time[1] = (byte)(yyyy >> 8);
        time[2] = (byte)mm;
        time[3] = (byte)dd;

        if(blen >= 10)
        {
            int hh = Integer.parseInt(datetime.substring(8,10));
            time[4] = (byte)hh;
        }
        if(blen >= 12){
            int MM = Integer.parseInt(datetime.substring(10,12));
            time[5] = (byte)MM;
        }
        if(blen >= 14){
            int ss = Integer.parseInt(datetime.substring(12,14));
            time[6] = (byte)ss;
        }

        return time;
    }

    /**
     * encode MCU ID
     *
     * @param mcuId <code>String</code> MCU ID
     * @return bytes <code>byte[]</code>
     */
    public static byte[] encodeMcuId(String mcuId)
    {
        return mcuId.getBytes();
    }

    /**
     * decode MCU ID
     *
     * @param mcuId <code>byte[]</code> MCU ID
     * @return mcuId <code>String</code>
     */
    public static String decodeMcuId(byte[] mcuId)
    {
        return new String(mcuId);
    }

    /**
     * get FEP ID Bytes
     *
     * @return bytes <code>byte[]</code>
     */
    public static byte[] getFepIdBytes()
    {
        String fepId="127.0.0.1";
        try {
            InetAddress ia = InetAddress.getLocalHost();
            fepId = ia.getHostAddress();
        }catch(Exception ex) { }
        return encodeIpAddr(fepId);
    }

    /**
     * get FEP ID String
     *
     * @return fepId <code>String</code>
     */
    private static String serverIpAddress = null;
    public static String getFepIdString()
    {
        if(serverIpAddress == null)
        {
            String fepId="127.0.0.1";
            try {
                InetAddress ia = InetAddress.getLocalHost();
                fepId = ia.getHostAddress();
            }catch(Exception ex) { }
            serverIpAddress = fepId;
        }
        return serverIpAddress;
    }

    /**
     * encode IP address
     *
     * @param fepId <code>String</code>
     * @return bytes <code>byte[]</code>
     */
    public static byte[] encodeIpAddr( String fepId)
    {
        String[] ips = fepId.split("[\\.]");
        byte[] res = new byte[ips.length];
        for(int i = 0 ; i < ips.length ; i++)
        {
            res[i] = getByteToInt(ips[i]);
        }

        return res;
    }

    /**
     * decode IP address
     *
     * @param fepId <code>byte[]</code>
     * @return fepId <code>String</code>
     */
    public static String decodeIpAddr(byte[] fepId )
    {
        StringBuffer sb = new StringBuffer();
        sb.append(getIntToByte(fepId[0]));
        for(int i = 1 ; i < fepId.length ; i++)
        {
            sb.append(',').append(getIntToByte(fepId[i]));
        }
        return sb.toString();
    }

    /**
     * decode IP address
     *
     * @param fepId <code>byte[]</code>
     * @return fepId <code>String</code>
     */
    public static String decodeGMT(byte[] gmtTime )
    {
        StringBuffer sb = new StringBuffer();
        byte[] gmt = new byte[2];
        byte[] dst = new byte[2];
        byte[] datetime = new byte[7];
        System.arraycopy(gmtTime,0,gmt,0,2);
        System.arraycopy(gmtTime,2,dst,0,2);
        System.arraycopy(gmtTime,4,datetime,0,7);
        sb.append("GMT").append(getIntTo2Byte(gmt)).append(",");
        sb.append("DST").append(getIntTo2Byte(dst)).append(",");
        sb.append(Bcd.decodeTime(datetime));

        return sb.toString();
    }

    /**
     * encode GMT
     *
     * @param GMT <code>String</code>
     * @return bytes <code>byte[]</code>
     */
    public static byte[] encodeGMT(String gmtTime)
    {
        String[] ips = gmtTime.split("[\\,]");
        byte[] res = new byte[11];
        int GMT = 0, DST = 0;
        if(ips[0].substring(3,4).equals("-")){
            GMT = Integer.parseInt(ips[0].substring(4))*(-1);
        }else{
            GMT = Integer.parseInt(ips[0].substring(3));
        }
        if(ips[1].substring(3,4).equals("-")){
            DST = Integer.parseInt(ips[1].substring(4))*(-1);
        }else{
            DST = Integer.parseInt(ips[1].substring(3));
        }

        byte[] gmt = get2ByteToInt(GMT);
        byte[] dst = get2ByteToInt(DST);
        byte[] datetime = Bcd.encodeTime(ips[2]);
        System.arraycopy(gmt,0,res,0,2);
        System.arraycopy(dst,0,res,2,2);
        System.arraycopy(datetime,0,res,4,7);
        return res;
    }

    // bit operation method

    /**
     * convert int to byte
     *
     * @param val <code>String</code>
     * @return byte <code>byte</code>
     */
    public static byte getByteToInt(String val)
    {
        int ival = Integer.parseInt(val);
        return (byte)ival;
    }

    /**
     * convert int to byte
     *
     * @param val <code>int</code>
     * @return byte <code>byte</code>
     */
    public static byte getByteToInt(int val)
    {
        return (byte)val;
    }

    /**
     * convert byte to int
     *
     * @param val <code>byte</code>
     * @return int <code>int</code>
     */
    public static int getIntToByte(byte val)
    {
        int ival = ((val & 0xff) << 0);
        return ival;
    }

    /**
     * convert int to 2 bytes
     *
     * @param val <code>val</code>
     * @return bytes <code>byte[]</code>
     */
    public static byte[] get2ByteToInt(String val)
    {
        int ival = 0;
        try { ival = Integer.parseInt(val); }
        catch(Exception ex) {}

        byte[] res = new byte[2];
        res[0] = (byte)(ival >> 8);
        res[1] = (byte)ival;
        return res;
    }

    /**
     * convert int to 2 bytes
     *
     * @param val <code>int</code>
     * @return bytes <code>byte[]</code>
     */
    public static byte[] get2ByteToInt(int val)
    {
        byte[] res = new byte[2];
        res[0] = (byte)(val >> 8);
        res[1] = (byte)val;
        return res;
    }

    public static byte[] get2ByteToInt(boolean isConvert, int val)
    {
        byte[] res = new byte[2];
        res[0] = (byte)(val >> 8);
        res[1] = (byte)val;
        convertEndian(isConvert, res);
        return res;
    }

    /**
     * convert 2 bytes to int
     *
     * @param val <code>byte[]</code>
     * @return int <code>int</code>
     */
    public static int getIntTo2Byte(byte[] val)
    {
        int res = ((val[0] & 0xff) << 8)
            + (val[1] & 0xff);
        return res;
    }
    /**
     * convert 2 bytes to int
     *
     * @param val1 <code>byte</code>
     * @param val2 <code>byte</code>
     * @return int <code>int</code>
     */
    public static int getIntTo2Byte(byte val1, byte val2)
    {
        int res = ((val1 & 0xff) << 8)
            + (val2 & 0xff);
        return res;
    }

    /**
     * convert int to 2 bytes
     *
     * @param val <code>int</code>
     * @return bytes <code>byte[]</code>
     */
    public static byte[] get3ByteToInt(int val)
    {
        byte[] res = new byte[3];
        res[0] = (byte)(val >> 16);
        res[1] = (byte)(val >> 8);
        res[2] = (byte)val;
        return res;
    }

    /**
     * convert int to 2 bytes
     *
     * @param val <code>int</code>
     * @return bytes <code>byte[]</code>
     */
    public static byte[] get4ByteToInt(int val)
    {
        byte[] res = new byte[4];
        res[0] = (byte)(val >> 24);
        res[1] = (byte)(val >> 16);
        res[2] = (byte)(val >> 8);
        res[3] = (byte)val;
        return res;
    }

    public static byte[] getPLCPortByte(boolean isConvert, int port) {
        byte[] res=get4ByteToInt(port);
        convertEndian(isConvert, res);
        return res;
    }


    /**
     * convert int to 2 bytes
     *
     * @param val <code>int</code>
     * @return bytes <code>byte[]</code>
     */
    public static byte[] get4ByteToInt(long val)
    {
        byte[] res = new byte[4];
        res[0] = (byte)(val >> 24);
        res[1] = (byte)(val >> 16);
        res[2] = (byte)(val >> 8);
        res[3] = (byte)val;
        return res;
    }

    /**
     * convert 2 bytes to int
     *
     * @param val <code>byte[]</code>
     * @return int <code>int</code>
     */
    public static int getIntTo4Byte(byte[] val)
    {
        int res = ((val[0] & 0xff) << 24)
            + ((val[1] & 0xff) << 16)
            + ((val[2] & 0xff) << 8)
            + (val[3] & 0xff);
        return res;
    }

    public static int getIntToBytes(byte[] data)
    {
        int res = 0;
        for(int i = 0 ; i < data.length ; i++)
        {
            if(i > 0) {
                res<<=8;
            }
            res|=(data[i]&0xff);
        }
        return res;
    }

    public static long getLongToBytes(byte[] data)
    {
        long res = 0;
        for(int i = 0 ; i < data.length ; i++)
        {
            if(i > 0) {
                res<<=8;
            }
            res|=(data[i]&0xff);
        }
        return res;
    }


    /**
     * getBCDtoBytes (Ex: 0x00045612 -> 45612)
     *
     * @param data
     * @return
     */
    public static String getBCDtoBytes(byte[] data){
        String bcd="";
        boolean first=true;
        for(int i=0;i<data.length;i++){
            if(bcd.length()==0&&Integer.parseInt((((data[i]&0xF0)>>4)+(data[i]&0x0F))+"")!=0){
                bcd+=((first&&((data[i]&0xF0)>>4)==0) ? "":((data[i]&0xF0)>>4)+"")+(data[i]&0x0F);
                first=false;
            }else if (!first){
                bcd+=((data[i]&0xF0)>>4)+""+(data[i]&0x0F);
            }
        }
        if(first){
            bcd="0";
        }
        return bcd;
    }


    /**
     * Get Type G Date For MBus
     * @param rawData
     * @return
     */
    public static Double getDateTo2Byte(byte[] rawData){
        int day=getIntTo2Byte(rawData)&0x0f;
        int month=(getIntTo2Byte(rawData)&0xf00)>>8;
        int year=((getIntTo2Byte(rawData)&0xf000)>>9)|((getIntTo2Byte(rawData)&0xe0)>>5);
        String yyyymmdd="20"+(year<10?"0"+year:year)+(month<10?"0"+month:month)+(day<10?"0"+day:day);
        Double doubleYyyymmdd=Double.parseDouble(yyyymmdd);
        return doubleYyyymmdd;
    }

    /**
     * 4 Bytes -> Date (LSB)
     * Typedef struct
     * {
     * unsigned long sec:6;
     * unsigned long min:6;
     * unsigned long hour:5;
     * unsigned long day:5;
     * unsigned long month:4;
     * unsigned long year:6;
     * } DATE
     * 0x121529B
     * 000100 1000 01010 10000 001010 011011
     *      4    8    10    16     10     27
     * 2004/08/10 16:10:27
     * @param data
     * @return
     */
    public static String convert4ByteToDateString(byte[] data){
        BigInteger b = new BigInteger(data);
        String temp = b.toString(2);
        DecimalFormat df = new DecimalFormat("00");
        StringBuffer sbTemp = new StringBuffer();
        int diff = 32 - temp.length();
        sbTemp.append(2000+Integer.parseInt(temp.substring(0,6-diff),2));
        sbTemp.append(df.format(Integer.parseInt(temp.substring(6-diff,10-diff),2)));
        sbTemp.append(df.format(Integer.parseInt(temp.substring(10-diff,15-diff),2)));
        sbTemp.append(df.format(Integer.parseInt(temp.substring(15-diff,20-diff),2)));
        sbTemp.append(df.format(Integer.parseInt(temp.substring(20-diff,26-diff),2)));
        sbTemp.append(df.format(Integer.parseInt(temp.substring(26-diff,32-diff),2)));

        return sbTemp.toString();
    }

    public static void arraycopy(IoBuffer src,int spos,byte[] dest,
            int dpos,int dlen)
    {
        for(int i = dpos ; i < dlen ; i++) {
            dest[i]=src.get(spos++);
        }
    }

    public static byte getVersionByte(String ver)
    {
        int idx = ver.indexOf(',');
        int pre = Integer.parseInt(ver.substring(0,idx));
        int post = Integer.parseInt(ver.substring(idx+1,
                    ver.length()));
        int f = (pre << 4) | post;
        return (byte)(f & 0xff);
    }

    public static String getVersionString(byte ver)
    {
        int pre = (ver & 0xf0) >> 4;
        int post = (ver & 0x0f);

        return pre+","+post;
    }

    /**
     * merge to byte array
     *
     * @param src1 <code>byte[]</code>
     * @param off1 <code>int</code>
     * @param len1 <code>int</code>
     * @param src2 <code>byte[]</code>
     * @param off2 <code>int</code>
     * @param len2 <code>int</code>
     * @return bytes <code>byte[]</code>
     */
    public static byte[] arrayAppend(byte[] src1,int off1,
            int len1, byte[] src2, int off2, int len2)
    {
        byte[] dest = new byte[len1 + len2];
        System.arraycopy(src1,off1,dest,0,len1);
        System.arraycopy(src2,off2,dest,len1,len2);
        return dest;
    }

    /**
     * merge to byte array
     *
     * @param buf1 <code>byte[]</code>
     * @param buf2 <code>byte[]</code>
     * @return bytes <code>byte[]</code>
     */
    public static byte[] append(byte[] buf1, byte[] buf2)
    {
        byte[] newbuf = new byte[buf1.length + buf2.length];
        System.arraycopy(buf1,0,newbuf,0,buf1.length);
        System.arraycopy(buf2,0,newbuf,buf1.length,buf2.length);
        return newbuf;
    }

    /**
     * get OID String
     *
     * @param oid <code>byte[]</code>
     * @return oid <code>String</code>
     */
    public static String getOIDString(byte[] oid)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(getIntToByte(oid[0]));
        sb.append(',');
        sb.append(getIntToByte(oid[1]));
        sb.append(',');
        sb.append(getIntToByte(oid[2]));
        return sb.toString();
    }

    public static OID getOIDByMIBName(String mibName)
    {
        MIBUtil mibUtil = MIBUtil.getInstance();
        return mibUtil.getOid(mibName);
    }
    
    public static OID getOIDByMIBName(String nameSpace, String mibName)
    {
        MIBUtil mibUtil = MIBUtil.getInstance(nameSpace);
        return mibUtil.getOid(mibName);
    }

    /**
     * get FMPVariable according to specified OID
     *
     * @param oid <code>OID</code>
     * @return variable <code>FMPVariable</code>
     */
    public static FMPVariable getFMPVariableObject(OID oid)
    {
        MIBUtil mibUtil = MIBUtil.getInstance();
        MIBNode node = mibUtil.getMIBNodeByOid(oid.getValue());
        if(node == null) {
            log.error("MIBNode matched oid : " + oid
                    + "is does not exist in MIB");
        }
        String type = ((DataMIBNode)node).getType();
        FMPVariable var = FMPVariable.getFMPVariableObject(type);

        return var;
    }

    /**
     * get FMPVariable according to specified OID
     *
     * @param oid <code>OID</code>
     * @return variable <code>FMPVariable</code>
     */
    public static FMPVariable getFMPVariableObject(String nameSpace, OID oid)
    {
        MIBUtil mibUtil = MIBUtil.getInstance(nameSpace);
        MIBNode node = mibUtil.getMIBNodeByOid(oid.getValue());        

        if(node == null) {
            log.error("MIBNode matched oid : " + oid
                    + "is does not exist in MIB");
        }
        String type = ((DataMIBNode)node).getType();
        FMPVariable var = FMPVariable.getFMPVariableObject(type);

        return var;
    }
    
    public static SMIValue getStringSMIValue(String value)
    {
        SMIValue smiValue = new SMIValue();
        smiValue.setOid(new OID("1.11.0"));
        OCTET variable = new OCTET(value);
        smiValue.setVariable(variable);
        return smiValue;
    }

    /**
     * make SMIValue
     *
     * @param name <code>String</code>
     * @param value <code>Object</code>
     * @return smivalue <code>SMIValue</code>
     */
    public static SMIValue getSMIValue(String name, Object value)
        throws Exception
    {
        if (name==null) {
            return null;
        }

        MIBUtil mibUtil = MIBUtil.getInstance();
        SMIValue smiValue = new SMIValue();
        smiValue.setOid(mibUtil.getOid(name));
        if(value == null) {
            return smiValue;
        }
        log.debug("name="+name+",oid="+smiValue.getOid()+",value="+value);
        log.debug("name="+name);
        log.debug("oid="+smiValue.getOid());
        log.debug("value="+value);
        if(!isEntryOid(smiValue.getOid()))
        {
            smiValue.setVariable(getFMPVariable(name,(String)value));
        }
        else
        {
            smiValue.setVariable(getFMPVariable(name,(Entry)value));
        }

        log.debug(smiValue);
        return smiValue;
    }
    
    /**
     * make SMIValue
     * @param nameSpace <code>String</code>
     * @param name <code>String</code>
     * @param value <code>Object</code>
     * @return smivalue <code>SMIValue</code>
     */
    public static SMIValue getSMIValue(String nameSpace, String name, Object value)
        throws Exception
    {
        if (name==null) {
            return null;
        }

        
        MIBUtil mibUtil = MIBUtil.getInstance(nameSpace);
        SMIValue smiValue = new SMIValue();
        smiValue.setOid(mibUtil.getOid(name));
        if(value == null) {
            return smiValue;
        }
        log.debug("name="+name+",oid="+smiValue.getOid()+",value="+value);
        log.debug("name="+name);
        log.debug("oid="+smiValue.getOid());
        log.debug("value="+value);
        if(!isEntryOid(smiValue.getOid()))
        {
            smiValue.setVariable(getFMPVariable(name,(String)value));
        }
        else
        {
            smiValue.setVariable(getFMPVariable(name,(Entry)value));
        }

        log.debug(smiValue);
        return smiValue;
    }

    /**
     * make SMIValue By Oid
     *
     * @param oid <code>String</code>
     * @param value <code>Object</code>
     * @return smivalue <code>SMIValue</code>
     */
    public static SMIValue getSMIValueByOid(String oid, Object value)
        throws Exception
    {
        MIBUtil mibUtil = MIBUtil.getInstance();
        SMIValue smiValue = new SMIValue();
        smiValue.setOid(new OID(oid));
        String name = mibUtil.getName(oid);
        if(value == null || name == null) {
            return smiValue;
        }
        if(!isEntryOid(smiValue.getOid()))
        {
            smiValue.setVariable(getFMPVariable(name,(String)value));
        }
        else
        {
            smiValue.setVariable(getFMPVariable(name,(Entry)value));
        }

        return smiValue;
    }
    

    /**
     * make SMIValue By Oid
     *
     * @param oid <code>String</code>
     * @param value <code>Object</code>
     * @return smivalue <code>SMIValue</code>
     */
    public static SMIValue getSMIValueByOid(String nameSpace, String oid, Object value)
        throws Exception
    {
        MIBUtil mibUtil = MIBUtil.getInstance(nameSpace);
        SMIValue smiValue = new SMIValue();
        smiValue.setOid(new OID(oid));
        String name = mibUtil.getName(oid);
        if(value == null || name == null) {
            return smiValue;
        }
        if(!isEntryOid(smiValue.getOid()))
        {
            smiValue.setVariable(getFMPVariable(name,(String)value));
        }
        else
        {
            smiValue.setVariable(getFMPVariable(name,(Entry)value));
        }

        return smiValue;
    }

    /**
     * make SMIValue
     *
     * @param value <code>FMPVariable</code>
     * @return smivalue <code>SMIValue</code>
    */
    public static SMIValue getSMIValue(FMPVariable value)
        throws Exception
    {
        if(value == null) {
            return null;
        }

        MIBUtil mibUtil = MIBUtil.getInstance();

        SMIValue smiValue = new SMIValue();
        smiValue.setOid(mibUtil.getOid(value.getMIBName()));
        smiValue.setVariable(value);

        return smiValue;
    }
    
    /**
     * make SMIValue
     *
     * @param value <code>FMPVariable</code>
     * @return smivalue <code>SMIValue</code>
    */
    public static SMIValue getSMIValue(String nameSpace, FMPVariable value)
        throws Exception
    {
        if(value == null) {
            return null;
        }

        MIBUtil mibUtil = MIBUtil.getInstance(nameSpace);

        SMIValue smiValue = new SMIValue();
        smiValue.setOid(mibUtil.getOid(value.getMIBName()));
        smiValue.setVariable(value);

        return smiValue;
    }

    /**
     * get FMPVariable Object
     *
     * @param name <code>String</code>
     * @param value <code>String</code>
     * @return variable <code>FMPVariable</code>
     */
    public static FMPVariable getFMPVariable(String name,String value)
        throws Exception
    {
        MIBUtil mibUtil = MIBUtil.getInstance();
        MIBNode node = mibUtil.getMIBNodeByName(name);
        if(node == null) {
            throw new Exception("not found such mib name["+name+"]");
        }
        String type = ((DataMIBNode)node).getType();
        log.debug("type : " + type);
        FMPVariable var = FMPVariable.getFMPVariableObject(type);
        if (value == null)
        {
            return var;
        }
        int nodeType = var.getSyntax();
        //log.debug("nodeType : " + nodeType);
        if(nodeType == DataType.BOOL)
        {
            try {
                int ival = Integer.parseInt(value);
                if(ival == 0) {
                    ((BOOL)var).setValue(false);
                }
                else {
                    ((BOOL)var).setValue(true);
                }
            } catch(Exception ex){
                ((BOOL)var).setValue(Boolean.valueOf(value).booleanValue());
            }
        }
        else if(nodeType == DataType.UINT) {
            ((UINT)var).setValue(Long.parseLong(value));
        }
        else if(nodeType == DataType.CHAR) {
            ((CHAR)var).setValue(value.toCharArray()[0]);
        }
        else if(nodeType == DataType.SHORT) {
            ((SHORT)var).setValue((short)Integer.parseInt(value));
        }
        else if(nodeType == DataType.INT) {
            ((INT)var).setValue(Integer.parseInt(value));
        }
        else if(nodeType == DataType.WORD) {
            ((WORD)var).setValue(Integer.parseInt(value));
        }
        else if(nodeType == DataType.BYTE) {
            ((BYTE)var).setValue(Integer.parseInt(value));
        }
        else if(nodeType == DataType.BCD) {
            ((BCD)var).setValue(value);
        }
        else if(nodeType == DataType.VER) {
            ((VER)var).setValue(value);
        }
        else if(nodeType == DataType.HEX) {
            ((HEX)var).setValue(value);
        }
        else if(nodeType == DataType.STRING
                || nodeType == DataType.STREAM) {
            ((OCTET)var).setValue(value);
        }
        else if(nodeType == DataType.IPADDR) {
            ((IPADDR)var).setValue(value);
        }
        else if(nodeType == DataType.TIMESTAMP) {
            ((TIMESTAMP)var).setValue(value);
        }
        else if(nodeType == DataType.TIMEDATE) {
            ((TIMEDATE)var).setValue(value);
        }
        else {
            throw new Exception("not defined Data type");
        }

        return var;
    }

    /**
     * get FMPVariable
     *
     * @param name <code>String</code>
     * @param value <code>Entry</code>
     * @return variable <code>FMPVariable</code>
     */
    public static FMPVariable getFMPVariable(String name,
            Entry value) throws Exception
    {
        OPAQUE var = new OPAQUE(value);
        return var;
    }

    /**
     *  check whether specified oid is entry oid or not
     *
     * @param oid <code>OID</code>
     * @return result <code>boolean</code>
     */
    public static boolean isEntryOid(OID oid)
    {
        String strOid = oid.getValue();
        if(strOid == null || !strOid.endsWith(".0")) {
            return false;
        }
        MIBNode node = MIBUtil.getInstance().getMIBNodeByOid(strOid);
        String type = ((DataMIBNode)node).getType();
        if(!type.toUpperCase().equals("OPAQUE")) {
            return false;
        }
        return true;
    }

    /**
     *  check whether specified oid is entry oid or not
     *
     * @param strOid <code>String</code>
     * @return result <code>boolean</code>
     */
    public static boolean isEntryOid(String nameSpace, String strOid)
    {
        if(strOid == null || !strOid.endsWith(".0")) {
            return false;
        }
        MIBNode node = MIBUtil.getInstance(nameSpace).getMIBNodeByOid(strOid);
        String type = ((DataMIBNode)node).getType();
        if(!type.toUpperCase().equals("OPAQUE")) {
            return false;
        }
        return true;
    }
    
    /**
     *  check whether specified oid is entry oid or not
     *
     * @param strOid <code>String</code>
     * @return result <code>boolean</code>
     */
    public static boolean isEntryOid(String strOid)
    {
        if(strOid == null || !strOid.endsWith(".0")) {
            return false;
        }
        MIBNode node = MIBUtil.getInstance().getMIBNodeByOid(strOid);
        String type = ((DataMIBNode)node).getType();
        if(!type.toUpperCase().equals("OPAQUE")) {
            return false;
        }
        return true;
    }

    public static boolean isSubMIBByOid(String parent,String coid)
    {
        try
        {
            MIBUtil mu = MIBUtil.getInstance();
            String poid = mu.getOid(parent).toString();
            int idx = poid.indexOf(".0");
            String prefix = null;
            if(idx > 0)
            {
                prefix = poid.substring(0,idx+1);
            } else
            {
                prefix = poid;
            }
            return coid.startsWith(prefix);
        }
        catch(Exception ex)
        {
            log.error("isSubMIBByOid failed :",ex);
        }
        return false;
    }

    public static boolean isSubMIBByName(String parent,String child)
    {
        try
        {
            MIBUtil mu = MIBUtil.getInstance();
            String poid = mu.getOid(parent).toString();
            String coid = mu.getOid(child).toString();
            int idx = poid.indexOf(".0");
            String prefix = null;
            if(idx > 0)
            {
                prefix = poid.substring(0,idx+1);
            } else
            {
                prefix = poid;
            }
            return coid.startsWith(prefix);
        }
        catch(Exception ex)
        {
            log.error("isSubMIBByOid failed :",ex);
        }
        return false;
    }

    public static boolean isSubOidByOid(String poid,String coid)
    {
        try
        {
            int idx = poid.indexOf(".0");
            String prefix = null;
            if(idx > 0)
            {
                prefix = poid.substring(0,idx+1);
            } else
            {
                prefix = poid;
            }
            return coid.startsWith(prefix);
        }
        catch(Exception ex)
        {
            log.error("isSubMIBByOid failed :",ex);
        }
        return false;
    }

    public static boolean isSuOidByName(String poid,String child)
    {
        try
        {
            MIBUtil mu = MIBUtil.getInstance();
            String coid = mu.getOid(child).toString();
            int idx = poid.indexOf(".0");
            String prefix = null;
            if(idx > 0)
            {
                prefix = poid.substring(0,idx+1);
            } else
            {
                prefix = poid;
            }
            return coid.startsWith(prefix);
        }
        catch(Exception ex)
        {
            log.error("isSubMIBByOid failed :",ex);
        }
        return false;
    }


    /**
     * convert endian
     * @param stream <code>byte[]</code> byte stream
     */
    public static void convertEndian(byte[] stream)
    {
        byte b = 0;
        if(GeneralDataConstants.LITTLE_ENDIAN)
        {
            int j = stream.length/2;
            int len = stream.length - 1;
            for(int i = 0 ; i < j ; i++)
            {
                b = stream[i];
                stream[i] = stream[len - i];
                stream[len - i] = b;
            }
        }
    }

    /**
     * convert endian
     * @param isConvert
     * @param stream
     */
    public static void convertEndian(boolean isConvert, byte[] stream){
        if(isConvert){
            convertEndian(stream);
        }
    }

    /**
     * convert endian
     * @param stream <code>byte[]</code> byte stream
     */
    public static void reverse(byte[] stream)
    {
        byte b = 0;
        int j = stream.length/2;
        int len = stream.length - 1;
        for(int i = 0 ; i < j ; i++)
        {
            b = stream[i];
            stream[i] = stream[len - i];
            stream[len - i] = b;
        }
    }

    /**
     * convert endian
     * @param stream <code>byte[]</code> byte stream
     */
    public static byte[] getReverseBytes(byte[] stream)
    {
        byte[] bx = new byte[stream.length];
        System.arraycopy(stream,0,bx,0,bx.length);
        reverse(bx);
        return bx;
    }

    /**
     * convert endian
     * @param stream <code>byte[]</code> byte stream
     */
    public static void reverse(byte[] stream,int pos,int len)
    {
        byte b = 0;
        int j = len/2;
        int length = len - 1;
        for(int i = pos ; i < pos+j ; i++)
        {
            b = stream[i+pos];
            stream[i+pos] = stream[(pos+length) - i];
            stream[(pos+length) - (i+pos)] = b;
        }
    }

    /**
     *  get MIB Class name specified oid
     *
     * @param oid <code>String</code>
     * @return clsas name <code>String</code>
     */
    public static String getMIBClassName(String oid)
    {
        if(!isEntryOid(oid)) {
            return null;
        }
        String prefix =
            "com.aimir.fep.protocol.fmp.frame.service.entry.";
        MIBUtil mibUtil = MIBUtil.getInstance();
        return prefix+mibUtil.getName(oid);
    }
    
    /**
     *  get MIB Class name specified oid
     *
     * @param oid <code>String</code>
     * @return clsas name <code>String</code>
     */
    public static String getMIBClassName(String nameSpace, String oid)
    {
        if(!isEntryOid(nameSpace, oid)) {
            return null;
        }
        String prefix =
            "com.aimir.fep.protocol.fmp.frame.service.entry.";
        MIBUtil mibUtil = MIBUtil.getInstance(nameSpace);
        return prefix+mibUtil.getName(oid);
    }

    public static String convType(String type)
    {
        String convType = null;

        if (type == null)
        {
            return null;
        }

        if (type.equals("string")) {
            convType = "OCTET";
        }
        else if (type.equals("char")) {
            convType = "OCTET";
        }
        else if (type.equals("int")) {
            convType = "INT";
        }
        else if (type.equals("short")) {
            convType = "SHORT";
        }
        else if (type.equals("STREAM")) {
            convType = "OCTET";
        }
        else if (type.equals("STRING")) {
            convType = "OCTET";
        }
        else {
            convType = type;
        }

        return convType;
    }

    public static byte[] readByteFromFile(File file)
        throws Exception
    {
        InputStream is = new FileInputStream(file);
        try {
            ByteArrayOutputStream baos =
                new ByteArrayOutputStream((int) file.length());
            // initial size
            byte[] buffer = new byte[128];
            int bytesRead = is.read(buffer, 0, buffer.length);
            while (bytesRead > 0) {
                baos.write(buffer, 0, bytesRead);
                bytesRead = is.read(buffer, 0, buffer.length);
            }
            return baos.toByteArray();
        } finally
        {
            if (is != null)
            {
                is.close();
            }
        }
    }

    private static void getSMIValueFileData(File file,
            CommandData command) throws Exception
    {
        byte[] bx = readByteFromFile(file);
        int divsize = 50000;
        try{
            divsize = Integer.parseInt( FMPProperty.getProperty(
                "firware.update.file.divsize","50000"));
        }catch(Exception Ignored) {}

        int seq = bx.length / divsize;
        int remain = bx.length % divsize;
        byte[] bxx = null;
        for(int i = 0 ; i < seq ; i++)
        {
            bxx = new byte[divsize];
            System.arraycopy(bx,i*divsize,bxx,0,bxx.length);
            command.append(DataUtil.getSMIValue(new OCTET(bxx)));
        }

        if(remain > 0)
        {
            bxx = new byte[remain];
            System.arraycopy(bx,seq*divsize,bxx,0,bxx.length);
            command.append(DataUtil.getSMIValue(new OCTET(bxx)));
        }
    }

    public static CommandData getFirmwareCommandData(
            String filename,int installType,String reservedTime)
        throws Exception
    {
        long perm = 777;
        File file = new File(filename);
        String fname = null;
        if(!file.exists()) {
            throw new Exception("File["+filename+"] does not exist");
        }
        int idx = filename.lastIndexOf("/");
        if(idx > 0) {
            fname = filename.substring(idx+1);
        }
        else {
            fname = filename;
        }
        MIBUtil mibUtil = MIBUtil.getInstance();
        CommandData command = new CommandData();
        command.setCmd(mibUtil.getOid("cmdInstallFile"));
        // File Name
        command.append(getStringSMIValue(fname));
        // File Size
        command.append(getSMIValue(new UINT(file.length())));
        // File Mode
        command.append(getSMIValue(new UINT(perm)));
        // File Time
        command.append(getSMIValue(new TIMESTAMP(
                        TimeUtil.getDateUsingFormat(
                            file.lastModified(), "yyMMddHHmmss"))));
        // File Install Type
        command.append(getSMIValue(new BYTE(installType)));
        // File Reserved Time
        command.append(getSMIValue(new TIMESTAMP(reservedTime)));

        getSMIValueFileData(file,command);

        return command;
    }

    public static CommandData getFirmwareCommand(String serverIp)
    throws Exception
    {
        MIBUtil mibUtil = MIBUtil.getInstance();
        CommandData command = new CommandData();
        command.setCmd(mibUtil.getOid("cmdFirmwareUpdate"));
        // address
        command.append(getStringSMIValue(serverIp));
        // File Size
        command.append(getStringSMIValue("filename"));
        return command;
    }

    public static CommandData getPutFileCommandData(String filename)
        throws Exception
    {
        long perm = 777;
        File file = new File(filename);
        String fname = null;
        if(!file.exists()) {
            throw new Exception("File["+filename+"] does not exist");
        }
        int idx = filename.lastIndexOf("/");
        if(idx > 0) {
            fname = filename.substring(idx+1);
        }
        else {
            fname = filename;
        }
        //fname = "/app/sw/"+fname;
        MIBUtil mibUtil = MIBUtil.getInstance();
        CommandData command = new CommandData();
        command.setCmd(mibUtil.getOid("cmdPutFile"));
        // File Name
        command.append(getStringSMIValue(fname));
        // File Size
        command.append(getSMIValue(new UINT(file.length())));
        // File Mode
        command.append(getSMIValue(new UINT(perm)));
        // File Time
        command.append(getSMIValue(new TIMESTAMP(
                        TimeUtil.getDateUsingFormat(
                            file.lastModified(), "yyMMddHHmmss"))));
        getSMIValueFileData(file,command);

        return command;
    }

    public static CommandData getPutFileCommandData(String filename,
            String fname)
        throws Exception
    {
        long perm = 777;
        File file = new File(filename);
        if(!file.exists()) {
            throw new Exception("File["+filename+"] does not exist");
        }
        int idx = filename.lastIndexOf("/");

        MIBUtil mibUtil = MIBUtil.getInstance();
        CommandData command = new CommandData();
        command.setCmd(mibUtil.getOid("cmdPutFile"));
        // File Name
        command.append(getStringSMIValue(fname));
        // File Size
        command.append(getSMIValue(new UINT(file.length())));
        // File Mode
        command.append(getSMIValue(new UINT(perm)));
        // File Time
        command.append(getSMIValue(new TIMESTAMP(
                        TimeUtil.getDateUsingFormat(
                            file.lastModified(), "yyMMddHHmmss"))));
        getSMIValueFileData(file,command);

        return command;
    }

    public static String saveDownloadFile(String filename,
            CommandData command) throws Exception
    {
        String mcuId = command.getMcuId();
        String dfile = filename.substring(
                filename.lastIndexOf("/")+1);
        String ddir = FMPProperty.getProperty("File.download.dir");
        dfile = ddir+"/"+mcuId+','+dfile;
        log.debug("download file["+dfile+"]");
        File file = new File(dfile);
        MIBUtil mibUtil = MIBUtil.getInstance();
        if(command.getErrCode().getValue() > 0)
        {
            throw new Exception("Command["
                    +mibUtil.getName(command.getCmd().getValue())
                    +"]  failed : ERROR["+ErrorCode.getMessage(
                        command.getErrCode().getValue())+"]");
        }
        SMIValue[] smiValues = command.getSMIValue();
        log.debug("filesize["+
                smiValues[0].getVariable().toString()+"]");
        log.debug("fileMode["+
                smiValues[1].getVariable().toString()+"]");
        log.debug("fileTime["+
                smiValues[2].getVariable().toString()+"]");

        FileOutputStream fos = new FileOutputStream(file);
        try
        {
            byte[] bx = null;
            for(int i = 3 ; i < smiValues.length ; i++)
            {
                bx = ((OCTET)smiValues[i].getVariable()).getValue();
                fos.write(bx,0,bx.length);
                fos.flush();
            }
        }catch(Exception ex)
        {
            try {fos.close();}catch(Exception exx) {}
            try{
                if(file.exists()) {
                    file.delete();
                }
            }catch(Exception exx) {}
            throw ex;
        }

        return file.getName();
    }

    public static CommandData getGetFileCommandData(String filename)
        throws Exception
    {
        MIBUtil mibUtil = MIBUtil.getInstance();
        CommandData command = new CommandData();
        command.setCmd(mibUtil.getOid("cmdGetFile"));
        // File Name
        command.append(getStringSMIValue(filename));

        return command;
    }

    public static byte[] setFloat(float v) throws Exception {
        return setInt(Float.floatToIntBits(v));
    }

    public static byte[] setInt(int v) throws Exception {

        byte[] b = new byte[4];
        b[0] = (byte) ((v >>> 24) & 0xFF);
        b[1] = (byte) ((v >>> 16) & 0xFF);
        b[2] = (byte) ((v >>>  8) & 0xFF);
        b[3] = (byte) ((v >>>  0) & 0xFF);
        return b;
    }

    public static float getFloat(byte[] b,int offset)
                    throws Exception {
        return Float.intBitsToFloat(getInt(b,offset));
    }

    public static int getInt(byte[] buf, int offset)
                throws Exception {

        int ch1 = buf[offset++]&0xFF;
        int ch2 = buf[offset++]&0xFF;
        int ch3 = buf[offset++]&0xFF;
        int ch4 = buf[offset++]&0xFF;
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new Exception("NULL");
        }
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    /**
     * @param pos
     * @param rawData
     * @param data
     * @return
     */
    public static int copyBytes(boolean isConvert, int pos, byte[] rawData, byte[] data) {
        System.arraycopy(rawData, pos, data, 0, data.length);
        convertEndian(isConvert, data);
        return pos+=data.length;
    }

    /**
     * @param data
     * @return
     */
    public static String getString(byte[] data)
    {
        return new String(data);
    }

    /**
     * @param data
     * @return yyyyMMddhhmmss
     * @throws Exception
     */
    public static String getPLCDate(byte[] data) throws Exception {
        String yyyy="";
        String MM="";
        String dd="";
        String hh="";
        String mm="";
        String ss="";
        if(data.length!=7) {
            throw new Exception("PLC TIME LENGTH IS INVALID!");
        }else {
            yyyy="20"+((data[0]&0xF0)>>4)+""+(data[0]&0x0F);
            MM=((data[1]&0xF0)>>4)+""+(data[1]&0x0F);
            dd=((data[2]&0xF0)>>4)+""+(data[2]&0x0F);
            hh=((data[4]&0xF0)>>4)+""+(data[4]&0x0F);
            mm=((data[5]&0xF0)>>4)+""+(data[5]&0x0F);
            ss=((data[6]&0xF0)>>4)+""+(data[6]&0x0F);
        }
        return yyyy+MM+dd+hh+mm+ss;
    }

    public static byte[] getPLCDateByte(String date) {
        try {
            byte[] dateByte=null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(date));
            //cal.add(Calendar.MONTH, 1);
            int dayOfWeek = cal.get(cal.DAY_OF_WEEK);
            //Java : sunday:1~saturday:7
            //PLC Format : monday:1~sunday:7
            if(dayOfWeek==cal.SUNDAY) {
                dayOfWeek=7;
            }else {
                dayOfWeek--;
            }
            dateByte=append(Hex.encode(date.substring(2, 8)),new byte[] {(byte) dayOfWeek});
            dateByte=append(dateByte,Hex.encode(date.substring(8, 14)));
            return dateByte;
        }
        catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param data
     * @return week(monday:01~sunday:07)
     * @throws Exception
     */
    public static String getPLCWeek(byte[] data) throws Exception {
        String week="";
        if(data.length!=7) {
            throw new Exception("PLC TIME LENGTH IS INVALID!");
        }else {
            if(data[3]>7) {
                throw new Exception("PLC WEEK IS INVALID!");
            }
            week=((data[3]&0xF0)>>4)+""+(data[3]&0x0F);
        }
        return week;
    }

    public static String getPLCVer(byte[] data){
        String ver="Unknown";
        if(data[0]==0x10) {
            ver="ver1.0";
        }
        else if(data[0]==0x11) {
            ver="ver1.1";
        }
        return ver;
    }

    /**
     * @param data
     * @return
     */
    public static String getPLCIp(byte[] data){
        String fepIp="Unknown";
        //fepIp = getIntToByte(data[0])+":"+getIntToByte(data[1])+":"+getIntToByte(data[2])+":"+getIntToByte(data[3])+"/"+getIntToByte(data[4])+":"+getIntToByte(data[5]);
        fepIp = getIntToByte(data[0])+","+getIntToByte(data[1])+","+getIntToByte(data[2])+","+getIntToByte(data[3]);
        return fepIp;
    }

    public static byte[] getPLCIpByte(String ip) {
        byte[] byteIp = null;
        String splitIp[]=ip.split("[./:]");
        for(int i=0;i<splitIp.length;i++) {
            if(byteIp==null) {
                byteIp=(new byte[]{getByteToInt(splitIp[i])});
            }
            else{
                byteIp=append(byteIp, new byte[]{getByteToInt(splitIp[i])});
            }
        }
        return byteIp;
    }

    public static void main(String args[]) {
        log.debug(Hex.decode(getPLCDateByte("20050811130501")));
    }

    public static String getPLCMacAddr(byte[] data){
        String fepIp=Hex.decode(data);
        fepIp = fepIp.substring(0, 2)+":"+fepIp.substring(2, 4)+":"+fepIp.substring(4, 6)+":"+fepIp.substring(6, 8)+":"+fepIp.substring(8, 10)+":"+fepIp.substring(10, 12);
        return fepIp;
    }

    public static byte[] getPLCMacAddrByte(String macAddr){
        return Hex.encode(macAddr.replace(":", ""));
    }

    /**
     * Return Fixed Length Byte Array
     * @param obj
     * @param length return length
     * @return
     */
    public static byte[] getFixedLengthByte(Object obj, int length) {
        byte[] original = null;
        byte[] appendByte = null;
        if(obj instanceof String) {
            original = ((String)obj).getBytes();
        }
        else if(obj instanceof byte[]) {
            original = (byte[])obj;
        }
        if(original.length<length) {
            appendByte = new byte[length-original.length];
        }
        return appendByte!=null ? DataUtil.append(original, appendByte):original;
    }

    public static String getPLCCommandStr(byte command) {
        switch (command) {
            case 'A':
                return "Acknowledge";
            case 'B':
                return "IRM Status Request";
            case 'C':
                return "IRM  Configuration Set";
            case 'D':
                return "Data Request(Metering Data)";
            case 'E':
                return "Data Request(Max Demand Data)";
            case 'F':
                return "Data Request(LP Data)";
            case 'G':
                return "Data Request(PowerOutage)";
            case 'H':
                return "Data Request(Comm. Status)";
            case 'I':
                return "Meter Status Request";
            case 'J':
                return "Data Request(Trans. Data)";
            case 'M':
                return "Trans. Configuration Data Request";
            case 'N':
                return "Trans. Configuration Set";
            case 'a':
                return "Not Acknowledge";
            case 'b':
                return "IRM Status Response / IRM Trap";
            case 'd':
                return "Response(Metering Data)";
            case 'e':
                return "Response(Max Demand Data)";
            case 'f':
                return "Response(LP Data)";
            case 'g':
                return "Response(PowerOutage)";
            case 'h':
                return "Response(Comm. Status)";
            case 'i':
                return "Meter Status Response / Meter Trap";
            case 'j':
                return "Response(Trans. Data)";
            case 'k':
                return "Trap Info.(PLC Comm. Error Trap)";
            case 'l':
                return "Trap Info.(IR Comm. Error Trap)";
            case 'm':
                return "Trans. Configuration Data Response";
            default:
                return "Unknown";
        }
    }

    private static String getFMPVarBindValue(FMPTrap trap,
            String oidName)
    {
        String oid = MIBUtil.getInstance().getOid(oidName).getValue();
        log.debug("getFMPVarBindValue oidName["+oidName+"]"
                + " oid["+oid+"]");
        String value = "";
        try
        {
            VarBinds vb = trap.getVarBinds();
            if(vb.containsKey(oid))
            {
                value = ((FMPVariable)vb.get(oid)).toString();
            }
        }
        catch(Exception ex)
        {
            log.error("getVarBindValue : trap[" + trap.toString()
                    + "] oid["+oid+"]");
        }

        return value;
    }

    private static String getFMPTrapValue(FMPTrap trap, String attr)
    {
        log.debug("getFMPTrapValue : " + attr);

        int idx = attr.indexOf(',');
        if(idx >= 0) {
            attr = attr.substring(idx+1);
        }
        try
        {
            Object val = null;
            if(attr.equals("mcuId")
                    || attr.equals("code")
                    || attr.equals("sourceType")
                    || attr.equals("timeStamp"))
            {
                val = PropertyUtils.getProperty(trap, attr);
            }
            else if(attr.startsWith("varBinds"))
            {
                String varBindOid =
                    attr.substring("varBinds".length()+1);
                val = getFMPVarBindValue(trap,varBindOid);
            }
            else
            {
                log.error("getFMPTrapValue unknown attr["+attr+"]");
            }

            if(val == null) {
                val = "";
            }
            return val.toString();
        }
        catch(Exception ex)
        {
            log.error("getFMPTrapValue failed : ",ex);
        }

        return "";
    }

    public static String buildFMPTrapMessage(FMPTrap trap,
            String format) throws Exception
    {
        log.debug("buildFMPTrapMessage : format["+format+"]");

        String msg = null;
        if(format == null || format.length() < 1) {
            return format;
        }

        msg = format;

        String pattern = "([$][(][a-zA-Z0-9.]+[)])";
        RE re = new RE(pattern);

        int pos = 0;

        String res = "";

        while(re.match(msg))
        {
            String var = re.getParen(1);
            int idx = msg.indexOf(var);
            String before = msg.substring(0,idx);
            res = res + before;
            int var_len = var.length();
            res = res +
                getFMPTrapValue(trap,var.substring(2,var_len -1));
            pos = idx+var_len;
            msg = msg.substring(pos);
            log.debug("buildMessage : msg : " + msg);
        }

        if(pos == 0) {
            res = msg;
        }
        else {
            res = res + msg;
        }

        log.debug("buildFMPTrapMessage : message : "+res);

        return res;
    }

    /**
     * @param sValue
     * @return
     */
    public static Integer toInteger(String sValue){
        Double d = Double.parseDouble(sValue) ;
        int i = (int)(d * 1);
        return i;
    }
    
    /**
     * encode
     *
     * @param data <code>String</code> data
     * @return result <code>byte[]</code>
     */
    public static byte[] encodeTime(String data)
    {
        byte[] out = new byte[data.length() >> 1];
        int f = 0;
        for(int i = 0 ; i < out.length ; i++)
        {
            if(i == 0)
            {
                f = Integer.parseInt(data.substring(i*2,(i*2)+4));
                byte[] bx = get2ByteToInt(f);
                convertEndian(bx);
                out[i++]=bx[0];
                out[i] = bx[1];
            }
            else
            {
                f = Integer.parseInt(data.substring(i*2,(i*2)+2));
                out[i]=(byte)(f & 0xff);
            }
        }
        return out;
    }

    /**
     * decode
     *
     * @param data <code>byte[]</code> data
     * @param result <code>String</code>
     */
    public static String decodeTime(byte[] data)
    {
        StringBuffer sb = new StringBuffer();
        int f = 0;
        for(int i = 0 ; i < data.length ; i++)
        {
            if(i == 0)
            {
                byte[] bx = new byte[2];
                bx[0]=data[i++];
                bx[1]=data[i];
                convertEndian(bx);
                f = getIntTo2Byte(bx);
                sb.append(TimeUtil.to4Digit(f));
            }
            else
            {
                f = (data[i] & 0xff);
                sb.append(TimeUtil.to2Digit(f));
            }
        }

        return sb.toString();
    }
}

