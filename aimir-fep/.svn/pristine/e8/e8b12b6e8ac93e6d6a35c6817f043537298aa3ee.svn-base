package com.aimir.fep.meter.parser.MX2Table;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;

/**
 * @author jiae
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DisplayIdSet", propOrder = {
		   "displayId"
		})
public class DisplayIdSet implements java.io.Serializable {
	private static Log log = LogFactory.getLog(DisplayIdSet.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -7499839843839604455L;

	String displayId;
	
	public DisplayIdSet(){}

	public String getDisplayId() {
		return displayId;
	}

	public void setDisplayId(String displayId) {
		this.displayId = displayId;
	}

	public byte[] toByteArray() throws Exception {

		if(this.displayId == null)
			throw new Exception("Can not found Calendar type data");
		
		displayId = displayId.toLowerCase();
		byte[] digit = new byte[3];
		byte[] data = new byte[3];
		
		for (int i = 0; i < digit.length; i++) {
			if((int)displayId.charAt(i) < 103 && (int)displayId.charAt(i) != 95 && (int)displayId.charAt(i) != 45) {   // 아스키 코드값으로 f의 코드값인 102를 포함한 hex값
				digit[i] = hexEncode(displayId.substring(i,i+1));
				data[i] =digit[i];
			} else {
				digit[i] = etcEncode(displayId.substring(i,i+1));
				data[i] =digit[i];
			}
		}
		
		int dataInt = DataUtil.getIntToBytes(data);
		
		data = DataUtil.get3ByteToInt(dataInt);

		return data;
	}
	
	/**
	 * @desc digit값을 HexEncode 처리 
	 * 
	 * @param digit
	 * @return 
	 * @returnType byte
	 */
	private static byte hexEncode(String digit)
    { 
       	if(digit.length()%2!=0)
       		digit="0"+digit;
        char[] chars = digit.toUpperCase().toCharArray();
        int len = chars.length; 
        if ((len & 0x01) != 0) { 
            log.info("Odd number of characters."); 
        }

        // two characters form the hex value.
        int f = toDigit(chars[0], 0) << 4;
        f = f | toDigit(chars[1], 1);
        byte out = (byte) (f & 0xFF);
        
        return out;
    }
	
	/**
	 * @desc digit값을 스펙문서에 있는 지정된 값으로 변환
	 * 
	 * @param digit
	 * @return 
	 * @returnType byte
	 */
	private static byte etcEncode(String digit)
    { 
        digit = digit.replace("h", "16").replace("i", "17").replace("j", "18").replace("l", "19").replace("n", "20")
        		.replace("o", "21").replace("p", "22").replace("r", "23").replace("t", "24").replace("u", "25")
				.replace("y", "26").replace("-", "27").replace("_", "28");
        
        if(Integer.parseInt(digit) > 28)
        	log.info("Odd number of characters.");  
        byte out = DataUtil.getByteToInt(digit);
        
        return out;
    }
	
	private static int toDigit(char ch, int index) 
    { 
        int digit = Character.digit(ch, 16); 
        return digit;
    }
	
}
