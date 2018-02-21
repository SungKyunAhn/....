package com.aimir.fep.protocol.security.frame;

import com.aimir.fep.protocol.fmp.frame.AMUFrameControl;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataFrame;
import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;


import com.aimir.fep.util.Hex;



import java.io.ByteArrayOutputStream;


/**
 * @author 
 *
 */
public class AuthDataFrame implements java.io.Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(AuthDataFrame.class);
    
    public FrameType   frameType;
    public short version;
    public byte[] deviceSerial;
    public int   payload_length;
    public byte  payload[];
    
    public AuthDataFrame(FrameType frameType, short version, byte[] deviceSerial, int payload_length, byte[] payload)
    {
    	this.frameType = frameType;
    	this.version = version;
    	this.deviceSerial = deviceSerial;
    	this.payload = new byte[payload_length];
    	System.arraycopy(payload, 0, this.payload, 0, payload_length);
    	this.payload_length = payload_length;
    }
    
/*    public AuthDataFrame(int frameType, short version, int payload_length,  byte[] payload, byte pending, byte frameCount, byte sequenceNumber)
    {
    	this.frameType = frameType;
    	this.version = version;
    	this.payload = new byte[payload_length];
    	System.arraycopy(payload, 0, this.payload, 0, payload_length);
    	this.payload_length = payload_length;
    	this.frameCount = frameCount;
    	this.pending = pending;
    	this.sequenceNumber = sequenceNumber;
    }*/
 
	/**
	 * Copy Constructor
	 *
	 * @param AuthFrameData a <code>AuthFrameData</code> object
	 */
	public AuthDataFrame(AuthDataFrame pData)
	{	
	    this.frameType = pData.frameType;
	    this.version = pData.version;
	    this.deviceSerial = pData.deviceSerial;
	    this.payload_length = pData.payload_length;
	    this.payload = pData.payload;
	}


	/**
	 * 
	 */
	public AuthDataFrame() {

	}


	/**
	 * @return the frameType
	 */
	public FrameType getFrameType() {
		return frameType;
	}

	/**
	 * @param frameType the frameType to set
	 */
	public void setFrameType(FrameType frameType) {
		this.frameType = frameType;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(short version) {
		this.version = version;
	}
	
	public byte[] getdeviceSerial(){
		return this.deviceSerial;
	}
	public void setdeviceSerial(byte[] deviceSerial){
		this.deviceSerial = deviceSerial;
	}

	/**
	 * @return the payload_length
	 */
	public int getPayload_length() {
		return payload_length;
	}

	/**
	 * @param payload_length the payload_length to set
	 */
	public void setPayload_length(int payload_length) {
		this.payload_length = payload_length;
	}

	/**
	 * @return the payload
	 */
	public byte[] getPayload() {
		return payload;
	}


	/**
	 * @param payload the payload to set
	 */
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation
	 * of this object.
	 */
	public String toString()
	{
	    final String TAB = "\n";

	    StringBuffer retValue = new StringBuffer();
	    
	    retValue.append("AuthGeneralFrame ( ")
	        .append(super.toString()).append(TAB)
	        .append("frametype = ").append(this.frameType).append(TAB)
	        .append("version = ").append(this.version).append(TAB)
	        .append("deviceSerial = ").append(Hex.decode(this.deviceSerial)).append(TAB)
	        .append("payload_length = ").append(this.payload_length).append(TAB)
	        .append("payload = ").append(Hex.getHexDump(this.payload)).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
