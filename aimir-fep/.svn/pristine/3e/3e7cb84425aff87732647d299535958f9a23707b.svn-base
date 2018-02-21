package com.aimir.fep.protocol.fmp.frame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * AMUFrameControl
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오전 10:02:57$
 */
public class AMUFrameControl implements java.io.Serializable{

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(AMUFrameControl.class);
	
	byte	frameType;		// 4bit
	boolean isSecurity;		
	boolean isCompression;
	/* 
	 * ACK Frame은 단순히 Frame을 정상적으로 받았다는 응답
	 * Ack Request가 '1'이면 전송되는 Frmae에 대한 Ack Frame을 요청한다
	 */
	boolean isAckRequest;	
	boolean isPending;
	byte	destAddrType;	// 3bit
	byte	sourceAddrType;	// 3bit
	
	/**
	 * constructor
	 */
	public AMUFrameControl(){
	}
	
	/**
	 * constructor
	 * 
	 * @param frameType
	 * @param isSecurity
	 * @param isCompression
	 * @param isAckRequest
	 * @param isPending
	 * @param destAddr
	 * @param souceAddr
	 */
	public AMUFrameControl(byte frameType, boolean isSecurity,
			boolean isCompression, boolean isAckRequest, boolean isPending,
			byte destAddrType, byte sourceAddrType) {
		
		this.frameType 		= frameType;
		this.isSecurity 	= isSecurity;
		this.isCompression 	= isCompression;
		this.isAckRequest 	= isAckRequest;
		this.isPending 		= isPending;
		this.destAddrType 	= destAddrType;
		this.sourceAddrType = sourceAddrType;
	}
	
	/**
	 * get FrameType
	 * @return
	 */
	public byte getFrameType() {
		return frameType;
	}

	/**
	 * set FrameType
	 * @param frameType
	 */
	public void setFrameType(byte frameType) {
		this.frameType = frameType;
	}

	/**
	 * get isSecurity
	 * @return
	 */
	public boolean isSecurity() {
		return isSecurity;
	}

	/**
	 * set isSecurity
	 * @param isSecurity
	 */
	public void setSecurity(boolean isSecurity) {
		this.isSecurity = isSecurity;
	}

	/**
	 * get isCompression
	 * @return
	 */
	public boolean isCompression() {
		return isCompression;
	}

	/**
	 * set Compression
	 * @param isCompression
	 */
	public void setCompression(boolean isCompression) {
		this.isCompression = isCompression;
	}

	/**
	 * get isAckRequest
	 * @return
	 */
	public boolean isAckRequest() {
		return isAckRequest;
	}

	/**
	 * set isAckRequest
	 * @param isAckRequest
	 */
	public void setAckRequest(boolean isAckRequest) {
		this.isAckRequest = isAckRequest;
	}

	/**
	 * get isPending
	 * @return
	 */
	public boolean isPending() {
		return isPending;
	}

	/**
	 * set isPending
	 * @param isPending
	 */
	public void setPending(boolean isPending) {
		this.isPending = isPending;
	}

	/**
	 * get dest Address Type
	 * @return
	 */
	public byte getDestAddrType() {
		return destAddrType;
	}

	/**
	 * set dest Address Type
	 * @param destAddrType
	 */
	public void setDestAddrType(byte destAddrType) {
		this.destAddrType = destAddrType;
	}

	/**
	 * get Source Address Type
	 * @return
	 */
	public byte getSourceAddrType() {
		return sourceAddrType;
	}

	/**
	 * set Source Address Type
	 * @param sourceAddrType
	 */
	public void setSourceAddrType(byte sourceAddrType) {
		this.sourceAddrType = sourceAddrType;
	}

	/**
	 * EnCode
	 * 
	 * @return byte[]
	 */
	public byte[] encode() throws Exception{
		
		byte[] fcByte = new byte[AMUGeneralDataConstants.FRAME_CTRL_LEN];
		
		try{
			
			fcByte[0] |= (this.frameType << 4);
			
			if(isSecurity)
				fcByte[0] |= AMUGeneralDataConstants.ATTR_SECURITY ;
			if(isCompression)
				fcByte[0] |= AMUGeneralDataConstants.ATTR_COMPRESS;
			if(isAckRequest)
				fcByte[0] |= AMUGeneralDataConstants.ATTR_ACK_REQUEST;
			if(isPending)
				fcByte[0] |= AMUGeneralDataConstants.ATTR_FRAME_PENDING;
			
			fcByte[1] |= (destAddrType	<< 5);
			fcByte[1] |= (sourceAddrType<< 2);
		}catch (Exception e) {
			log.error("Frame Control encode failed : ", e);
			throw e;
		}
		
		return fcByte;
	}
	
	/**
	 * Decode
	 * 
	 * @param in 
	 * @return byte[]
	 * 
	 *  0000 0000  0000 0000
	 *    in[0]		 in[1]
	 *    
	 *    in[0] 	0000		0				0				0				0
	 *    		  frame Type	securtiy		compression		ack.requst	  frame pending
	 *  
	 */
	public static AMUFrameControl decode(byte[] in) throws Exception{
		
		AMUFrameControl control = new AMUFrameControl();
		
		try{
			control.setFrameType((byte)((byte)(in[0] & 0xF0) >> 4));	// Frame Type
			
			if((byte)((byte)(in[0] & AMUGeneralDataConstants.ATTR_SECURITY)) > 0)		// 암호화 여부
				control.setSecurity(true);
			else control.setSecurity(false);
			
			if((byte)((byte)(in[0] & AMUGeneralDataConstants.ATTR_COMPRESS)) > 0)		// 압축 여부	 
				control.setCompression(true);
			else control.setCompression(false);
			
			if((byte)((byte)(in[0] & AMUGeneralDataConstants.ATTR_ACK_REQUEST)) > 0)		// 응답 여부 
				control.setAckRequest(true);
			else control.setAckRequest(false);
			
			if((byte)((byte)(in[0] & AMUGeneralDataConstants.ATTR_FRAME_PENDING)) > 0)		// 미완료 여부
				control.setPending(true);
			else control.setPending(false);
					
			control.setDestAddrType((byte)(((byte)(in[1] & 0xE0)) >> 5));	// address type 설정
			control.setSourceAddrType((byte)(((byte)(in[1] & 0x1C)) >> 2));	// source type 설정
		}catch (Exception e) {
			log.error("Frame Control decode failed : ", e);
			throw e;
		}
		return control;
	}
	
	
	/**
	 * get Frame Type Message
	 * 
	 * @return String
	 */
	public String getFrameTypeMessage(){
		// @TODO OperationCode로 변경해야 함. 
		switch(this.frameType){
			case AMUGeneralDataConstants.FRAMETYPE_EVENT :
				return "Event Frame";		
			case AMUGeneralDataConstants.FRAMETYPE_LINK :
				return "Link Frame"; 		
			case AMUGeneralDataConstants.FRAMETYPE_BYPASS :
				return "Bypass Frame";	
			case AMUGeneralDataConstants.FRAMETYPE_COMMAND :
				return "Command Frame";	
			case AMUGeneralDataConstants.FRAMETYPE_METERING :
				return "Metering Frame"; 
			default :
				log.error("Not Found frame Type");
		}
		
		return "";
	}
	
	/**
	 * get Dest Address Desc
	 * 
	 * @return String
	 */
	public String getDestAddrDesc(){
		
		switch(this.destAddrType)
		{
			case (byte)0x00 :
				return "No Address";
			case (byte)0x01 :
				return "IP Address";
			case (byte)0x02 :
				return "EUI64 ID";
			case (byte)0x03 :
				return "Mobile Number";
			default :
				log.error("Not Found Dest Addr Desc");
		return "";
		}
	}
	
	/**
	 * get Source Address Desc
	 * 
	 * @return String
	 */
	public String getSourceAddrDesc(){
		
		switch(this.sourceAddrType)
		{
			case  AMUGeneralDataConstants.ATTR_ADDRTYPE_NONE :
				return "No Address";
			case AMUGeneralDataConstants.ATTR_ADDRTYPE_IP :
				return "IP Address";
			case AMUGeneralDataConstants.ATTR_ADDRTYPE_EUI64 :
				return "EUI64 ID";
			case AMUGeneralDataConstants.ATTR_ADDRTYPE_MOBILE :
				return "Mobile Number";
			default :
				log.error("Not Found Source Addr Desc");
		return "";
		}
	}
	
	/**
	 * get Desc Address Length
	 * 
	 * @return
	 */
	public int getDestTypeLenth() {
		
		switch(this.destAddrType){
			case AMUGeneralDataConstants.ATTR_ADDRTYPE_NONE :
				return AMUGeneralDataConstants.ADDRTYPE_NONE_LEN;
			case AMUGeneralDataConstants.ATTR_ADDRTYPE_IP :
				return AMUGeneralDataConstants.ADDRTYPE_IP_LEN;			// Hexdecimal(4 byte)
			case AMUGeneralDataConstants.ATTR_ADDRTYPE_EUI64 :
				return AMUGeneralDataConstants.ADDRTYPE_EUI64_LEN;		// EUI64 ID(8 byte)
			case AMUGeneralDataConstants.ATTR_ADDRTYPE_MOBILE :												
				return AMUGeneralDataConstants.ADDRTYPE_MOBILE_LEN;		// BCD Mobile number(6 byte)
			default :
				log.error("Not Found Dest Type Length");
				return 0;
		}
	}
	
	/**
	 * get Source Address Type Length
	 * @return
	 */
	public int getSourceTypeLenth() {
		
		switch(this.sourceAddrType){
			case AMUGeneralDataConstants.ATTR_ADDRTYPE_NONE :
				return AMUGeneralDataConstants.ADDRTYPE_NONE_LEN;
			case AMUGeneralDataConstants.ATTR_ADDRTYPE_IP :
				return AMUGeneralDataConstants.ADDRTYPE_IP_LEN;
			case AMUGeneralDataConstants.ATTR_ADDRTYPE_EUI64 :
				return AMUGeneralDataConstants.ADDRTYPE_EUI64_LEN;
			case AMUGeneralDataConstants.ATTR_ADDRTYPE_MOBILE :
				return AMUGeneralDataConstants.ADDRTYPE_MOBILE_LEN;
			default :
				log.error("Not Found Source Type Length");
		return 0;
		}
	}
	
	/**
	 * get Source Type
	 * (Sensor or MCU)
	 * @return
	 */
	public String getSourceType() {
		
		switch(this.sourceAddrType){
			case AMUGeneralDataConstants.ATTR_ADDRTYPE_NONE :
				return "";
			case AMUGeneralDataConstants.ATTR_ADDRTYPE_IP :
				return AMUGeneralDataConstants.MCU;
			case AMUGeneralDataConstants.ATTR_ADDRTYPE_EUI64 :
				return AMUGeneralDataConstants.SERSOR;
			case AMUGeneralDataConstants.ATTR_ADDRTYPE_MOBILE :
				return AMUGeneralDataConstants.MCU;
			default :
				log.error("Not Found Source Type ");
		return "";
		}
	}
	
	/**
	 * get Source Addr String
	 * (Sensor or MCU)
	 * @return
	 */
	public String getSourceAddr(byte[] sourceAddr) {
		
	    if(this.sourceAddrType == AMUGeneralDataConstants.ATTR_ADDRTYPE_IP){
            return DataUtil.getIntToByte(sourceAddr[0]) + "." +
                   DataUtil.getIntToByte(sourceAddr[1]) + "." +
                   DataUtil.getIntToByte(sourceAddr[2]) + "." +
                   DataUtil.getIntToByte(sourceAddr[3]);
	    } else if (this.sourceAddrType == AMUGeneralDataConstants.ATTR_ADDRTYPE_MOBILE) {
            return Hex.decode(sourceAddr).substring(1);
        } else {
            return Hex.decode(sourceAddr);
        }
	}
	
	/**
     * get Dest Id String
     * (Sensor or MCU)
     * @return
     */
    public String getDestAddr(byte[] destAddr) {
        
        if(this.destAddrType == AMUGeneralDataConstants.ATTR_ADDRTYPE_IP){
            return DataUtil.getIntToByte(destAddr[0]) + "." +
                   DataUtil.getIntToByte(destAddr[1]) + "." +
                   DataUtil.getIntToByte(destAddr[2]) + "." +
                   DataUtil.getIntToByte(destAddr[3]);
        } else if (this.destAddrType == AMUGeneralDataConstants.ATTR_ADDRTYPE_MOBILE) {
            return Hex.decode(destAddr).substring(1);
        } else {
            return Hex.decode(destAddr);
        }
    }
	
	public String toString(){
		
		StringBuffer retVal = new StringBuffer();
		
		retVal.append("\n==Frame Control==").append('\n')
			.append("Frame Type : " + getFrameTypeMessage()).append('\n')
			.append("Security Enable : " + isSecurity() ).append('\n')
			.append("Compression Enable : " + isCompression()).append('\n')
			.append("Ack. Request : "+ isAckRequest()).append('\n')
			.append("Frame Pending : " +  isPending()).append('\n')
			.append("Dest. address type ["+Hex.decode(new byte[]{getDestAddrType()})+"]" + getDestAddrDesc()).append('\n')
			.append("Source Address type["+Hex.decode(new byte[]{getSourceAddrType()})+"]" + getSourceAddrDesc()).append('\n');
		
		return retVal.toString();
	}	
}