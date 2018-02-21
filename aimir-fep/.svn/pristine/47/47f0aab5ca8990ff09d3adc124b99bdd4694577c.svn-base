package com.aimir.fep.util;

/**
 * Hex encoder and decoder.
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Hex {
    private static Log log = LogFactory.getLog(Hex.class);

    private static final char[] DIGITS = {
        '0', '1', '2', '3', '4', '5', '6', '7',
           '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /**
     *  encode
     *
     * @param data <code>String</code> Hex String
     * @return result <code>byte[]</code>
     */
    public static byte[] encode(String data)
    { 
        data = data.trim();
       	if(data.length()%2!=0)
            data=data+"0";
        char[] chars = data.toUpperCase().toCharArray();
        int len = chars.length; 
        if ((len & 0x01) != 0) { 
            log.info("Odd number of characters."); 
        }
        byte[] out = new byte[len >> 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(chars[j], j) << 4;
            j++;
            f = f | toDigit(chars[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }
    protected static int toDigit(char ch, int index) 
    { 
        int digit = Character.digit(ch, 16); 
        if (digit == -1) { 
            log.info("Illegal hexadecimal charcter " + ch 
                    + " at index " + index); 
        }
        return digit;
    }

    /**
     *  decode
     *
     * @param data <code>byte[]</code> Hex String
     * @return result <code>String</code>
     */
    public static String decode(byte[] data) 
    { 
        if(data == null)
            return "";

        int l = data.length; 
        char[] out = new char[l << 1]; 
        // two characters form the hex value.  
        for (int i = 0, j = 0; i < l; i++) { 
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4 ]; 
            out[j++] = DIGITS[ 0x0F & data[i] ]; 
        } 
        return new String(out);
    }

    /**
     *  decode
     *
     * @param data <code>byte[]</code> Hex String
     * @return result <code>String</code>
     */
    public static String decode(byte[] data,int pos,int len) 
    { 
        if(data == null)
            return "";

        //int l = data.length; 
        char[] out = new char[len << 1]; 
        // two characters form the hex value.  
        for (int i = pos, j = 0; i < len; i++) { 
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4 ]; 
            out[j++] = DIGITS[ 0x0F & data[i] ]; 
        } 
        return new String(out);
    }
    
    /**
     * Transform to with empty String. <p>
     * For Logging <p>
     * ex)  0x09 0x81 --> "09 81"
     * @param b byte array b
     * @return
     */
    public static String getHexDump(byte[] b){
        
        StringBuffer buf = new StringBuffer();
        
        try{
            for(int i = 0; i < b.length; i++) {
                int val = b[i];
                val = val & 0xff;
                buf.append(frontAppendNStr('0',Integer.toHexString(val),2)+" ");
                if(((i+1)%24) == 0) buf.append('\n');
            }
        }catch(Exception e){
            
        }
        return buf.toString();
    }
    
    /**
     * @param append source of String
     * @param str   to append  
     * @param length
     * @return
     */
    public static String frontAppendNStr(char append, String str, int length)
    {
        StringBuffer b = new StringBuffer("");

        try {
            if(str.length() < length)
            {
               for(int i = 0; i < length-str.length() ; i++)
                   b.append(append);
               b.append(str);
            }
            else
            {
                b.append(str);
            }
        } catch(Exception e) {
            
        }
        return b.toString();
    }
}

