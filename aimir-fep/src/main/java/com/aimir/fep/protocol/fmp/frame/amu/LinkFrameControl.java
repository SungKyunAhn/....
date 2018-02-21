package com.aimir.fep.protocol.fmp.frame.amu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataConstants;
import com.aimir.fep.util.Hex;

/**
 * Link Frame Control 
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 14. 오전 10:34:11$
 */
public class LinkFrameControl {

	private static Log log = LogFactory.getLog(LinkFrameControl.class);
	
	boolean isRequest;
	boolean isOpened;
	boolean isResRequest;
	boolean isSetLinkTimeOut;
	byte miuAddrType;
	
	/**
	 * constructor
	 */
	public LinkFrameControl(){
	}
	
	/**
	 * constructor
	 * @param isRequest			
	 * @param isOpened			
	 * @param isResRequest		수신자 응답여부
	 * @param isSetLinkTimeOut	Time Out설정여부
	 * @param miuAddrType		MIU Address Type 설정 field
	 */
	public LinkFrameControl( boolean isRequest, boolean isOpened, 
			boolean isResRequest, boolean isSetLinkTimeOut, byte miuAddrType ) {
		
		this.isRequest 			= isRequest;
		this.isOpened 			= isOpened;
		this.isResRequest 		= isResRequest;
		this.isSetLinkTimeOut 	= isSetLinkTimeOut;
		this.miuAddrType		= miuAddrType;
	}

	/**
	 * get isRequest
	 * @return
	 */
	public boolean isRequest() {
		return isRequest;
	}

	/**
	 * set isRequest
	 * @param isRequest
	 */
	public void setRequest(boolean isRequest) {
		this.isRequest = isRequest;
	}

	/**
	 * get isOpened
	 * @return 
	 */
	public boolean isOpened() {
		return isOpened;
	}

	/**
	 * set isOpened
	 * @param isOpened
	 */
	public void setOpened(boolean isOpened) {
		this.isOpened = isOpened;
	}

	/**
	 * get isResRequest
	 * @return
	 */
	public boolean isResRequest() {
		return isResRequest;
	}

	/**
	 * set isResRequest
	 * @param isResRequest
	 */
	public void setResRequest(boolean isResRequest) {
		this.isResRequest = isResRequest;
	}

	/**
	 * get isSetLinkTimeOut
	 * @return
	 */
	public boolean isSetLinkTimeOut() {
		return isSetLinkTimeOut;
	}

	/**
	 * set isSetLinkTimeOut
	 * @param isSetLinkTimeOut
	 */
	public void setLinkTimeOut(boolean isSetLinkTimeOut) {
		this.isSetLinkTimeOut = isSetLinkTimeOut;
	}

	/**
	 * get miuAddrType
	 * @return
	 */
	public byte getMiuAddrType() {
		return miuAddrType;
	}

	/**
	 * set miuAddrType
	 * @param miuAddrType
	 */
	public void setMiuAddrType(byte miuAddrType) {
		this.miuAddrType = miuAddrType;
	}
	
	/**
	 * encode
	 * 
	 * @return byte[]
	 */
	public byte encode() throws Exception{
		
		byte fcByte = 0;
		
		try{
			
			if(this.isRequest)
				fcByte |=  (byte)0x80; 	
			if(this.isOpened)
				fcByte |=  (byte)0x40;
			if(this.isResRequest)
				fcByte |=  (byte)0x20;
			if(this.isSetLinkTimeOut)
				fcByte |=  (byte)0x10;
			
			fcByte |=  (this.miuAddrType << 1);
			
		}catch(Exception e){
			log.error("Link Frame Control encode failed : ", e);
			throw e;
		}
		return fcByte;
	}
	
	/**
	 * decode 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static LinkFrameControl decode(byte in) throws Exception{
		
		try{
			LinkFrameControl control = new LinkFrameControl();
			log.debug("LinkFrameControl Decode : " + Hex.decode(new byte[]{in}));
			log.debug("##### LinkFrameControl Decode Start ######");
			
			if(((byte)in & 0x80) > 0){		// Request or Response 여부
				control.setRequest(true);
				log.debug("Set Request True");
			}
			else{
				control.setRequest(false);
				log.debug("Set Request false");
			}
			
			if(((byte)in & 0x40) > 0){		// Open or Close 여부
				control.setOpened(true);
				log.debug("Set LinkOpen True");
			}
			else{
				control.setOpened(false);
				log.debug("Set LinkOpen false");
			}
			
			if(((byte)in & 0x20) > 0){		// 수신자  응답 여부
				control.setResRequest(true);
				log.debug("Set Res Request True");
			}
			else {
				control.setResRequest(false);
				log.debug("Set Res Request false");
			}
			
			if(((byte)in & 0x10) > 0){		// Set Link Time Out 설정 여부
				control.setLinkTimeOut(true);
				log.debug("Set set LinkTimeOut True");
			}
			else{ 
				control.setLinkTimeOut(false);
				log.debug("Set set LinkTimeOut false");
			}
			
			control.setMiuAddrType((byte)(in & 0x0E));	// miu address type 설정
			
			log.debug("##### LinkFrameControl Decode End ######");
			return control;
		}catch (Exception e) {
			log.error("Frame Control decode failed : ", e);
			throw e;
		}
	}
	
	/**
	 * get MIU Address Desc
	 * 
	 * @return String
	 */
	public String getMiuAddrDesc(){
		switch(this.miuAddrType){
			case (byte)0x00 :
				return "No Address";
			case (byte)0x01 :
				return "IP Address";
			case (byte)0x02 :
				return "EUI64 ID";
			case (byte)0x03 :
				return "Mobile Number";
			default :
				log.error("Not Found MIU Address Type Desc");
		return null;
		}
	}
	
	/**
	 * get MIU Address Length
	 * 
	 * @return
	 */
	public int getMiuAddrTypeLenth() {
		
		switch(this.miuAddrType){
			case (byte)0x00 :
				return AMUGeneralDataConstants.ADDRTYPE_NONE_LEN;
			case (byte)0x01 :
				return AMUGeneralDataConstants.ADDRTYPE_IP_LEN;			// Hexdecimal(4 byte)
			case (byte)0x02 :
				return AMUGeneralDataConstants.ADDRTYPE_EUI64_LEN;		// EUI64 ID(8 byte)
			case (byte)0x03 :												
				return AMUGeneralDataConstants.ADDRTYPE_MOBILE_LEN;		// BCD Mobile number(6 byte)
			default :
				log.error("Not FoundMIU Address Type Length");
				return 0;
		}
	}
	
}


